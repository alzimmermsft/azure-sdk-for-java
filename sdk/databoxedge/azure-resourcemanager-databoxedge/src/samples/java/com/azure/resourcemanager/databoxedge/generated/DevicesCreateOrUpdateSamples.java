// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.databoxedge.generated;

import com.azure.resourcemanager.databoxedge.models.Sku;
import com.azure.resourcemanager.databoxedge.models.SkuName;
import com.azure.resourcemanager.databoxedge.models.SkuTier;
import java.util.HashMap;
import java.util.Map;

/**
 * Samples for Devices CreateOrUpdate.
 */
public final class DevicesCreateOrUpdateSamples {
    /*
     * x-ms-original-file:
     * specification/databoxedge/resource-manager/Microsoft.DataBoxEdge/stable/2019-08-01/examples/DataBoxEdgeDevicePut.
     * json
     */
    /**
     * Sample code: DataBoxEdgeDevicePut.
     * 
     * @param manager Entry point to DataBoxEdgeManager.
     */
    public static void dataBoxEdgeDevicePut(com.azure.resourcemanager.databoxedge.DataBoxEdgeManager manager) {
        manager.devices()
            .define("testedgedevice")
            .withRegion("eastus")
            .withExistingResourceGroup("GroupForEdgeAutomation")
            .withTags(mapOf())
            .withSku(new Sku().withName(SkuName.EDGE).withTier(SkuTier.STANDARD))
            .create();
    }

    // Use "Map.of" if available
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
