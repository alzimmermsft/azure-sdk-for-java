// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.apimanagement.generated;

/**
 * Samples for WorkspaceApiDiagnostic GetEntityTag.
 */
public final class WorkspaceApiDiagnosticGetEntityTagSamples {
    /*
     * x-ms-original-file:
     * specification/apimanagement/resource-manager/Microsoft.ApiManagement/stable/2024-05-01/examples/
     * ApiManagementHeadWorkspaceApiDiagnostic.json
     */
    /**
     * Sample code: ApiManagementHeadWorkspaceApiDiagnostic.
     * 
     * @param manager Entry point to ApiManagementManager.
     */
    public static void
        apiManagementHeadWorkspaceApiDiagnostic(com.azure.resourcemanager.apimanagement.ApiManagementManager manager) {
        manager.workspaceApiDiagnostics()
            .getEntityTagWithResponse("rg1", "apimService1", "wks1", "57d1f7558aa04f15146d9d8a", "applicationinsights",
                com.azure.core.util.Context.NONE);
    }
}
