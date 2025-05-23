// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.chaos.models;

import com.azure.core.util.ExpandableStringEnum;
import java.util.Collection;

/**
 * Enum union of Chaos experiment action types.
 */
public final class ExperimentActionType extends ExpandableStringEnum<ExperimentActionType> {
    /**
     * Static value delay for ExperimentActionType.
     */
    public static final ExperimentActionType DELAY = fromString("delay");

    /**
     * Static value discrete for ExperimentActionType.
     */
    public static final ExperimentActionType DISCRETE = fromString("discrete");

    /**
     * Static value continuous for ExperimentActionType.
     */
    public static final ExperimentActionType CONTINUOUS = fromString("continuous");

    /**
     * Creates a new instance of ExperimentActionType value.
     * 
     * @deprecated Use the {@link #fromString(String)} factory method.
     */
    @Deprecated
    public ExperimentActionType() {
    }

    /**
     * Creates or finds a ExperimentActionType from its string representation.
     * 
     * @param name a name to look for.
     * @return the corresponding ExperimentActionType.
     */
    public static ExperimentActionType fromString(String name) {
        return fromString(name, ExperimentActionType.class);
    }

    /**
     * Gets known ExperimentActionType values.
     * 
     * @return known ExperimentActionType values.
     */
    public static Collection<ExperimentActionType> values() {
        return values(ExperimentActionType.class);
    }
}
