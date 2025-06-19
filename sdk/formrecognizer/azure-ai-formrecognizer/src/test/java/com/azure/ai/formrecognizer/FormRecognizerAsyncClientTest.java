// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.formrecognizer;

import com.azure.ai.formrecognizer.models.FormContentType;
import com.azure.ai.formrecognizer.models.FormPage;
import com.azure.ai.formrecognizer.models.FormRecognizerErrorInformation;
import com.azure.ai.formrecognizer.models.FormRecognizerLanguage;
import com.azure.ai.formrecognizer.models.FormRecognizerLocale;
import com.azure.ai.formrecognizer.models.FormRecognizerOperationResult;
import com.azure.ai.formrecognizer.models.RecognizeBusinessCardsOptions;
import com.azure.ai.formrecognizer.models.RecognizeContentOptions;
import com.azure.ai.formrecognizer.models.RecognizeCustomFormsOptions;
import com.azure.ai.formrecognizer.models.RecognizeInvoicesOptions;
import com.azure.ai.formrecognizer.models.RecognizeReceiptsOptions;
import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.azure.ai.formrecognizer.training.FormTrainingAsyncClient;
import com.azure.ai.formrecognizer.training.models.CustomFormModel;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpClient;
import com.azure.core.util.polling.SyncPoller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.azure.ai.formrecognizer.TestUtils.BLANK_PDF;
import static com.azure.ai.formrecognizer.TestUtils.CONTENT_FORM_JPG;
import static com.azure.ai.formrecognizer.TestUtils.CONTENT_GERMAN_PDF;
import static com.azure.ai.formrecognizer.TestUtils.DISPLAY_NAME_WITH_ARGUMENTS;
import static com.azure.ai.formrecognizer.TestUtils.INVALID_SOURCE_URL_ERROR_CODE;
import static com.azure.ai.formrecognizer.TestUtils.INVALID_URL;
import static com.azure.ai.formrecognizer.TestUtils.NON_EXIST_MODEL_ID;
import static com.azure.ai.formrecognizer.TestUtils.SELECTION_MARK_PDF;
import static com.azure.ai.formrecognizer.TestUtils.getContentDetectionFileData;
import static com.azure.ai.formrecognizer.TestUtils.validateExceptionSource;
import static com.azure.ai.formrecognizer.implementation.Utility.toFluxByteBuffer;
import static com.azure.ai.formrecognizer.models.FormContentType.APPLICATION_PDF;
import static com.azure.ai.formrecognizer.models.FormContentType.IMAGE_JPEG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ParameterizedClass(name = DISPLAY_NAME_WITH_ARGUMENTS)
@MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
public class FormRecognizerAsyncClientTest extends FormRecognizerClientTestBase {
    private final HttpClient httpClient;
    private final FormRecognizerServiceVersion serviceVersion;

    private FormRecognizerAsyncClient recognizerClient;
    private FormTrainingAsyncClient trainingClient;

    public FormRecognizerAsyncClientTest(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        this.httpClient = httpClient;
        this.serviceVersion = serviceVersion;
    }

    @BeforeEach
    public void createClient() {
        this.recognizerClient = getFormRecognizerAsyncClient();
        this.trainingClient = getFormTrainingAsyncClient();
    }

    private FormRecognizerAsyncClient getFormRecognizerAsyncClient() {
        return getFormRecognizerClientBuilder(httpClient, serviceVersion).buildAsyncClient();
    }

    private FormTrainingAsyncClient getFormTrainingAsyncClient() {
        return getFormTrainingClientBuilder(httpClient, serviceVersion).buildAsyncClient();
    }

    // Receipt recognition

    // Receipt - non-URL

