// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.carbonoptimization.models;

import com.azure.resourcemanager.carbonoptimization.fluent.models.CarbonEmissionDataListResultInner;
import java.util.List;

/**
 * An immutable client-side representation of CarbonEmissionDataListResult.
 */
public interface CarbonEmissionDataListResult {
    /**
     * Gets the value property: The CarbonEmissionData items on this page.
     * 
     * @return the value value.
     */
    List<CarbonEmissionData> value();

    /**
     * Gets the skipToken property: The pagination token to fetch next page data, it's null or empty if it doesn't have
     * next page data.
     * 
     * @return the skipToken value.
     */
    String skipToken();

    /**
     * Gets the subscriptionAccessDecisionList property: The access decision list for each input subscription.
     * 
     * @return the subscriptionAccessDecisionList value.
     */
    List<SubscriptionAccessDecision> subscriptionAccessDecisionList();

    /**
     * Gets the inner com.azure.resourcemanager.carbonoptimization.fluent.models.CarbonEmissionDataListResultInner
     * object.
     * 
     * @return the inner object.
     */
    CarbonEmissionDataListResultInner innerModel();
}
