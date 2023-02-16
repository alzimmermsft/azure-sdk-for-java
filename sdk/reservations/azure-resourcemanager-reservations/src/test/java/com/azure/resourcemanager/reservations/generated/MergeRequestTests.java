// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.reservations.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.reservations.models.MergeRequest;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class MergeRequestTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        MergeRequest model =
            BinaryData.fromString("{\"properties\":{\"sources\":[\"mohctb\"]}}").toObject(MergeRequest.class);
        Assertions.assertEquals("mohctb", model.sources().get(0));
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        MergeRequest model = new MergeRequest().withSources(Arrays.asList("mohctb"));
        model = BinaryData.fromObject(model).toObject(MergeRequest.class);
        Assertions.assertEquals("mohctb", model.sources().get(0));
    }
}