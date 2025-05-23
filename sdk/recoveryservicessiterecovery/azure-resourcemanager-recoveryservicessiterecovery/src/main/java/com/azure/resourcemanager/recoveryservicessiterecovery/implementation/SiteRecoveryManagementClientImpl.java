// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicessiterecovery.implementation;

import com.azure.core.annotation.ServiceClient;
import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.rest.Response;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.exception.ManagementError;
import com.azure.core.management.exception.ManagementException;
import com.azure.core.management.polling.PollResult;
import com.azure.core.management.polling.PollerFactory;
import com.azure.core.util.Context;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.logging.ClientLogger;
import com.azure.core.util.polling.AsyncPollResponse;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollerFlux;
import com.azure.core.util.serializer.SerializerAdapter;
import com.azure.core.util.serializer.SerializerEncoding;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ClusterRecoveryPointOperationsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ClusterRecoveryPointsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.MigrationRecoveryPointsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.OperationsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.RecoveryPointsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationAlertSettingsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationAppliancesClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationEligibilityResultsOperationsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationEventsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationFabricsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationJobsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationLogicalNetworksClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationMigrationItemsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationNetworkMappingsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationNetworksClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationPoliciesClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationProtectableItemsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationProtectedItemsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationProtectionClustersClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationProtectionContainerMappingsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationProtectionContainersClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationProtectionIntentsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationRecoveryPlansClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationRecoveryServicesProvidersClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationVaultHealthsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationVaultSettingsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.ReplicationvCentersClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.SiteRecoveryManagementClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.StorageClassificationMappingsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.StorageClassificationsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.SupportedOperatingSystemsOperationsClient;
import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.TargetComputeSizesClient;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Initializes a new instance of the SiteRecoveryManagementClientImpl type.
 */
@ServiceClient(builder = SiteRecoveryManagementClientBuilder.class)
public final class SiteRecoveryManagementClientImpl implements SiteRecoveryManagementClient {
    /**
     * The subscription Id.
     */
    private final String subscriptionId;

    /**
     * Gets The subscription Id.
     * 
     * @return the subscriptionId value.
     */
    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    /**
     * server parameter.
     */
    private final String endpoint;

    /**
     * Gets server parameter.
     * 
     * @return the endpoint value.
     */
    public String getEndpoint() {
        return this.endpoint;
    }

    /**
     * Api Version.
     */
    private final String apiVersion;

    /**
     * Gets Api Version.
     * 
     * @return the apiVersion value.
     */
    public String getApiVersion() {
        return this.apiVersion;
    }

    /**
     * The HTTP pipeline to send requests through.
     */
    private final HttpPipeline httpPipeline;

    /**
     * Gets The HTTP pipeline to send requests through.
     * 
     * @return the httpPipeline value.
     */
    public HttpPipeline getHttpPipeline() {
        return this.httpPipeline;
    }

    /**
     * The serializer to serialize an object into a string.
     */
    private final SerializerAdapter serializerAdapter;

    /**
     * Gets The serializer to serialize an object into a string.
     * 
     * @return the serializerAdapter value.
     */
    SerializerAdapter getSerializerAdapter() {
        return this.serializerAdapter;
    }

    /**
     * The default poll interval for long-running operation.
     */
    private final Duration defaultPollInterval;

    /**
     * Gets The default poll interval for long-running operation.
     * 
     * @return the defaultPollInterval value.
     */
    public Duration getDefaultPollInterval() {
        return this.defaultPollInterval;
    }

    /**
     * The OperationsClient object to access its operations.
     */
    private final OperationsClient operations;

    /**
     * Gets the OperationsClient object to access its operations.
     * 
     * @return the OperationsClient object.
     */
    public OperationsClient getOperations() {
        return this.operations;
    }

    /**
     * The ReplicationAlertSettingsClient object to access its operations.
     */
    private final ReplicationAlertSettingsClient replicationAlertSettings;

