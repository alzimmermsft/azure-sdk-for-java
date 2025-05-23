// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.carbonoptimization.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.carbonoptimization.models.CarbonEmissionTopItemsSummaryData;
import com.azure.resourcemanager.carbonoptimization.models.CategoryTypeEnum;
import org.junit.jupiter.api.Assertions;

public final class CarbonEmissionTopItemsSummaryDataTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        CarbonEmissionTopItemsSummaryData model = BinaryData.fromString(
            "{\"dataType\":\"TopItemsSummaryData\",\"itemName\":\"niwpwcukj\",\"categoryType\":\"ResourceGroup\",\"latestMonthEmissions\":29.348962683922853,\"previousMonthEmissions\":36.07977033198502,\"monthOverMonthEmissionsChangeRatio\":54.508796010660944,\"monthlyEmissionsChangeValue\":17.8025238081559}")
            .toObject(CarbonEmissionTopItemsSummaryData.class);
        Assertions.assertEquals(29.348962683922853, model.latestMonthEmissions());
        Assertions.assertEquals(36.07977033198502, model.previousMonthEmissions());
        Assertions.assertEquals(54.508796010660944D, model.monthOverMonthEmissionsChangeRatio());
        Assertions.assertEquals(17.8025238081559D, model.monthlyEmissionsChangeValue());
        Assertions.assertEquals("niwpwcukj", model.itemName());
        Assertions.assertEquals(CategoryTypeEnum.RESOURCE_GROUP, model.categoryType());
    }
}
