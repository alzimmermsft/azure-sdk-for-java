// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datafactory.generated;

/**
 * Samples for ManagedPrivateEndpoints GetSync.
 */
public final class ManagedPrivateEndpointsGetSyncSamples {
    /*
     * x-ms-original-file: specification/datafactory/resource-manager/Microsoft.DataFactory/stable/2018-06-01/examples/
     * ManagedPrivateEndpoints_Get.json
     */
    /**
     * Sample code: ManagedPrivateEndpoints_Get.
     * 
     * @param manager Entry point to DataFactoryManager.
     */
    public static void managedPrivateEndpointsGet(com.azure.resourcemanager.datafactory.DataFactoryManager manager) {
        manager.managedPrivateEndpoints()
            .getWithResponse("exampleResourceGroup", "exampleFactoryName", "exampleManagedVirtualNetworkName",
                "exampleManagedPrivateEndpointName", null, com.azure.core.util.Context.NONE);
    }
}
