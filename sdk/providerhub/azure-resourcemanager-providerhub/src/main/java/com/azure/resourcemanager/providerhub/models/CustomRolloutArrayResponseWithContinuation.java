// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.providerhub.models;

import com.azure.core.annotation.Fluent;
import com.azure.resourcemanager.providerhub.fluent.models.CustomRolloutInner;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** The CustomRolloutArrayResponseWithContinuation model. */
@Fluent
public final class CustomRolloutArrayResponseWithContinuation {
    /*
     * The value property.
     */
    @JsonProperty(value = "value")
    private List<CustomRolloutInner> value;

    /*
     * The URL to get to the next set of results, if there are any.
     */
    @JsonProperty(value = "nextLink")
    private String nextLink;

    /** Creates an instance of CustomRolloutArrayResponseWithContinuation class. */
    public CustomRolloutArrayResponseWithContinuation() {
    }

    /**
     * Get the value property: The value property.
     *
     * @return the value value.
     */
    public List<CustomRolloutInner> value() {
        return this.value;
    }

    /**
     * Set the value property: The value property.
     *
     * @param value the value value to set.
     * @return the CustomRolloutArrayResponseWithContinuation object itself.
     */
    public CustomRolloutArrayResponseWithContinuation withValue(List<CustomRolloutInner> value) {
        this.value = value;
        return this;
    }

    /**
     * Get the nextLink property: The URL to get to the next set of results, if there are any.
     *
     * @return the nextLink value.
     */
    public String nextLink() {
        return this.nextLink;
    }

    /**
     * Set the nextLink property: The URL to get to the next set of results, if there are any.
     *
     * @param nextLink the nextLink value to set.
     * @return the CustomRolloutArrayResponseWithContinuation object itself.
     */
    public CustomRolloutArrayResponseWithContinuation withNextLink(String nextLink) {
        this.nextLink = nextLink;
        return this;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (value() != null) {
            value().forEach(e -> e.validate());
        }
    }
}