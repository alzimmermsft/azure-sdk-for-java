// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.frontdoor.generated;

import com.azure.resourcemanager.frontdoor.models.State;

/** Samples for NetworkExperimentProfiles CreateOrUpdate. */
public final class NetworkExperimentProfilesCreateOrUpdateSamples {
    /*
     * x-ms-original-file: specification/frontdoor/resource-manager/Microsoft.Network/stable/2019-11-01/examples/NetworkExperimentCreateProfile.json
     */
    /**
     * Sample code: Creates an NetworkExperiment Profile in a Resource Group.
     *
     * @param manager Entry point to FrontDoorManager.
     */
    public static void createsAnNetworkExperimentProfileInAResourceGroup(
        com.azure.resourcemanager.frontdoor.FrontDoorManager manager) {
        manager
            .networkExperimentProfiles()
            .define("MyProfile")
            .withRegion("WestUs")
            .withExistingResourceGroup("MyResourceGroup")
            .withEnabledState(State.ENABLED)
            .create();
    }
}