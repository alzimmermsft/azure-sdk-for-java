// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.vmwarecloudsimple.models;

import com.azure.core.util.ExpandableStringEnum;
import java.util.Collection;

/**
 * Identity type.
 */
public final class CustomizationIdentityType extends ExpandableStringEnum<CustomizationIdentityType> {
    /**
     * Static value WINDOWS_TEXT for CustomizationIdentityType.
     */
    public static final CustomizationIdentityType WINDOWS_TEXT = fromString("WINDOWS_TEXT");

    /**
     * Static value WINDOWS for CustomizationIdentityType.
     */
    public static final CustomizationIdentityType WINDOWS = fromString("WINDOWS");

    /**
     * Static value LINUX for CustomizationIdentityType.
     */
    public static final CustomizationIdentityType LINUX = fromString("LINUX");

    /**
     * Creates a new instance of CustomizationIdentityType value.
     * 
     * @deprecated Use the {@link #fromString(String)} factory method.
     */
    @Deprecated
    public CustomizationIdentityType() {
    }

    /**
     * Creates or finds a CustomizationIdentityType from its string representation.
     * 
     * @param name a name to look for.
     * @return the corresponding CustomizationIdentityType.
     */
    public static CustomizationIdentityType fromString(String name) {
        return fromString(name, CustomizationIdentityType.class);
    }

    /**
     * Gets known CustomizationIdentityType values.
     * 
     * @return known CustomizationIdentityType values.
     */
    public static Collection<CustomizationIdentityType> values() {
        return values(CustomizationIdentityType.class);
    }
}
