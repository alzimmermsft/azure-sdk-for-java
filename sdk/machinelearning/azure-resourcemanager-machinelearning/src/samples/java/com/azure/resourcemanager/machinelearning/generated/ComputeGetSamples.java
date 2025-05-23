// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.machinelearning.generated;

/**
 * Samples for Compute Get.
 */
public final class ComputeGetSamples {
    /*
     * x-ms-original-file:
     * specification/machinelearningservices/resource-manager/Microsoft.MachineLearningServices/stable/2024-04-01/
     * examples/Compute/get/AKSCompute.json
     */
    /**
     * Sample code: Get a AKS Compute.
     * 
     * @param manager Entry point to MachineLearningManager.
     */
    public static void getAAKSCompute(com.azure.resourcemanager.machinelearning.MachineLearningManager manager) {
        manager.computes()
            .getWithResponse("testrg123", "workspaces123", "compute123", com.azure.core.util.Context.NONE);
    }

    /*
     * x-ms-original-file:
     * specification/machinelearningservices/resource-manager/Microsoft.MachineLearningServices/stable/2024-04-01/
     * examples/Compute/get/KubernetesCompute.json
     */
    /**
     * Sample code: Get a Kubernetes Compute.
     * 
     * @param manager Entry point to MachineLearningManager.
     */
    public static void getAKubernetesCompute(com.azure.resourcemanager.machinelearning.MachineLearningManager manager) {
        manager.computes()
            .getWithResponse("testrg123", "workspaces123", "compute123", com.azure.core.util.Context.NONE);
    }

    /*
     * x-ms-original-file:
     * specification/machinelearningservices/resource-manager/Microsoft.MachineLearningServices/stable/2024-04-01/
     * examples/Compute/get/ComputeInstance.json
     */
    /**
     * Sample code: Get an ComputeInstance.
     * 
     * @param manager Entry point to MachineLearningManager.
     */
    public static void getAnComputeInstance(com.azure.resourcemanager.machinelearning.MachineLearningManager manager) {
        manager.computes()
            .getWithResponse("testrg123", "workspaces123", "compute123", com.azure.core.util.Context.NONE);
    }

    /*
     * x-ms-original-file:
     * specification/machinelearningservices/resource-manager/Microsoft.MachineLearningServices/stable/2024-04-01/
     * examples/Compute/get/AmlCompute.json
     */
    /**
     * Sample code: Get a AML Compute.
     * 
     * @param manager Entry point to MachineLearningManager.
     */
    public static void getAAMLCompute(com.azure.resourcemanager.machinelearning.MachineLearningManager manager) {
        manager.computes()
            .getWithResponse("testrg123", "workspaces123", "compute123", com.azure.core.util.Context.NONE);
    }
}
