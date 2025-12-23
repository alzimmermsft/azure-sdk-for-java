// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation.throughputControl;

import com.azure.cosmos.implementation.Strings;

import java.util.concurrent.atomic.AtomicReference;

import static com.azure.cosmos.implementation.Utils.checkArgument;

public class ThroughputControlRequestContext {
    private final String configString;
    private final AtomicReference<String> throughputControlCycleId;

    public ThroughputControlRequestContext(String configString) {
        checkArgument(Strings.isNotEmpty(configString), "Argument 'configString' cannot be null or empty.");
        this.configString = configString;
        this.throughputControlCycleId = new AtomicReference<>();
    }

    public String getConfigString() {
        return this.configString;
    }

    public String getThroughputControlCycleId() {
        return this.throughputControlCycleId.get();
    }

    public void setThroughputControlCycleId(String throughputControlCycleId) {
        this.throughputControlCycleId.set(throughputControlCycleId);
    }
}
