// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicesbackup.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.recoveryservicesbackup.models.ClientScriptForConnect;
import org.junit.jupiter.api.Assertions;

public final class ClientScriptForConnectTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        ClientScriptForConnect model = BinaryData.fromString(
            "{\"scriptContent\":\"ogewij\",\"scriptExtension\":\"rhbguzozkyewnfn\",\"osType\":\"hhqosmffjku\",\"url\":\"cyar\",\"scriptNameSuffix\":\"oohguabzoghkt\"}")
            .toObject(ClientScriptForConnect.class);
        Assertions.assertEquals("ogewij", model.scriptContent());
        Assertions.assertEquals("rhbguzozkyewnfn", model.scriptExtension());
        Assertions.assertEquals("hhqosmffjku", model.osType());
        Assertions.assertEquals("cyar", model.url());
        Assertions.assertEquals("oohguabzoghkt", model.scriptNameSuffix());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        ClientScriptForConnect model = new ClientScriptForConnect().withScriptContent("ogewij")
            .withScriptExtension("rhbguzozkyewnfn")
            .withOsType("hhqosmffjku")
            .withUrl("cyar")
            .withScriptNameSuffix("oohguabzoghkt");
        model = BinaryData.fromObject(model).toObject(ClientScriptForConnect.class);
        Assertions.assertEquals("ogewij", model.scriptContent());
        Assertions.assertEquals("rhbguzozkyewnfn", model.scriptExtension());
        Assertions.assertEquals("hhqosmffjku", model.osType());
        Assertions.assertEquals("cyar", model.url());
        Assertions.assertEquals("oohguabzoghkt", model.scriptNameSuffix());
    }
}
