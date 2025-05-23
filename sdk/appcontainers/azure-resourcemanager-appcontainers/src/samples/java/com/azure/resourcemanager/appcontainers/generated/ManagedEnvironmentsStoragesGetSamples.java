// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appcontainers.generated;

/**
 * Samples for ManagedEnvironmentsStorages Get.
 */
public final class ManagedEnvironmentsStoragesGetSamples {
    /*
     * x-ms-original-file:
     * specification/app/resource-manager/Microsoft.App/stable/2025-01-01/examples/ManagedEnvironmentsStorages_Get.json
     */
    /**
     * Sample code: get a environments storage.
     * 
     * @param manager Entry point to ContainerAppsApiManager.
     */
    public static void
        getAEnvironmentsStorage(com.azure.resourcemanager.appcontainers.ContainerAppsApiManager manager) {
        manager.managedEnvironmentsStorages()
            .getWithResponse("examplerg", "managedEnv", "jlaw-demo1", com.azure.core.util.Context.NONE);
    }

    /*
     * x-ms-original-file: specification/app/resource-manager/Microsoft.App/stable/2025-01-01/examples/
     * ManagedEnvironmentsStorages_Get_NfsAzureFile.json
     */
    /**
     * Sample code: get a environments storage for NFS Azure file.
     * 
     * @param manager Entry point to ContainerAppsApiManager.
     */
    public static void getAEnvironmentsStorageForNFSAzureFile(
        com.azure.resourcemanager.appcontainers.ContainerAppsApiManager manager) {
        manager.managedEnvironmentsStorages()
            .getWithResponse("examplerg", "managedEnv", "jlaw-demo1", com.azure.core.util.Context.NONE);
    }
}
