// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.security.confidentialledger.generated;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.BinaryData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public final class GetUserDefinedFunctionsTests extends ConfidentialLedgerClientTestBase {
    @Test
    @Disabled
    public void testGetUserDefinedFunctionsTests() {
        RequestOptions requestOptions = new RequestOptions();
        PagedIterable<BinaryData> response = confidentialLedgerClient.listUserDefinedFunctions(requestOptions);
        Assertions.assertEquals(200, response.iterableByPage().iterator().next().getStatusCode());
        Assertions.assertEquals(
            BinaryData.fromString("{\"code\":\"export function main() { return true }\",\"id\":\"myFunction\"}")
                .toObject(Object.class),
            response.iterator().next().toObject(Object.class));
    }
}
