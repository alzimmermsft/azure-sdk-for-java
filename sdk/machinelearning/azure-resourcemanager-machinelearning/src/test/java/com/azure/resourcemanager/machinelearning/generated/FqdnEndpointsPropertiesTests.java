// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.machinelearning.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.machinelearning.models.FqdnEndpoint;
import com.azure.resourcemanager.machinelearning.models.FqdnEndpointsProperties;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class FqdnEndpointsPropertiesTests {
    @Test
    public void testDeserialize() {
        FqdnEndpointsProperties model =
            BinaryData
                .fromString(
                    "{\"category\":\"qxn\",\"endpoints\":[{\"domainName\":\"gxhuriplbp\",\"endpointDetails\":[]},{\"domainName\":\"nkbebxmuby\",\"endpointDetails\":[]},{\"domainName\":\"lrb\",\"endpointDetails\":[]}]}")
                .toObject(FqdnEndpointsProperties.class);
        Assertions.assertEquals("qxn", model.category());
        Assertions.assertEquals("gxhuriplbp", model.endpoints().get(0).domainName());
    }

    @Test
    public void testSerialize() {
        FqdnEndpointsProperties model =
            new FqdnEndpointsProperties()
                .withCategory("qxn")
                .withEndpoints(
                    Arrays
                        .asList(
                            new FqdnEndpoint().withDomainName("gxhuriplbp").withEndpointDetails(Arrays.asList()),
                            new FqdnEndpoint().withDomainName("nkbebxmuby").withEndpointDetails(Arrays.asList()),
                            new FqdnEndpoint().withDomainName("lrb").withEndpointDetails(Arrays.asList())));
        model = BinaryData.fromObject(model).toObject(FqdnEndpointsProperties.class);
        Assertions.assertEquals("qxn", model.category());
        Assertions.assertEquals("gxhuriplbp", model.endpoints().get(0).domainName());
    }
}