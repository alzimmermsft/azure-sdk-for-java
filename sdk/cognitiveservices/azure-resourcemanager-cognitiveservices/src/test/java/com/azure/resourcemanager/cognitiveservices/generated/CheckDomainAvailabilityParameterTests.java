// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.cognitiveservices.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.cognitiveservices.models.CheckDomainAvailabilityParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class CheckDomainAvailabilityParameterTests {
    @Test
    public void testDeserialize() {
        CheckDomainAvailabilityParameter model =
            BinaryData
                .fromString("{\"subdomainName\":\"xcug\",\"type\":\"cjooxdjebwpucwwf\",\"kind\":\"vbvmeu\"}")
                .toObject(CheckDomainAvailabilityParameter.class);
        Assertions.assertEquals("xcug", model.subdomainName());
        Assertions.assertEquals("cjooxdjebwpucwwf", model.type());
        Assertions.assertEquals("vbvmeu", model.kind());
    }

    @Test
    public void testSerialize() {
        CheckDomainAvailabilityParameter model =
            new CheckDomainAvailabilityParameter()
                .withSubdomainName("xcug")
                .withType("cjooxdjebwpucwwf")
                .withKind("vbvmeu");
        model = BinaryData.fromObject(model).toObject(CheckDomainAvailabilityParameter.class);
        Assertions.assertEquals("xcug", model.subdomainName());
        Assertions.assertEquals("cjooxdjebwpucwwf", model.type());
        Assertions.assertEquals("vbvmeu", model.kind());
    }
}