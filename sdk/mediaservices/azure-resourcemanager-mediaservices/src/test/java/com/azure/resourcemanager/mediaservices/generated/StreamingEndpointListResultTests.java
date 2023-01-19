// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.mediaservices.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.mediaservices.fluent.models.StreamingEndpointInner;
import com.azure.resourcemanager.mediaservices.models.ArmStreamingEndpointCurrentSku;
import com.azure.resourcemanager.mediaservices.models.StreamingEndpointListResult;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class StreamingEndpointListResultTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        StreamingEndpointListResult model =
            BinaryData
                .fromString(
                    "{\"value\":[{\"properties\":{\"description\":\"i\",\"scaleUnits\":2120830890,\"availabilitySetName\":\"pdtii\",\"maxCacheAge\":233619173932499743,\"customHostNames\":[],\"hostName\":\"xoruzfgsquyfxrx\",\"cdnEnabled\":false,\"cdnProvider\":\"tramxjez\",\"cdnProfile\":\"wnwxuqlcvyd\",\"provisioningState\":\"atdooaojkniod\",\"resourceState\":\"Running\",\"freeTrialEndTime\":\"2021-10-14T05:21:41Z\",\"created\":\"2021-11-05T10:39:03Z\",\"lastModified\":\"2021-12-09T17:34:33Z\"},\"sku\":{\"name\":\"sbvdkcrodtjinfw\",\"capacity\":2087401910},\"location\":\"lt\",\"tags\":{\"gaowpulpqblylsyx\":\"jvefkdlfoakggkfp\",\"xsdszuempsb\":\"qjnsjervtia\",\"v\":\"kfzbeyvpnqicvi\"},\"id\":\"jjxd\",\"name\":\"rbuukzclewyhmlwp\",\"type\":\"ztzp\"},{\"properties\":{\"description\":\"cckwyfzqwhxxbu\",\"scaleUnits\":397594857,\"availabilitySetName\":\"xzfe\",\"maxCacheAge\":6702265057684474491,\"customHostNames\":[],\"hostName\":\"o\",\"cdnEnabled\":false,\"cdnProvider\":\"jaltolmnc\",\"cdnProfile\":\"obqwcsdbnwdcfh\",\"provisioningState\":\"qdpfuvglsbjjca\",\"resourceState\":\"Stopping\",\"freeTrialEndTime\":\"2021-10-12T20:22:23Z\",\"created\":\"2021-05-11T02:00:05Z\",\"lastModified\":\"2021-04-07T04:06:35Z\"},\"sku\":{\"name\":\"ormrlxqtvcofudfl\",\"capacity\":360831817},\"location\":\"jub\",\"tags\":{\"nqntorudsgsahm\":\"nnqvsa\",\"rauwjuetaebu\":\"yc\",\"dmovsm\":\"u\"},\"id\":\"l\",\"name\":\"wabm\",\"type\":\"oefki\"}],\"@odata.count\":841009999,\"@odata.nextLink\":\"puqujmqlgkfbtn\"}")
                .toObject(StreamingEndpointListResult.class);
        Assertions.assertEquals("lt", model.value().get(0).location());
        Assertions.assertEquals("jvefkdlfoakggkfp", model.value().get(0).tags().get("gaowpulpqblylsyx"));
        Assertions.assertEquals(2087401910, model.value().get(0).sku().capacity());
        Assertions.assertEquals("i", model.value().get(0).description());
        Assertions.assertEquals(2120830890, model.value().get(0).scaleUnits());
        Assertions.assertEquals("pdtii", model.value().get(0).availabilitySetName());
        Assertions.assertEquals(233619173932499743L, model.value().get(0).maxCacheAge());
        Assertions.assertEquals(false, model.value().get(0).cdnEnabled());
        Assertions.assertEquals("tramxjez", model.value().get(0).cdnProvider());
        Assertions.assertEquals("wnwxuqlcvyd", model.value().get(0).cdnProfile());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        StreamingEndpointListResult model =
            new StreamingEndpointListResult()
                .withValue(
                    Arrays
                        .asList(
                            new StreamingEndpointInner()
                                .withLocation("lt")
                                .withTags(
                                    mapOf(
                                        "gaowpulpqblylsyx",
                                        "jvefkdlfoakggkfp",
                                        "xsdszuempsb",
                                        "qjnsjervtia",
                                        "v",
                                        "kfzbeyvpnqicvi"))
                                .withSku(new ArmStreamingEndpointCurrentSku().withCapacity(2087401910))
                                .withDescription("i")
                                .withScaleUnits(2120830890)
                                .withAvailabilitySetName("pdtii")
                                .withMaxCacheAge(233619173932499743L)
                                .withCustomHostNames(Arrays.asList())
                                .withCdnEnabled(false)
                                .withCdnProvider("tramxjez")
                                .withCdnProfile("wnwxuqlcvyd"),
                            new StreamingEndpointInner()
                                .withLocation("jub")
                                .withTags(mapOf("nqntorudsgsahm", "nnqvsa", "rauwjuetaebu", "yc", "dmovsm", "u"))
                                .withSku(new ArmStreamingEndpointCurrentSku().withCapacity(360831817))
                                .withDescription("cckwyfzqwhxxbu")
                                .withScaleUnits(397594857)
                                .withAvailabilitySetName("xzfe")
                                .withMaxCacheAge(6702265057684474491L)
                                .withCustomHostNames(Arrays.asList())
                                .withCdnEnabled(false)
                                .withCdnProvider("jaltolmnc")
                                .withCdnProfile("obqwcsdbnwdcfh")));
        model = BinaryData.fromObject(model).toObject(StreamingEndpointListResult.class);
        Assertions.assertEquals("lt", model.value().get(0).location());
        Assertions.assertEquals("jvefkdlfoakggkfp", model.value().get(0).tags().get("gaowpulpqblylsyx"));
        Assertions.assertEquals(2087401910, model.value().get(0).sku().capacity());
        Assertions.assertEquals("i", model.value().get(0).description());
        Assertions.assertEquals(2120830890, model.value().get(0).scaleUnits());
        Assertions.assertEquals("pdtii", model.value().get(0).availabilitySetName());
        Assertions.assertEquals(233619173932499743L, model.value().get(0).maxCacheAge());
        Assertions.assertEquals(false, model.value().get(0).cdnEnabled());
        Assertions.assertEquals("tramxjez", model.value().get(0).cdnProvider());
        Assertions.assertEquals("wnwxuqlcvyd", model.value().get(0).cdnProfile());
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