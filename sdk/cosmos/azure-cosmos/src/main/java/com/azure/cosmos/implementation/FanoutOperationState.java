// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation;

public enum FanoutOperationState {
    /**
     * Fanout operation started
     */
    Started,

    /**
     * Fanout operation completed
     */
    Completed;

    public static FanoutOperationState getIgnoreCase(String str) {
        if (Strings.isEmpty(str)) {
            return null;
        }

        if ("Started".equalsIgnoreCase(str)) {
            return Started;
        } else if ("Completed".equalsIgnoreCase(str)) {
            return Completed;
        }

        return null;
    }
}
