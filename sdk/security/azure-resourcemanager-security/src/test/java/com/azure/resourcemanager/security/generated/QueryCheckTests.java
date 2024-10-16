// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.security.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.security.models.QueryCheck;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class QueryCheckTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        QueryCheck model =
            BinaryData
                .fromString(
                    "{\"query\":\"tbwbqamteuli\",\"expectedResult\":[[\"kcvmwfauxxepmy\",\"bormcqmiciijqpkz\"],[\"ojxjmcsmyqwix\",\"cp\",\"nkwywzwofa\"],[\"ckduoiqtamtyvskn\",\"rwzawnvs\",\"cfhzagxnvhycv\"],[\"mwrzregzgyufutrw\",\"weryekzk\",\"hmeott\",\"w\"]],\"columnNames\":[\"sxwwhnhjtf\"]}")
                .toObject(QueryCheck.class);
        Assertions.assertEquals("tbwbqamteuli", model.query());
        Assertions.assertEquals("kcvmwfauxxepmy", model.expectedResult().get(0).get(0));
        Assertions.assertEquals("sxwwhnhjtf", model.columnNames().get(0));
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        QueryCheck model =
            new QueryCheck()
                .withQuery("tbwbqamteuli")
                .withExpectedResult(
                    Arrays
                        .asList(
                            Arrays.asList("kcvmwfauxxepmy", "bormcqmiciijqpkz"),
                            Arrays.asList("ojxjmcsmyqwix", "cp", "nkwywzwofa"),
                            Arrays.asList("ckduoiqtamtyvskn", "rwzawnvs", "cfhzagxnvhycv"),
                            Arrays.asList("mwrzregzgyufutrw", "weryekzk", "hmeott", "w")))
                .withColumnNames(Arrays.asList("sxwwhnhjtf"));
        model = BinaryData.fromObject(model).toObject(QueryCheck.class);
        Assertions.assertEquals("tbwbqamteuli", model.query());
        Assertions.assertEquals("kcvmwfauxxepmy", model.expectedResult().get(0).get(0));
        Assertions.assertEquals("sxwwhnhjtf", model.columnNames().get(0));
    }
}
