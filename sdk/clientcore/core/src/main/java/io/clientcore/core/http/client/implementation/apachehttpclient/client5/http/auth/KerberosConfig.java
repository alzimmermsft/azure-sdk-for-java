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

package io.clientcore.core.http.client.implementation.apachehttpclient.client5.http.auth;

import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.Contract;
import io.clientcore.core.http.client.implementation.apachehttpclient.core5.annotation.ThreadingBehavior;

/**
 *  Immutable class encapsulating Kerberos configuration options.
 *
 *  @since 4.6
 *
 * @deprecated Do not use. The GGS based experimental authentication schemes are no longer
 * supported. Consider using Basic or Bearer authentication with TLS instead.
 *
 */
@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class KerberosConfig implements Cloneable {

    public enum Option {

        DEFAULT,
        ENABLE,
        DISABLE

    }

    public static final KerberosConfig DEFAULT = new Builder().build();

    private final Option stripPort;
    private final Option useCanonicalHostname;
    private final Option requestDelegCreds;

    KerberosConfig(
            final Option stripPort,
            final Option useCanonicalHostname,
            final Option requestDelegCreds) {
        super();
        this.stripPort = stripPort;
        this.useCanonicalHostname = useCanonicalHostname;
        this.requestDelegCreds = requestDelegCreds;
    }

    public Option getStripPort() {
        return stripPort;
    }

    public Option getUseCanonicalHostname() {
        return useCanonicalHostname;
    }

    public Option getRequestDelegCreds() {
        return requestDelegCreds;
    }

    @Override
    protected KerberosConfig clone() throws CloneNotSupportedException {
        return (KerberosConfig) super.clone();
    }

    @Override
    public String toString() {
        return "[stripPort=" + stripPort + ", useCanonicalHostname=" + useCanonicalHostname
            + ", requestDelegCreds=" + requestDelegCreds + "]";
    }

    public static Builder custom() {
        return new Builder();
    }

    public static Builder copy(final KerberosConfig config) {
        return new Builder()
                .setStripPort(config.getStripPort())
                .setUseCanonicalHostname(config.getUseCanonicalHostname())
                .setRequestDelegCreds(config.getRequestDelegCreds());
    }

    public static class Builder {

        private Option stripPort;
        private Option useCanonicalHostname;
        private Option requestDelegCreds;

        Builder() {
            super();
            this.stripPort = Option.DEFAULT;
            this.useCanonicalHostname = Option.DEFAULT;
            this.requestDelegCreds = Option.DEFAULT;
        }

        public Builder setStripPort(final Option stripPort) {
            this.stripPort = stripPort;
            return this;
        }

        public Builder setUseCanonicalHostname(final Option useCanonicalHostname) {
            this.useCanonicalHostname = useCanonicalHostname;
            return this;
        }

        public Builder setRequestDelegCreds(final Option requestDelegCreds) {
            this.requestDelegCreds = requestDelegCreds;
            return this;
        }

        public KerberosConfig build() {
            return new KerberosConfig(
                    stripPort,
                    useCanonicalHostname,
                    requestDelegCreds);
        }

    }

}
