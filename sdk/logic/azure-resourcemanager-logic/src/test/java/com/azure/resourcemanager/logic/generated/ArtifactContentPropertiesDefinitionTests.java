// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.logic.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.logic.models.ArtifactContentPropertiesDefinition;
import com.azure.resourcemanager.logic.models.ContentLink;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;

public final class ArtifactContentPropertiesDefinitionTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        ArtifactContentPropertiesDefinition model = BinaryData.fromString(
            "{\"content\":\"datadlwwqfbumlkxt\",\"contentType\":\"jfsmlmbtxhwgfwsr\",\"contentLink\":{\"uri\":\"coezbrhubskh\",\"contentVersion\":\"ygo\",\"contentSize\":1410009075037068900,\"contentHash\":{\"algorithm\":\"qjbvleorfmlu\",\"value\":\"tqzfavyv\"},\"metadata\":\"dataqybaryeua\"},\"createdTime\":\"2021-06-23T02:25:47Z\",\"changedTime\":\"2021-04-14T17:43:50Z\",\"metadata\":\"dataqgzsles\"}")
            .toObject(ArtifactContentPropertiesDefinition.class);
        Assertions.assertEquals(OffsetDateTime.parse("2021-06-23T02:25:47Z"), model.createdTime());
        Assertions.assertEquals(OffsetDateTime.parse("2021-04-14T17:43:50Z"), model.changedTime());
        Assertions.assertEquals("jfsmlmbtxhwgfwsr", model.contentType());
        Assertions.assertEquals("coezbrhubskh", model.contentLink().uri());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        ArtifactContentPropertiesDefinition model
            = new ArtifactContentPropertiesDefinition().withCreatedTime(OffsetDateTime.parse("2021-06-23T02:25:47Z"))
                .withChangedTime(OffsetDateTime.parse("2021-04-14T17:43:50Z"))
                .withMetadata("dataqgzsles")
                .withContent("datadlwwqfbumlkxt")
                .withContentType("jfsmlmbtxhwgfwsr")
                .withContentLink(new ContentLink().withUri("coezbrhubskh"));
        model = BinaryData.fromObject(model).toObject(ArtifactContentPropertiesDefinition.class);
        Assertions.assertEquals(OffsetDateTime.parse("2021-06-23T02:25:47Z"), model.createdTime());
        Assertions.assertEquals(OffsetDateTime.parse("2021-04-14T17:43:50Z"), model.changedTime());
        Assertions.assertEquals("jfsmlmbtxhwgfwsr", model.contentType());
        Assertions.assertEquals("coezbrhubskh", model.contentLink().uri());
    }
}
