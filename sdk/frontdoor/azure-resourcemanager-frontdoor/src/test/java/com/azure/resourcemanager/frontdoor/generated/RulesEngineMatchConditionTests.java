// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.frontdoor.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.frontdoor.models.RulesEngineMatchCondition;
import com.azure.resourcemanager.frontdoor.models.RulesEngineMatchVariable;
import com.azure.resourcemanager.frontdoor.models.RulesEngineOperator;
import com.azure.resourcemanager.frontdoor.models.Transform;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class RulesEngineMatchConditionTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        RulesEngineMatchCondition model =
            BinaryData
                .fromString(
                    "{\"rulesEngineMatchVariable\":\"QueryString\",\"selector\":\"gfbcvkcv\",\"rulesEngineOperator\":\"GreaterThanOrEqual\",\"negateCondition\":true,\"rulesEngineMatchValue\":[\"qdcvdrhvoo\",\"sotbob\"],\"transforms\":[\"Lowercase\",\"UrlDecode\",\"Trim\"]}")
                .toObject(RulesEngineMatchCondition.class);
        Assertions.assertEquals(RulesEngineMatchVariable.QUERY_STRING, model.rulesEngineMatchVariable());
        Assertions.assertEquals("gfbcvkcv", model.selector());
        Assertions.assertEquals(RulesEngineOperator.GREATER_THAN_OR_EQUAL, model.rulesEngineOperator());
        Assertions.assertEquals(true, model.negateCondition());
        Assertions.assertEquals("qdcvdrhvoo", model.rulesEngineMatchValue().get(0));
        Assertions.assertEquals(Transform.LOWERCASE, model.transforms().get(0));
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        RulesEngineMatchCondition model =
            new RulesEngineMatchCondition()
                .withRulesEngineMatchVariable(RulesEngineMatchVariable.QUERY_STRING)
                .withSelector("gfbcvkcv")
                .withRulesEngineOperator(RulesEngineOperator.GREATER_THAN_OR_EQUAL)
                .withNegateCondition(true)
                .withRulesEngineMatchValue(Arrays.asList("qdcvdrhvoo", "sotbob"))
                .withTransforms(Arrays.asList(Transform.LOWERCASE, Transform.URL_DECODE, Transform.TRIM));
        model = BinaryData.fromObject(model).toObject(RulesEngineMatchCondition.class);
        Assertions.assertEquals(RulesEngineMatchVariable.QUERY_STRING, model.rulesEngineMatchVariable());
        Assertions.assertEquals("gfbcvkcv", model.selector());
        Assertions.assertEquals(RulesEngineOperator.GREATER_THAN_OR_EQUAL, model.rulesEngineOperator());
        Assertions.assertEquals(true, model.negateCondition());
        Assertions.assertEquals("qdcvdrhvoo", model.rulesEngineMatchValue().get(0));
        Assertions.assertEquals(Transform.LOWERCASE, model.transforms().get(0));
    }
}