// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.securityinsights.generated;

/**
 * Samples for SentinelOnboardingStates Delete.
 */
public final class SentinelOnboardingStatesDeleteSamples {
    /*
     * x-ms-original-file:
     * specification/securityinsights/resource-manager/Microsoft.SecurityInsights/stable/2022-11-01/examples/
     * onboardingStates/DeleteSentinelOnboardingState.json
     */
    /**
     * Sample code: Delete Sentinel onboarding state.
     * 
     * @param manager Entry point to SecurityInsightsManager.
     */
    public static void
        deleteSentinelOnboardingState(com.azure.resourcemanager.securityinsights.SecurityInsightsManager manager) {
        manager.sentinelOnboardingStates()
            .deleteWithResponse("myRg", "myWorkspace", "default", com.azure.core.util.Context.NONE);
    }
}
