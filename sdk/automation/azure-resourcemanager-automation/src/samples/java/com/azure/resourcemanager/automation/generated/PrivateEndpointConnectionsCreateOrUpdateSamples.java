// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.automation.generated;

import com.azure.resourcemanager.automation.models.PrivateLinkServiceConnectionStateProperty;

/**
 * Samples for PrivateEndpointConnections CreateOrUpdate.
 */
public final class PrivateEndpointConnectionsCreateOrUpdateSamples {
    /*
     * x-ms-original-file:
     * specification/automation/resource-manager/Microsoft.Automation/preview/2020-01-13-preview/examples/
     * PrivateEndpointConnectionUpdate.json
     */
    /**
     * Sample code: Approve or reject a private endpoint connection with a given name.
     * 
     * @param manager Entry point to AutomationManager.
     */
    public static void approveOrRejectAPrivateEndpointConnectionWithAGivenName(
        com.azure.resourcemanager.automation.AutomationManager manager) {
        manager.privateEndpointConnections()
            .define("privateEndpointConnectionName")
            .withExistingAutomationAccount("rg1", "ddb1")
            .withPrivateLinkServiceConnectionState(
                new PrivateLinkServiceConnectionStateProperty().withStatus("Approved")
                    .withDescription("Approved by johndoe@contoso.com"))
            .create();
    }
}
