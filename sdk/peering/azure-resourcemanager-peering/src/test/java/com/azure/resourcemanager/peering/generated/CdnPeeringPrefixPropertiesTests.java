// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.peering.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.peering.fluent.models.CdnPeeringPrefixProperties;

public final class CdnPeeringPrefixPropertiesTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        CdnPeeringPrefixProperties model =
            BinaryData
                .fromString(
                    "{\"prefix\":\"rcryuanzwuxzdxta\",\"azureRegion\":\"lhmwhfpmrqobm\",\"azureService\":\"kknryrtihf\",\"isPrimaryRegion\":true,\"bgpCommunity\":\"bpzvgn\"}")
                .toObject(CdnPeeringPrefixProperties.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        CdnPeeringPrefixProperties model = new CdnPeeringPrefixProperties();
        model = BinaryData.fromObject(model).toObject(CdnPeeringPrefixProperties.class);
    }
}