    /**
     * Gets the ReplicationAlertSettingsClient object to access its operations.
     * 
     * @return the ReplicationAlertSettingsClient object.
     */
    public ReplicationAlertSettingsClient getReplicationAlertSettings() {
        return this.replicationAlertSettings;
    }

    /**
     * The ReplicationAppliancesClient object to access its operations.
     */
    private final ReplicationAppliancesClient replicationAppliances;

    /**
     * Gets the ReplicationAppliancesClient object to access its operations.
     * 
     * @return the ReplicationAppliancesClient object.
     */
    public ReplicationAppliancesClient getReplicationAppliances() {
        return this.replicationAppliances;
    }

    /**
     * The ReplicationEligibilityResultsOperationsClient object to access its operations.
     */
    private final ReplicationEligibilityResultsOperationsClient replicationEligibilityResultsOperations;

    /**
     * Gets the ReplicationEligibilityResultsOperationsClient object to access its operations.
     * 
     * @return the ReplicationEligibilityResultsOperationsClient object.
     */
    public ReplicationEligibilityResultsOperationsClient getReplicationEligibilityResultsOperations() {
        return this.replicationEligibilityResultsOperations;
    }

    /**
     * The ReplicationEventsClient object to access its operations.
     */
    private final ReplicationEventsClient replicationEvents;

    /**
     * Gets the ReplicationEventsClient object to access its operations.
     * 
     * @return the ReplicationEventsClient object.
     */
    public ReplicationEventsClient getReplicationEvents() {
        return this.replicationEvents;
    }

    /**
     * The ReplicationFabricsClient object to access its operations.
     */
    private final ReplicationFabricsClient replicationFabrics;

    /**
     * Gets the ReplicationFabricsClient object to access its operations.
     * 
     * @return the ReplicationFabricsClient object.
     */
    public ReplicationFabricsClient getReplicationFabrics() {
        return this.replicationFabrics;
    }

    /**
     * The ReplicationLogicalNetworksClient object to access its operations.
     */
    private final ReplicationLogicalNetworksClient replicationLogicalNetworks;

    /**
     * Gets the ReplicationLogicalNetworksClient object to access its operations.
     * 
     * @return the ReplicationLogicalNetworksClient object.
     */
    public ReplicationLogicalNetworksClient getReplicationLogicalNetworks() {
        return this.replicationLogicalNetworks;
    }

    /**
     * The ReplicationNetworksClient object to access its operations.
     */
    private final ReplicationNetworksClient replicationNetworks;

    /**
     * Gets the ReplicationNetworksClient object to access its operations.
     * 
     * @return the ReplicationNetworksClient object.
     */
    public ReplicationNetworksClient getReplicationNetworks() {
        return this.replicationNetworks;
    }

    /**
     * The ReplicationNetworkMappingsClient object to access its operations.
     */
    private final ReplicationNetworkMappingsClient replicationNetworkMappings;

    /**
     * Gets the ReplicationNetworkMappingsClient object to access its operations.
     * 
     * @return the ReplicationNetworkMappingsClient object.
     */
    public ReplicationNetworkMappingsClient getReplicationNetworkMappings() {
        return this.replicationNetworkMappings;
    }

    /**
     * The ReplicationProtectionContainersClient object to access its operations.
     */
    private final ReplicationProtectionContainersClient replicationProtectionContainers;

    /**
     * Gets the ReplicationProtectionContainersClient object to access its operations.
     * 
     * @return the ReplicationProtectionContainersClient object.
     */
    public ReplicationProtectionContainersClient getReplicationProtectionContainers() {
        return this.replicationProtectionContainers;
    }

    /**
     * The ReplicationMigrationItemsClient object to access its operations.
     */
    private final ReplicationMigrationItemsClient replicationMigrationItems;

    /**
     * Gets the ReplicationMigrationItemsClient object to access its operations.
     * 
     * @return the ReplicationMigrationItemsClient object.
     */
    public ReplicationMigrationItemsClient getReplicationMigrationItems() {
        return this.replicationMigrationItems;
    }

    /**
     * The MigrationRecoveryPointsClient object to access its operations.
     */
    private final MigrationRecoveryPointsClient migrationRecoveryPoints;

