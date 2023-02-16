// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.providerhub.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** The CheckNameAvailabilitySpecifications model. */
@Fluent
public class CheckNameAvailabilitySpecifications {
    /*
     * The enableDefaultValidation property.
     */
    @JsonProperty(value = "enableDefaultValidation")
    private Boolean enableDefaultValidation;

    /*
     * The resourceTypesWithCustomValidation property.
     */
    @JsonProperty(value = "resourceTypesWithCustomValidation")
    private List<String> resourceTypesWithCustomValidation;

    /** Creates an instance of CheckNameAvailabilitySpecifications class. */
    public CheckNameAvailabilitySpecifications() {
    }

    /**
     * Get the enableDefaultValidation property: The enableDefaultValidation property.
     *
     * @return the enableDefaultValidation value.
     */
    public Boolean enableDefaultValidation() {
        return this.enableDefaultValidation;
    }

    /**
     * Set the enableDefaultValidation property: The enableDefaultValidation property.
     *
     * @param enableDefaultValidation the enableDefaultValidation value to set.
     * @return the CheckNameAvailabilitySpecifications object itself.
     */
    public CheckNameAvailabilitySpecifications withEnableDefaultValidation(Boolean enableDefaultValidation) {
        this.enableDefaultValidation = enableDefaultValidation;
        return this;
    }

    /**
     * Get the resourceTypesWithCustomValidation property: The resourceTypesWithCustomValidation property.
     *
     * @return the resourceTypesWithCustomValidation value.
     */
    public List<String> resourceTypesWithCustomValidation() {
        return this.resourceTypesWithCustomValidation;
    }

    /**
     * Set the resourceTypesWithCustomValidation property: The resourceTypesWithCustomValidation property.
     *
     * @param resourceTypesWithCustomValidation the resourceTypesWithCustomValidation value to set.
     * @return the CheckNameAvailabilitySpecifications object itself.
     */
    public CheckNameAvailabilitySpecifications withResourceTypesWithCustomValidation(
        List<String> resourceTypesWithCustomValidation) {
        this.resourceTypesWithCustomValidation = resourceTypesWithCustomValidation;
        return this;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }
}