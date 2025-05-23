// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.apimanagement.generated;

/**
 * Samples for Operation ListByTags.
 */
public final class OperationListByTagsSamples {
    /*
     * x-ms-original-file:
     * specification/apimanagement/resource-manager/Microsoft.ApiManagement/stable/2024-05-01/examples/
     * ApiManagementListApiOperationsByTags.json
     */
    /**
     * Sample code: ApiManagementListApiOperationsByTags.
     * 
     * @param manager Entry point to ApiManagementManager.
     */
    public static void
        apiManagementListApiOperationsByTags(com.azure.resourcemanager.apimanagement.ApiManagementManager manager) {
        manager.operations()
            .listByTags("rg1", "apimService1", "a1", null, null, null, null, com.azure.core.util.Context.NONE);
    }
}
