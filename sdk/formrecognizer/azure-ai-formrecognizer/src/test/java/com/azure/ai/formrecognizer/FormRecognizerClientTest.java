// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.formrecognizer;

import com.azure.ai.formrecognizer.models.CreateComposedModelOptions;
import com.azure.ai.formrecognizer.models.FormContentType;
import com.azure.ai.formrecognizer.models.FormField;
import com.azure.ai.formrecognizer.models.FormPage;
import com.azure.ai.formrecognizer.models.FormRecognizerErrorInformation;
import com.azure.ai.formrecognizer.models.FormRecognizerLanguage;
import com.azure.ai.formrecognizer.models.FormRecognizerLocale;
import com.azure.ai.formrecognizer.models.FormRecognizerOperationResult;
import com.azure.ai.formrecognizer.models.RecognizeBusinessCardsOptions;
import com.azure.ai.formrecognizer.models.RecognizeContentOptions;
import com.azure.ai.formrecognizer.models.RecognizeCustomFormsOptions;
import com.azure.ai.formrecognizer.models.RecognizeIdentityDocumentOptions;
import com.azure.ai.formrecognizer.models.RecognizeInvoicesOptions;
import com.azure.ai.formrecognizer.models.RecognizeReceiptsOptions;
import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.azure.ai.formrecognizer.training.FormTrainingClient;
import com.azure.ai.formrecognizer.training.models.CustomFormModel;
import com.azure.ai.formrecognizer.training.models.CustomFormSubmodel;
import com.azure.ai.formrecognizer.training.models.TrainingOptions;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpClient;
import com.azure.core.util.Context;
import com.azure.core.util.polling.SyncPoller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.azure.ai.formrecognizer.TestUtils.BLANK_PDF;
import static com.azure.ai.formrecognizer.TestUtils.CONTENT_FORM_JPG;
import static com.azure.ai.formrecognizer.TestUtils.CONTENT_GERMAN_PDF;
import static com.azure.ai.formrecognizer.TestUtils.DISPLAY_NAME_WITH_ARGUMENTS;
import static com.azure.ai.formrecognizer.TestUtils.INVALID_URL;
import static com.azure.ai.formrecognizer.TestUtils.NON_EXIST_MODEL_ID;
import static com.azure.ai.formrecognizer.TestUtils.SELECTION_MARK_PDF;
import static com.azure.ai.formrecognizer.TestUtils.getContentDetectionFileData;
import static com.azure.ai.formrecognizer.TestUtils.validateExceptionSource;
import static com.azure.ai.formrecognizer.models.FormContentType.APPLICATION_PDF;
import static com.azure.ai.formrecognizer.models.FormContentType.IMAGE_JPEG;
import static com.azure.ai.formrecognizer.models.FormContentType.IMAGE_PNG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ParameterizedClass(name = DISPLAY_NAME_WITH_ARGUMENTS)
@MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
public class FormRecognizerClientTest extends FormRecognizerClientTestBase {

    private final HttpClient httpClient;
    private final FormRecognizerServiceVersion serviceVersion;

    private FormRecognizerClient recognizerClient;
    private FormTrainingClient trainingClient;

    public FormRecognizerClientTest(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        this.httpClient = httpClient;
        this.serviceVersion = serviceVersion;
    }

    @BeforeEach
    public void createClient() {
        this.recognizerClient = getFormRecognizerClient();
        this.trainingClient = getFormTrainingClient();
    }

    private FormRecognizerClient getFormRecognizerClient() {
        return getFormRecognizerClientBuilder(httpClient, serviceVersion).buildClient();
    }

    private FormTrainingClient getFormTrainingClient() {
        return getFormTrainingClientBuilder(httpClient, serviceVersion).buildClient();
    }

    // Receipt recognition

    // Receipt - non-URL