    /**
     * Gets the MigrationRecoveryPointsClient object to access its operations.
     * 
     * @return the MigrationRecoveryPointsClient object.
     */
    public MigrationRecoveryPointsClient getMigrationRecoveryPoints() {
        return this.migrationRecoveryPoints;
    }

    /**
     * The ReplicationProtectableItemsClient object to access its operations.
     */
    private final ReplicationProtectableItemsClient replicationProtectableItems;

    /**
     * Gets the ReplicationProtectableItemsClient object to access its operations.
     * 
     * @return the ReplicationProtectableItemsClient object.
     */
    public ReplicationProtectableItemsClient getReplicationProtectableItems() {
        return this.replicationProtectableItems;
    }

    /**
     * The ReplicationProtectedItemsClient object to access its operations.
     */
    private final ReplicationProtectedItemsClient replicationProtectedItems;

    /**
     * Gets the ReplicationProtectedItemsClient object to access its operations.
     * 
     * @return the ReplicationProtectedItemsClient object.
     */
    public ReplicationProtectedItemsClient getReplicationProtectedItems() {
        return this.replicationProtectedItems;
    }

    /**
     * The RecoveryPointsClient object to access its operations.
     */
    private final RecoveryPointsClient recoveryPoints;

    /**
     * Gets the RecoveryPointsClient object to access its operations.
     * 
     * @return the RecoveryPointsClient object.
     */
    public RecoveryPointsClient getRecoveryPoints() {
        return this.recoveryPoints;
    }

    /**
     * The TargetComputeSizesClient object to access its operations.
     */
    private final TargetComputeSizesClient targetComputeSizes;

    /**
     * Gets the TargetComputeSizesClient object to access its operations.
     * 
     * @return the TargetComputeSizesClient object.
     */
    public TargetComputeSizesClient getTargetComputeSizes() {
        return this.targetComputeSizes;
    }

    /**
     * The ReplicationProtectionClustersClient object to access its operations.
     */
    private final ReplicationProtectionClustersClient replicationProtectionClusters;

    /**
     * Gets the ReplicationProtectionClustersClient object to access its operations.
     * 
     * @return the ReplicationProtectionClustersClient object.
     */
    public ReplicationProtectionClustersClient getReplicationProtectionClusters() {
        return this.replicationProtectionClusters;
    }

    /**
     * The ClusterRecoveryPointsClient object to access its operations.
     */
    private final ClusterRecoveryPointsClient clusterRecoveryPoints;

    /**
     * Gets the ClusterRecoveryPointsClient object to access its operations.
     * 
     * @return the ClusterRecoveryPointsClient object.
     */
    public ClusterRecoveryPointsClient getClusterRecoveryPoints() {
        return this.clusterRecoveryPoints;
    }

    /**
     * The ClusterRecoveryPointOperationsClient object to access its operations.
     */
    private final ClusterRecoveryPointOperationsClient clusterRecoveryPointOperations;

    /**
     * Gets the ClusterRecoveryPointOperationsClient object to access its operations.
     * 
     * @return the ClusterRecoveryPointOperationsClient object.
     */
    public ClusterRecoveryPointOperationsClient getClusterRecoveryPointOperations() {
        return this.clusterRecoveryPointOperations;
    }

    /**
     * The ReplicationProtectionContainerMappingsClient object to access its operations.
     */
    private final ReplicationProtectionContainerMappingsClient replicationProtectionContainerMappings;

    /**
     * Gets the ReplicationProtectionContainerMappingsClient object to access its operations.
     * 
     * @return the ReplicationProtectionContainerMappingsClient object.
     */
    public ReplicationProtectionContainerMappingsClient getReplicationProtectionContainerMappings() {
        return this.replicationProtectionContainerMappings;
    }

    /**
     * The ReplicationRecoveryServicesProvidersClient object to access its operations.
     */
    private final ReplicationRecoveryServicesProvidersClient replicationRecoveryServicesProviders;

