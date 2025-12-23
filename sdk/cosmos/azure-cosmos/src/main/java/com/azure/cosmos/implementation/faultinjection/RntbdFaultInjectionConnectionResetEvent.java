// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation.faultinjection;

import com.azure.cosmos.implementation.Strings;

import static com.azure.cosmos.implementation.Utils.checkArgument;

public class RntbdFaultInjectionConnectionResetEvent {
    private final String faultInjectionRuleId;


    public RntbdFaultInjectionConnectionResetEvent(String faultInjectionRuleId) {
        checkArgument(Strings.isNotEmpty(faultInjectionRuleId), "Argument 'faultInjectionRuleId' can not be null nor empty");
        this.faultInjectionRuleId = faultInjectionRuleId;
    }

    public String getFaultInjectionRuleId() {
        return this.faultInjectionRuleId;
    }
}
