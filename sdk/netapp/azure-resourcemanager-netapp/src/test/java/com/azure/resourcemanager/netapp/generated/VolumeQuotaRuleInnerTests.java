// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.netapp.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.netapp.fluent.models.VolumeQuotaRuleInner;
import com.azure.resourcemanager.netapp.models.Type;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class VolumeQuotaRuleInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        VolumeQuotaRuleInner model = BinaryData.fromString(
            "{\"properties\":{\"provisioningState\":\"Patching\",\"quotaSizeInKiBs\":2412566794920993195,\"quotaType\":\"DefaultGroupQuota\",\"quotaTarget\":\"wxlp\"},\"location\":\"kftnkhtjsyin\",\"tags\":{\"gikdgsz\":\"qatmtdhtmdvy\"},\"id\":\"w\",\"name\":\"birryuzhl\",\"type\":\"kj\"}")
            .toObject(VolumeQuotaRuleInner.class);
        Assertions.assertEquals("kftnkhtjsyin", model.location());
        Assertions.assertEquals("qatmtdhtmdvy", model.tags().get("gikdgsz"));
        Assertions.assertEquals(2412566794920993195L, model.quotaSizeInKiBs());
        Assertions.assertEquals(Type.DEFAULT_GROUP_QUOTA, model.quotaType());
        Assertions.assertEquals("wxlp", model.quotaTarget());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        VolumeQuotaRuleInner model = new VolumeQuotaRuleInner().withLocation("kftnkhtjsyin")
            .withTags(mapOf("gikdgsz", "qatmtdhtmdvy"))
            .withQuotaSizeInKiBs(2412566794920993195L)
            .withQuotaType(Type.DEFAULT_GROUP_QUOTA)
            .withQuotaTarget("wxlp");
        model = BinaryData.fromObject(model).toObject(VolumeQuotaRuleInner.class);
        Assertions.assertEquals("kftnkhtjsyin", model.location());
        Assertions.assertEquals("qatmtdhtmdvy", model.tags().get("gikdgsz"));
        Assertions.assertEquals(2412566794920993195L, model.quotaSizeInKiBs());
        Assertions.assertEquals(Type.DEFAULT_GROUP_QUOTA, model.quotaType());
        Assertions.assertEquals("wxlp", model.quotaTarget());
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
