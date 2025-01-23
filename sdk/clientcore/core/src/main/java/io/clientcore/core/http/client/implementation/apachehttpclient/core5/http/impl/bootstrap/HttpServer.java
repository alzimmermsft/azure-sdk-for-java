/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.impl.bootstrap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Internal;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.concurrent.DefaultThreadFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.function.Callback;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.ExceptionListener;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.URIScheme;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.config.CharCodingConfig;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.config.Http1Config;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.impl.io.DefaultBHttpServerConnection;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.impl.io.DefaultBHttpServerConnectionFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.impl.io.HttpService;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.io.HttpConnectionFactory;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.http.io.SocketConfig;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.io.CloseMode;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.io.Closer;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.io.ModalCloseable;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.io.SocketSupport;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Args;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.TimeValue;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.util.Timeout;

/**
 * HTTP/1.1 server side message exchange handler.
 *
 * @since 4.4
 */
public class HttpServer implements ModalCloseable {

    enum Status { READY, ACTIVE, STOPPING }

    private final int port;
    private final InetAddress ifAddress;
    private final SocketConfig socketConfig;
    private final ServerSocketFactory serverSocketFactory;
    private final HttpService httpService;
    private final HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory;
    private final SSLContext sslContext;
    private final Callback<SSLParameters> sslSetupHandler;
    private final ExceptionListener exceptionListener;
    private final ThreadPoolExecutor listenerExecutorService;
    private final ThreadGroup workerThreads;
    private final WorkerPoolExecutor workerExecutorService;
    private final AtomicReference<Status> status;

    private volatile ServerSocket serverSocket;
    private volatile RequestListener requestListener;

    @Internal
    public HttpServer(
            final int port,
            final HttpService httpService,
            final InetAddress ifAddress,
            final SocketConfig socketConfig,
            final ServerSocketFactory serverSocketFactory,
            final HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory,
            final SSLContext sslContext,
            final Callback<SSLParameters> sslSetupHandler,
            final ExceptionListener exceptionListener) {
        this.port = Args.notNegative(port, "Port value is negative");
        this.httpService = Args.notNull(httpService, "HTTP service");
        this.ifAddress = ifAddress;
        this.socketConfig = socketConfig != null ? socketConfig : SocketConfig.DEFAULT;
        this.serverSocketFactory = serverSocketFactory != null ? serverSocketFactory : ServerSocketFactory.getDefault();
        this.connectionFactory = connectionFactory != null ? connectionFactory : new DefaultBHttpServerConnectionFactory(
                this.serverSocketFactory instanceof SSLServerSocketFactory ? URIScheme.HTTPS.id : URIScheme.HTTP.id,
                Http1Config.DEFAULT,
                CharCodingConfig.DEFAULT);
        this.sslContext = sslContext;
        this.sslSetupHandler = sslSetupHandler;
        this.exceptionListener = exceptionListener != null ? exceptionListener : ExceptionListener.NO_OP;
        this.listenerExecutorService = new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new DefaultThreadFactory("HTTP-listener-" + this.port));
        this.workerThreads = new ThreadGroup("HTTP-workers");
        this.workerExecutorService = new WorkerPoolExecutor(
                0, Integer.MAX_VALUE, 1L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new DefaultThreadFactory("HTTP-worker", this.workerThreads, true));
        this.status = new AtomicReference<>(Status.READY);
    }

    public InetAddress getInetAddress() {
        final ServerSocket localSocket = this.serverSocket;
        if (localSocket != null) {
            return localSocket.getInetAddress();
        }
        return null;
    }

    public int getLocalPort() {
        final ServerSocket localSocket = this.serverSocket;
        if (localSocket != null) {
            return localSocket.getLocalPort();
        }
        return -1;
    }

    public void start() throws IOException {
        if (this.status.compareAndSet(Status.READY, Status.ACTIVE)) {
            this.serverSocket = this.serverSocketFactory.createServerSocket(
                    this.port, this.socketConfig.getBacklogSize(), this.ifAddress);
            this.serverSocket.setReuseAddress(this.socketConfig.isSoReuseAddress());
            if (this.socketConfig.getRcvBufSize() > 0) {
                this.serverSocket.setReceiveBufferSize(this.socketConfig.getRcvBufSize());
            }
            if (this.socketConfig.getTcpKeepIdle() > 0) {
                SocketSupport.setOption(this.serverSocket, SocketSupport.TCP_KEEPIDLE, this.socketConfig.getTcpKeepIdle());
            }
            if (this.socketConfig.getTcpKeepInterval() > 0) {
                SocketSupport.setOption(this.serverSocket, SocketSupport.TCP_KEEPINTERVAL, this.socketConfig.getTcpKeepInterval());
            }
            if (this.socketConfig.getTcpKeepCount() > 0) {
                SocketSupport.setOption(this.serverSocket, SocketSupport.TCP_KEEPCOUNT, this.socketConfig.getTcpKeepCount());
            }
            if (this.sslSetupHandler != null && this.serverSocket instanceof SSLServerSocket) {
                final SSLServerSocket sslServerSocket = (SSLServerSocket) this.serverSocket;
                final SSLParameters sslParameters = sslServerSocket.getSSLParameters();
                this.sslSetupHandler.execute(sslParameters);
                sslServerSocket.setSSLParameters(sslParameters);
            }
            this.requestListener = new RequestListener(
                    this.socketConfig,
                    this.serverSocket,
                    this.httpService,
                    this.connectionFactory,
                    this.sslContext != null ? this.sslContext.getSocketFactory() : null,
                    this.sslSetupHandler,
                    this.exceptionListener,
                    this.workerExecutorService);
            this.listenerExecutorService.execute(this.requestListener);
        }
    }

    public void stop() {
        if (this.status.compareAndSet(Status.ACTIVE, Status.STOPPING)) {
            this.listenerExecutorService.shutdownNow();
            this.workerExecutorService.shutdown();
            final RequestListener local = this.requestListener;
            if (local != null) {
                try {
                    local.terminate();
                } catch (final IOException ex) {
                    this.exceptionListener.onError(ex);
                }
            }
            this.workerThreads.interrupt();
        }
    }

    public void initiateShutdown() {
        stop();
    }

    public void awaitTermination(final TimeValue waitTime) throws InterruptedException {
        Args.notNull(waitTime, "Wait time");
        this.workerExecutorService.awaitTermination(waitTime.getDuration(), waitTime.getTimeUnit());
    }

    @Override
    public void close(final CloseMode closeMode) {
        close(closeMode, Timeout.ofSeconds(5));
    }

    /**
     * Closes this process or endpoint and releases any system resources associated
     * with it. If the endpoint or the process is already closed then invoking this
     * method has no effect.
     *
     * @param closeMode How to close the receiver.
     * @param timeout  How long to wait for the HttpServer to close gracefully.
     * @since 5.2
     */
    public void close(final CloseMode closeMode, final Timeout timeout) {
        initiateShutdown();
        if (closeMode == CloseMode.GRACEFUL) {
            try {
                awaitTermination(timeout);
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        final Set<Worker> workers = this.workerExecutorService.getWorkers();
        for (final Worker worker: workers) {
            Closer.close(worker.getConnection(), CloseMode.GRACEFUL);
        }
    }

    @Override
    public void close() {
        close(CloseMode.GRACEFUL);
    }

}
