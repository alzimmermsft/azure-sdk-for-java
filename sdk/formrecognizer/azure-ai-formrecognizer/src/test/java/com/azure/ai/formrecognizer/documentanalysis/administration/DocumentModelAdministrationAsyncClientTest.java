// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.formrecognizer.documentanalysis.administration;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisAsyncClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisServiceVersion;
import com.azure.ai.formrecognizer.documentanalysis.TestUtils;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.BlobContentSource;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.BlobFileListContentSource;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.BuildDocumentClassifierOptions;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.BuildDocumentModelOptions;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.ClassifierDocumentTypeDetails;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.ComposeDocumentModelOptions;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.CopyAuthorizationOptions;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.DocumentClassifierDetails;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.DocumentModelBuildMode;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.DocumentModelBuildOperationDetails;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.DocumentModelComposeOperationDetails;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.DocumentModelCopyAuthorization;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.DocumentModelCopyToOperationDetails;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.DocumentModelDetails;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.OperationResult;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpClient;
import com.azure.core.http.rest.Response;
import com.azure.core.models.ResponseError;
import com.azure.core.test.annotation.RecordWithoutRequestBody;
import com.azure.core.test.http.AssertingHttpClientBuilder;
import com.azure.core.util.BinaryData;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.polling.PollerFlux;
import com.azure.core.util.polling.SyncPoller;
import com.azure.identity.AzureAuthorityHosts;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.DISPLAY_NAME_WITH_ARGUMENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ParameterizedClass(name = DISPLAY_NAME_WITH_ARGUMENTS)
@MethodSource("com.azure.ai.formrecognizer.documentanalysis.TestUtils#getTestParameters")
public class DocumentModelAdministrationAsyncClientTest extends DocumentModelAdministrationClientTestBase {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient httpClient;
    private final DocumentAnalysisServiceVersion serviceVersion;

    private DocumentModelAdministrationAsyncClient client;

    public DocumentModelAdministrationAsyncClientTest(HttpClient httpClient,
        DocumentAnalysisServiceVersion serviceVersion) {
        this.httpClient = httpClient;
        this.serviceVersion = serviceVersion;
    }

    @BeforeEach
    public void createClient() {
        this.client = getDocumentModelAdminAsyncClient();
    }

    private HttpClient buildAsyncAssertingClient(HttpClient httpClient) {
        return new AssertingHttpClientBuilder(httpClient).skipRequest((ignored1, ignored2) -> false)
            .assertAsync()
            .build();
    }

    private DocumentModelAdministrationAsyncClient getDocumentModelAdminAsyncClient() {
        return getDocumentModelAdminClientBuilder(
            buildAsyncAssertingClient(
                interceptorManager.isPlaybackMode() ? interceptorManager.getPlaybackClient() : httpClient),
            serviceVersion).buildAsyncClient();
    }