    /**
     * Verifies receipt data from a document using file data as source.
     */
    @Test
    public void recognizeReceiptData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeReceipts(toFluxByteBuffer(data), dataLength,
                    new RecognizeReceiptsOptions().setContentType(FormContentType.IMAGE_JPEG))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), false, FormContentType.IMAGE_JPEG);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies content type will be auto detected when using custom form API with input stream data overload.
     */
    @Test
    public void recognizeReceiptDataWithContentTypeAutoDetection() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeReceipts(toFluxByteBuffer(getContentDetectionFileData(filePath)), dataLength)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), false, FormContentType.IMAGE_JPEG);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies receipt data from a document using file data as source and including element reference details.
     */
    @Test
    public void recognizeReceiptDataIncludeFieldElements() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeReceipts(toFluxByteBuffer(data), dataLength,
                        new RecognizeReceiptsOptions().setContentType(FormContentType.IMAGE_JPEG)
                            .setFieldElementsIncluded(true))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), true, FormContentType.IMAGE_JPEG);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies receipt data from a document using PNG file data as source and including element reference details.
     */
    @Test
    public void recognizeReceiptDataWithPngFile() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeReceipts(toFluxByteBuffer(data), dataLength,
                        new RecognizeReceiptsOptions().setContentType(FormContentType.IMAGE_PNG)
                            .setFieldElementsIncluded(true))
                    .getSyncPoller();
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
                .beginRecognizeReceipts(toFluxByteBuffer(data), dataLength,
                    new RecognizeReceiptsOptions().setFieldElementsIncluded(true)
                        .setContentType(FormContentType.APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    @Test
    public void recognizeReceiptFromDataMultiPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeReceipts(toFluxByteBuffer(data), dataLength,
                    new RecognizeReceiptsOptions().setFieldElementsIncluded(true)
                        .setContentType(FormContentType.APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                    .beginRecognizeReceipts(toFluxByteBuffer(data), dataLength,
                        new RecognizeReceiptsOptions().setFieldElementsIncluded(true))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
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
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient.beginRecognizeReceiptsFromUrl(sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
                () -> this.recognizerClient.beginRecognizeReceiptsFromUrl(sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());

            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void recognizeReceiptInvalidSourceUrl() {
        invalidSourceUrlRunner((invalidSourceUrl) -> assertThrows(HttpResponseException.class,
            () -> this.recognizerClient.beginRecognizeReceiptsFromUrl(invalidSourceUrl)
                .setPollInterval(durationTestMode)
                .getSyncPoller()
                .getFinalResult()));
    }

    /**
     * Verifies receipt data for a document using source as file url and include content when includeFieldElements is
     * true.
     */
    @Test
    public void recognizeReceiptFromUrlIncludeFieldElements() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeReceiptsFromUrl(sourceUrl, new RecognizeReceiptsOptions().setFieldElementsIncluded(true))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), true, FormContentType.IMAGE_JPEG);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies receipt data for a document using source as PNG file url and include element references when
     * includeFieldElements is true.
     */
    @Test
    public void recognizeReceiptSourceUrlWithPngFile() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeReceiptsFromUrl(sourceUrl, new RecognizeReceiptsOptions().setFieldElementsIncluded(true))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateReceiptData(syncPoller.getFinalResult(), true, FormContentType.IMAGE_PNG);
        }, RECEIPT_CONTOSO_PNG);
    }

    @Test
    @Disabled
    public void recognizeReceiptFromUrlMultiPage() {
        urlRunner(fileUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient.beginRecognizeReceiptsFromUrl(fileUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateMultipageReceiptData(syncPoller.getFinalResult());
            // validate from service team, receipt size returned 2 for 3 pages?
        }, MULTIPAGE_INVOICE_PDF);
    }

    // Content Recognition

    // Content - non-URL

    /**
     * Verifies layout data for a document using source as input stream data.
     */
    @Test
    public void recognizeContent() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller = this.recognizerClient
                .beginRecognizeContent(toFluxByteBuffer(data), dataLength,
                    new RecognizeContentOptions().setContentType(FormContentType.IMAGE_JPEG))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller = this.recognizerClient
                .beginRecognizeContent(toFluxByteBuffer(getContentDetectionFileData(filePath)), dataLength)
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller = this.recognizerClient
                .beginRecognizeContent(toFluxByteBuffer(data), dataLength,
                    new RecognizeContentOptions().setContentType(FormContentType.APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), false);
        }, BLANK_PDF);
    }

    @Test
    public void recognizeContentFromDataMultiPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller = this.recognizerClient
                .beginRecognizeContent(toFluxByteBuffer(data), dataLength,
                    new RecognizeContentOptions().setContentType(FormContentType.APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
            () -> this.recognizerClient.beginRecognizeContent(toFluxByteBuffer(data), dataLength)
                .setPollInterval(durationTestMode)
                .getSyncPoller()
                .getFinalResult()));
    }

    @Test
    public void recognizeContentWithSelectionMarks() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient
                    .beginRecognizeContent(toFluxByteBuffer(data), dataLength,
                        new RecognizeContentOptions().setContentType(FormContentType.APPLICATION_PDF))
                    .getSyncPoller();
            syncPoller.waitForCompletion();

            validateContentData(syncPoller.getFinalResult(), true);
        }, SELECTION_MARK_PDF);
    }

    @Test
    public void recognizeContentWithPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient
                    .beginRecognizeContent(toFluxByteBuffer(data), dataLength,
                        new RecognizeContentOptions().setContentType(APPLICATION_PDF)
                            .setPages(Collections.singletonList("1")))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            List<FormPage> formPages = syncPoller.getFinalResult();
            validateContentData(formPages, true);
            assertEquals(1, formPages.size());
        }, MULTIPAGE_INVOICE_PDF);
    }

    @Test
    public void recognizeContentWithPages() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller = this.recognizerClient
                .beginRecognizeContent(toFluxByteBuffer(data), dataLength,
                    new RecognizeContentOptions().setContentType(APPLICATION_PDF).setPages(Arrays.asList("1", "2")))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                    .beginRecognizeContent(toFluxByteBuffer(data), dataLength,
                        new RecognizeContentOptions().setContentType(APPLICATION_PDF)
                            .setPages(Arrays.asList("1-2", "3")))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
                .beginRecognizeContent(toFluxByteBuffer(data), dataLength,
                    new RecognizeContentOptions().setContentType(FormContentType.IMAGE_JPEG))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            List<FormPage> formPages = syncPoller.getFinalResult();
            validateContentData(formPages, true);
            assertNotNull(formPages.get(0).getLines().get(0).getAppearance().getStyleName());
        }, CONTENT_FORM_JPG);
    }

    // Content - URL

    /**
     * Verifies layout data for a document using source as input stream data.
     */
    @Test
    public void recognizeContentFromUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient.beginRecognizeContentFromUrl(sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
                () -> this.recognizerClient.beginRecognizeContentFromUrl(sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());
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
                = this.recognizerClient.beginRecognizeContentFromUrl(sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, INVOICE_6_PDF);
    }

    /**
     * Verifies that an exception is thrown for invalid status model Id.
     */
    @Test
    public void recognizeContentInvalidSourceUrl() {
        invalidSourceUrlRunner((invalidSourceUrl) -> assertThrows(HttpResponseException.class,
            () -> this.recognizerClient.beginRecognizeContentFromUrl(invalidSourceUrl)
                .setPollInterval(durationTestMode)
                .getSyncPoller()
                .getFinalResult()));
    }

    @Test
    public void recognizeContentFromUrlMultiPage() {
        urlRunner((formUrl) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient.beginRecognizeContentFromUrl(formUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, MULTIPAGE_INVOICE_PDF);
    }

    @Test
    public void recognizeContentWithSelectionMarksFromUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller
                = this.recognizerClient.beginRecognizeContentFromUrl(sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult(), true);
        }, SELECTION_MARK_PDF);
    }

    @Test
    public void recognizeGermanContentFromUrl() {
        testingContainerUrlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller = this.recognizerClient
                .beginRecognizeContentFromUrl(sourceUrl,
                    new RecognizeContentOptions().setLanguage(FormRecognizerLanguage.DE))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                        new RecognizeContentOptions().setLanguage(FormRecognizerLanguage.fromString("language")))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller());
            assertEquals(((FormRecognizerErrorInformation) exception.getValue()).getErrorCode(),
                "NotSupportedLanguage");
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), toFluxByteBuffer(data),
                        dataLength,
                        new RecognizeCustomFormsOptions().setContentType(FormContentType.IMAGE_JPEG)
                            .setFieldElementsIncluded(true))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();

            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), toFluxByteBuffer(data),
                    dataLength, new RecognizeCustomFormsOptions().setContentType(FormContentType.IMAGE_JPEG))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), toFluxByteBuffer(data),
                    dataLength, new RecognizeCustomFormsOptions().setContentType(FormContentType.APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateBlankCustomForm(syncPoller.getFinalResult(), 1, true);
        }), BLANK_PDF);
    }

    /**
     * Verifies custom form data for a document using source as input stream data and valid labeled model Id,
     * excluding element references.
     */
    @Test
    public void recognizeCustomFormLabeledDataExcludeFieldElements() {
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), toFluxByteBuffer(data),
                    dataLength, new RecognizeCustomFormsOptions().setContentType(FormContentType.APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), false, 1, true);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies an exception thrown for a document using null data value.
     */
    @Test
    public void recognizeCustomFormLabeledDataWithNullFormData() {
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();

            syncPoller.waitForCompletion();

            assertThrows(RuntimeException.class,
                () -> this.recognizerClient
                    .beginRecognizeCustomForms(syncPoller.getFinalResult().getModelId(), null, dataLength,
                        new RecognizeCustomFormsOptions().setContentType(FormContentType.APPLICATION_PDF)
                            .setFieldElementsIncluded(true))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller());
        }), INVOICE_6_PDF);
    }

    @Test
    public void recognizeCustomFormInvalidStatus() {
        invalidSourceUrlRunner((invalidSourceUrl) -> beginTrainingLabeledRunner((training, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = this.trainingClient.beginTraining(training, useTrainingLabels)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();

            CustomFormModel createdModel = syncPoller.getFinalResult();
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient
                    .beginRecognizeCustomFormsFromUrl(createdModel.getModelId(), invalidSourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
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
                        .setPollInterval(durationTestMode)
                        .getSyncPoller();
                trainingPoller.waitForCompletion();

                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                    .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(),
                        toFluxByteBuffer(getContentDetectionFileData(filePath)), dataLength,
                        new RecognizeCustomFormsOptions().setFieldElementsIncluded(true))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
                syncPoller.waitForCompletion();
                validateJpegCustomForm(syncPoller.getFinalResult(), true, 1, true);
            }), CONTENT_FORM_JPG);
    }

    /**
     * Verify custom form for a data stream of multi-page labeled data
     */
    @Test
    public void recognizeCustomFormMultiPageLabeled() {
        dataRunner((data, dataLength) -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, true)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();
            String modelId = trainingPoller.getFinalResult().getModelId();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(modelId, toFluxByteBuffer(data), dataLength,
                    new RecognizeCustomFormsOptions().setFieldElementsIncluded(true)
                        .setContentType(FormContentType.APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                        .setPollInterval(durationTestMode)
                        .getSyncPoller();
                trainingPoller.waitForCompletion();

                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                    = this.recognizerClient
                        .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), toFluxByteBuffer(data),
                            dataLength,
                            new RecognizeCustomFormsOptions().setContentType(FormContentType.APPLICATION_PDF)
                                .setFieldElementsIncluded(true))
                        .setPollInterval(durationTestMode)
                        .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), toFluxByteBuffer(data),
                    dataLength,
                    new RecognizeCustomFormsOptions().setFieldElementsIncluded(true)
                        .setContentType(FormContentType.APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateUnlabeledCustomForm(syncPoller.getFinalResult(), true, 1);
        }), INVOICE_6_PDF);
    }

    /**
     * Verifies custom form data for a document using source as input stream data and valid include field elements
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormUnlabeledDataIncludeFieldElements() {
        dataRunner((data, dataLength) -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), toFluxByteBuffer(data),
                        dataLength,
                        new RecognizeCustomFormsOptions().setContentType(FormContentType.APPLICATION_PDF)
                            .setFieldElementsIncluded(true))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateUnlabeledCustomForm(syncPoller.getFinalResult(), true, 1);
        }), INVOICE_6_PDF);
    }

    /**
     * Verify custom form for a data stream of multi-page unlabeled data
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormMultiPageUnlabeled() {
        dataRunner((data, dataLength) -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, false)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), toFluxByteBuffer(data),
                    dataLength,
                    new RecognizeCustomFormsOptions().setFieldElementsIncluded(true)
                        .setContentType(FormContentType.APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), toFluxByteBuffer(data),
                    dataLength, new RecognizeCustomFormsOptions().setContentType(FormContentType.IMAGE_JPEG))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), toFluxByteBuffer(data),
                    dataLength,
                    new RecognizeCustomFormsOptions().setFieldElementsIncluded(true)
                        .setContentType(FormContentType.APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl)
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), false, 1, false);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies custom form data for an URL document data without labeled data and include element references.
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormUrlUnlabeledDataIncludeFieldElements() {
        urlRunner(fileUrl -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl,
                    new RecognizeCustomFormsOptions().setFieldElementsIncluded(true))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), true, 1, false);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verify custom form for an URL of multi-page unlabeled data
     */
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41049")
    public void recognizeCustomFormUrlMultiPageUnlabeled() {
        testingContainerUrlRunner(fileUrl -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, false)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl)
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateMultiPageDataUnlabeled(syncPoller.getFinalResult());
        }), MULTIPAGE_INVOICE_PDF);
    }

    // Custom form - URL - labeled data

    /**
     * Verifies that an exception is thrown for invalid status model Id.
     */
    @Test
    public void recognizeCustomFormInvalidSourceUrl() {
        beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();
            StepVerifier
                .create(this.recognizerClient.beginRecognizeCustomFormsFromUrl(createdModel.getModelId(), INVALID_URL)
                    .setPollInterval(durationTestMode))
                .expectErrorSatisfies(throwable -> {
                    final HttpResponseException httpResponseException = (HttpResponseException) throwable;
                    final FormRecognizerErrorInformation errorInformation
                        = (FormRecognizerErrorInformation) httpResponseException.getValue();
                    assertEquals(INVALID_SOURCE_URL_ERROR_CODE, errorInformation.getErrorCode());
                })
                .verify(Duration.ofSeconds(30));
        });
    }

    /**
     * Verifies custom form data for an URL document data with labeled data
     */
    @Test
    public void recognizeCustomFormUrlLabeledData() {
        urlRunner(fileUrl -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl)
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), false, 1, true);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies custom form data for an URL document data with labeled data and include element references.
     */
    @Test
    public void recognizeCustomFormUrlLabeledDataIncludeFieldElements() {
        urlRunner(fileUrl -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl,
                    new RecognizeCustomFormsOptions().setFieldElementsIncluded(true))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateJpegCustomForm(syncPoller.getFinalResult(), true, 1, true);
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verify custom form for an URL of multi-page labeled data
     */
    @Test
    public void recognizeCustomFormUrlMultiPageLabeled() {
        urlRunner(fileUrl -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, true)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();
            String modelId = trainingPoller.getFinalResult().getModelId();
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeCustomFormsFromUrl(modelId, fileUrl,
                        new RecognizeCustomFormsOptions().setPollInterval(durationTestMode))
                    .getSyncPoller();
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
                () -> this.recognizerClient.beginRecognizeCustomFormsFromUrl(NON_EXIST_MODEL_ID, sourceUrl)
                    .getSyncPoller()
                    .getFinalResult());
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verify that custom form with invalid model id.
     */
    @Test
    public void recognizeCustomFormUrlNonExistModelId() {
        urlRunner(fileUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> this.recognizerClient.beginRecognizeCustomFormsFromUrl(NON_EXIST_MODEL_ID, fileUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());
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
                    = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                        .setPollInterval(durationTestMode)
                        .getSyncPoller();
                trainingPoller.waitForCompletion();

                HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                    () -> this.recognizerClient
                        .beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), toFluxByteBuffer(data),
                            dataLength, new RecognizeCustomFormsOptions().setFieldElementsIncluded(true))
                        .setPollInterval(durationTestMode)
                        .getSyncPoller()
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl,
                    new RecognizeCustomFormsOptions().setFieldElementsIncluded(true))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateCustomFormWithSelectionMarks(syncPoller.getFinalResult(), true, 1);
        }), SELECTION_MARK_PDF);
    }

    /**
     * Verifies custom form data for an URL using specified pages.
     */
    @Test
    public void recognizeCustomFormUrlLabeledDataWithPages() {
        urlRunner(fileUrl -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller
                = this.trainingClient.beginTraining(trainingFilesUrl, useTrainingLabels)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeCustomFormsFromUrl(trainingPoller.getFinalResult().getModelId(), fileUrl,
                        new RecognizeCustomFormsOptions().setFieldElementsIncluded(true)
                            .setPages(Collections.singletonList("1")))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            List<RecognizedForm> recognizedForms = syncPoller.getFinalResult();
            validateJpegCustomForm(syncPoller.getFinalResult(), true, 1, true);
            assertEquals(1, recognizedForms.size());
        }), CONTENT_FORM_JPG);
    }

    // Business Card Recognition

    /**
     * Verifies business card data from a document using file data as source.
     */
    @Test
    public void recognizeBusinessCardData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeBusinessCards(toFluxByteBuffer(data), dataLength,
                    new RecognizeBusinessCardsOptions().setContentType(FormContentType.IMAGE_JPEG))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult(), false);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies content type will be auto-detected when using custom form API with input stream data overload.
     */
    @Test
    public void recognizeBusinessCardDataWithContentTypeAutoDetection() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeBusinessCards(toFluxByteBuffer(getContentDetectionFileData(filePath)), dataLength,
                        new RecognizeBusinessCardsOptions())
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult(), false);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies business card data from a document using file data as source and including element reference details.
     */
    @Test
    public void recognizeBusinessCardDataIncludeFieldElements() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeBusinessCards(toFluxByteBuffer(data), dataLength,
                        new RecognizeBusinessCardsOptions().setContentType(FormContentType.IMAGE_JPEG)
                            .setFieldElementsIncluded(true))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult(), true);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies business card data from a document using PNG file data as source and including element reference details.
     */
    @Test
    public void recognizeBusinessCardDataWithPngFile() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeBusinessCards(toFluxByteBuffer(data), dataLength,
                        new RecognizeBusinessCardsOptions().setContentType(FormContentType.IMAGE_PNG)
                            .setFieldElementsIncluded(true))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
                .beginRecognizeBusinessCards(toFluxByteBuffer(data), dataLength,
                    new RecognizeBusinessCardsOptions().setContentType(FormContentType.APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                    .beginRecognizeBusinessCards(toFluxByteBuffer(data), dataLength,
                        new RecognizeBusinessCardsOptions().setContentType(FormContentType.APPLICATION_PDF))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
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
                    .beginRecognizeBusinessCards(toFluxByteBuffer(data), dataLength,
                        new RecognizeBusinessCardsOptions().setContentType(FormContentType.APPLICATION_PDF)
                            .setFieldElementsIncluded(true))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();

            syncPoller.waitForCompletion();
            validateMultipageBusinessData(syncPoller.getFinalResult());
        }, MULTIPAGE_BUSINESS_CARD_PDF);
    }

    // Business Card - URL

    /**
     * Verifies business card data for a document using source as file url.
     */
    @Test
    public void recognizeBusinessCardSourceUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeBusinessCardsFromUrl(sourceUrl, new RecognizeBusinessCardsOptions())
                .setPollInterval(durationTestMode)
                .getSyncPoller();

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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void recognizeBusinessCardInvalidSourceUrl() {
        invalidSourceUrlRunner((invalidSourceUrl) -> assertThrows(HttpResponseException.class,
            () -> this.recognizerClient.beginRecognizeBusinessCardsFromUrl(invalidSourceUrl)
                .setPollInterval(durationTestMode)
                .getSyncPoller()
                .getFinalResult()));
    }

    /**
     * Verifies business card data for a document using source as file url and include content when
     * includeFieldElements is true.
     */
    @Test
    public void recognizeBusinessCardFromUrlIncludeFieldElements() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeBusinessCardsFromUrl(sourceUrl,
                    new RecognizeBusinessCardsOptions().setFieldElementsIncluded(true))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();

            validateBusinessCardData(syncPoller.getFinalResult(), true);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies business card data for a document using source as PNG file url and include element references when
     * includeFieldElements is true.
     */
    @Test
    public void recognizeBusinessCardSourceUrlWithPngFile() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeBusinessCardsFromUrl(sourceUrl,
                    new RecognizeBusinessCardsOptions().setFieldElementsIncluded(true))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                    new RecognizeBusinessCardsOptions().setFieldElementsIncluded(true))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();

            validateMultipageBusinessData(syncPoller.getFinalResult());
        }, MULTIPAGE_BUSINESS_CARD_PDF);
    }

    /**
     * Verify locale parameter passed when specified by user.
     */
    @Test
    public void receiptValidLocale() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeReceiptsFromUrl(sourceUrl,
                    new RecognizeReceiptsOptions().setLocale(FormRecognizerLocale.EN_US))
                .setPollInterval(durationTestMode)
                .getSyncPoller();

            validateReceiptData(syncPoller.getFinalResult(), false, IMAGE_JPEG);

        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verify locale parameter passed when specified by user for business cards API.
     */
    @Test
    public void businessCardValidLocale() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeBusinessCardsFromUrl(sourceUrl,
                    new RecognizeBusinessCardsOptions().setLocale(FormRecognizerLocale.EN_US))
                .setPollInterval(durationTestMode)
                .getSyncPoller();

            validateBusinessCardData(syncPoller.getFinalResult(), false);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verify pages parameter passed when specified by user.
     */
    @Test
    public void receiptWithPage() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeReceiptsFromUrl(sourceUrl,
                    new RecognizeReceiptsOptions().setPages(Collections.singletonList("1")))
                .setPollInterval(durationTestMode)
                .getSyncPoller();

            List<RecognizedForm> recognizedForms = syncPoller.getFinalResult();
            assertEquals(1, recognizedForms.size());
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verify pages parameter passed when specified by user for business cards API.
     */
    @Test
    public void businessCardWithPage() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeBusinessCardsFromUrl(sourceUrl,
                    new RecognizeBusinessCardsOptions().setPages(Collections.singletonList("1")))
                .setPollInterval(durationTestMode)
                .getSyncPoller();

            List<RecognizedForm> recognizedForms = syncPoller.getFinalResult();
            assertEquals(1, recognizedForms.size());
        }, BUSINESS_CARD_JPG);
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
                .beginRecognizeInvoices(toFluxByteBuffer(data), dataLength,
                    new RecognizeInvoicesOptions().setContentType(APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();

            validateInvoiceData(syncPoller.getFinalResult(), true);
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
                    .beginRecognizeInvoices(toFluxByteBuffer(getContentDetectionFileData(filePath)), dataLength,
                        new RecognizeInvoicesOptions())
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();

            validateInvoiceData(syncPoller.getFinalResult(), true);
        }, INVOICE_PDF);
    }

    /**
     * Verifies invoice data for a document using source as as input stream data and text content when
     * includeFieldElements is true.
     */
    @Test
    public void recognizeInvoiceDataIncludeFieldElements() {
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeInvoices(toFluxByteBuffer(data), dataLength,
                    new RecognizeInvoicesOptions().setContentType(APPLICATION_PDF).setFieldElementsIncluded(true))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                .beginRecognizeInvoices(toFluxByteBuffer(data), dataLength,
                    new RecognizeInvoicesOptions().setContentType(APPLICATION_PDF))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                    .beginRecognizeInvoices(toFluxByteBuffer(data), dataLength,
                        new RecognizeInvoicesOptions().setContentType(APPLICATION_PDF))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
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
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeInvoices(toFluxByteBuffer(data), dataLength,
                    new RecognizeInvoicesOptions().setContentType(APPLICATION_PDF).setFieldElementsIncluded(true))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                = this.recognizerClient.beginRecognizeInvoicesFromUrl(sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateInvoiceData(syncPoller.getFinalResult(), true);
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
                () -> this.recognizerClient.beginRecognizeInvoicesFromUrl(sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller());
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void recognizeInvoiceInvalidSourceUrl() {
        invalidSourceUrlRunner((sourceUrl) -> assertThrows(HttpResponseException.class,
            () -> this.recognizerClient.beginRecognizeInvoicesFromUrl(sourceUrl)
                .setPollInterval(durationTestMode)
                .getSyncPoller()));
    }

    /**
     * Verifies invoice data for a document using source as file url and include form element references
     * when includeFieldElements is true.
     */
    @Test
    public void recognizeInvoiceFromUrlIncludeFieldElements() {
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeInvoicesFromUrl(sourceUrl, new RecognizeInvoicesOptions().setFieldElementsIncluded(true))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();

            validateInvoiceData(syncPoller.getFinalResult(), true);
        }, INVOICE_PDF);
    }

    /**
     * Verify locale parameter passed when specified by user.
     */
    @Test
    public void invoiceValidLocale() {
        urlRunner(sourceUrl -> {
            final SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = this.recognizerClient
                .beginRecognizeInvoicesFromUrl(sourceUrl,
                    new RecognizeInvoicesOptions().setLocale(FormRecognizerLocale.EN_US))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.getFinalResult();
            validateInvoiceData(syncPoller.getFinalResult(), false);
        }, INVOICE_PDF);
    }

    @Test
    public void recognizeInvoiceWithPage() {
        urlRunner(sourceUrl -> {
            final SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller
                = this.recognizerClient
                    .beginRecognizeInvoicesFromUrl(sourceUrl,
                        new RecognizeInvoicesOptions().setLocale(FormRecognizerLocale.EN_US)
                            .setPages(Collections.singletonList("1")))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();

            List<RecognizedForm> recognizedForms = syncPoller.getFinalResult();
            assertEquals(1, recognizedForms.size());
        }, INVOICE_PDF);
    }
}
