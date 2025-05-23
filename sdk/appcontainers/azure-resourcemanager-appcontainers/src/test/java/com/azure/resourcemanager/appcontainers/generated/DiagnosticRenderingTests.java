// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appcontainers.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.appcontainers.models.DiagnosticRendering;
import org.junit.jupiter.api.Assertions;

public final class DiagnosticRenderingTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        DiagnosticRendering model = BinaryData
            .fromString(
                "{\"type\":316735321,\"title\":\"kxoyzunbixxr\",\"description\":\"kvcpwpgclr\",\"isVisible\":false}")
            .toObject(DiagnosticRendering.class);
        Assertions.assertEquals(316735321, model.type());
        Assertions.assertEquals("kxoyzunbixxr", model.title());
        Assertions.assertEquals("kvcpwpgclr", model.description());
        Assertions.assertEquals(false, model.isVisible());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        DiagnosticRendering model = new DiagnosticRendering().withType(316735321)
            .withTitle("kxoyzunbixxr")
            .withDescription("kvcpwpgclr")
            .withIsVisible(false);
        model = BinaryData.fromObject(model).toObject(DiagnosticRendering.class);
        Assertions.assertEquals(316735321, model.type());
        Assertions.assertEquals("kxoyzunbixxr", model.title());
        Assertions.assertEquals("kvcpwpgclr", model.description());
        Assertions.assertEquals(false, model.isVisible());
    }
}
