// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicesbackup.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.recoveryservicesbackup.models.DpmJobExtendedInfo;
import com.azure.resourcemanager.recoveryservicesbackup.models.DpmJobTaskDetails;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class DpmJobExtendedInfoTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        DpmJobExtendedInfo model = BinaryData.fromString(
            "{\"tasksList\":[{\"taskId\":\"nxaulk\",\"startTime\":\"2021-05-04T10:50:30Z\",\"endTime\":\"2021-07-31T21:19:31Z\",\"duration\":\"PT117H14M50S\",\"status\":\"jnnawtqa\"},{\"taskId\":\"xuckpggqoweyir\",\"startTime\":\"2021-10-13T06:42:41Z\",\"endTime\":\"2021-03-03T15:45:16Z\",\"duration\":\"PT61H45M7S\",\"status\":\"fl\"}],\"propertyBag\":{\"cng\":\"pizruwnpqxpxiw\",\"kv\":\"saasiixtmkzj\"},\"dynamicErrorMessage\":\"rhgfgrwsdpgrat\"}")
            .toObject(DpmJobExtendedInfo.class);
        Assertions.assertEquals("nxaulk", model.tasksList().get(0).taskId());
        Assertions.assertEquals(OffsetDateTime.parse("2021-05-04T10:50:30Z"), model.tasksList().get(0).startTime());
        Assertions.assertEquals(OffsetDateTime.parse("2021-07-31T21:19:31Z"), model.tasksList().get(0).endTime());
        Assertions.assertEquals(Duration.parse("PT117H14M50S"), model.tasksList().get(0).duration());
        Assertions.assertEquals("jnnawtqa", model.tasksList().get(0).status());
        Assertions.assertEquals("pizruwnpqxpxiw", model.propertyBag().get("cng"));
        Assertions.assertEquals("rhgfgrwsdpgrat", model.dynamicErrorMessage());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        DpmJobExtendedInfo model = new DpmJobExtendedInfo()
            .withTasksList(Arrays.asList(
                new DpmJobTaskDetails().withTaskId("nxaulk")
                    .withStartTime(OffsetDateTime.parse("2021-05-04T10:50:30Z"))
                    .withEndTime(OffsetDateTime.parse("2021-07-31T21:19:31Z"))
                    .withDuration(Duration.parse("PT117H14M50S"))
                    .withStatus("jnnawtqa"),
                new DpmJobTaskDetails().withTaskId("xuckpggqoweyir")
                    .withStartTime(OffsetDateTime.parse("2021-10-13T06:42:41Z"))
                    .withEndTime(OffsetDateTime.parse("2021-03-03T15:45:16Z"))
                    .withDuration(Duration.parse("PT61H45M7S"))
                    .withStatus("fl")))
            .withPropertyBag(mapOf("cng", "pizruwnpqxpxiw", "kv", "saasiixtmkzj"))
            .withDynamicErrorMessage("rhgfgrwsdpgrat");
        model = BinaryData.fromObject(model).toObject(DpmJobExtendedInfo.class);
        Assertions.assertEquals("nxaulk", model.tasksList().get(0).taskId());
        Assertions.assertEquals(OffsetDateTime.parse("2021-05-04T10:50:30Z"), model.tasksList().get(0).startTime());
        Assertions.assertEquals(OffsetDateTime.parse("2021-07-31T21:19:31Z"), model.tasksList().get(0).endTime());
        Assertions.assertEquals(Duration.parse("PT117H14M50S"), model.tasksList().get(0).duration());
        Assertions.assertEquals("jnnawtqa", model.tasksList().get(0).status());
        Assertions.assertEquals("pizruwnpqxpxiw", model.propertyBag().get("cng"));
        Assertions.assertEquals("rhgfgrwsdpgrat", model.dynamicErrorMessage());
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
