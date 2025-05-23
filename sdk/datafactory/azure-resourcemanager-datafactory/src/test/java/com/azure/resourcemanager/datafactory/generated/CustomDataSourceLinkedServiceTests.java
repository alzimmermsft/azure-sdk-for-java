// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datafactory.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.datafactory.models.CustomDataSourceLinkedService;
import com.azure.resourcemanager.datafactory.models.IntegrationRuntimeReference;
import com.azure.resourcemanager.datafactory.models.ParameterSpecification;
import com.azure.resourcemanager.datafactory.models.ParameterType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class CustomDataSourceLinkedServiceTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        CustomDataSourceLinkedService model = BinaryData.fromString(
            "{\"type\":\"CustomDataSource\",\"typeProperties\":\"datahrjwyxduwimwnuvj\",\"version\":\"wpfxi\",\"connectVia\":{\"referenceName\":\"fkmnj\",\"parameters\":{\"cbpkig\":\"datawmyjdbcknsojtmpd\",\"dwgussctnppxxeys\":\"datavvaitkce\",\"zmvnbckls\":\"dataenfwugonysemun\"}},\"description\":\"uuksvfsukpkiea\",\"parameters\":{\"cezt\":{\"type\":\"Float\",\"defaultValue\":\"datannen\"},\"ohjx\":{\"type\":\"Int\",\"defaultValue\":\"datazwk\"},\"uudr\":{\"type\":\"Object\",\"defaultValue\":\"datamvuamorhkne\"}},\"annotations\":[\"dataznk\",\"dataarjii\",\"datapbychcwhlhdzdcrl\",\"datacmeyxypxlzcr\"],\"\":{\"c\":\"datavj\",\"fh\":\"dataetpdezebvtkgzjna\",\"kogcvazozo\":\"dataeilnhkcxuounzz\"}}")
            .toObject(CustomDataSourceLinkedService.class);
        Assertions.assertEquals("wpfxi", model.version());
        Assertions.assertEquals("fkmnj", model.connectVia().referenceName());
        Assertions.assertEquals("uuksvfsukpkiea", model.description());
        Assertions.assertEquals(ParameterType.FLOAT, model.parameters().get("cezt").type());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        CustomDataSourceLinkedService model = new CustomDataSourceLinkedService().withVersion("wpfxi")
            .withConnectVia(new IntegrationRuntimeReference().withReferenceName("fkmnj")
                .withParameters(mapOf("cbpkig", "datawmyjdbcknsojtmpd", "dwgussctnppxxeys", "datavvaitkce", "zmvnbckls",
                    "dataenfwugonysemun")))
            .withDescription("uuksvfsukpkiea")
            .withParameters(mapOf("cezt",
                new ParameterSpecification().withType(ParameterType.FLOAT).withDefaultValue("datannen"), "ohjx",
                new ParameterSpecification().withType(ParameterType.INT).withDefaultValue("datazwk"), "uudr",
                new ParameterSpecification().withType(ParameterType.OBJECT).withDefaultValue("datamvuamorhkne")))
            .withAnnotations(Arrays.asList("dataznk", "dataarjii", "datapbychcwhlhdzdcrl", "datacmeyxypxlzcr"))
            .withTypeProperties("datahrjwyxduwimwnuvj");
        model = BinaryData.fromObject(model).toObject(CustomDataSourceLinkedService.class);
        Assertions.assertEquals("wpfxi", model.version());
        Assertions.assertEquals("fkmnj", model.connectVia().referenceName());
        Assertions.assertEquals("uuksvfsukpkiea", model.description());
        Assertions.assertEquals(ParameterType.FLOAT, model.parameters().get("cezt").type());
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
