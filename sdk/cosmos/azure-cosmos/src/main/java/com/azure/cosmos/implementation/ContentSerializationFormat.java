// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation;

public enum ContentSerializationFormat {
    /**
     * Standard JSON RFC UTF-8 text
     */
    JsonText,

    /**
     * CUSTOM binary for Cosmos DB that encodes a superset of JSON values.
     */
    CosmosBinary;

    public static ContentSerializationFormat getIgnoreCase(String str) {
        if (Strings.isEmpty(str)) {
            return null;
        }

        if ("JsonText".equalsIgnoreCase(str)) {
            return JsonText;
        } else if ("CosmosBinary".equalsIgnoreCase(str)) {
            return CosmosBinary;
        }

        return null;
    }
}
