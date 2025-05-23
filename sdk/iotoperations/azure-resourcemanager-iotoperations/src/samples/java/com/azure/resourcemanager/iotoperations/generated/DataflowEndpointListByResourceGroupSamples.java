// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.iotoperations.generated;

/**
 * Samples for DataflowEndpoint ListByResourceGroup.
 */
public final class DataflowEndpointListByResourceGroupSamples {
    /*
     * x-ms-original-file: 2025-04-01/DataflowEndpoint_ListByResourceGroup_MaximumSet_Gen.json
     */
    /**
     * Sample code: DataflowEndpoint_ListByResourceGroup.
     * 
     * @param manager Entry point to IoTOperationsManager.
     */
    public static void
        dataflowEndpointListByResourceGroup(com.azure.resourcemanager.iotoperations.IoTOperationsManager manager) {
        manager.dataflowEndpoints()
            .listByResourceGroup("rgiotoperations", "resource-name123", com.azure.core.util.Context.NONE);
    }
}
