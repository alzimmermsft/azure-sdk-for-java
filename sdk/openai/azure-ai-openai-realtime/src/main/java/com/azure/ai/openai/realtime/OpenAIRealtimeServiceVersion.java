// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.ai.openai.realtime;

import com.azure.core.util.ServiceVersion;

/**
 * Azure OpenAI Realtime service version.
 */
public enum OpenAIRealtimeServiceVersion implements ServiceVersion {

    /**
     * Enum value 2024-10-01-preview.
     */
    V2024_10_01_PREVIEW("2024-10-01-preview");

    private final String version;

    OpenAIRealtimeServiceVersion(String version) {
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion() {
        return this.version;
    }

    /**
     * Gets the latest service version supported by this client library.
     * 
     * @return The latest {@link OpenAIRealtimeServiceVersion}.
     */
    public static OpenAIRealtimeServiceVersion getLatest() {
        return V2024_10_01_PREVIEW;
    }
}
