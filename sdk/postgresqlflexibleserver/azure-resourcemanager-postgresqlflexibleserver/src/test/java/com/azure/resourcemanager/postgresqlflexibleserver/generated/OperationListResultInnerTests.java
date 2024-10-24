// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.postgresqlflexibleserver.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.postgresqlflexibleserver.fluent.models.OperationListResultInner;
import com.azure.resourcemanager.postgresqlflexibleserver.models.Operation;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class OperationListResultInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        OperationListResultInner model =
            BinaryData
                .fromString(
                    "{\"value\":[{\"name\":\"rxsbkyvp\",\"display\":{\"provider\":\"n\",\"resource\":\"bpzkafkuwbc\",\"operation\":\"wbme\",\"description\":\"seyvj\"},\"isDataAction\":false,\"origin\":\"system\",\"properties\":{\"aofmxagkvtme\":\"dataspkdee\",\"c\":\"datamqkrhahvljuahaqu\",\"aex\":\"datahmdua\"}},{\"name\":\"vfadmws\",\"display\":{\"provider\":\"gvxp\",\"resource\":\"omzlfmi\",\"operation\":\"wbnb\",\"description\":\"ldawkzbaliourqha\"},\"isDataAction\":true,\"origin\":\"user\",\"properties\":{\"gicjooxdjeb\":\"datasfwxosowzxc\",\"ecivyh\":\"datapucwwfvovbvme\",\"ojgjrwjueiotwmc\":\"datace\",\"nrjawgqwg\":\"dataytdxwit\"}},{\"name\":\"ni\",\"display\":{\"provider\":\"fbkp\",\"resource\":\"gklwn\",\"operation\":\"hjdauwhvylwz\",\"description\":\"dhxujznbmpo\"},\"isDataAction\":false,\"origin\":\"system\",\"properties\":{\"alupjm\":\"datalve\",\"iplrbpbewtghfgb\":\"datahfxobbcswsrtj\",\"wxzvlvqhjkb\":\"datac\",\"iebwwaloayqcgwrt\":\"datagibtnm\"}}],\"nextLink\":\"uzgwyzmhtx\"}")
                .toObject(OperationListResultInner.class);
        Assertions.assertEquals(false, model.value().get(0).isDataAction());
        Assertions.assertEquals("uzgwyzmhtx", model.nextLink());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        OperationListResultInner model =
            new OperationListResultInner()
                .withValue(
                    Arrays
                        .asList(
                            new Operation().withIsDataAction(false),
                            new Operation().withIsDataAction(true),
                            new Operation().withIsDataAction(false)))
                .withNextLink("uzgwyzmhtx");
        model = BinaryData.fromObject(model).toObject(OperationListResultInner.class);
        Assertions.assertEquals(false, model.value().get(0).isDataAction());
        Assertions.assertEquals("uzgwyzmhtx", model.nextLink());
    }
}
