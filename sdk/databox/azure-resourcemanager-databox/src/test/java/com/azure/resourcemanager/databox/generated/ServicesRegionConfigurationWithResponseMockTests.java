// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.databox.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.databox.DataBoxManager;
import com.azure.resourcemanager.databox.models.RegionConfigurationRequest;
import com.azure.resourcemanager.databox.models.RegionConfigurationResponse;
import com.azure.resourcemanager.databox.models.ScheduleAvailabilityRequest;
import com.azure.resourcemanager.databox.models.SkuName;
import com.azure.resourcemanager.databox.models.TransportAvailabilityRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class ServicesRegionConfigurationWithResponseMockTests {
    @Test
    public void testRegionConfigurationWithResponse() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr =
            "{\"scheduleAvailabilityResponse\":{\"availableDates\":[\"2021-11-20T12:54:01Z\",\"2021-06-02T12:49:15Z\",\"2020-12-28T22:53:56Z\"]},\"transportAvailabilityResponse\":{\"transportAvailabilityDetails\":[]}}";

        Mockito.when(httpResponse.getStatusCode()).thenReturn(200);
        Mockito.when(httpResponse.getHeaders()).thenReturn(new HttpHeaders());
        Mockito
            .when(httpResponse.getBody())
            .thenReturn(Flux.just(ByteBuffer.wrap(responseStr.getBytes(StandardCharsets.UTF_8))));
        Mockito
            .when(httpResponse.getBodyAsByteArray())
            .thenReturn(Mono.just(responseStr.getBytes(StandardCharsets.UTF_8)));
        Mockito
            .when(httpClient.send(httpRequest.capture(), Mockito.any()))
            .thenReturn(
                Mono
                    .defer(
                        () -> {
                            Mockito.when(httpResponse.getRequest()).thenReturn(httpRequest.getValue());
                            return Mono.just(httpResponse);
                        }));

        DataBoxManager manager =
            DataBoxManager
                .configure()
                .withHttpClient(httpClient)
                .authenticate(
                    tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                    new AzureProfile("", "", AzureEnvironment.AZURE));

        RegionConfigurationResponse response =
            manager
                .services()
                .regionConfigurationWithResponse(
                    "i",
                    new RegionConfigurationRequest()
                        .withScheduleAvailabilityRequest(
                            new ScheduleAvailabilityRequest()
                                .withStorageLocation("nkedyatrwyhqmib")
                                .withCountry("hwit"))
                        .withTransportAvailabilityRequest(
                            new TransportAvailabilityRequest().withSkuName(SkuName.DATA_BOX)),
                    com.azure.core.util.Context.NONE)
                .getValue();
    }
}