    /**
     * Gets the ReplicationRecoveryServicesProvidersClient object to access its operations.
     * 
     * @return the ReplicationRecoveryServicesProvidersClient object.
     */
    public ReplicationRecoveryServicesProvidersClient getReplicationRecoveryServicesProviders() {
        return this.replicationRecoveryServicesProviders;
    }

    /**
     * The StorageClassificationsClient object to access its operations.
     */
    private final StorageClassificationsClient storageClassifications;

    /**
     * Gets the StorageClassificationsClient object to access its operations.
     * 
     * @return the StorageClassificationsClient object.
     */
    public StorageClassificationsClient getStorageClassifications() {
        return this.storageClassifications;
    }

    /**
     * The StorageClassificationMappingsClient object to access its operations.
     */
    private final StorageClassificationMappingsClient storageClassificationMappings;

    /**
     * Gets the StorageClassificationMappingsClient object to access its operations.
     * 
     * @return the StorageClassificationMappingsClient object.
     */
    public StorageClassificationMappingsClient getStorageClassificationMappings() {
        return this.storageClassificationMappings;
    }

    /**
     * The ReplicationvCentersClient object to access its operations.
     */
    private final ReplicationvCentersClient replicationvCenters;

    /**
     * Gets the ReplicationvCentersClient object to access its operations.
     * 
     * @return the ReplicationvCentersClient object.
     */
    public ReplicationvCentersClient getReplicationvCenters() {
        return this.replicationvCenters;
    }

    /**
     * The ReplicationJobsClient object to access its operations.
     */
    private final ReplicationJobsClient replicationJobs;

    /**
     * Gets the ReplicationJobsClient object to access its operations.
     * 
     * @return the ReplicationJobsClient object.
     */
    public ReplicationJobsClient getReplicationJobs() {
        return this.replicationJobs;
    }

    /**
     * The ReplicationPoliciesClient object to access its operations.
     */
    private final ReplicationPoliciesClient replicationPolicies;

    /**
     * Gets the ReplicationPoliciesClient object to access its operations.
     * 
     * @return the ReplicationPoliciesClient object.
     */
    public ReplicationPoliciesClient getReplicationPolicies() {
        return this.replicationPolicies;
    }

    /**
     * The ReplicationProtectionIntentsClient object to access its operations.
     */
    private final ReplicationProtectionIntentsClient replicationProtectionIntents;

    /**
     * Gets the ReplicationProtectionIntentsClient object to access its operations.
     * 
     * @return the ReplicationProtectionIntentsClient object.
     */
    public ReplicationProtectionIntentsClient getReplicationProtectionIntents() {
        return this.replicationProtectionIntents;
    }

    /**
     * The ReplicationRecoveryPlansClient object to access its operations.
     */
    private final ReplicationRecoveryPlansClient replicationRecoveryPlans;

    /**
     * Gets the ReplicationRecoveryPlansClient object to access its operations.
     * 
     * @return the ReplicationRecoveryPlansClient object.
     */
    public ReplicationRecoveryPlansClient getReplicationRecoveryPlans() {
        return this.replicationRecoveryPlans;
    }

    /**
     * The SupportedOperatingSystemsOperationsClient object to access its operations.
     */
    private final SupportedOperatingSystemsOperationsClient supportedOperatingSystemsOperations;

    /**
     * Gets the SupportedOperatingSystemsOperationsClient object to access its operations.
     * 
     * @return the SupportedOperatingSystemsOperationsClient object.
     */
    public SupportedOperatingSystemsOperationsClient getSupportedOperatingSystemsOperations() {
        return this.supportedOperatingSystemsOperations;
    }

    /**
     * The ReplicationVaultHealthsClient object to access its operations.
     */
    private final ReplicationVaultHealthsClient replicationVaultHealths;

    /**
     * Gets the ReplicationVaultHealthsClient object to access its operations.
     * 
     * @return the ReplicationVaultHealthsClient object.
     */
    public ReplicationVaultHealthsClient getReplicationVaultHealths() {
        return this.replicationVaultHealths;
    }

    /**
     * The ReplicationVaultSettingsClient object to access its operations.
     */
    private final ReplicationVaultSettingsClient replicationVaultSettings;

