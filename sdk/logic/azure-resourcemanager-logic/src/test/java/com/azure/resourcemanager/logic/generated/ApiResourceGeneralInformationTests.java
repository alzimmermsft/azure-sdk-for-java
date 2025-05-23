// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.logic.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.logic.models.ApiResourceGeneralInformation;
import com.azure.resourcemanager.logic.models.ApiTier;
import org.junit.jupiter.api.Assertions;

public final class ApiResourceGeneralInformationTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        ApiResourceGeneralInformation model = BinaryData.fromString(
            "{\"iconUrl\":\"zlwhhmemhooclu\",\"displayName\":\"pqmem\",\"description\":\"jk\",\"termsOfUseUrl\":\"ykyujxsg\",\"releaseTag\":\"srrryejylmbkzu\",\"tier\":\"Standard\"}")
            .toObject(ApiResourceGeneralInformation.class);
        Assertions.assertEquals("zlwhhmemhooclu", model.iconUrl());
        Assertions.assertEquals("pqmem", model.displayName());
        Assertions.assertEquals("jk", model.description());
        Assertions.assertEquals("ykyujxsg", model.termsOfUseUrl());
        Assertions.assertEquals("srrryejylmbkzu", model.releaseTag());
        Assertions.assertEquals(ApiTier.STANDARD, model.tier());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        ApiResourceGeneralInformation model = new ApiResourceGeneralInformation().withIconUrl("zlwhhmemhooclu")
            .withDisplayName("pqmem")
            .withDescription("jk")
            .withTermsOfUseUrl("ykyujxsg")
            .withReleaseTag("srrryejylmbkzu")
            .withTier(ApiTier.STANDARD);
        model = BinaryData.fromObject(model).toObject(ApiResourceGeneralInformation.class);
        Assertions.assertEquals("zlwhhmemhooclu", model.iconUrl());
        Assertions.assertEquals("pqmem", model.displayName());
        Assertions.assertEquals("jk", model.description());
        Assertions.assertEquals("ykyujxsg", model.termsOfUseUrl());
        Assertions.assertEquals("srrryejylmbkzu", model.releaseTag());
        Assertions.assertEquals(ApiTier.STANDARD, model.tier());
    }
}