    /**
     * Verifies receipt data for a document using source as input stream data.
     */
    @Test
    public void recognizeReceiptData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeReceipts(data, dataLength,
                    new RecognizeReceiptsOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), false, FormContentType.IMAGE_JPEG);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies content type will be auto detected when using receipt API with input stream data overload.
     */
    @Test
    public void recognizeReceiptDataWithContentTypeAutoDetection() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeReceipts(getContentDetectionFileData(filePath), dataLength,
                        new RecognizeReceiptsOptions(), Context.NONE)
                    .setPollInterval(durationTestMode);

            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), false, FormContentType.IMAGE_JPEG);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies receipt data for a document using source as as input stream data and text content when
     * includeFieldElements is true.
     */
    @Test
    public void recognizeReceiptDataIncludeFieldElements() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeReceipts(data, dataLength,
                        new RecognizeReceiptsOptions().setContentType(FormContentType.IMAGE_JPEG)
                            .setFieldElementsIncluded(true),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), true, FormContentType.IMAGE_JPEG);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies receipt data from a document using PNG file data as source and including text content details.
     */
    @Test
    public void recognizeReceiptDataWithPngFile() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeReceipts(data, dataLength,
                        new RecognizeReceiptsOptions().setContentType(FormContentType.IMAGE_PNG)
                            .setFieldElementsIncluded(true),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), true, FormContentType.IMAGE_PNG);
        }, RECEIPT_CONTOSO_PNG);
    }

    /**
     * Verifies receipt data from a document using blank PDF.
     */
    @Test
    public void recognizeReceiptDataWithBlankPdf() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeReceipts(data, dataLength,
                    new RecognizeReceiptsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    @Test
    public void recognizeReceiptFromDataMultiPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeReceipts(data, dataLength,
                    new RecognizeReceiptsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateMultipageReceiptData(syncPoller.getFinalResult());
        }, MULTIPAGE_RECEIPT_PDF);
    }

    /**
     * Verify that receipt recognition with damaged PDF file.
     */
    @Test
    public void recognizeReceiptFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient
                    .beginRecognizeReceipts(data, dataLength,
                        new RecognizeReceiptsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            FormRecognizerErrorInformation errorInformation
                = (FormRecognizerErrorInformation) httpResponseException.getValue();
            assertEquals(BAD_ARGUMENT_CODE, errorInformation.getErrorCode());
        });
    }

    // Receipt - URL

    /**
     * Verifies receipt data for a document using source as file url.
     */
    @Test
    public void recognizeReceiptSourceUrl() {
        urlRunner((sourceUrl) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient.beginRecognizeReceiptsFromUrl(sourceUrl).setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), false, FormContentType.IMAGE_JPEG);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with
     * encoded blank space as input data to recognize receipt from url API.
     */
    @Test
    public void recognizeReceiptFromUrlWithEncodedBlankSpaceSourceUrl() {
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient.beginRecognizeReceiptsFromUrl(sourceUrl).setPollInterval(durationTestMode));
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void recognizeReceiptInvalidSourceUrl() {
        invalidSourceUrlRunner((sourceUrl) -> assertThrows(HttpResponseException.class,
            () -> this.recognizerClient.beginRecognizeReceiptsFromUrl(sourceUrl).setPollInterval(durationTestMode)));
    }

    /**
     * Verifies receipt data for a document using source as file url and include form element references
     * when includeFieldElements is true.
     */
    @Test
    public void recognizeReceiptFromUrlIncludeFieldElements() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeReceiptsFromUrl(sourceUrl,
                        new RecognizeReceiptsOptions().setFieldElementsIncluded(true), Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), true, IMAGE_JPEG);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies receipt data for a document using source as PNG file url and include form element references
     * when includeFieldElements is true.
     */
    @Test
    public void recognizeReceiptSourceUrlWithPngFile() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeReceiptsFromUrl(sourceUrl,
                        new RecognizeReceiptsOptions().setFieldElementsIncluded(true), Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), true, IMAGE_PNG);
        }, RECEIPT_CONTOSO_PNG);
    }

    @Test
    @Disabled
    public void recognizeReceiptFromUrlMultiPage() {
        // TODO: (https://github.com/Azure/azure-sdk-for-java/issues/20012)
        urlRunner(receiptUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient.beginRecognizeReceiptsFromUrl(receiptUrl).setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateMultipageReceiptData(syncPoller.getFinalResult());
        }, MULTIPAGE_RECEIPT_PDF);
    }

    /**
     * Verify locale parameter passed when specified by user.
     */
    @Test
    public void receiptValidLocale() {

        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> receiptPoller = this.recognizerClient
                .beginRecognizeReceipts(getContentDetectionFileData(filePath), dataLength,
                    new RecognizeReceiptsOptions().setLocale(FormRecognizerLocale.EN_US), Context.NONE)
                .setPollInterval(durationTestMode);
            validateReceiptData(receiptPoller.getFinalResult(), false, FormContentType.IMAGE_JPEG);

        }, RECEIPT_CONTOSO_JPG);
    }

    // Content Recognition

    // Content - non-URL

    /**
     * Verifies layout/content data for a document using source as input stream data.
     */
    @Test
    public void recognizeContent() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller = this.recognizerClient
                .beginRecognizeContent(data, dataLength,
                    new RecognizeContentOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, CONTENT_FORM_JPG);
    }

    /**
     * Verifies content type will be auto detected when using content/layout API with input stream data overload.
     */
    @Test
    public void recognizeContentResultWithContentTypeAutoDetection() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient
                    .beginRecognizeContent(getContentDetectionFileData(filePath), dataLength,
                        new RecognizeContentOptions().setContentType(null), Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, CONTENT_FORM_JPG);

    }

    /**
     * Verifies blank form file is still a valid file to process
     */
    @Test
    public void recognizeContentResultWithBlankPdf() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient
                    .beginRecognizeContent(data, dataLength,
                        new RecognizeContentOptions().setContentType(APPLICATION_PDF), Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, BLANK_PDF);
    }

    @Test
    public void recognizeContentFromDataMultiPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient
                    .beginRecognizeContent(data, dataLength,
                        new RecognizeContentOptions().setContentType(APPLICATION_PDF), Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, MULTIPAGE_INVOICE_PDF);
    }

    /**
     * Verify that content recognition with damaged PDF file.
     */
    @Test
    public void recognizeContentFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> assertThrows(HttpResponseException.class,
            () -> this.recognizerClient
                .beginRecognizeContent(data, dataLength, new RecognizeContentOptions().setContentType(APPLICATION_PDF),
                    Context.NONE)
                .setPollInterval(durationTestMode)
                .getFinalResult()));
    }

    @Test
    public void recognizeContentWithSelectionMarks() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient
                    .beginRecognizeContent(data, dataLength,
                        new RecognizeContentOptions().setContentType(APPLICATION_PDF), Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, SELECTION_MARK_PDF);
    }

    @Test
    public void recognizeContentWithPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient
                    .beginRecognizeContent(data, dataLength,
                        new RecognizeContentOptions().setContentType(APPLICATION_PDF)
                            .setPages(Collections.singletonList("1")),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            List<FormPage> formPages = syncPoller.getFinalResult();
            validateContentData(formPages, true);
            assertEquals(1, formPages.size());
        }, MULTIPAGE_INVOICE_PDF);
    }

    @Test
    public void recognizeContentWithPages() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient
                    .beginRecognizeContent(data, dataLength,
                        new RecognizeContentOptions().setContentType(APPLICATION_PDF).setPages(Arrays.asList("1", "2")),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            List<FormPage> formPages = syncPoller.getFinalResult();
            validateContentData(formPages, true);
            assertEquals(2, formPages.size());
        }, MULTIPAGE_INVOICE_PDF);
    }

    @Test
    public void recognizeContentWithPageRange() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient
                    .beginRecognizeContent(data, dataLength,
                        new RecognizeContentOptions().setContentType(APPLICATION_PDF)
                            .setPages(Arrays.asList("1-2", "3")),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            List<FormPage> formPages = syncPoller.getFinalResult();
            validateContentData(formPages, true);
            assertEquals(3, formPages.size());
        }, MULTIPAGE_INVOICE_PDF);
    }

    /**
     * Verifies layout data for a document using source as input stream data.
     */
    @Test
    public void recognizeContentAppearance() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller = this.recognizerClient
                .beginRecognizeContent(data, dataLength,
                    new RecognizeContentOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            List<FormPage> formPages = syncPoller.getFinalResult();
            validateContentData(formPages, true);
            assertNotNull(formPages.get(0).getLines().get(0).getAppearance().getStyleName());
        }, CONTENT_FORM_JPG);
    }

    // Content - URL

    @Test
    public void recognizeContentFromUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient.beginRecognizeContentFromUrl(sourceUrl).setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, CONTENT_FORM_JPG);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with
     * encoded blank space as input data to recognize a content from url API.
     */
    @Test
    public void recognizeContentFromUrlWithEncodedBlankSpaceSourceUrl() {
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient.beginRecognizeContentFromUrl(sourceUrl).setPollInterval(durationTestMode));
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies layout data for a pdf url
     */
    @Test
    public void recognizeContentFromUrlWithPdf() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient.beginRecognizeContentFromUrl(sourceUrl).setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, INVOICE_6_PDF);
    }

    /**
     * Verifies that an exception is thrown for invalid source url for recognizing content/layout information.
     */
    @Test
    public void recognizeContentInvalidSourceUrl() {
        invalidSourceUrlRunner((invalidSourceUrl) -> assertThrows(HttpResponseException.class,
            () -> this.recognizerClient.beginRecognizeContentFromUrl(invalidSourceUrl)
                .setPollInterval(durationTestMode)));
    }

    @Test
    public void recognizeContentFromUrlMultiPage() {
        urlRunner((formUrl) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient.beginRecognizeContentFromUrl(formUrl).setPollInterval(durationTestMode);

            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, MULTIPAGE_INVOICE_PDF);
    }

    @Test
    public void recognizeContentWithSelectionMarksFromUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient.beginRecognizeContentFromUrl(sourceUrl).setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, SELECTION_MARK_PDF);
    }

    @Test
    public void recognizeGermanContentFromUrl() {
        testingContainerUrlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller = this.recognizerClient
                .beginRecognizeContentFromUrl(sourceUrl,
                    new RecognizeContentOptions().setLanguage(FormRecognizerLanguage.DE), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, CONTENT_GERMAN_PDF);
    }

    @Test
    public void recognizeContentIncorrectLanguageFromUrl() {
        testingContainerUrlRunner(sourceUrl -> {
            HttpResponseException exception = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient
                    .beginRecognizeContentFromUrl(sourceUrl,
                        new RecognizeContentOptions().setLanguage(FormRecognizerLanguage.fromString("language")),
                        Context.NONE)
                    .setPollInterval(durationTestMode));

            assertEquals("NotSupportedLanguage",
                ((FormRecognizerErrorInformation) exception.getValue()).getErrorCode());
        }, CONTENT_GERMAN_PDF);
    }

    // Custom form recognition

    // Custom form - non-URL - labeled data

    /**
     * Verifies custom form data for a document using source as input stream data and valid labeled model Id.
     */
    @Test
    public void recognizeCustomFormLabeledData() {
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data, dataLength,
                        new RecognizeCustomFormsOptions().setContentType(IMAGE_JPEG).setFieldElementsIncluded(true),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), true, 1, true);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies custom form data for a JPG content type with labeled data
     */
    @Test
    public void recognizeCustomFormLabeledDataWithJpgContentType() {
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), false, 1, true);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies custom form data for a blank PDF content type with labeled data
     */
    @Test
    public void recognizeCustomFormLabeledDataWithBlankPdfContentType() {
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBlankCustomForm(syncPoller.getFinalResult(), 1, true);
        }), BLANK_PDF);
    }

    /**
     * Verifies custom form data for a document using source as input stream data and valid labeled model Id,
     * excluding field elements.
     */
    @Test
    public void recognizeCustomFormLabeledDataExcludeFieldElements() {
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), false, 1, true);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies an exception thrown for a document using null form data value.
     */
    @Test
    public void recognizeCustomFormLabeledDataWithNullFormData() {
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();

            assertThrows(RuntimeException.class,
                () -> this.recognizerClient
                    .beginRecognizeCustomForms(syncPoller.getFinalResult().getModelId(), (InputStream) null, dataLength,
                        new RecognizeCustomFormsOptions().setContentType(APPLICATION_PDF)
                            .setFieldElementsIncluded(true),
                        Context.NONE)
                    .setPollInterval(durationTestMode));
        }), INVOICE_6_PDF);
    }

    @Test
    public void recognizeCustomFormInvalidStatus() {
        invalidSourceUrlRunner((invalidSourceUrl) -> beginTrainingLabeledRunner((training, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = this.trainingClient.beginTraining(training, useTrainingLabels).setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient
                    .beginRecognizeCustomFormsFromUrl(createdModel.getModelId(), invalidSourceUrl)
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            FormRecognizerErrorInformation errorInformation
                = (FormRecognizerErrorInformation) httpResponseException.getValue();
            assertEquals(INVALID_SOURCE_URL_EXCEPTION_MESSAGE, errorInformation.getMessage());
        }));
    }

    /**
     * Verifies content type will be auto detected when using custom form API with input stream data overload.
     */
    @Test
    public void recognizeCustomFormLabeledDataWithContentTypeAutoDetection() {
        localFilePathRunner(
            (filePath, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                    = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                        .setPollInterval(durationTestMode);
                trainingPoller.waitForCompletion();
                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                    .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(),
                        getContentDetectionFileData(filePath), dataLength,
                        new RecognizeCustomFormsOptions().setFieldElementsIncluded(true), Context.NONE)
                    .setPollInterval(durationTestMode);
                syncPoller.waitForCompletion();
                validateJpegCustomForm(syncPoller.getFinalResult(), true, 1, true);
            }), CONTENT_FORM_JPG);
    }

    @Test
    public void recognizeCustomFormMultiPageLabeled() {
        dataRunner((data, dataLength) -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, true).setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();
            String modelId = trainingPoller.getFinalResult().getModelId();
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(modelId, data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateMultiPageDataLabeled(syncPoller.getFinalResult(), modelId);
        }), MULTIPAGE_INVOICE_PDF);
    }

    @Test
    public void recognizeCustomFormLabeledDataWithSelectionMark() {
        dataRunner(
            (data, dataLength) -> beginSelectionMarkTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                    = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                        .setPollInterval(durationTestMode);
                trainingPoller.waitForCompletion();

                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                    = this.recognizerClient
                        .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data, dataLength,
                            new RecognizeCustomFormsOptions().setContentType(APPLICATION_PDF)
                                .setFieldElementsIncluded(true),
                            Context.NONE)
                        .setPollInterval(durationTestMode);
                syncPoller.waitForCompletion();
                validateCustomFormWithSelectionMarks(syncPoller.getFinalResult(), true, 1);
            }), SELECTION_MARK_PDF);
    }

    // Custom form - non-URL - unlabeled data

    /**
     * Verifies custom form data for a document using source as input stream data and valid labeled model Id.
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormUnlabeledData() {
        dataRunner((data, dataLength) -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateUnlabeledCustomForm(syncPoller.getFinalResult(), false, 1);
        }), INVOICE_6_PDF);
    }

    /**
     * Verifies custom form data for a document using source as input stream data and valid include element references
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormUnlabeledDataIncludeFieldElements() {

        dataRunner((data, dataLength) -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data, dataLength,
                        new RecognizeCustomFormsOptions().setContentType(APPLICATION_PDF)
                            .setFieldElementsIncluded(true),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateUnlabeledCustomForm(syncPoller.getFinalResult(), true, 1);
        }), INVOICE_6_PDF);
    }

    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormMultiPageUnlabeled() {
        dataRunner((data, dataLength) -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, false).setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateMultiPageDataUnlabeled(syncPoller.getFinalResult());
        }), MULTIPAGE_INVOICE_PDF);
    }

    /**
     * Verifies custom form data for a JPG content type with unlabeled data
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormUnlabeledDataWithJpgContentType() {
        dataRunner((data, dataLength) -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, false).setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), false, 1, false);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies custom form data for a blank PDF content type with unlabeled data
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormUnlabeledDataWithBlankPdfContentType() {
        dataRunner((data, dataLength) -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBlankCustomForm(syncPoller.getFinalResult(), 1, false);
        }), BLANK_PDF);
    }

    // Custom form - URL - unlabeled data

    /**
     * Verifies custom form data for an URL document data without labeled data
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormUrlUnlabeledData() {
        urlRunner(fileUrl -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), false, 1, false);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies custom form data for an URL document data without labeled data and include element references
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormUrlUnlabeledDataIncludeFieldElements() {
        urlRunner(fileUrl -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl,
                    new RecognizeCustomFormsOptions().setFieldElementsIncluded(true), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), true, 1, false);
        }), CONTENT_FORM_JPG);
    }

    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormUrlMultiPageUnlabeled() {
        testingContainerUrlRunner(fileUrl -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, false).setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateMultiPageDataUnlabeled(syncPoller.getFinalResult());
        }), MULTIPAGE_INVOICE_PDF);
    }

    // Custom form - URL - labeled data

    /**
     * Verifies that an exception is thrown for invalid training data source.
     */
    @Test
    public void recognizeCustomFormInvalidSourceUrl() {
        beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();
            assertThrows(HttpResponseException.class,
                () -> this.recognizerClient.beginRecognizeCustomFormsFromUrl(createdModel.getModelId(), INVALID_URL)
                    .getFinalResult());
        });
    }

    /**
     * Verifies custom form data for an URL document data with labeled data and include element references
     */
    @Test
    public void recognizeCustomFormUrlLabeledDataIncludeFieldElements() {

        urlRunner(fileUrl -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl,
                        new RecognizeCustomFormsOptions().setFieldElementsIncluded(true)
                            .setPollInterval(durationTestMode),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), true, 1, true);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies custom form data for an URL document data with labeled data
     */
    @Test
    public void recognizeCustomFormUrlLabeledData() {
        urlRunner(fileUrl -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {

            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), false, 1, true);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verify custom form for an URL of multi-page labeled data
     */
    @Test
    public void recognizeCustomFormUrlMultiPageLabeled() {
        urlRunner(fileUrl -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, true).setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();
            String modelId = trainingPoller.getFinalResult().getModelId();
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient.beginRecognizeCustomFormsFromUrl(modelId, fileUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateMultiPageDataLabeled(syncPoller.getFinalResult(), modelId);
        }), MULTIPAGE_INVOICE_PDF);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with \
     * encoded blank space as input data to recognize a custom form from url API.
     */
    @Test
    public void recognizeCustomFormFromUrlWithEncodedBlankSpaceSourceUrl() {
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient.beginRecognizeCustomFormsFromUrl(NON_EXIST_MODEL_ID, sourceUrl));
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verify that custom forom with invalid model id.
     */
    @Test
    public void recognizeCustomFormUrlNonExistModelId() {
        urlRunner(fileUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient.beginRecognizeCustomFormsFromUrl(NON_EXIST_MODEL_ID, fileUrl));
            FormRecognizerErrorInformation errorInformation
                = (FormRecognizerErrorInformation) errorResponseException.getValue();
            assertEquals(INVALID_MODEL_ID_ERROR_CODE, errorInformation.getErrorCode());
        }, CONTENT_FORM_JPG);
    }

    /**
     * Verify that custom form with damaged PDF file.
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormDamagedPdf() {
        damagedPdfDataRunner(
            (data, dataLength) -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                    = this.trainingClient.beginTraining(trainingFilesUrl, false).setPollInterval(durationTestMode);
                trainingPoller.waitForCompletion();

                HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                    () -> this.recognizerClient
                        .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data, dataLength,
                            new RecognizeCustomFormsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                        .setPollInterval(durationTestMode)
                        .getFinalResult());

                FormRecognizerErrorInformation errorInformation
                    = (FormRecognizerErrorInformation) httpResponseException.getValue();
                assertEquals("Invalid input file.", errorInformation.getMessage());
            }));
    }

    @Test
    public void recognizeCustomFormUrlLabeledDataWithSelectionMark() {
        urlRunner(fileUrl -> beginSelectionMarkTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {

            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl,
                    new RecognizeCustomFormsOptions().setFieldElementsIncluded(true), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateCustomFormWithSelectionMarks(syncPoller.getFinalResult(), true, 1);
        }), SELECTION_MARK_PDF);
    }

    /**
     * Verifies recognized form type when labeled model used for recognition and model name is provided by user.
     */
    @Test
    public void checkRecognizeFormTypeLabeledWithModelName() {
        final FormTrainingClient formTrainingClient = this.trainingClient;
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = formTrainingClient
                    .beginTraining(trainingFilesUrl, useTrainingLabels, new TrainingOptions().setModelName("model1"),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();

            FormRecognizerClient formRecognizerClient = this.trainingClient.getFormRecognizerClient();
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller1 = formRecognizerClient
                .beginRecognizeCustomForms(createdModel.getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller1.waitForCompletion();
            final RecognizedForm recognizedForm = syncPoller1.getFinalResult().stream().findFirst().get();
            assertEquals("custom:model1", recognizedForm.getFormType());
            assertNotNull(recognizedForm.getFormTypeConfidence());

            // check formtype set on submodel
            final CustomFormSubmodel submodel = createdModel.getSubmodels().get(0);
            assertEquals("custom:model1", submodel.getFormType());
            formTrainingClient.deleteModel(createdModel.getModelId());
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies recognized form type when labeled model used for recognition and model name is not provided by user.
     */
    @Test
    public void checkRecognizedFormTypeLabeledModel() {
        final FormTrainingClient formTrainingClient = this.trainingClient;
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = formTrainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller1 = this.recognizerClient
                .beginRecognizeCustomForms(createdModel.getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller1.waitForCompletion();
            final RecognizedForm recognizedForm = syncPoller1.getFinalResult().stream().findFirst().get();
            assertEquals("custom:" + createdModel.getModelId(), recognizedForm.getFormType());
            assertNotNull(recognizedForm.getFormTypeConfidence());

            // check formtype set on submodel
            final CustomFormSubmodel submodel = createdModel.getSubmodels().get(0);
            assertEquals("custom:" + createdModel.getModelId(), submodel.getFormType());
            formTrainingClient.deleteModel(createdModel.getModelId());
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies recognized form type when unlabeled model used for recognition and model name is not provided by user.
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void checkRecognizedFormTypeUnlabeledModel() {
        final FormTrainingClient formTrainingClient = this.trainingClient;
        dataRunner((data, dataLength) -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = formTrainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller1 = this.recognizerClient
                .beginRecognizeCustomForms(createdModel.getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller1.waitForCompletion();
            final RecognizedForm recognizedForm = syncPoller1.getFinalResult().stream().findFirst().get();
            assertEquals("form-0", recognizedForm.getFormType());

            // check formtype set on submodel
            final CustomFormSubmodel submodel = createdModel.getSubmodels().get(0);
            assertEquals("form-0", submodel.getFormType());
            formTrainingClient.deleteModel(createdModel.getModelId());
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies recognized form type when unlabeled model used for recognition and model name is provided by user.
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void checkRecognizedFormTypeUnlabeledModelWithModelName() {
        final FormTrainingClient formTrainingClient = this.trainingClient;
        dataRunner((data, dataLength) -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = formTrainingClient
                    .beginTraining(trainingFilesUrl, useTrainingLabels, new TrainingOptions().setModelName("model1"),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller1 = this.recognizerClient
                .beginRecognizeCustomForms(createdModel.getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller1.waitForCompletion();
            final RecognizedForm recognizedForm = syncPoller1.getFinalResult().stream().findFirst().get();
            assertEquals("form-0", recognizedForm.getFormType());

            // check formtype set on submodel
            final CustomFormSubmodel submodel = createdModel.getSubmodels().get(0);
            assertEquals("form-0", submodel.getFormType());

            formTrainingClient.deleteModel(createdModel.getModelId());
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies recognized form type when using composed model for recognition when display name is not provided by user.
     */
    @Test
    public void checkRecognizeFormTypeComposedModel() {
        final FormTrainingClient formTrainingClient = this.trainingClient;
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = formTrainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller1
                = formTrainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode);
            syncPoller1.waitForCompletion();
            CustomFormModel createdModel1 = syncPoller1.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller2 = formTrainingClient
                .beginCreateComposedModel(Arrays.asList(createdModel.getModelId(), createdModel1.getModelId()))
                .setPollInterval(durationTestMode);
            syncPoller2.waitForCompletion();
            CustomFormModel composedModel = syncPoller2.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller3 = this.recognizerClient
                .beginRecognizeCustomForms(composedModel.getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller3.waitForCompletion();

            final RecognizedForm recognizedForm = syncPoller3.getFinalResult().stream().findFirst().get();
            if (recognizedForm.getFormType().equals("custom:" + createdModel1.getModelId())
                || recognizedForm.getFormType().equals("custom:" + createdModel.getModelId())) {
                assertTrue(true);
            } else {
                fail();
            }
            assertNotNull(recognizedForm.getFormTypeConfidence());

            // check formtype set on submodel
            composedModel.getSubmodels().forEach(customFormSubmodel -> {
                if (createdModel.getModelId().equals(customFormSubmodel.getModelId())) {
                    assertEquals("custom:" + createdModel.getModelId(), customFormSubmodel.getFormType());
                } else {
                    assertEquals("custom:" + createdModel1.getModelId(), customFormSubmodel.getFormType());
                }
            });

            formTrainingClient.deleteModel(createdModel.getModelId());
            formTrainingClient.deleteModel(createdModel1.getModelId());
            formTrainingClient.deleteModel(composedModel.getModelId());
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies recognized form type when using composed model for recognition when model name is provided by user.
     */
    @Test
    public void checkRecognizeFormTypeComposedModelWithModelName() {
        final FormTrainingClient formTrainingClient = this.trainingClient;
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = formTrainingClient
                    .beginTraining(trainingFilesUrl, useTrainingLabels, new TrainingOptions().setModelName("model1"),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel1 = syncPoller.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller1
                = formTrainingClient
                    .beginTraining(trainingFilesUrl, useTrainingLabels, new TrainingOptions().setModelName("model2"),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller1.waitForCompletion();
            CustomFormModel createdModel2 = syncPoller1.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller2 = formTrainingClient
                .beginCreateComposedModel(Arrays.asList(createdModel1.getModelId(), createdModel2.getModelId()),
                    new CreateComposedModelOptions().setModelName("composedModelName"), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller2.waitForCompletion();
            CustomFormModel composedModel = syncPoller2.getFinalResult();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller3 = this.recognizerClient
                .beginRecognizeCustomForms(composedModel.getModelId(), data, dataLength,
                    new RecognizeCustomFormsOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller3.waitForCompletion();

            final RecognizedForm recognizedForm = syncPoller3.getFinalResult().stream().findFirst().get();
            String expectedFormType1 = "composedModelName:model1";
            String expectedFormType2 = "composedModelName:model2";
            assertTrue(expectedFormType1.equals(recognizedForm.getFormType())
                || expectedFormType2.equals(recognizedForm.getFormType()));

            assertNotNull(recognizedForm.getFormTypeConfidence());

            formTrainingClient.deleteModel(createdModel1.getModelId());
            formTrainingClient.deleteModel(createdModel2.getModelId());
            formTrainingClient.deleteModel(composedModel.getModelId());
        }), CONTENT_FORM_JPG);
    }

    // Business card recognition

    // Business card - non-URL

    /**
     * Verifies business card data for a document using source as input stream data.
     */
    @Test
    public void recognizeBusinessCardData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeBusinessCards(data, dataLength,
                    new RecognizeBusinessCardsOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult(), false);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies content type will be auto detected when using business card API with input stream data overload.
     */
    @Test
    public void recognizeBusinessCardDataWithContentTypeAutoDetection() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient.beginRecognizeBusinessCards(getContentDetectionFileData(filePath), dataLength)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult(), false);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies business card data for a document using source as as input stream data and text content when
     * includeFieldElements is true.
     */
    @Test
    public void recognizeBusinessCardDataIncludeFieldElements() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeBusinessCards(data, dataLength,
                        new RecognizeBusinessCardsOptions().setContentType(FormContentType.IMAGE_JPEG)
                            .setFieldElementsIncluded(true),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult(), true);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies business card data from a document using PNG file data as source and including text content details.
     */
    @Test
    public void recognizeBusinessCardDataWithPngFile() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeBusinessCards(data, dataLength,
                        new RecognizeBusinessCardsOptions().setContentType(FormContentType.IMAGE_PNG)
                            .setFieldElementsIncluded(true),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult(), true);
        }, BUSINESS_CARD_PNG);
    }

    /**
     * Verifies business card data from a document using blank PDF.
     */
    @Test
    public void recognizeBusinessCardDataWithBlankPdf() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeBusinessCards(data, dataLength,
                    new RecognizeBusinessCardsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    /**
     * Verify that business card recognition with damaged PDF file.
     */
    @Test
    public void recognizeBusinessCardFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient
                    .beginRecognizeBusinessCards(data, dataLength,
                        new RecognizeBusinessCardsOptions().setContentType(APPLICATION_PDF), Context.NONE)
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            FormRecognizerErrorInformation errorInformation
                = (FormRecognizerErrorInformation) httpResponseException.getValue();
            assertEquals(BAD_ARGUMENT_CODE, errorInformation.getErrorCode());
        });
    }

    /**
     * Verify business card recognition with multipage pdf.
     */
    @Test
    public void recognizeMultipageBusinessCard() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeBusinessCards(data, dataLength,
                        new RecognizeBusinessCardsOptions().setContentType(APPLICATION_PDF)
                            .setFieldElementsIncluded(true),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateMultipageBusinessData(syncPoller.getFinalResult());
        }, MULTIPAGE_BUSINESS_CARD_PDF);
    }

    // business card - URL

    /**
     * Verifies business card data for a document using source as file url.
     */
    @Test
    public void recognizeBusinessCardSourceUrl() {
        urlRunner((sourceUrl) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient.beginRecognizeBusinessCardsFromUrl(sourceUrl).setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult(), false);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with
     * encoded blank space as input data to recognize business card from url API.
     */
    @Test
    public void recognizeBusinessCardFromUrlWithEncodedBlankSpaceSourceUrl() {
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient.beginRecognizeBusinessCardsFromUrl(sourceUrl)
                    .setPollInterval(durationTestMode));
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void recognizeBusinessCardInvalidSourceUrl() {
        invalidSourceUrlRunner((sourceUrl) -> assertThrows(HttpResponseException.class,
            () -> this.recognizerClient.beginRecognizeBusinessCardsFromUrl(sourceUrl)
                .setPollInterval(durationTestMode)));
    }

    /**
     * Verifies business card data for a document using source as file url and include form element references
     * when includeFieldElements is true.
     */
    @Test
    public void recognizeBusinessCardFromUrlIncludeFieldElements() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeBusinessCardsFromUrl(sourceUrl,
                    new RecognizeBusinessCardsOptions().setFieldElementsIncluded(true), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult(), true);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies business card data for a document using source as PNG file url and include form element references
     * when includeFieldElements is true.
     */
    @Test
    public void recognizeBusinessCardSourceUrlWithPngFile() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeBusinessCardsFromUrl(sourceUrl,
                    new RecognizeBusinessCardsOptions().setFieldElementsIncluded(true), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult(), true);
        }, BUSINESS_CARD_PNG);
    }

    /**
     * Verify business card recognition with multipage pdf url.
     */
    @Test
    public void recognizeMultipageBusinessCardUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeBusinessCardsFromUrl(sourceUrl,
                    new RecognizeBusinessCardsOptions().setFieldElementsIncluded(true), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateMultipageBusinessData(syncPoller.getFinalResult());
        }, MULTIPAGE_BUSINESS_CARD_PDF);
    }

    // Invoice recognition

    // Invoice - non-URL

    /**
     * Verifies invoice data recognition  for a document using source as input stream data.
     */
    @Test
    public void recognizeInvoiceData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeInvoices(data, dataLength,
                    new RecognizeInvoicesOptions().setContentType(APPLICATION_PDF), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateInvoiceData(syncPoller.getFinalResult(), false);
        }, INVOICE_PDF);
    }

    /**
     * Verifies content type will be auto detected when using invoice API with input stream data overload.
     */
    @Test
    public void recognizeInvoiceDataWithContentTypeAutoDetection() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeInvoices(getContentDetectionFileData(filePath), dataLength,
                        new RecognizeInvoicesOptions(), Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateInvoiceData(syncPoller.getFinalResult(), false);
        }, INVOICE_PDF);
    }

    /**
     * Verifies invoice data for a document using source as as input stream data and text content when
     * includeFieldElements is true.
     */
    @Test
    public void recognizeInvoiceDataIncludeFieldElements() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeInvoices(data, dataLength,
                        new RecognizeInvoicesOptions().setContentType(APPLICATION_PDF).setFieldElementsIncluded(true),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateInvoiceData(syncPoller.getFinalResult(), true);
        }, INVOICE_PDF);
    }

    /**
     * Verifies invoice data from a document using blank PDF.
     */
    @Test
    public void recognizeInvoiceDataWithBlankPdf() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeInvoices(data, dataLength,
                    new RecognizeInvoicesOptions().setContentType(APPLICATION_PDF), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    /**
     * Verify that invoice recognition with damaged PDF file.
     */
    @Test
    public void recognizeInvoiceFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient
                    .beginRecognizeInvoices(data, dataLength,
                        new RecognizeInvoicesOptions().setContentType(APPLICATION_PDF), Context.NONE)
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            FormRecognizerErrorInformation errorInformation
                = (FormRecognizerErrorInformation) httpResponseException.getValue();
            assertEquals(BAD_ARGUMENT_CODE, errorInformation.getErrorCode());
        });
    }

    /**
     * Verify invoice data recognition with multipage pdf.
     */
    @Test
    public void recognizeMultipageInvoice() {
        // confirm if pageResults should be returned for prebuilt model recognition
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeInvoices(data, dataLength,
                        new RecognizeInvoicesOptions().setContentType(APPLICATION_PDF).setFieldElementsIncluded(true),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateMultipageInvoiceData(syncPoller.getFinalResult());
        }, MULTIPAGE_VENDOR_INVOICE_PDF);
    }

    // invoice - URL

    /**
     * Verifies invoice card data for a document using source as file url.
     */
    @Test
    public void recognizeInvoiceSourceUrl() {
        urlRunner((sourceUrl) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient.beginRecognizeInvoicesFromUrl(sourceUrl).setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateInvoiceData(syncPoller.getFinalResult(), false);
        }, INVOICE_PDF);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with
     * encoded blank space as input data to recognize invoice card from url API.
     */
    @Test
    public void recognizeInvoiceFromUrlWithEncodedBlankSpaceSourceUrl() {
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient.beginRecognizeInvoicesFromUrl(sourceUrl).setPollInterval(durationTestMode));
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void recognizeInvoiceInvalidSourceUrl() {
        invalidSourceUrlRunner((sourceUrl) -> assertThrows(HttpResponseException.class,
            () -> this.recognizerClient.beginRecognizeInvoicesFromUrl(sourceUrl).setPollInterval(durationTestMode)));
    }

    /**
     * Verifies invoice data for a document using source as file url and include form element references
     * when includeFieldElements is true.
     */
    @Test
    public void recognizeInvoiceFromUrlIncludeFieldElements() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeInvoicesFromUrl(sourceUrl,
                        new RecognizeInvoicesOptions().setFieldElementsIncluded(true), Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateInvoiceData(syncPoller.getFinalResult(), true);
        }, INVOICE_PDF);
    }

    /**
     * Verify locale parameter passed when specified by user.
     */
    @Test
    public void invoiceValidLocale() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeInvoices(getContentDetectionFileData(filePath), dataLength,
                    new RecognizeInvoicesOptions().setLocale(FormRecognizerLocale.EN_US), Context.NONE)
                .setPollInterval(durationTestMode);
            validateInvoiceData(syncPoller.getFinalResult(), false);
        }, INVOICE_PDF);
    }

    /**
     * Verify SDK returns empty object and array for null sub line items field.
     */
    @Test
    public void invoiceSubLineItemsNull() {
        localFilePathRunner((filePath, dataLength) -> {
            List<RecognizedForm> recognizedForms = this.recognizerClient
                .beginRecognizeInvoices(getContentDetectionFileData(filePath), dataLength,
                    new RecognizeInvoicesOptions().setLocale(FormRecognizerLocale.EN_US), Context.NONE)
                .setPollInterval(durationTestMode)
                .getFinalResult();

            RecognizedForm recognizedForm = recognizedForms.get(0);
            FormField itemFieldList = recognizedForm.getFields().get("Items").getValue().asList().get(0);
            Map<String, FormField> formFieldMap = itemFieldList.getValue().asMap();

            assertNull(formFieldMap);
            assertEquals(String.valueOf(1), itemFieldList.getValueData().getText());

        }, INVOICE_NO_SUB_LINE_PDF);
    }

    // Identity Document Recognition

    /**
     * Verifies license card data from a document using file data as source.
     */
    @Test
    public void recognizeLicenseCardData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeIdentityDocuments(data, dataLength,
                    new RecognizeIdentityDocumentOptions().setContentType(FormContentType.IMAGE_JPEG), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateIdentityData(syncPoller.getFinalResult(), false);
        }, LICENSE_CARD_JPG);
    }

    /**
     * Verifies content type will be auto detected when using custom form API with input stream data overload.
     */
    @Test
    public void recognizeLicenseDataWithContentTypeAutoDetection() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeIdentityDocuments(getContentDetectionFileData(filePath), dataLength,
                    new RecognizeIdentityDocumentOptions(), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateIdentityData(syncPoller.getFinalResult(), false);
        }, LICENSE_CARD_JPG);
    }

    /**
     * Verifies identity document data from a document using file data as source and including element reference details.
     */
    @Test
    public void recognizeLicenseDataIncludeFieldElements() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeIdentityDocuments(data, dataLength,
                        new RecognizeIdentityDocumentOptions().setContentType(FormContentType.IMAGE_JPEG)
                            .setFieldElementsIncluded(true),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateIdentityData(syncPoller.getFinalResult(), true);
        }, LICENSE_CARD_JPG);
    }

    /**
     * Verifies identity document data from a document using blank PDF.
     */
    @Test
    public void recognizeIDDocumentWithBlankPdf() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeIdentityDocuments(data, dataLength,
                        new RecognizeIdentityDocumentOptions().setContentType(FormContentType.APPLICATION_PDF),
                        Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            assertEquals(0, syncPoller.getFinalResult().size());
        }, BLANK_PDF);
    }

    /**
     * Verify that identity document recognition with damaged PDF file.
     */
    @Test
    public void recognizeIDDocumentFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient
                    .beginRecognizeIdentityDocuments(data, dataLength,
                        new RecognizeIdentityDocumentOptions().setContentType(FormContentType.APPLICATION_PDF),
                        Context.NONE)
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            FormRecognizerErrorInformation errorInformation
                = (FormRecognizerErrorInformation) httpResponseException.getValue();
            assertEquals(BAD_ARGUMENT_CODE, errorInformation.getErrorCode());
        });
    }

    // Identity Document - URL

    /**
     * Verifies business card data for a document using source as file url.
     */
    @Test
    public void recognizeLicenseSourceUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient.beginRecognizeIdentityDocumentsFromUrl(sourceUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateIdentityData(syncPoller.getFinalResult(), false);
        }, LICENSE_CARD_JPG);
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void recognizeIDDocumentInvalidSourceUrl() {
        invalidSourceUrlRunner((invalidSourceUrl) -> assertThrows(HttpResponseException.class,
            () -> this.recognizerClient.beginRecognizeIdentityDocumentsFromUrl(invalidSourceUrl)
                .setPollInterval(durationTestMode)
                .getFinalResult()));
    }

    /**
     * Verifies license identity data for a document using source as file url and include content when
     * includeFieldElements is true.
     */
    @Test
    public void recognizeIDDocumentFromUrlIncludeFieldElements() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeIdentityDocumentsFromUrl(sourceUrl,
                    new RecognizeIdentityDocumentOptions().setFieldElementsIncluded(true), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateIdentityData(syncPoller.getFinalResult(), true);
        }, LICENSE_CARD_JPG);
    }
}
