// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.formrecognizer;

import com.azure.ai.formrecognizer.models.CreateComposedModelOptions;
import com.azure.ai.formrecognizer.models.FormContentType;
import com.azure.ai.formrecognizer.models.FormPage;
import com.azure.ai.formrecognizer.models.FormRecognizerErrorInformation;
import com.azure.ai.formrecognizer.models.FormRecognizerException;
import com.azure.ai.formrecognizer.models.FormRecognizerOperationResult;
import com.azure.ai.formrecognizer.models.RecognizeContentOptions;
import com.azure.ai.formrecognizer.training.FormTrainingClient;
import com.azure.ai.formrecognizer.training.models.AccountProperties;
import com.azure.ai.formrecognizer.training.models.CopyAuthorization;
import com.azure.ai.formrecognizer.training.models.CustomFormModel;
import com.azure.ai.formrecognizer.training.models.CustomFormModelInfo;
import com.azure.ai.formrecognizer.training.models.CustomFormModelStatus;
import com.azure.ai.formrecognizer.training.models.TrainingFileFilter;
import com.azure.ai.formrecognizer.training.models.TrainingOptions;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpClient;
import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.core.util.polling.SyncPoller;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;

import static com.azure.ai.formrecognizer.FormRecognizerClientTestBase.MODEL_ID_NOT_FOUND_ERROR_CODE;
import static com.azure.ai.formrecognizer.TestUtils.DISPLAY_NAME_WITH_ARGUMENTS;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ParameterizedClass(name = DISPLAY_NAME_WITH_ARGUMENTS)
@MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
public class FormTrainingClientTest extends FormTrainingClientTestBase {
    private final HttpClient httpClient;
    private final FormRecognizerServiceVersion serviceVersion;

    private FormTrainingClient client;

    public FormTrainingClientTest(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        this.httpClient = httpClient;
        this.serviceVersion = serviceVersion;
    }

    @BeforeEach
    public void createClient() {
        this.client = getFormTrainingClient();
    }

    private FormTrainingClient getFormTrainingClient() {
        return getFormTrainingClientBuilder(httpClient, serviceVersion).buildClient();
    }

