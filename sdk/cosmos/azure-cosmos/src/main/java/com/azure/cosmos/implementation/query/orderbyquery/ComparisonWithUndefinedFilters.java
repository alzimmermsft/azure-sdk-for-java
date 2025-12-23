// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation.query.orderbyquery;

import com.azure.cosmos.implementation.Strings;

import static com.azure.cosmos.implementation.Utils.checkArgument;

/**
 * Handling undefined needs filter literals
 * What we really want is to support expression > undefined,
 * but the engine evaluates to undefined instead of true or false,
 * so we work around this by using the IS_DEFINED() system functio
 */
public class ComparisonWithUndefinedFilters implements ComparisonFilters {
    private final String expression;

    public ComparisonWithUndefinedFilters(String expression) {
        checkArgument(Strings.isNotEmpty(expression), "Expression can not be null or empty");

        this.expression = expression;
    }

    public String lessThan() {
        return "false";
    }

    public String lessThanOrEqualTo() {
        return "NOT IS_DEFINED(" + this.expression + ")";
    }

    public String equalTo() {
        return "NOT IS_DEFINED(" + this.expression + ")";
    }

    public String greaterThan() {
        return "IS_DEFINED(" + this.expression + ")";
    }

    public String greaterThanOrEqualTo() {
        return "true";
    }
}
