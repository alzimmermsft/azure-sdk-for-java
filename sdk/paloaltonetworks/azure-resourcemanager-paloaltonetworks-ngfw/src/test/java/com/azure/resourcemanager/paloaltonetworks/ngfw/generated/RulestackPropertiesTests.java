// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.paloaltonetworks.ngfw.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.paloaltonetworks.ngfw.fluent.models.RulestackProperties;
import com.azure.resourcemanager.paloaltonetworks.ngfw.models.DefaultMode;
import com.azure.resourcemanager.paloaltonetworks.ngfw.models.ScopeType;
import com.azure.resourcemanager.paloaltonetworks.ngfw.models.SecurityServices;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class RulestackPropertiesTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        RulestackProperties model = BinaryData.fromString(
            "{\"panEtag\":\"jq\",\"panLocation\":\"hmuouqfprwzwbn\",\"scope\":\"GLOBAL\",\"associatedSubscriptions\":[\"wu\"],\"description\":\"gazxuf\",\"defaultMode\":\"NONE\",\"minAppIdVersion\":\"kyfi\",\"provisioningState\":\"Canceled\",\"securityServices\":{\"vulnerabilityProfile\":\"fvzwdzuhty\",\"antiSpywareProfile\":\"isdkfthwxmnteiw\",\"antiVirusProfile\":\"pvkmijcmmxdcuf\",\"urlFilteringProfile\":\"srp\",\"fileBlockingProfile\":\"zidnsezcxtbzsgfy\",\"dnsSubscription\":\"sne\",\"outboundUnTrustCertificate\":\"dwzjeiach\",\"outboundTrustCertificate\":\"osfln\"}}")
            .toObject(RulestackProperties.class);
        Assertions.assertEquals("jq", model.panEtag());
        Assertions.assertEquals("hmuouqfprwzwbn", model.panLocation());
        Assertions.assertEquals(ScopeType.GLOBAL, model.scope());
        Assertions.assertEquals("wu", model.associatedSubscriptions().get(0));
        Assertions.assertEquals("gazxuf", model.description());
        Assertions.assertEquals(DefaultMode.NONE, model.defaultMode());
        Assertions.assertEquals("kyfi", model.minAppIdVersion());
        Assertions.assertEquals("fvzwdzuhty", model.securityServices().vulnerabilityProfile());
        Assertions.assertEquals("isdkfthwxmnteiw", model.securityServices().antiSpywareProfile());
        Assertions.assertEquals("pvkmijcmmxdcuf", model.securityServices().antiVirusProfile());
        Assertions.assertEquals("srp", model.securityServices().urlFilteringProfile());
        Assertions.assertEquals("zidnsezcxtbzsgfy", model.securityServices().fileBlockingProfile());
        Assertions.assertEquals("sne", model.securityServices().dnsSubscription());
        Assertions.assertEquals("dwzjeiach", model.securityServices().outboundUnTrustCertificate());
        Assertions.assertEquals("osfln", model.securityServices().outboundTrustCertificate());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        RulestackProperties model = new RulestackProperties().withPanEtag("jq")
            .withPanLocation("hmuouqfprwzwbn")
            .withScope(ScopeType.GLOBAL)
            .withAssociatedSubscriptions(Arrays.asList("wu"))
            .withDescription("gazxuf")
            .withDefaultMode(DefaultMode.NONE)
            .withMinAppIdVersion("kyfi")
            .withSecurityServices(new SecurityServices().withVulnerabilityProfile("fvzwdzuhty")
                .withAntiSpywareProfile("isdkfthwxmnteiw")
                .withAntiVirusProfile("pvkmijcmmxdcuf")
                .withUrlFilteringProfile("srp")
                .withFileBlockingProfile("zidnsezcxtbzsgfy")
                .withDnsSubscription("sne")
                .withOutboundUnTrustCertificate("dwzjeiach")
                .withOutboundTrustCertificate("osfln"));
        model = BinaryData.fromObject(model).toObject(RulestackProperties.class);
        Assertions.assertEquals("jq", model.panEtag());
        Assertions.assertEquals("hmuouqfprwzwbn", model.panLocation());
        Assertions.assertEquals(ScopeType.GLOBAL, model.scope());
        Assertions.assertEquals("wu", model.associatedSubscriptions().get(0));
        Assertions.assertEquals("gazxuf", model.description());
        Assertions.assertEquals(DefaultMode.NONE, model.defaultMode());
        Assertions.assertEquals("kyfi", model.minAppIdVersion());
        Assertions.assertEquals("fvzwdzuhty", model.securityServices().vulnerabilityProfile());
        Assertions.assertEquals("isdkfthwxmnteiw", model.securityServices().antiSpywareProfile());
        Assertions.assertEquals("pvkmijcmmxdcuf", model.securityServices().antiVirusProfile());
        Assertions.assertEquals("srp", model.securityServices().urlFilteringProfile());
        Assertions.assertEquals("zidnsezcxtbzsgfy", model.securityServices().fileBlockingProfile());
        Assertions.assertEquals("sne", model.securityServices().dnsSubscription());
        Assertions.assertEquals("dwzjeiach", model.securityServices().outboundUnTrustCertificate());
        Assertions.assertEquals("osfln", model.securityServices().outboundTrustCertificate());
    }
}