    /**
     * Gets the ReplicationVaultSettingsClient object to access its operations.
     * 
     * @return the ReplicationVaultSettingsClient object.
     */
    public ReplicationVaultSettingsClient getReplicationVaultSettings() {
        return this.replicationVaultSettings;
    }

    /**
     * Initializes an instance of SiteRecoveryManagementClient client.
     * 
     * @param httpPipeline The HTTP pipeline to send requests through.
     * @param serializerAdapter The serializer to serialize an object into a string.
     * @param defaultPollInterval The default poll interval for long-running operation.
     * @param environment The Azure environment.
     * @param subscriptionId The subscription Id.
     * @param endpoint server parameter.
     */
    SiteRecoveryManagementClientImpl(HttpPipeline httpPipeline, SerializerAdapter serializerAdapter,
        Duration defaultPollInterval, AzureEnvironment environment, String subscriptionId, String endpoint) {
        this.httpPipeline = httpPipeline;
        this.serializerAdapter = serializerAdapter;
        this.defaultPollInterval = defaultPollInterval;
        this.subscriptionId = subscriptionId;
        this.endpoint = endpoint;
        this.apiVersion = "2025-01-01";
        this.operations = new OperationsClientImpl(this);
        this.replicationAlertSettings = new ReplicationAlertSettingsClientImpl(this);
        this.replicationAppliances = new ReplicationAppliancesClientImpl(this);
        this.replicationEligibilityResultsOperations = new ReplicationEligibilityResultsOperationsClientImpl(this);
        this.replicationEvents = new ReplicationEventsClientImpl(this);
        this.replicationFabrics = new ReplicationFabricsClientImpl(this);
        this.replicationLogicalNetworks = new ReplicationLogicalNetworksClientImpl(this);
        this.replicationNetworks = new ReplicationNetworksClientImpl(this);
        this.replicationNetworkMappings = new ReplicationNetworkMappingsClientImpl(this);
        this.replicationProtectionContainers = new ReplicationProtectionContainersClientImpl(this);
        this.replicationMigrationItems = new ReplicationMigrationItemsClientImpl(this);
        this.migrationRecoveryPoints = new MigrationRecoveryPointsClientImpl(this);
        this.replicationProtectableItems = new ReplicationProtectableItemsClientImpl(this);
        this.replicationProtectedItems = new ReplicationProtectedItemsClientImpl(this);
        this.recoveryPoints = new RecoveryPointsClientImpl(this);
        this.targetComputeSizes = new TargetComputeSizesClientImpl(this);
        this.replicationProtectionClusters = new ReplicationProtectionClustersClientImpl(this);
        this.clusterRecoveryPoints = new ClusterRecoveryPointsClientImpl(this);
        this.clusterRecoveryPointOperations = new ClusterRecoveryPointOperationsClientImpl(this);
        this.replicationProtectionContainerMappings = new ReplicationProtectionContainerMappingsClientImpl(this);
        this.replicationRecoveryServicesProviders = new ReplicationRecoveryServicesProvidersClientImpl(this);
        this.storageClassifications = new StorageClassificationsClientImpl(this);
        this.storageClassificationMappings = new StorageClassificationMappingsClientImpl(this);
        this.replicationvCenters = new ReplicationvCentersClientImpl(this);
        this.replicationJobs = new ReplicationJobsClientImpl(this);
        this.replicationPolicies = new ReplicationPoliciesClientImpl(this);
        this.replicationProtectionIntents = new ReplicationProtectionIntentsClientImpl(this);
        this.replicationRecoveryPlans = new ReplicationRecoveryPlansClientImpl(this);
        this.supportedOperatingSystemsOperations = new SupportedOperatingSystemsOperationsClientImpl(this);
        this.replicationVaultHealths = new ReplicationVaultHealthsClientImpl(this);
        this.replicationVaultSettings = new ReplicationVaultSettingsClientImpl(this);
    }

    /**
     * Gets default client context.
     * 
     * @return the default client context.
     */
    public Context getContext() {
        return Context.NONE;
    }