    /**
     * Verifies the form recognizer client is valid.
     */
    @Test
    public void getFormRecognizerClientAndValidate() {
        FormRecognizerClient formRecognizerClient = this.client.getFormRecognizerClient();
        blankPdfDataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = formRecognizerClient.beginRecognizeContent(data, dataLength,
                    new RecognizeContentOptions().setContentType(FormContentType.APPLICATION_PDF)
                        .setPollInterval(durationTestMode),
                    Context.NONE);
            syncPoller.waitForCompletion();
            assertNotNull(syncPoller.getFinalResult());
        });
    }

    /**
     * Verifies custom model info returned with response for a valid model Id.
     */
    @Test
    public void getCustomModelWithResponse() {
        beginTrainingUnlabeledRunner((trainingDataSasUrl, useTrainingLabels) -> {
            CustomFormModel trainedUnlabeledModel
                = client
                    .beginTraining(trainingDataSasUrl, useTrainingLabels,
                        new TrainingOptions().setPollInterval(durationTestMode), Context.NONE)
                    .getFinalResult();
            Response<CustomFormModel> customModelWithResponse
                = client.getCustomModelWithResponse(trainedUnlabeledModel.getModelId(), Context.NONE);
            assertEquals(customModelWithResponse.getStatusCode(), HttpResponseStatus.OK.code());
            validateCustomModelData(customModelWithResponse.getValue(), false, false);
        });
    }

    /**
     * Verifies unlabeled custom model info returned with response for a valid model Id.
     */
    @Test
    public void getCustomModelUnlabeled() {
        beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            CustomFormModel trainedUnlabeledModel = syncPoller.getFinalResult();
            validateCustomModelData(client.getCustomModel(trainedUnlabeledModel.getModelId()), false, false);
        });
    }

    /**
     * Verifies labeled custom model info returned with response for a valid model Id.
     */
    @Test
    public void getCustomModelLabeled() {
        beginTrainingLabeledRunner((trainingDataSASUrl, useTrainingLabels) -> {
            CustomFormModel customFormModel
                = client
                    .beginTraining(trainingDataSASUrl, useTrainingLabels,
                        new TrainingOptions().setPollInterval(durationTestMode), Context.NONE)
                    .getFinalResult();
            validateCustomModelData(client.getCustomModel(customFormModel.getModelId()), true, false);
        });
    }

    /**
     * Verifies account properties returned for a subscription account.
     */
    @Test
    @Disabled
    public void validGetAccountProperties() {
        // TODO (service bug): APIM error
        validateAccountProperties(client.getAccountProperties());
    }

    /**
     * Verifies account properties returned with an Http Response for a subscription account.
     */
    @Test
    @Disabled
    public void validGetAccountPropertiesWithResponse() {
        // TODO (service bug): APIM error
        Response<AccountProperties> accountPropertiesResponse = client.getAccountPropertiesWithResponse(Context.NONE);
        assertEquals(accountPropertiesResponse.getStatusCode(), HttpResponseStatus.OK.code());
        validateAccountProperties(accountPropertiesResponse.getValue());
    }

    @Test
    public void deleteModelValidModelIdWithResponse() {
        beginTrainingLabeledRunner((trainingDataSASUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = client.beginTraining(trainingDataSASUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();

            final Response<Void> deleteModelWithResponse
                = client.deleteModelWithResponse(createdModel.getModelId(), Context.NONE);
            assertEquals(deleteModelWithResponse.getStatusCode(), HttpResponseStatus.NO_CONTENT.code());
            final HttpResponseException exception = assertThrows(HttpResponseException.class,
                () -> client.getCustomModelWithResponse(createdModel.getModelId(), Context.NONE));
            final FormRecognizerErrorInformation errorInformation
                = (FormRecognizerErrorInformation) exception.getValue();
            assertEquals(MODEL_ID_NOT_FOUND_ERROR_CODE, errorInformation.getErrorCode());
        });
    }

    @Test
    public void deleteModelValidModelIdWithResponseWithoutTrainingLabels() {
        beginTrainingUnlabeledRunner((trainingDataSASUrl, notUseTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = client.beginTraining(trainingDataSASUrl, notUseTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();

            final Response<Void> deleteModelWithResponse
                = client.deleteModelWithResponse(createdModel.getModelId(), Context.NONE);
            assertEquals(deleteModelWithResponse.getStatusCode(), HttpResponseStatus.NO_CONTENT.code());
            final HttpResponseException exception = assertThrows(HttpResponseException.class,
                () -> client.getCustomModelWithResponse(createdModel.getModelId(), Context.NONE));
            final FormRecognizerErrorInformation errorInformation
                = (FormRecognizerErrorInformation) exception.getValue();
            assertEquals(MODEL_ID_NOT_FOUND_ERROR_CODE, errorInformation.getErrorCode());
        });
    }

    /**
     * Test for listing all models information.
     */
    @Test
    @Disabled
    public void listCustomModels() {
        // TODO (service bug): APIM error
        for (CustomFormModelInfo modelInfo : client.listCustomModels()) {
            assertTrue(modelInfo.getModelId() != null
                && modelInfo.getTrainingStartedOn() != null
                && modelInfo.getTrainingCompletedOn() != null
                && modelInfo.getStatus() != null);
        }
    }

    /**
     * Test for listing all models information with {@link Context}.
     */
    @Test
    @Disabled
    public void listCustomModelsWithContext() {
        // TODO (service bug): APIM error
        for (CustomFormModelInfo modelInfo : client.listCustomModels(Context.NONE)) {
            assertTrue(modelInfo.getModelId() != null
                && modelInfo.getTrainingStartedOn() != null
                && modelInfo.getTrainingCompletedOn() != null
                && modelInfo.getStatus() != null);
        }
    }

    /**
     * Verifies the result of the copy operation for valid parameters.
     */
    @Test
    public void beginCopy() {
        beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            CustomFormModel actualModel = syncPoller.getFinalResult();

            beginCopyRunner((resourceId, resourceRegion) -> {
                CopyAuthorization target = client.getCopyAuthorization(resourceId, resourceRegion);
                SyncPoller<FormRecognizerOperationResult, CustomFormModelInfo> copyPoller
                    = client.beginCopyModel(actualModel.getModelId(), target, durationTestMode, Context.NONE);
                CustomFormModelInfo copyModel = copyPoller.getFinalResult();
                assertEquals(target.getModelId(), copyModel.getModelId());
                assertNotNull(actualModel.getTrainingStartedOn());
                assertNotNull(actualModel.getTrainingCompletedOn());
                assertEquals(CustomFormModelStatus.READY, copyModel.getStatus());
            });
        });
    }

    /**
     * Verifies the Invalid region HttpResponseException is thrown for invalid region input to copy operation.
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void beginCopyInvalidRegion() {
        beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            final CustomFormModel actualModel = syncPoller.getFinalResult();

            beginCopyInvalidRegionRunner((resourceId, resourceRegion) -> {
                final CopyAuthorization target = client.getCopyAuthorization(resourceId, resourceRegion);
                final HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                    () -> client.beginCopyModel(actualModel.getModelId(), target));
                final FormRecognizerErrorInformation errorInformation
                    = (FormRecognizerErrorInformation) httpResponseException.getValue();
                assertEquals(COPY_REQUEST_INVALID_TARGET_RESOURCE_REGION_ERROR_CODE, errorInformation.getErrorCode());
            });
        });
    }

    /**
     * Verifies {@link FormRecognizerException} is thrown for invalid region input to copy operation.
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void beginCopyIncorrectRegion() {
        beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            CustomFormModel actualModel = syncPoller.getFinalResult();

            beginCopyIncorrectRegionRunner((resourceId, resourceRegion) -> {
                final CopyAuthorization target = client.getCopyAuthorization(resourceId, resourceRegion);
                FormRecognizerException formRecognizerException = assertThrows(FormRecognizerException.class,
                    () -> client.beginCopyModel(actualModel.getModelId(), target, durationTestMode, Context.NONE)
                        .getFinalResult());
                assertTrue(formRecognizerException.getMessage()
                    .startsWith(FormRecognizerClientTestBase.COPY_OPERATION_FAILED_STATUS_MESSAGE));
            });
        });
    }

    /**
     * Verifies the result of the copy authorization for valid parameters.
     */
    @Test
    public void copyAuthorization() {
        beginCopyRunner((resourceId, resourceRegion) -> validateCopyAuthorizationResult(resourceId, resourceRegion,
            client.getCopyAuthorization(resourceId, resourceRegion)));
    }

    /**
     * Verifies the training operation throws FormRecognizerException when an invalid status model is returned.
     */
    @Test
    @Disabled
    public void beginTrainingInvalidModelStatus() {
        beginTrainingInvalidModelStatusRunner((invalidTrainingFilesUrl, useTrainingLabels) -> {
            FormRecognizerException formRecognizerException = assertThrows(FormRecognizerException.class,
                () -> client
                    .beginTraining(invalidTrainingFilesUrl, useTrainingLabels,
                        new TrainingOptions().setPollInterval(durationTestMode), Context.NONE)
                    .getFinalResult());
            FormRecognizerErrorInformation errorInformation = formRecognizerException.getErrorInformation().get(0);
            assertEquals(INVALID_MODEL_STATUS_ERROR_CODE, errorInformation.getErrorCode());
        });
    }

    /**
     * Verifies the result of the training operation for a valid labeled model Id and JPG training set Url.
     */
    @Test
    public void beginTrainingWithTrainingLabelsForJPGTrainingSet() {
        beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();
            validateCustomModelData(trainingPoller.getFinalResult(), true, false);
        });
    }

    /**
     * Verifies the result of the training operation for a valid unlabeled model Id and JPG training set Url.
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void beginTrainingWithoutTrainingLabelsForJPGTrainingSet() {
        beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();
            validateCustomModelData(trainingPoller.getFinalResult(), false, false);
        });
    }

    /**
     * Verifies the result of the training operation for a valid labeled model Id and multi-page PDF training set Url.
     */
    @Test
    public void beginTrainingWithTrainingLabelsForMultiPagePDFTrainingSet() {
        beginTrainingMultipageRunner(trainingFilesUrl -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller = client.beginTraining(
                trainingFilesUrl, true, new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();
            validateCustomModelData(trainingPoller.getFinalResult(), true, false);
        });
    }

    /**
     * Verifies the result of the training operation for a valid unlabeled model Id and multi-page PDF training set Url.
     */
    @Test
    public void beginTrainingWithoutTrainingLabelsForMultiPagePDFTrainingSet() {
        beginTrainingMultipageRunner(trainingFilesUrl -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller = client.beginTraining(
                trainingFilesUrl, false, new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();
            validateCustomModelData(trainingPoller.getFinalResult(), false, false);
        });
    }

    /**
     * Verifies the result of the training operation for a valid unlabeled model Id and include subfolder training set
     * Url with existing prefix name.
     */
    @Test
    public void beginTrainingWithoutTrainingLabelsIncludeSubfolderWithPrefixName() {
        beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();
            validateCustomModelData(trainingPoller.getFinalResult(), false, false);
        });
    }

    /**
     * Verifies the result of the training operation for a valid unlabeled model ID and exclude subfolder training set
     * URL with existing prefix name.
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void beginTrainingWithoutTrainingLabelsExcludeSubfolderWithPrefixName() {
        beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();
            validateCustomModelData(trainingPoller.getFinalResult(), false, false);
        });
    }

    /**
     * Verifies the result of the training operation for a valid unlabeled model Id and exclude subfolder training set
     * Url with non-existing prefix name.
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void beginTrainingWithoutTrainingLabelsExcludeSubfolderWithNonExistPrefixName() {
        beginTrainingMultipageRunner(trainingFilesUrl -> {
            FormRecognizerException thrown = assertThrows(FormRecognizerException.class,
                () -> client
                    .beginTraining(trainingFilesUrl, false,
                        new TrainingOptions()
                            .setTrainingFileFilter(new TrainingFileFilter().setPrefix(INVALID_PREFIX_FILE_NAME))
                            .setPollInterval(durationTestMode),
                        Context.NONE)
                    .getFinalResult());
            assertEquals(NO_VALID_BLOB_FOUND_ERROR_CODE, thrown.getErrorInformation().get(0).getErrorCode());
        });
    }

    /**
     * Verifies the result of the create composed model for valid parameters.
     */
    @Test
    public void beginCreateComposedModel() {
        beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller1
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller1.waitForCompletion();
            CustomFormModel model1 = syncPoller1.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller2
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller2.waitForCompletion();
            CustomFormModel model2 = syncPoller2.getFinalResult();

            final List<String> modelIdList = Arrays.asList(model1.getModelId(), model2.getModelId());

            CustomFormModel composedModel
                = client.beginCreateComposedModel(modelIdList, new CreateComposedModelOptions(), Context.NONE)
                    .setPollInterval(durationTestMode)
                    .getFinalResult();

            assertNotNull(composedModel.getModelId());
            assertNotNull(composedModel.getCustomModelProperties());
            assertTrue(composedModel.getCustomModelProperties().isComposed());
            assertEquals(2, (long) composedModel.getSubmodels().size());
            composedModel.getSubmodels()
                .forEach(customFormSubmodel -> assertTrue(modelIdList.contains(customFormSubmodel.getModelId())));
            validateCustomModelData(composedModel, false, true);

            client.deleteModel(model1.getModelId());
            client.deleteModel(model2.getModelId());
            client.deleteModel(composedModel.getModelId());
        });
    }

    /**
     * Verifies the result of the create composed model for valid parameters with options.
     */
    @Test
    public void beginCreateComposedModelWithOptions() {
        beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller1
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller1.waitForCompletion();
            CustomFormModel model1 = syncPoller1.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller2
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller2.waitForCompletion();
            CustomFormModel model2 = syncPoller2.getFinalResult();

            final List<String> modelIdList = Arrays.asList(model1.getModelId(), model2.getModelId());

            CustomFormModel composedModel = client
                .beginCreateComposedModel(modelIdList,
                    new CreateComposedModelOptions().setModelName("composedModelDisplayName"), Context.NONE)
                .setPollInterval(durationTestMode)
                .getFinalResult();

            client.deleteModel(model1.getModelId());
            client.deleteModel(model2.getModelId());
            client.deleteModel(composedModel.getModelId());

            assertNotNull(composedModel.getModelId());
            assertNotNull(composedModel.getCustomModelProperties());
            assertTrue(composedModel.getCustomModelProperties().isComposed());
            assertEquals("composedModelDisplayName", composedModel.getModelName());
            assertEquals(2, (long) composedModel.getSubmodels().size());
            composedModel.getSubmodels()
                .forEach(customFormSubmodel -> assertTrue(modelIdList.contains(customFormSubmodel.getModelId())));
            validateCustomModelData(composedModel, false, true);
        });
    }

    /**
     * Verifies the create composed model using unlabeled models fails.
     */
    @Test
    public void beginCreateComposedUnlabeledModel() {
        beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller1
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller1.waitForCompletion();
            CustomFormModel model1 = syncPoller1.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller2
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller2.waitForCompletion();
            CustomFormModel model2 = syncPoller2.getFinalResult();

            final List<String> modelIdList = Arrays.asList(model1.getModelId(), model2.getModelId());

            final HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> client.beginCreateComposedModel(modelIdList, new CreateComposedModelOptions(), Context.NONE)
                    .setPollInterval(durationTestMode));
            assertEquals(BAD_REQUEST.code(), httpResponseException.getResponse().getStatusCode());

            client.deleteModel(model1.getModelId());
            client.deleteModel(model2.getModelId());
        });
    }

    /**
     * Verifies the create composed model operation fails when supplied duplicate Ids.
     */
    @Test
    public void beginCreateComposedDuplicateModels() {
        beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller1
                = client.beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller1.waitForCompletion();
            CustomFormModel model1 = syncPoller1.getFinalResult();

            final List<String> modelIdList = Arrays.asList(model1.getModelId(), model1.getModelId());
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> client.beginCreateComposedModel(modelIdList, new CreateComposedModelOptions(), Context.NONE)
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            assertEquals(BAD_REQUEST.code(), httpResponseException.getResponse().getStatusCode());

            client.deleteModel(model1.getModelId());
        });
    }

    // APIM bug - 8404889
    // /**
    //  * Verifies the composed model attributes are returned when listing models.
    //  */
    // @Test
    // public void listComposedModels() {
    //         //     beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
    //         SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller1
    //             = client.beginTraining(trainingFilesUrl,
    //             useTrainingLabels,
    //             new TrainingOptions()
    //                 .setPollInterval(durationTestMode), Context.NONE);
    //         syncPoller1.waitForCompletion();
    //         CustomFormModel model1 = syncPoller1.getFinalResult();
    //
    //         SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller2
    //             = client.beginTraining(trainingFilesUrl,
    //             useTrainingLabels,
    //             new TrainingOptions()
    //                 .setPollInterval(durationTestMode), Context.NONE);
    //         syncPoller2.waitForCompletion();
    //         CustomFormModel model2 = syncPoller2.getFinalResult();
    //
    //         final List<String> modelIdList = Arrays.asList(model1.getModelId(), model2.getModelId());
    //
    //         CustomFormModel composedModel
    //             = client.beginCreateComposedModel(
    //             modelIdList,
    //             new CreateComposedModelOptions()
    //                 .setModelDisplayName("composedModelDisplayName")
    //                 .setPollInterval(durationTestMode), Context.NONE)
    //             .getFinalResult();
    //
    //         client.listCustomModels()
    //             .stream()
    //             .filter(customFormModelInfo ->
    //                 Objects.equals(composedModel.getModelId(), customFormModelInfo.getModelId()))
    //             .forEach(customFormModelInfo -> {
    //                 assertEquals("composedModelDisplayName", customFormModelInfo.getModelDisplayName());
    //                 assertTrue(customFormModelInfo.getCustomModelProperties().isComposed());
    //             });
    //
    //         client.deleteModel(model1.getModelId());
    //         client.deleteModel(model2.getModelId());
    //         client.deleteModel(composedModel.getModelId());
    //     });
    // }

    /**
     * Verifies the result contains the user defined model display name for labeled model.
     */
    @Test
    public void beginTrainingLabeledModelDisplayName() {
        beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller = client.beginTraining(
                trainingFilesUrl, useTrainingLabels,
                new TrainingOptions().setPollInterval(durationTestMode).setModelName("modelDisplayName"), Context.NONE);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();

            CustomFormModel customFormModel = client.getCustomModel(createdModel.getModelId());
            assertEquals("modelDisplayName", customFormModel.getModelName());

            validateCustomModelData(createdModel, true, false);
        });
    }
}
