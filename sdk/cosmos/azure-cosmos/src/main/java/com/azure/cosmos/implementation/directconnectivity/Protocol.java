// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation.directconnectivity;

public enum Protocol {
    HTTPS("https", "Https"),
    TCP("rntbd", "Rntbd");

    private final String scheme;
    private final String capitalizedScheme;

    private Protocol(String scheme, String capitalizedScheme) {
        this.scheme = scheme;
        this.capitalizedScheme = capitalizedScheme;
    }

    String scheme() {
        return scheme;
    }

    @Override
    public String toString() {
        return capitalizedScheme;
    }
}
