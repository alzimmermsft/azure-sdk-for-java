// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.apimanagement.generated;

import com.azure.resourcemanager.apimanagement.models.PolicyIdName;

/**
 * Samples for ApiPolicy GetEntityTag.
 */
public final class ApiPolicyGetEntityTagSamples {
    /*
     * x-ms-original-file:
     * specification/apimanagement/resource-manager/Microsoft.ApiManagement/stable/2024-05-01/examples/
     * ApiManagementHeadApiPolicy.json
     */
    /**
     * Sample code: ApiManagementHeadApiPolicy.
     * 
     * @param manager Entry point to ApiManagementManager.
     */
    public static void
        apiManagementHeadApiPolicy(com.azure.resourcemanager.apimanagement.ApiManagementManager manager) {
        manager.apiPolicies()
            .getEntityTagWithResponse("rg1", "apimService1", "57d1f7558aa04f15146d9d8a", PolicyIdName.POLICY,
                com.azure.core.util.Context.NONE);
    }
}
