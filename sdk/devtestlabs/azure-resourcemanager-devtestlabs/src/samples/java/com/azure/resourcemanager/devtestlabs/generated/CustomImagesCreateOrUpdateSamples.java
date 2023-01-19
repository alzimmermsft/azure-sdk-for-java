// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.devtestlabs.generated;

import com.azure.resourcemanager.devtestlabs.models.CustomImagePropertiesFromVm;
import com.azure.resourcemanager.devtestlabs.models.LinuxOsInfo;
import com.azure.resourcemanager.devtestlabs.models.LinuxOsState;
import java.util.HashMap;
import java.util.Map;

/** Samples for CustomImages CreateOrUpdate. */
public final class CustomImagesCreateOrUpdateSamples {
    /*
     * x-ms-original-file: specification/devtestlabs/resource-manager/Microsoft.DevTestLab/stable/2018-09-15/examples/CustomImages_CreateOrUpdate.json
     */
    /**
     * Sample code: CustomImages_CreateOrUpdate.
     *
     * @param manager Entry point to DevTestLabsManager.
     */
    public static void customImagesCreateOrUpdate(com.azure.resourcemanager.devtestlabs.DevTestLabsManager manager) {
        manager
            .customImages()
            .define("{customImageName}")
            .withRegion((String) null)
            .withExistingLab("resourceGroupName", "{labName}")
            .withTags(mapOf("tagName1", "tagValue1"))
            .withVm(
                new CustomImagePropertiesFromVm()
                    .withSourceVmId(
                        "/subscriptions/{subscriptionId}/resourcegroups/resourceGroupName/providers/microsoft.devtestlab/labs/{labName}/virtualmachines/{vmName}")
                    .withLinuxOsInfo(new LinuxOsInfo().withLinuxOsState(LinuxOsState.NON_DEPROVISIONED)))
            .withDescription("My Custom Image")
            .create();
    }

    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> mapOf(Object... inputs) {
        Map<String, T> map = new HashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            String key = (String) inputs[i];
            T value = (T) inputs[i + 1];
            map.put(key, value);
        }
        return map;
    }
}