    /**
     * Verifies the document analysis async client is valid.
     */
    @Test
    public void getDocumentAnalysisClientAndValidate() {
        DocumentAnalysisAsyncClient documentAnalysisAsyncClient = this.client.getDocumentAnalysisAsyncClient();
        blankPdfDataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = documentAnalysisAsyncClient
                .beginAnalyzeDocument("prebuilt-receipt", BinaryData.fromStream(data, dataLength))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            assertNotNull(syncPoller.getFinalResult());
        });
    }

    /**
     * Verifies account properties returned for a subscription account.
     */
    @Test
    public void validGetResourceDetails() {
        StepVerifier.create(client.getResourceDetails())
            .assertNext(DocumentModelAdministrationClientTestBase::validateResourceInfo)
            .expectComplete()
            .verify(DEFAULT_TIMEOUT);
    }

    /**
     * Verifies account properties returned with a Http Response for a subscription account.
     */
    @Test
    public void validGetResourceDetailsWithResponse() {
        StepVerifier.create(client.getResourceDetails())
            .assertNext(DocumentModelAdministrationClientTestBase::validateResourceInfo)
            .expectComplete()
            .verify(DEFAULT_TIMEOUT);
    }

    @Test
    public void deleteModelValidModelIdWithResponse() {

        buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> syncPoller1
                = client.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE, null, null)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller1.waitForCompletion();
            DocumentModelDetails createdModel = syncPoller1.getFinalResult();

            StepVerifier.create(client.deleteDocumentModelWithResponse(createdModel.getModelId()))
                .assertNext(response -> assertEquals(response.getStatusCode(), HttpResponseStatus.NO_CONTENT.code()))
                .expectComplete()
                .verify(DEFAULT_TIMEOUT);

            StepVerifier.create(client.getDocumentModelWithResponse(createdModel.getModelId()))
                .expectErrorSatisfies(throwable -> {
                    assertEquals(HttpResponseException.class, throwable.getClass());
                    final ResponseError responseError = (ResponseError) ((HttpResponseException) throwable).getValue();
                    assertEquals("NotFound", responseError.getCode());
                })
                .verify(DEFAULT_TIMEOUT);
        });
    }

    /**
     * Verifies the result of the copy authorization for valid parameters.
     */
    @Test
    public void copyAuthorization() {
        String modelId = "java_copy_model_test";
        StepVerifier.create(client.getCopyAuthorizationWithResponse(new CopyAuthorizationOptions().setModelId(modelId)))
            .assertNext(response -> validateCopyAuthorizationResult(response.getValue()))
            .expectComplete()
            .verify(DEFAULT_TIMEOUT);

        StepVerifier.create(client.deleteDocumentModel(modelId)).expectComplete().verify(DEFAULT_TIMEOUT);
    }

    /**
     * Verifies the result of the create composed model for valid parameters.
     */
    @Test
    public void beginCreateComposedModel() {

        buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> syncPoller1 = client
                .beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE, null,
                    new BuildDocumentModelOptions().setModelId("async_component_model_1"))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller1.waitForCompletion();
            DocumentModelDetails createdModel1 = syncPoller1.getFinalResult();

            SyncPoller<OperationResult, DocumentModelDetails> syncPoller2 = client
                .beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE, null,
                    new BuildDocumentModelOptions().setModelId("async_component_model_2"))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller2.waitForCompletion();
            DocumentModelDetails createdModel2 = syncPoller2.getFinalResult();

            final List<String> modelIdList = Arrays.asList(createdModel1.getModelId(), createdModel2.getModelId());

            DocumentModelDetails composedModel = client
                .beginComposeDocumentModel(modelIdList,
                    new ComposeDocumentModelOptions().setDescription(TestUtils.EXPECTED_DESC))
                .setPollInterval(durationTestMode)
                .getSyncPoller()
                .getFinalResult();

            assertNotNull(composedModel.getModelId());
            assertEquals(TestUtils.EXPECTED_DESC, composedModel.getDescription());
            assertEquals(2, composedModel.getDocumentTypes().size());
            composedModel.getDocumentTypes().forEach((key, docTypeInfo) -> {
                if (key.contains("async_component_model_1") || key.contains("async_component_model_2")) {
                    assert true;
                } else {
                    assert false;
                }
            });
            validateDocumentModelData(composedModel);

            client.deleteDocumentModel(createdModel1.getModelId()).block();
            client.deleteDocumentModel(createdModel2.getModelId()).block();
            client.deleteDocumentModel(composedModel.getModelId()).block();
        });
    }

    /**
     * Verifies the result of building a document analysis  with Options.
     */
    @Test
    public void createComposedModelWithOptions() {
        buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> syncPoller1
                = client.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE, null, null)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller1.waitForCompletion();
            DocumentModelDetails createdModel1 = syncPoller1.getFinalResult();

            SyncPoller<OperationResult, DocumentModelDetails> syncPoller2
                = client.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE, null, null)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller2.waitForCompletion();
            DocumentModelDetails createdModel2 = syncPoller2.getFinalResult();

            final List<String> modelIdList = Arrays.asList(createdModel1.getModelId(), createdModel2.getModelId());
            String composedModelId = "test-composed-model";

            DocumentModelDetails composedModel = client
                .beginComposeDocumentModel(modelIdList,
                    new ComposeDocumentModelOptions().setModelId(composedModelId)
                        .setDescription(TestUtils.EXPECTED_DESC)
                        .setTags(TestUtils.EXPECTED_MODEL_TAGS))
                .setPollInterval(durationTestMode)
                .getSyncPoller()
                .getFinalResult();

            validateDocumentModelData(composedModel);
            Assertions.assertEquals(TestUtils.EXPECTED_DESC, composedModel.getDescription());
            Assertions.assertNotNull(composedModel.getTags());
            Assertions.assertEquals(composedModelId, composedModel.getModelId());

            client.deleteDocumentModel(createdModel1.getModelId()).block();
            client.deleteDocumentModel(createdModel2.getModelId()).block();
            client.deleteDocumentModel(composedModel.getModelId()).block();
        });
    }

    /**
     * Verifies the result of building a document analysis model.
     */
    @Test
    public void beginBuildModel() {
        buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> syncPoller1
                = client.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE, null, null)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller1.waitForCompletion();
            DocumentModelDetails createdModel1 = syncPoller1.getFinalResult();

            validateDocumentModelData(createdModel1);
            client.deleteDocumentModel(createdModel1.getModelId()).block();
        });
    }

    /**
     * Verifies that building a model throws a DocumentModelOperationException when the training container is missing
     * OCR files.
     */
    @Test
    public void beginBuildModelThrowsHttpResponseException() {
        buildModelErrorRunner((errorTrainingFilesUrl) -> {
            if (!AzureAuthorityHosts.AZURE_GOVERNMENT.equals(TestUtils.getAuthority(client.getEndpoint()))) {
                HttpResponseException httpResponseException = Assertions.assertThrows(HttpResponseException.class,
                    () -> client.beginBuildDocumentModel(errorTrainingFilesUrl, DocumentModelBuildMode.TEMPLATE)
                        .setPollInterval(durationTestMode)
                        .getSyncPoller()
                        .getFinalResult());

                ResponseError actualError = (ResponseError) httpResponseException.getValue();
                Assertions.assertNotNull(actualError.getCode());
            } else {
                HttpResponseException httpResponseException = Assertions.assertThrows(HttpResponseException.class,
                    () -> client
                        .beginBuildDocumentModel(errorTrainingFilesUrl, DocumentModelBuildMode.TEMPLATE, null, null)
                        .setPollInterval(durationTestMode)
                        .getSyncPoller()
                        .getFinalResult());

                ResponseError actualError = (ResponseError) httpResponseException.getValue();
                Assertions.assertEquals(
                    "Invalid request., errorCode: [ContentSourceNotAccessible], message: Content is not accessible: Invalid data URL",
                    actualError.getMessage());
                Assertions.assertEquals("InvalidRequest", actualError.getCode());
            }
        });
    }

    /**
     * Verifies the result of building a document analysis  with Options.
     */
    @Test
    public void beginBuildModelWithOptions() {
        String modelId = "test-model";

        buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> syncPoller1
                = client
                    .beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE, null,
                        new BuildDocumentModelOptions().setModelId(modelId)
                            .setDescription(TestUtils.EXPECTED_DESC)
                            .setTags(TestUtils.EXPECTED_MODEL_TAGS))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller1.waitForCompletion();
            DocumentModelDetails createdModel = syncPoller1.getFinalResult();

            validateDocumentModelData(createdModel);
            Assertions.assertEquals(TestUtils.EXPECTED_DESC, createdModel.getDescription());
            Assertions.assertNotNull(createdModel.getTags());
            Assertions.assertEquals(modelId, createdModel.getModelId());

            client.deleteDocumentModel(createdModel.getModelId()).block();
        });
    }

    /**
     * Verifies that building a document model fails with an Invalid prefix.
     */
    @Test
    public void beginBuildModelFailsWithInvalidPrefix() {
        buildModelRunner((trainingFilesUrl) -> StepVerifier.create(
            client.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE, "invalidPrefix", null)
                .setPollInterval(durationTestMode))
            .expectErrorSatisfies(throwable -> {
                assertEquals(HttpResponseException.class, throwable.getClass());
                final ResponseError responseError = (ResponseError) ((HttpResponseException) throwable).getValue();
                assertEquals("InvalidRequest", responseError.getCode());
            })
            .verify(DEFAULT_TIMEOUT));
    }

    /**
     * Verifies the result of the copy operation for valid parameters.
     */
    @Test
    public void beginCopy() {
        buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> syncPoller1
                = client.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE, null, null)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller1.waitForCompletion();
            DocumentModelDetails actualModel = syncPoller1.getFinalResult();

            Mono<DocumentModelCopyAuthorization> targetMono = client.getCopyAuthorization();
            DocumentModelCopyAuthorization target = targetMono.block();
            if (actualModel == null) {
                fail();
                return;
            }

            PollerFlux<OperationResult, DocumentModelDetails> copyPoller
                = client.beginCopyDocumentModelTo(actualModel.getModelId(), target).setPollInterval(durationTestMode);
            DocumentModelDetails copiedModel = copyPoller.getSyncPoller().getFinalResult();
            Assertions.assertEquals(target.getTargetModelId(), copiedModel.getModelId());

            client.deleteDocumentModel(actualModel.getModelId()).block();
            client.deleteDocumentModel(copiedModel.getModelId()).block();
        });
    }

    /**
     * Verifies the result of the copy operation for valid parameters.
     */
    @Test
    public void beginCopyWithOptions() {
        String modelId = "my-copied-model-id";

        buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> syncPoller1
                = client.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE, null, null)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller1.waitForCompletion();
            DocumentModelDetails actualModel = syncPoller1.getFinalResult();

            Mono<Response<DocumentModelCopyAuthorization>> targetMono
                = client.getCopyAuthorizationWithResponse(new CopyAuthorizationOptions().setModelId(modelId)
                    .setDescription(TestUtils.EXPECTED_DESC)
                    .setTags(TestUtils.EXPECTED_MODEL_TAGS));

            DocumentModelCopyAuthorization target = targetMono.block().getValue();
            if (actualModel == null) {
                fail();
                return;
            }

            PollerFlux<OperationResult, DocumentModelDetails> copyPoller
                = client.beginCopyDocumentModelTo(actualModel.getModelId(), target).setPollInterval(durationTestMode);

            DocumentModelDetails copiedModel = copyPoller.getSyncPoller().getFinalResult();
            Assertions.assertEquals(target.getTargetModelId(), copiedModel.getModelId());
            validateDocumentModelData(copiedModel);
            Assertions.assertEquals(TestUtils.EXPECTED_DESC, copiedModel.getDescription());
            Assertions.assertNotNull(copiedModel.getTags());
            Assertions.assertEquals(modelId, target.getTargetModelId());

            client.deleteDocumentModel(actualModel.getModelId()).block();
            client.deleteDocumentModel(copiedModel.getModelId()).block();
        });
    }

    /**
     * Test for listing all models information.
     */
    @Test
    @Disabled
    public void listModels() {
        StepVerifier.create(client.listDocumentModels().byPage().take(4))
            .thenConsumeWhile(documentModelInfoPagedResponse -> {
                documentModelInfoPagedResponse.getValue().forEach(documentModelInfo -> {
                    assertNotNull(documentModelInfo.getModelId());
                    assertNotNull(documentModelInfo.getCreatedOn());
                });
                return true;
            });
        // TODO (alzimmer): This test needs to be recorded again as it was never verifying, therefore never
        //  subscribing to the reactive API call.
        //            .expectComplete()
        //            .verify(DEFAULT_TIMEOUT);
    }

    /**
     * Verifies document model info returned with response for a valid model ID.
     */
    @Test
    public void getModelWithResponse() {
        buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> syncPoller1
                = client.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE, null, null)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller1.waitForCompletion();
            DocumentModelDetails createdModel = syncPoller1.getFinalResult();

            StepVerifier.create(client.getDocumentModelWithResponse(createdModel.getModelId()))
                .assertNext(documentModelResponse -> {
                    assertEquals(documentModelResponse.getStatusCode(), HttpResponseStatus.OK.code());
                    validateDocumentModelData(documentModelResponse.getValue());
                });
            // TODO (alzimmer): This test needs to be recorded again as it was never verifying, therefore never
            //  subscribing to the reactive API call.
            //                .expectComplete()
            //                .verify(DEFAULT_TIMEOUT);
        });
    }

    /**
     * Test for listing all operations' information.
     */
    @Test
    @Disabled
    public void listOperations() {

        List<String> operationIdList = new ArrayList<>();
        StepVerifier.create(client.listOperations().byPage().take(4))
            .thenConsumeWhile(modelOperationInfoPagedResponse -> {
                modelOperationInfoPagedResponse.getValue().forEach(modelOperationInfo -> {
                    operationIdList.add(modelOperationInfo.getOperationId());
                    assertTrue(modelOperationInfo.getOperationId() != null
                        && modelOperationInfo.getStatus() != null
                        && modelOperationInfo.getCreatedOn() != null
                        && modelOperationInfo.getLastUpdatedOn() != null
                        && modelOperationInfo.getResourceLocation() != null
                        && modelOperationInfo.getPercentCompleted() != null);
                });
                return true;
            })
            .expectComplete()
            .verify(DEFAULT_TIMEOUT);

        if (!CoreUtils.isNullOrEmpty(operationIdList)) {
            operationIdList.forEach(
                operationId -> StepVerifier.create(client.getOperation(operationId)).assertNext(operationDetails -> {
                    assertNotNull(operationDetails.getOperationId());
                    assertNotNull(operationDetails.getCreatedOn());
                    if (operationDetails instanceof DocumentModelBuildOperationDetails) {
                        assertNotNull(((DocumentModelBuildOperationDetails) operationDetails).getResult());
                    } else if (operationDetails instanceof DocumentModelComposeOperationDetails) {
                        assertNotNull(((DocumentModelComposeOperationDetails) operationDetails).getResult());
                    } else if (operationDetails instanceof DocumentModelCopyToOperationDetails) {
                        assertNotNull(((DocumentModelCopyToOperationDetails) operationDetails).getResult());
                    }
                }).expectComplete().verify(DEFAULT_TIMEOUT));
        }
    }

    @RecordWithoutRequestBody
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41027")
    public void beginBuildClassifier() {
        beginClassifierRunner((trainingFilesUrl) -> {
            Map<String, ClassifierDocumentTypeDetails> documentTypeDetailsMap = new HashMap<>();
            documentTypeDetailsMap.put("IRS-1040-A", new ClassifierDocumentTypeDetails(
                new BlobContentSource(trainingFilesUrl).setPrefix("IRS-1040-A/train")));
            documentTypeDetailsMap.put("IRS-1040-B", new ClassifierDocumentTypeDetails(
                new BlobContentSource(trainingFilesUrl).setPrefix("IRS-1040-B/train")));
            documentTypeDetailsMap.put("IRS-1040-C", new ClassifierDocumentTypeDetails(
                new BlobContentSource(trainingFilesUrl).setPrefix("IRS-1040-C/train")));
            documentTypeDetailsMap.put("IRS-1040-D", new ClassifierDocumentTypeDetails(
                new BlobContentSource(trainingFilesUrl).setPrefix("IRS-1040-D/train")));
            documentTypeDetailsMap.put("IRS-1040-E", new ClassifierDocumentTypeDetails(
                new BlobContentSource(trainingFilesUrl).setPrefix("IRS-1040-E/train")));
            SyncPoller<OperationResult, DocumentClassifierDetails> buildModelPoller
                = client.beginBuildDocumentClassifier(documentTypeDetailsMap)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();

            DocumentClassifierDetails documentClassifierDetails = buildModelPoller.getFinalResult();
            validateClassifierModelData(buildModelPoller.getFinalResult());
            assertNotNull(documentClassifierDetails.getDocumentTypes());
            documentClassifierDetails.getDocumentTypes()
                .forEach((s, classifierDocumentTypeDetails) -> assertNotNull(
                    ((BlobContentSource) classifierDocumentTypeDetails.getContentSource()).getContainerUrl()));
        });
    }

    /**
     * Verifies the result of the training operation for a classifier with a valid training data set with jsonL files.
     */
    @RecordWithoutRequestBody
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41027")
    public void beginBuildClassifierWithJsonL() {
        beginClassifierRunner((trainingFilesUrl) -> {
            Map<String, ClassifierDocumentTypeDetails> documentTypeDetailsMap = new HashMap<>();
            documentTypeDetailsMap.put("IRS-1040-A",
                new ClassifierDocumentTypeDetails(new BlobFileListContentSource(trainingFilesUrl, "IRS-1040-A.jsonl")));
            documentTypeDetailsMap.put("IRS-1040-B",
                new ClassifierDocumentTypeDetails(new BlobFileListContentSource(trainingFilesUrl, "IRS-1040-B.jsonl")));
            documentTypeDetailsMap.put("IRS-1040-C",
                new ClassifierDocumentTypeDetails(new BlobFileListContentSource(trainingFilesUrl, "IRS-1040-C.jsonl")));
            documentTypeDetailsMap.put("IRS-1040-D",
                new ClassifierDocumentTypeDetails(new BlobFileListContentSource(trainingFilesUrl, "IRS-1040-D.jsonl")));
            documentTypeDetailsMap.put("IRS-1040-E",
                new ClassifierDocumentTypeDetails(new BlobFileListContentSource(trainingFilesUrl, "IRS-1040-E.jsonl")));
            SyncPoller<OperationResult, DocumentClassifierDetails> buildModelPoller = client
                .beginBuildDocumentClassifier(documentTypeDetailsMap,
                    new BuildDocumentClassifierOptions().setDescription("Json L classifier model"))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            DocumentClassifierDetails documentClassifierDetails = buildModelPoller.getFinalResult();
            assertNotNull(documentClassifierDetails.getDocumentTypes());
            documentClassifierDetails.getDocumentTypes()
                .forEach((s, classifierDocumentTypeDetails) -> assertNotNull(
                    ((BlobFileListContentSource) classifierDocumentTypeDetails.getContentSource()).getContainerUrl()));

            validateClassifierModelData(buildModelPoller.getFinalResult());
        });
    }
}
