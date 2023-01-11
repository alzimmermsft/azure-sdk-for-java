// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.postgresqlflexibleserver.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.postgresqlflexibleserver.fluent.models.FirewallRuleInner;
import org.junit.jupiter.api.Assertions;

public final class FirewallRuleInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        FirewallRuleInner model =
            BinaryData
                .fromString(
                    "{\"properties\":{\"startIpAddress\":\"jf\",\"endIpAddress\":\"ebrjcxe\"},\"id\":\"uwutttxfvjrbi\",\"name\":\"phxepcyvahf\",\"type\":\"ljkyqxjvuuj\"}")
                .toObject(FirewallRuleInner.class);
        Assertions.assertEquals("jf", model.startIpAddress());
        Assertions.assertEquals("ebrjcxe", model.endIpAddress());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        FirewallRuleInner model = new FirewallRuleInner().withStartIpAddress("jf").withEndIpAddress("ebrjcxe");
        model = BinaryData.fromObject(model).toObject(FirewallRuleInner.class);
        Assertions.assertEquals("jf", model.startIpAddress());
        Assertions.assertEquals("ebrjcxe", model.endIpAddress());
    }
}