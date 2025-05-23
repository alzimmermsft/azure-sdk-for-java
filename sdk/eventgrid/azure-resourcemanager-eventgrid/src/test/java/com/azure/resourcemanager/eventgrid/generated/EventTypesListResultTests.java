// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.eventgrid.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.eventgrid.fluent.models.EventTypeInner;
import com.azure.resourcemanager.eventgrid.models.EventTypesListResult;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class EventTypesListResultTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        EventTypesListResult model = BinaryData.fromString(
            "{\"value\":[{\"properties\":{\"displayName\":\"zknmzlanrupd\",\"description\":\"nphcnzqtpjhmqrh\",\"schemaUrl\":\"hlaiwd\",\"isInDefaultSet\":true},\"id\":\"mlzzhzdtxetlgyd\",\"name\":\"hqvlnnpxybafiqg\",\"type\":\"aarbgjekg\"},{\"properties\":{\"displayName\":\"byu\",\"description\":\"dw\",\"schemaUrl\":\"vmzegj\",\"isInDefaultSet\":true},\"id\":\"hj\",\"name\":\"rwgdnqzbrfks\",\"type\":\"zhzmtksjci\"},{\"properties\":{\"displayName\":\"gsxcdgljplkeua\",\"description\":\"tomflrytswfpf\",\"schemaUrl\":\"gycxnmskwhqjjys\",\"isInDefaultSet\":false},\"id\":\"lpshhkvpedwqslsr\",\"name\":\"mpqvwwsk\",\"type\":\"ndcbrwi\"}]}")
            .toObject(EventTypesListResult.class);
        Assertions.assertEquals("zknmzlanrupd", model.value().get(0).displayName());
        Assertions.assertEquals("nphcnzqtpjhmqrh", model.value().get(0).description());
        Assertions.assertEquals("hlaiwd", model.value().get(0).schemaUrl());
        Assertions.assertEquals(true, model.value().get(0).isInDefaultSet());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        EventTypesListResult model = new EventTypesListResult().withValue(Arrays.asList(
            new EventTypeInner().withDisplayName("zknmzlanrupd")
                .withDescription("nphcnzqtpjhmqrh")
                .withSchemaUrl("hlaiwd")
                .withIsInDefaultSet(true),
            new EventTypeInner().withDisplayName("byu")
                .withDescription("dw")
                .withSchemaUrl("vmzegj")
                .withIsInDefaultSet(true),
            new EventTypeInner().withDisplayName("gsxcdgljplkeua")
                .withDescription("tomflrytswfpf")
                .withSchemaUrl("gycxnmskwhqjjys")
                .withIsInDefaultSet(false)));
        model = BinaryData.fromObject(model).toObject(EventTypesListResult.class);
        Assertions.assertEquals("zknmzlanrupd", model.value().get(0).displayName());
        Assertions.assertEquals("nphcnzqtpjhmqrh", model.value().get(0).description());
        Assertions.assertEquals("hlaiwd", model.value().get(0).schemaUrl());
        Assertions.assertEquals(true, model.value().get(0).isInDefaultSet());
    }
}
