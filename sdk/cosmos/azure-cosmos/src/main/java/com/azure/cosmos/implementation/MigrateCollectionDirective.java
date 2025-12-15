// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation;

public enum MigrateCollectionDirective {
    /**
     * Move to SSD
     */
    Thaw,

    /**
     * Move to HDD
     */
    Freeze;

    public static MigrateCollectionDirective getIgnoreCase(String str) {
        if (Strings.isEmpty(str)) {
            return null;
        }

        if ("Thaw".equalsIgnoreCase(str)) {
            return Thaw;
        } else if ("Freeze".equalsIgnoreCase(str)) {
            return Freeze;
        }

        return null;
    }
}