    /**
     * Merges default client context with provided context.
     * 
     * @param context the context to be merged with default client context.
     * @return the merged context.
     */
    public Context mergeContext(Context context) {
        return CoreUtils.mergeContexts(this.getContext(), context);
    }

    /**
     * Gets long running operation result.
     * 
     * @param activationResponse the response of activation operation.
     * @param httpPipeline the http pipeline.
     * @param pollResultType type of poll result.
     * @param finalResultType type of final result.
     * @param context the context shared by all requests.
     * @param <T> type of poll result.
     * @param <U> type of final result.
     * @return poller flux for poll result and final result.
     */
    public <T, U> PollerFlux<PollResult<T>, U> getLroResult(Mono<Response<Flux<ByteBuffer>>> activationResponse,
        HttpPipeline httpPipeline, Type pollResultType, Type finalResultType, Context context) {
        return PollerFactory.create(serializerAdapter, httpPipeline, pollResultType, finalResultType,
            defaultPollInterval, activationResponse, context);
    }

    /**
     * Gets the final result, or an error, based on last async poll response.
     * 
     * @param response the last async poll response.
     * @param <T> type of poll result.
     * @param <U> type of final result.
     * @return the final result, or an error.
     */
    public <T, U> Mono<U> getLroFinalResultOrError(AsyncPollResponse<PollResult<T>, U> response) {
        if (response.getStatus() != LongRunningOperationStatus.SUCCESSFULLY_COMPLETED) {
            String errorMessage;
            ManagementError managementError = null;
            HttpResponse errorResponse = null;
            PollResult.Error lroError = response.getValue().getError();
            if (lroError != null) {
                errorResponse = new HttpResponseImpl(lroError.getResponseStatusCode(), lroError.getResponseHeaders(),
                    lroError.getResponseBody());

                errorMessage = response.getValue().getError().getMessage();
                String errorBody = response.getValue().getError().getResponseBody();
                if (errorBody != null) {
                    // try to deserialize error body to ManagementError
                    try {
                        managementError = this.getSerializerAdapter()
                            .deserialize(errorBody, ManagementError.class, SerializerEncoding.JSON);
                        if (managementError.getCode() == null || managementError.getMessage() == null) {
                            managementError = null;
                        }
                    } catch (IOException | RuntimeException ioe) {
                        LOGGER.logThrowableAsWarning(ioe);
                    }
                }
            } else {
                // fallback to default error message
                errorMessage = "Long running operation failed.";
            }
            if (managementError == null) {
                // fallback to default ManagementError
                managementError = new ManagementError(response.getStatus().toString(), errorMessage);
            }
            return Mono.error(new ManagementException(errorMessage, errorResponse, managementError));
        } else {
            return response.getFinalResult();
        }
    }

    private static final class HttpResponseImpl extends HttpResponse {
        private final int statusCode;

        private final byte[] responseBody;

        private final HttpHeaders httpHeaders;

        HttpResponseImpl(int statusCode, HttpHeaders httpHeaders, String responseBody) {
            super(null);
            this.statusCode = statusCode;
            this.httpHeaders = httpHeaders;
            this.responseBody = responseBody == null ? null : responseBody.getBytes(StandardCharsets.UTF_8);
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getHeaderValue(String s) {
            return httpHeaders.getValue(HttpHeaderName.fromString(s));
        }

        public HttpHeaders getHeaders() {
            return httpHeaders;
        }

        public Flux<ByteBuffer> getBody() {
            return Flux.just(ByteBuffer.wrap(responseBody));
        }

        public Mono<byte[]> getBodyAsByteArray() {
            return Mono.just(responseBody);
        }

        public Mono<String> getBodyAsString() {
            return Mono.just(new String(responseBody, StandardCharsets.UTF_8));
        }

        public Mono<String> getBodyAsString(Charset charset) {
            return Mono.just(new String(responseBody, charset));
        }
    }

    private static final ClientLogger LOGGER = new ClientLogger(SiteRecoveryManagementClientImpl.class);
}
