// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.dynatrace.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.dynatrace.fluent.models.SsoDetailsResponseInner;
import com.azure.resourcemanager.dynatrace.models.SsoStatus;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class SsoDetailsResponseInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        SsoDetailsResponseInner model = BinaryData.fromString(
            "{\"isSsoEnabled\":\"Enabled\",\"metadataUrl\":\"zvdudgwdslfhotwm\",\"singleSignOnUrl\":\"npwlbjnpg\",\"aadDomains\":[\"tadehxnltyfsopp\",\"suesnzw\",\"ej\",\"avo\"],\"adminUsers\":[\"dmoh\",\"tbqvudw\",\"dndnvow\"]}")
            .toObject(SsoDetailsResponseInner.class);
        Assertions.assertEquals(SsoStatus.ENABLED, model.isSsoEnabled());
        Assertions.assertEquals("zvdudgwdslfhotwm", model.metadataUrl());
        Assertions.assertEquals("npwlbjnpg", model.singleSignOnUrl());
        Assertions.assertEquals("tadehxnltyfsopp", model.aadDomains().get(0));
        Assertions.assertEquals("dmoh", model.adminUsers().get(0));
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        SsoDetailsResponseInner model = new SsoDetailsResponseInner().withIsSsoEnabled(SsoStatus.ENABLED)
            .withMetadataUrl("zvdudgwdslfhotwm")
            .withSingleSignOnUrl("npwlbjnpg")
            .withAadDomains(Arrays.asList("tadehxnltyfsopp", "suesnzw", "ej", "avo"))
            .withAdminUsers(Arrays.asList("dmoh", "tbqvudw", "dndnvow"));
        model = BinaryData.fromObject(model).toObject(SsoDetailsResponseInner.class);
        Assertions.assertEquals(SsoStatus.ENABLED, model.isSsoEnabled());
        Assertions.assertEquals("zvdudgwdslfhotwm", model.metadataUrl());
        Assertions.assertEquals("npwlbjnpg", model.singleSignOnUrl());
        Assertions.assertEquals("tadehxnltyfsopp", model.aadDomains().get(0));
        Assertions.assertEquals("dmoh", model.adminUsers().get(0));
    }
}
