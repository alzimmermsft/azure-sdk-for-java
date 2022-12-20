// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.digitaltwins.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.digitaltwins.fluent.models.GroupIdInformationInner;
import com.azure.resourcemanager.digitaltwins.fluent.models.GroupIdInformationResponseInner;
import com.azure.resourcemanager.digitaltwins.models.GroupIdInformationProperties;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class GroupIdInformationResponseInnerTests {
    @Test
    public void testDeserialize() {
        GroupIdInformationResponseInner model =
            BinaryData
                .fromString(
                    "{\"value\":[{\"properties\":{\"groupId\":\"qjvsovmyokacs\",\"requiredMembers\":[],\"requiredZoneNames\":[]},\"id\":\"hzdobpxjmflbvvnc\",\"name\":\"kcciwwzjuqkhr\",\"type\":\"jiwkuofoskghsau\"}]}")
                .toObject(GroupIdInformationResponseInner.class);
        Assertions.assertEquals("qjvsovmyokacs", model.value().get(0).properties().groupId());
        Assertions.assertEquals("hzdobpxjmflbvvnc", model.value().get(0).id());
    }

    @Test
    public void testSerialize() {
        GroupIdInformationResponseInner model =
            new GroupIdInformationResponseInner()
                .withValue(
                    Arrays
                        .asList(
                            new GroupIdInformationInner()
                                .withProperties(
                                    new GroupIdInformationProperties()
                                        .withGroupId("qjvsovmyokacs")
                                        .withRequiredMembers(Arrays.asList())
                                        .withRequiredZoneNames(Arrays.asList()))
                                .withId("hzdobpxjmflbvvnc")));
        model = BinaryData.fromObject(model).toObject(GroupIdInformationResponseInner.class);
        Assertions.assertEquals("qjvsovmyokacs", model.value().get(0).properties().groupId());
        Assertions.assertEquals("hzdobpxjmflbvvnc", model.value().get(0).id());
    }
}