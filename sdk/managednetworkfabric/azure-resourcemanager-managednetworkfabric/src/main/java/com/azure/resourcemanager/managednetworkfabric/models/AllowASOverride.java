// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.managednetworkfabric.models;

import com.azure.core.util.ExpandableStringEnum;
import java.util.Collection;

/**
 * Enable Or Disable state.
 */
public final class AllowASOverride extends ExpandableStringEnum<AllowASOverride> {
    /**
     * Static value Enable for AllowASOverride.
     */
    public static final AllowASOverride ENABLE = fromString("Enable");

    /**
     * Static value Disable for AllowASOverride.
     */
    public static final AllowASOverride DISABLE = fromString("Disable");

    /**
     * Creates a new instance of AllowASOverride value.
     * 
     * @deprecated Use the {@link #fromString(String)} factory method.
     */
    @Deprecated
    public AllowASOverride() {
    }

    /**
     * Creates or finds a AllowASOverride from its string representation.
     * 
     * @param name a name to look for.
     * @return the corresponding AllowASOverride.
     */
    public static AllowASOverride fromString(String name) {
        return fromString(name, AllowASOverride.class);
    }

    /**
     * Gets known AllowASOverride values.
     * 
     * @return known AllowASOverride values.
     */
    public static Collection<AllowASOverride> values() {
        return values(AllowASOverride.class);
    }
}
