// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation;

public enum RemoteStorageType {
    /**
     * Use standard storage
     */
    NotSpecified,

    /**
     * Use standard storage
     */
    Standard,

    /**
     * Use premium storage
     */
    Premium;

    public static RemoteStorageType getIgnoreCase(String str) {
        if (Strings.isEmpty(str)) {
            return null;
        }

        if ("NotSpecified".equalsIgnoreCase(str)) {
            return NotSpecified;
        } else if ("Standard".equalsIgnoreCase(str)) {
            return Standard;
        } else if ("Premium".equalsIgnoreCase(str)) {
            return Premium;
        }

        return null;
    }
}
