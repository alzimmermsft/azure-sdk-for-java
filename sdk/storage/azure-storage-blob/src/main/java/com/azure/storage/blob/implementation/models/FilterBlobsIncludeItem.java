// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.storage.blob.implementation.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Defines values for FilterBlobsIncludeItem. */
public enum FilterBlobsIncludeItem {
    /** Enum value none. */
    NONE("none"),

    /** Enum value versions. */
    VERSIONS("versions");

    /** The actual serialized value for a FilterBlobsIncludeItem instance. */
    private final String value;

    FilterBlobsIncludeItem(String value) {
        this.value = value;
    }

    /**
     * Parses a serialized value to a FilterBlobsIncludeItem instance.
     *
     * @param value the serialized value to parse.
     * @return the parsed FilterBlobsIncludeItem object, or null if unable to parse.
     */
    @JsonCreator
    public static FilterBlobsIncludeItem fromString(String value) {
        if (value == null) {
            return null;
        }
        FilterBlobsIncludeItem[] items = FilterBlobsIncludeItem.values();
        for (FilterBlobsIncludeItem item : items) {
            if (item.toString().equalsIgnoreCase(value)) {
                return item;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}