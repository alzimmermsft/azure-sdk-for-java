// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.chaos.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.models.AzureCloud;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.resourcemanager.chaos.ChaosManager;
import com.azure.resourcemanager.chaos.models.ExperimentExecution;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public final class ExperimentsListAllExecutionsMockTests {
    @Test
    public void testListAllExecutions() throws Exception {
        String responseStr
            = "{\"value\":[{\"properties\":{\"status\":\"gmfpgvmp\",\"startedAt\":\"2021-10-24T06:37:57Z\",\"stoppedAt\":\"2021-03-23T07:07Z\"},\"id\":\"haq\",\"name\":\"x\",\"type\":\"smwutwbdsrezpd\"}]}";

        HttpClient httpClient
            = response -> Mono.just(new MockHttpResponse(response, 200, responseStr.getBytes(StandardCharsets.UTF_8)));
        ChaosManager manager = ChaosManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureCloud.AZURE_PUBLIC_CLOUD));

        PagedIterable<ExperimentExecution> response
            = manager.experiments().listAllExecutions("bjbsybb", "wrv", com.azure.core.util.Context.NONE);

    }
}
