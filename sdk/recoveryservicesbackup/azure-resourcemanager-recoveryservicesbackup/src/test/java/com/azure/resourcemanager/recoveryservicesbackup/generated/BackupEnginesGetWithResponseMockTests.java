// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicesbackup.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.recoveryservicesbackup.RecoveryServicesBackupManager;
import com.azure.resourcemanager.recoveryservicesbackup.models.BackupEngineBaseResource;
import com.azure.resourcemanager.recoveryservicesbackup.models.BackupManagementType;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class BackupEnginesGetWithResponseMockTests {
    @Test
    public void testGetWithResponse() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr =
            "{\"properties\":{\"backupEngineType\":\"BackupEngineBase\",\"friendlyName\":\"uumljcirvpefyc\",\"backupManagementType\":\"DefaultBackup\",\"registrationStatus\":\"ti\",\"backupEngineState\":\"nsxzajlns\",\"healthStatus\":\"wjuyxx\",\"canReRegister\":true,\"backupEngineId\":\"mvuaytuadxkxe\",\"dpmVersion\":\"wp\",\"azureBackupAgentVersion\":\"ghyksarcdrnxs\",\"isAzureBackupAgentUpgradeAvailable\":false,\"isDpmUpgradeAvailable\":true,\"extendedInfo\":{\"databaseName\":\"dltxkpbq\",\"protectedItemsCount\":748877186,\"protectedServersCount\":1732466847,\"diskCount\":503403273,\"usedDiskSpace\":17.043813639171457,\"availableDiskSpace\":89.01523864002633,\"refreshedAt\":\"2021-09-06T19:39:13Z\",\"azureProtectedInstances\":1099100315}},\"eTag\":\"isgglmvokat\",\"location\":\"ztjctibpvbkae\",\"tags\":{\"dfwakwseivmak\":\"mzy\"},\"id\":\"hysowljuxlkbect\",\"name\":\"tfjmskdchmaiub\",\"type\":\"vlzw\"}";

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

        RecoveryServicesBackupManager manager =
            RecoveryServicesBackupManager
                .configure()
                .withHttpClient(httpClient)
                .authenticate(
                    tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                    new AzureProfile("", "", AzureEnvironment.AZURE));

        BackupEngineBaseResource response =
            manager
                .backupEngines()
                .getWithResponse(
                    "gyrihlgm",
                    "behlqtxnr",
                    "lkndrndpgfjodh",
                    "aqotwfhipxwgsabv",
                    "ipowza",
                    com.azure.core.util.Context.NONE)
                .getValue();

        Assertions.assertEquals("ztjctibpvbkae", response.location());
        Assertions.assertEquals("mzy", response.tags().get("dfwakwseivmak"));
        Assertions.assertEquals("uumljcirvpefyc", response.properties().friendlyName());
        Assertions.assertEquals(BackupManagementType.DEFAULT_BACKUP, response.properties().backupManagementType());
        Assertions.assertEquals("ti", response.properties().registrationStatus());
        Assertions.assertEquals("nsxzajlns", response.properties().backupEngineState());
        Assertions.assertEquals("wjuyxx", response.properties().healthStatus());
        Assertions.assertEquals(true, response.properties().canReRegister());
        Assertions.assertEquals("mvuaytuadxkxe", response.properties().backupEngineId());
        Assertions.assertEquals("wp", response.properties().dpmVersion());
        Assertions.assertEquals("ghyksarcdrnxs", response.properties().azureBackupAgentVersion());
        Assertions.assertEquals(false, response.properties().isAzureBackupAgentUpgradeAvailable());
        Assertions.assertEquals(true, response.properties().isDpmUpgradeAvailable());
        Assertions.assertEquals("dltxkpbq", response.properties().extendedInfo().databaseName());
        Assertions.assertEquals(748877186, response.properties().extendedInfo().protectedItemsCount());
        Assertions.assertEquals(1732466847, response.properties().extendedInfo().protectedServersCount());
        Assertions.assertEquals(503403273, response.properties().extendedInfo().diskCount());
        Assertions.assertEquals(17.043813639171457D, response.properties().extendedInfo().usedDiskSpace());
        Assertions.assertEquals(89.01523864002633D, response.properties().extendedInfo().availableDiskSpace());
        Assertions
            .assertEquals(
                OffsetDateTime.parse("2021-09-06T19:39:13Z"), response.properties().extendedInfo().refreshedAt());
        Assertions.assertEquals(1099100315, response.properties().extendedInfo().azureProtectedInstances());
        Assertions.assertEquals("isgglmvokat", response.etag());
    }
}
