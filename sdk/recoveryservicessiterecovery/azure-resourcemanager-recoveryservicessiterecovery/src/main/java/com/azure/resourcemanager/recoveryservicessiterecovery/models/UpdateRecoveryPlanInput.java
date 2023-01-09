// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicessiterecovery.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Update recovery plan input class. */
@Fluent
public final class UpdateRecoveryPlanInput {
    /*
     * Recovery plan update properties.
     */
    @JsonProperty(value = "properties")
    private UpdateRecoveryPlanInputProperties properties;

    /** Creates an instance of UpdateRecoveryPlanInput class. */
    public UpdateRecoveryPlanInput() {
    }

    /**
     * Get the properties property: Recovery plan update properties.
     *
     * @return the properties value.
     */
    public UpdateRecoveryPlanInputProperties properties() {
        return this.properties;
    }

    /**
     * Set the properties property: Recovery plan update properties.
     *
     * @param properties the properties value to set.
     * @return the UpdateRecoveryPlanInput object itself.
     */
    public UpdateRecoveryPlanInput withProperties(UpdateRecoveryPlanInputProperties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (properties() != null) {
            properties().validate();
        }
    }
}