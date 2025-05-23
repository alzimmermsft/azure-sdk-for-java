// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.redisenterprise.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.models.AzureCloud;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.resourcemanager.redisenterprise.RedisEnterpriseManager;
import com.azure.resourcemanager.redisenterprise.models.PrivateLinkResource;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public final class PrivateLinkResourcesListByClusterMockTests {
    @Test
    public void testListByCluster() throws Exception {
        String responseStr
            = "{\"value\":[{\"properties\":{\"groupId\":\"fqjhhkxbpvjymj\",\"requiredMembers\":[\"j\",\"n\",\"u\"],\"requiredZoneNames\":[\"krtswbxqz\"]},\"id\":\"szjfauvjfdxxivet\",\"name\":\"t\",\"type\":\"qaqtdoqmcbxvwvxy\"}]}";

        HttpClient httpClient
            = response -> Mono.just(new MockHttpResponse(response, 200, responseStr.getBytes(StandardCharsets.UTF_8)));
        RedisEnterpriseManager manager = RedisEnterpriseManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureCloud.AZURE_PUBLIC_CLOUD));

        PagedIterable<PrivateLinkResource> response
            = manager.privateLinkResources().listByCluster("moizpos", "mgrcfbu", com.azure.core.util.Context.NONE);

        Assertions.assertEquals("krtswbxqz", response.iterator().next().requiredZoneNames().get(0));
    }
}
