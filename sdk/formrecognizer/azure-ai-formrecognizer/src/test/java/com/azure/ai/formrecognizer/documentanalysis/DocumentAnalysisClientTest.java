// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.formrecognizer.documentanalysis;

import com.azure.ai.formrecognizer.documentanalysis.administration.DocumentModelAdministrationClient;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.BlobContentSource;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.ClassifierDocumentTypeDetails;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.DocumentClassifierDetails;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.DocumentModelBuildMode;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.DocumentModelDetails;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeDocumentOptions;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzedDocument;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentField;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentWord;
import com.azure.ai.formrecognizer.documentanalysis.models.OperationResult;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpClient;
import com.azure.core.models.ResponseError;
import com.azure.core.test.annotation.DoNotRecord;
import com.azure.core.test.annotation.RecordWithoutRequestBody;
import com.azure.core.test.http.AssertingHttpClientBuilder;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.core.util.polling.SyncPoller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.BLANK_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.BUSINESS_CARD_JPG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.BUSINESS_CARD_PNG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.CONTENT_FORM_JPG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.CONTENT_GERMAN_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.DISPLAY_NAME_WITH_ARGUMENTS;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.EXAMPLE_DOCX;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.EXAMPLE_HTML;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.EXAMPLE_PPTX;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.EXAMPLE_XLSX;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.INVALID_URL;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.INVOICE_6_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.INVOICE_NO_SUB_LINE_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.INVOICE_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.IRS_1040;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.LICENSE_PNG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.MULTIPAGE_BUSINESS_CARD_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.MULTIPAGE_INVOICE_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.MULTIPAGE_RECEIPT_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.MULTIPAGE_VENDOR_INVOICE_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.NON_EXIST_MODEL_ID;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.RECEIPT_CONTOSO_JPG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.RECEIPT_CONTOSO_PNG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.SELECTION_MARK_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.damagedPdfDataRunner;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.encodedBlankSpaceSourceUrlRunner;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.invalidSourceUrlRunner;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.urlRunner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ParameterizedClass(name = DISPLAY_NAME_WITH_ARGUMENTS)
@MethodSource("com.azure.ai.formrecognizer.documentanalysis.TestUtils#getTestParameters")
public class DocumentAnalysisClientTest extends DocumentAnalysisClientTestBase {
    private final HttpClient httpClient;
    private final DocumentAnalysisServiceVersion serviceVersion;

    private DocumentAnalysisClient analysisClient;
    private DocumentModelAdministrationClient adminClient;

    public DocumentAnalysisClientTest(HttpClient httpClient, DocumentAnalysisServiceVersion serviceVersion) {
        this.httpClient = httpClient;
        this.serviceVersion = serviceVersion;
    }

    @BeforeEach
    public void createClient() {
        this.analysisClient = getDocumentAnalysisClient();
        this.adminClient = getDocumentModelAdminClient();
    }

    private HttpClient buildSyncAssertingClient(HttpClient httpClient) {
        return new AssertingHttpClientBuilder(httpClient).skipRequest((ignored1, ignored2) -> false)
            .assertSync()
            .build();
    }

    private DocumentAnalysisClient getDocumentAnalysisClient() {
        return getDocumentAnalysisBuilder(
            buildSyncAssertingClient(
                interceptorManager.isPlaybackMode() ? interceptorManager.getPlaybackClient() : httpClient),
            serviceVersion).buildClient();
    }

    private DocumentModelAdministrationClient getDocumentModelAdminClient() {
        return getDocumentModelAdminClientBuilder(
            buildSyncAssertingClient(
                interceptorManager.isPlaybackMode() ? interceptorManager.getPlaybackClient() : httpClient),
            serviceVersion).buildClient();
    }

    // Receipt recognition
    // Receipt - non-URL

    /**
     * Verifies receipt data for a document using source as input stream data.
     */
    @Test
    public void analyzeReceiptData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-receipt", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateJpegReceiptData(syncPoller.getFinalResult());
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies content type will be auto-detected when using receipt API with input stream data overload.
     */
    @Test
    public void analyzeReceiptDataWithContentTypeAutoDetection() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-receipt", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);

            syncPoller.waitForCompletion();
            validateJpegReceiptData(syncPoller.getFinalResult());
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies receipt data from a document using PNG file data as source and including text content details.
     */
    @Test
    public void analyzeReceiptDataWithPngFile() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-receipt", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validatePngReceiptData(syncPoller.getFinalResult());
        }, RECEIPT_CONTOSO_PNG);
    }

    /**
     * Verifies receipt data from a document using blank PDF.
     */
    @Test
    public void analyzeReceiptDataWithBlankPdf() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-receipt", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    @Test
    public void analyzeReceiptFromDataMultiPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-receipt", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateMultipageReceiptData(syncPoller.getFinalResult());
        }, MULTIPAGE_RECEIPT_PDF);
    }

    /**
     * Verify that receipt recognition with damaged PDF file.
     */
    @Test
    public void analyzeReceiptFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocument("prebuilt-receipt", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    // Receipt - URL

    // Receipt - URL

    /**
     * Verifies receipt data for a document using source as file url.
     */
    @Test
    public void analyzeReceiptSourceUrl() {
        urlRunner((sourceUrl) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-receipt", sourceUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateJpegReceiptData(syncPoller.getFinalResult());
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with
     * encoded blank space as input data to recognize receipt from url API.
     */
    @Test
    public void analyzeReceiptFromUrlWithEncodedBlankSpaceSourceUrl() {
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-receipt", sourceUrl)
                    .setPollInterval(durationTestMode));
            validateEncodedUrlExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void analyzeReceiptInvalidSourceUrl() {
        invalidSourceUrlRunner((sourceUrl) -> Assertions.assertThrows(HttpResponseException.class,
            () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-receipt", sourceUrl)
                .setPollInterval(durationTestMode)));
    }

    /**
     * Verifies receipt data for a document using source as PNG file url and include form element references
     * when includeFieldElements is true.
     */
    @Test
    public void analyzeReceiptSourceUrlWithPngFile() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-receipt", sourceUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validatePngReceiptData(syncPoller.getFinalResult());
        }, RECEIPT_CONTOSO_PNG);
    }

    @Test
    public void analyzeReceiptFromUrlMultiPage() {
        urlRunner(receiptUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-receipt", receiptUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateMultipageReceiptData(syncPoller.getFinalResult());
        }, MULTIPAGE_RECEIPT_PDF);
    }

    // Content Recognition

    // Content - non-URL

    /**
     * Verifies layout/content data for a document using source as input stream data.
     */
    @Test
    public void analyzeContent() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult());
        }, CONTENT_FORM_JPG);
    }

    /**
     * Verifies content type will be auto-detected when using content/layout API with input stream data overload.
     */
    @Test
    public void analyzeContentResultWithContentTypeAutoDetection() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult());
        }, CONTENT_FORM_JPG);

    }

    /**
     * Verifies blank form file is still a valid file to process
     */
    @Test
    public void analyzeContentResultWithBlankPdf() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    @Test
    public void analyzeContentFromDataMultiPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            assertEquals(3, analyzeResult.getPages().size());
            validateMultipageLayoutContent(analyzeResult);
        }, MULTIPAGE_INVOICE_PDF);
    }

    /**
     * Verify that content recognition with damaged PDF file.
     */
    @Test
    public void analyzeContentFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException errorResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            ResponseError responseError = (ResponseError) errorResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    @Test
    public void analyzeContentWithSelectionMarks() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateSelectionMarkContentData(syncPoller.getFinalResult());
        }, SELECTION_MARK_PDF);
    }

    @Test
    public void analyzeContentWithPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength),
                    new AnalyzeDocumentOptions().setPages(Collections.singletonList("1")), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertEquals(1, analyzeResult.getPages().size());
        }, MULTIPAGE_INVOICE_PDF);
    }

    @Test
    public void analyzeContentWithPages() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength),
                    new AnalyzeDocumentOptions().setPages(Arrays.asList("1", "2")), Context.NONE)
                .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertEquals(2, analyzeResult.getPages().size());
        }, MULTIPAGE_INVOICE_PDF);
    }

    // Content - URL

    @Test
    public void analyzeContentFromUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-layout", sourceUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult());
        }, CONTENT_FORM_JPG);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with
     * encoded blank space as input data to recognize a content from url API.
     */
    @Test
    public void analyzeContentFromUrlWithEncodedBlankSpaceSourceUrl() {
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-layout", sourceUrl)
                    .setPollInterval(durationTestMode));
            validateEncodedUrlExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies layout data for a pdf url
     */
    @Test
    public void analyzeContentFromUrlWithPdf() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-layout", sourceUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validatePdfContentData(syncPoller.getFinalResult());
        }, INVOICE_6_PDF);
    }

    /**
     * Verifies that an exception is thrown for invalid source url for recognizing content/layout information.
     */
    @Test
    public void analyzeContentInvalidSourceUrl() {
        invalidSourceUrlRunner((invalidSourceUrl) -> Assertions.assertThrows(HttpResponseException.class,
            () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-layout", invalidSourceUrl)
                .setPollInterval(durationTestMode)));
    }

    @Test
    public void analyzeContentFromUrlMultiPage() {
        urlRunner((sourceUrl) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-layout", sourceUrl)
                    .setPollInterval(durationTestMode);

            syncPoller.waitForCompletion();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            assertEquals(3, analyzeResult.getPages().size());
            validateMultipageLayoutContent(analyzeResult);
        }, MULTIPAGE_INVOICE_PDF);
    }

    @Test
    public void analyzeContentWithSelectionMarksFromUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-layout", sourceUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateSelectionMarkContentData(syncPoller.getFinalResult());
        }, SELECTION_MARK_PDF);
    }

    @Test
    public void analyzeGermanContentFromUrl() {
        testingContainerUrlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient
                    .beginAnalyzeDocumentFromUrl("prebuilt-layout", sourceUrl,
                        new AnalyzeDocumentOptions().setLocale("de"), Context.NONE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateGermanContentData(syncPoller.getFinalResult());
        }, CONTENT_GERMAN_PDF);
    }

    // Custom Document recognition

    /**
     * Verifies custom form data for a document using source as input stream data and valid model Id.
     */
    @Test
    public void analyzeCustomDocument() {
        dataRunner((data, dataLength) -> buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> buildModelPoller
                = adminClient.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE)
                    .setPollInterval(durationTestMode);
            buildModelPoller.waitForCompletion();

            String modelId = buildModelPoller.getFinalResult().getModelId();

            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument(modelId, BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();

            adminClient.deleteDocumentModel(modelId);
            validateJpegCustomDocument(syncPoller.getFinalResult());
        }), CONTENT_FORM_JPG);
    }

    /**
     * Verifies custom form data for a blank PDF content type with labeled data
     */
    @Test
    public void analyzeCustomDocumentBlankPdf() {
        dataRunner((data, dataLength) -> buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> buildModelPoller
                = adminClient.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE)
                    .setPollInterval(durationTestMode);
            buildModelPoller.waitForCompletion();

            String modelId = buildModelPoller.getFinalResult().getModelId();

            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument(modelId, BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            adminClient.deleteDocumentModel(modelId);

            validateBlankPdfData(syncPoller.getFinalResult());
        }), BLANK_PDF);
    }

    /**
     * Verifies content type will be auto-detected when using custom form API with input stream data overload.
     */
    @Test
    public void analyzeCustomDocumentWithContentTypeAutoDetection() {
        dataRunner((data, dataLength) -> buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> buildModelPoller
                = adminClient.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE)
                    .setPollInterval(durationTestMode);
            buildModelPoller.waitForCompletion();

            String modelId = buildModelPoller.getFinalResult().getModelId();

            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument(modelId, BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            adminClient.deleteDocumentModel(modelId);

            validateJpegCustomDocument(syncPoller.getFinalResult());
        }), CONTENT_FORM_JPG);
    }

    @Test
    public void analyzeCustomDocumentMultiPage() {

        dataRunner((data, dataLength) -> multipageTrainingRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> buildModelPoller
                = adminClient.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE)
                    .setPollInterval(durationTestMode);
            buildModelPoller.waitForCompletion();
            String modelId = buildModelPoller.getFinalResult().getModelId();

            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument(modelId, BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            adminClient.deleteDocumentModel(modelId);

            validateMultiPagePdfData(syncPoller.getFinalResult());
        }), MULTIPAGE_INVOICE_PDF);
    }

    @Test
    public void analyzeCustomDocumentWithSelectionMark() {
        dataRunner((data, dataLength) -> selectionMarkTrainingRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> buildModelPoller
                = adminClient.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE)
                    .setPollInterval(durationTestMode);
            buildModelPoller.waitForCompletion();

            String modelId = buildModelPoller.getFinalResult().getModelId();

            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument(modelId, BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            adminClient.deleteDocumentModel(modelId);
            validateCustomDocumentWithSelectionMarks(syncPoller.getFinalResult());
        }), SELECTION_MARK_PDF);
    }

    // Custom Document - URL

    /**
     * Verifies custom form data for an URL document data without labeled data
     */
    @Test
    public void analyzeCustomDocumentUrl() {
        urlRunner((fileUrl) -> buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> buildModelPoller
                = adminClient.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE)
                    .setPollInterval(durationTestMode);
            buildModelPoller.waitForCompletion();
            String modelId = buildModelPoller.getFinalResult().getModelId();
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl(modelId, fileUrl).setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            adminClient.deleteDocumentModel(modelId);

            validateJpegCustomDocument(syncPoller.getFinalResult());
        }), CONTENT_FORM_JPG);
    }

    @Test
    public void analyzeCustomDocumentUrlMultiPage() {
        testingContainerUrlRunner((fileUrl) -> multipageTrainingRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> buildModelPoller
                = adminClient.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE)
                    .setPollInterval(durationTestMode);
            buildModelPoller.waitForCompletion();
            String modelId = buildModelPoller.getFinalResult().getModelId();

            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl(modelId, fileUrl).setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            adminClient.deleteDocumentModel(modelId);

            validateMultiPagePdfData(syncPoller.getFinalResult());
        }), MULTIPAGE_INVOICE_PDF);
    }

    // Custom Document - URL

    /**
     * Verifies that an exception is thrown for invalid training data source.
     */
    @Test
    public void analyzeCustomDocumentInvalidSourceUrl() {
        buildModelRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> syncPoller
                = this.adminClient.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            DocumentModelDetails createdModel = syncPoller.getFinalResult();

            HttpResponseException httpResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl(createdModel.getModelId(), INVALID_URL)
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            final ResponseError responseError = (ResponseError) httpResponseException.getValue();

            adminClient.deleteDocumentModel(createdModel.getModelId());

            Assertions.assertEquals("InvalidArgument", responseError.getCode());
        });
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with \
     * encoded blank space as input data to recognize a custom form from url API.
     */
    @Test
    public void analyzeCustomDocumentFromUrlWithEncodedBlankSpaceSourceUrl() {
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl(NON_EXIST_MODEL_ID, sourceUrl)
                    .setPollInterval(durationTestMode));
            validateEncodedUrlExceptionSource(errorResponseException);
        });
    }

    /**
     * Verify that custom document with invalid model id.
     */
    @Test
    public void analyzeCustomDocumentUrlNonExistModelId() {
        urlRunner(fileUrl -> {
            HttpResponseException errorResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl(NON_EXIST_MODEL_ID, fileUrl)
                    .setPollInterval(durationTestMode));
            ResponseError responseError = (ResponseError) errorResponseException.getValue();
            Assertions.assertEquals("NotFound", responseError.getCode());
        }, CONTENT_FORM_JPG);
    }

    /**
     * Verify that custom form with damaged PDF file.
     */
    @Test
    public void analyzeCustomDocumentDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> buildModelRunner((trainingFilesUrl -> {
            SyncPoller<OperationResult, DocumentModelDetails> buildModelPoller
                = adminClient.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE)
                    .setPollInterval(durationTestMode);
            buildModelPoller.waitForCompletion();
            String modelId = buildModelPoller.getFinalResult().getModelId();

            HttpResponseException httpResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocument(modelId, BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            adminClient.deleteDocumentModel(modelId);

            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        })));
    }

    @Test
    public void analyzeCustomDocumentUrlWithSelectionMark() {
        urlRunner(fileUrl -> selectionMarkTrainingRunner((trainingFilesUrl) -> {
            SyncPoller<OperationResult, DocumentModelDetails> buildModelPoller
                = adminClient.beginBuildDocumentModel(trainingFilesUrl, DocumentModelBuildMode.TEMPLATE)
                    .setPollInterval(durationTestMode);
            buildModelPoller.waitForCompletion();
            String modelId = buildModelPoller.getFinalResult().getModelId();

            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl(modelId, fileUrl).setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();

            adminClient.deleteDocumentModel(modelId);
            validateCustomDocumentWithSelectionMarks(syncPoller.getFinalResult());
        }), SELECTION_MARK_PDF);
    }

    // Business card recognition

    // Business card - non-URL

    /**
     * Verifies business card data for a document using source as input stream data.
     */
    @Test
    public void analyzeBusinessCardData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-businessCard", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult());
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies content type will be auto-detected when using business card API with input stream data overload.
     */
    @Test
    public void analyzeBusinessCardDataWithContentTypeAutoDetection() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-businessCard", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult());
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies business card data from a document using PNG file data as source and including text content details.
     */
    @Test
    public void analyzeBusinessCardDataWithPngFile() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-businessCard", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult());
        }, BUSINESS_CARD_PNG);
    }

    /**
     * Verifies business card data from a document using blank PDF.
     */
    @Test
    public void analyzeBusinessCardDataWithBlankPdf() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-businessCard", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    /**
     * Verify that business card recognition with damaged PDF file.
     */
    @Test
    public void analyzeBusinessCardFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient
                    .beginAnalyzeDocument("prebuilt-businessCard", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    /**
     * Verify business card recognition with multipage pdf.
     */
    @Test
    public void analyzeMultipageBusinessCard() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-businessCard", BinaryData.fromStream(data, dataLength))
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
    public void analyzeBusinessCardSourceUrl() {
        urlRunner((sourceUrl) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-businessCard", sourceUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult());
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with
     * encoded blank space as input data to recognize business card from url API.
     */
    @Test
    public void analyzeBusinessCardFromUrlWithEncodedBlankSpaceSourceUrl() {
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-businessCard", sourceUrl)
                    .setPollInterval(durationTestMode));
            validateEncodedUrlExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void analyzeBusinessCardInvalidSourceUrl() {
        invalidSourceUrlRunner((sourceUrl) -> Assertions.assertThrows(HttpResponseException.class,
            () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-businessCard", sourceUrl)
                .setPollInterval(durationTestMode)));
    }

    /**
     * Verifies business card data for a document using source as PNG file url and include form element references
     * when includeFieldElements is true.
     */
    @Test
    public void analyzeBusinessCardSourceUrlWithPngFile() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-businessCard", sourceUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult());
        }, BUSINESS_CARD_PNG);
    }

    /**
     * Verify business card recognition with multipage pdf url.
     */
    @Test
    public void analyzeMultipageBusinessCardUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-businessCard", sourceUrl)
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
    @Disabled("until service regression is fixed #33187")
    public void analyzeInvoiceData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-invoice", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateInvoiceData(syncPoller.getFinalResult());
        }, INVOICE_PDF);
    }

    /**
     * Verifies content type will be auto-detected when using invoice API with input stream data overload.
     */
    @Test
    @Disabled("until service regression is fixed #33187")
    public void analyzeInvoiceDataWithContentTypeAutoDetection() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-invoice", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateInvoiceData(syncPoller.getFinalResult());
        }, INVOICE_PDF);
    }

    /**
     * Verifies invoice data from a document using blank PDF.
     */
    @Test
    public void analyzeInvoiceDataWithBlankPdf() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-invoice", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    /**
     * Verify that invoice recognition with damaged PDF file.
     */
    @Test
    public void analyzeInvoiceFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocument("prebuilt-invoice", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    /**
     * Verify invoice data recognition with multipage pdf.
     */
    @Test
    @Disabled("until service regression is fixed #33187")
    public void analyzeMultipageInvoice() {
        // confirm if pageResults should be returned for prebuilt model recognition
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-invoice", BinaryData.fromStream(data, dataLength))
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
    @Disabled("until service regression is fixed #33187")
    public void analyzeInvoiceSourceUrl() {
        urlRunner((sourceUrl) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-invoice", sourceUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateInvoiceData(syncPoller.getFinalResult());
        }, INVOICE_PDF);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with
     * encoded blank space as input data to recognize invoice card from url API.
     */
    @Test
    public void analyzeInvoiceFromUrlWithEncodedBlankSpaceSourceUrl() {
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-invoice", sourceUrl)
                    .setPollInterval(durationTestMode));
            validateEncodedUrlExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void analyzeInvoiceInvalidSourceUrl() {
        invalidSourceUrlRunner((sourceUrl) -> Assertions.assertThrows(HttpResponseException.class,
            () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-invoice", sourceUrl)
                .setPollInterval(durationTestMode)));
    }

    /**
     * Verifies invoice data for a document using source as file url and include form element references
     * when includeFieldElements is true.
     */
    @Test
    @Disabled("until service regression is fixed #33187")
    public void analyzeInvoiceFromUrlIncludeFieldElements() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-invoice", sourceUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateInvoiceData(syncPoller.getFinalResult());
        }, INVOICE_PDF);
    }

    /**
     * Verify locale parameter passed when specified by user.
     */
    @Test
    @Disabled("until service regression is fixed #33187")
    public void invoiceValidLocale() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-invoice", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            validateInvoiceData(syncPoller.getFinalResult());
        }, INVOICE_PDF);
    }

    /**
     * Verify SDK returns empty object and array for null sub line items field.
     */
    @Test
    @Disabled("until service regression is fixed #33187")
    public void invoiceSubLineItemsNull() {
        dataRunner((data, dataLength) -> {
            AnalyzeResult analyzeResult
                = analysisClient.beginAnalyzeDocument("prebuilt-invoice", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getFinalResult();

            AnalyzedDocument analyzedDocument = analyzeResult.getDocuments().get(0);
            DocumentField itemFieldList = analyzedDocument.getFields().get("Items").getValueAsList().get(0);
            Map<String, DocumentField> documentFieldMap = itemFieldList.getValueAsMap();

            Assertions.assertNull(documentFieldMap);
            Assertions.assertEquals(String.valueOf(1), itemFieldList.getContent());

        }, INVOICE_NO_SUB_LINE_PDF);
    }

    // Identity Document Recognition

    /**
     * Verifies license card data from a document using file data as source.
     */
    @Test
    public void analyzeLicenseCardData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-idDocument", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateIdentityData(syncPoller.getFinalResult());
        }, LICENSE_PNG);
    }

    /**
     * Verifies content type will be auto-detected when using custom form API with input stream data overload.
     */
    @Test
    public void analyzeLicenseDataWithContentTypeAutoDetection() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-idDocument", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateIdentityData(syncPoller.getFinalResult());
        }, LICENSE_PNG);
    }

    /**
     * Verifies identity document data from a document using blank PDF.
     */
    @Test
    public void analyzeIDDocumentWithBlankPdf() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-idDocument", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    /**
     * Verify that identity document recognition with damaged PDF file.
     */
    @Test
    public void analyzeIDDocumentFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient
                    .beginAnalyzeDocument("prebuilt-idDocument", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    // Identity Document - URL

    /**
     * Verifies business card data for a document using source as file url.
     */
    @Test
    public void analyzeLicenseSourceUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-idDocument", sourceUrl)
                    .setPollInterval(durationTestMode);
            syncPoller.waitForCompletion();
            validateIdentityData(syncPoller.getFinalResult());
        }, LICENSE_PNG);
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void analyzeIDDocumentInvalidSourceUrl() {
        invalidSourceUrlRunner((invalidSourceUrl) -> {
            HttpResponseException errorResponseException = Assertions.assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-idDocument", invalidSourceUrl)
                    .setPollInterval(durationTestMode)
                    .getFinalResult());
            ResponseError responseError = (ResponseError) errorResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    @Test
    @Disabled
    public void testGetWordsInALine() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-document", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            List<DocumentWord> actualWords = analyzeResult.getPages().get(0).getLines().get(2).getWords();
            List<String> expectedWords = Arrays.stream("1 Redmond way Suite".split(" ")).collect(Collectors.toList());
            int expectedWordCount = 4;
            assertEquals(expectedWordCount, actualWords.size());
            AtomicInteger i = new AtomicInteger(0);
            actualWords.forEach(
                documentWord -> assertEquals(expectedWords.get(i.getAndIncrement()), documentWord.getContent()));
        }, INVOICE_PDF);
    }

    /**
     * Verifies license card data from a document using file data as source.
     */
    @Test
    @DoNotRecord(skipInPlayback = true)
    public void analyzeDataWithInvalidLength() {
        dataRunner((data, dataLength) -> {
            IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> analysisClient.beginAnalyzeDocument("prebuilt-idDocument", BinaryData.fromStream(data, null))
                    .setPollInterval(durationTestMode));
            Assertions.assertEquals("'document length' is required and cannot be null",
                illegalArgumentException.getMessage());
        }, LICENSE_PNG);
    }

    /**
     * Verifies support for pptx when using "prebuilt-read".
     */
    @Test
    public void testPptDocumentPrebuiltRead() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-read", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertNotNull(analyzeResult);
            Assertions.assertEquals("This is a pptx example.", analyzeResult.getContent());
        }, EXAMPLE_PPTX);
    }

    @Test
    public void testHtmlDocumentPrebuiltRead() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-read", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertNotNull(analyzeResult);
            Assertions.assertTrue(analyzeResult.getContent().contains("html example."));
        }, EXAMPLE_HTML);
    }

    @Test
    public void testDocxDocumentPrebuiltRead() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-read", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertNotNull(analyzeResult);
            Assertions.assertEquals("This is a docx example.", analyzeResult.getContent());
        }, EXAMPLE_DOCX);
    }

    @Test
    public void testXlsxDocumentPrebuiltRead() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-read", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode);
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertNotNull(analyzeResult);
            Assertions.assertTrue(analyzeResult.getContent().contains("This is a xlsx example."));
        }, EXAMPLE_XLSX);
    }

    @RecordWithoutRequestBody
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41027")
    public void testClassifyAnalyzeFromUrl() {
        AtomicReference<DocumentClassifierDetails> documentClassifierDetails = new AtomicReference<>();
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
                = adminClient.beginBuildDocumentClassifier(documentTypeDetailsMap).setPollInterval(durationTestMode);
            buildModelPoller.waitForCompletion();
            documentClassifierDetails.set(buildModelPoller.getFinalResult());

        });

        if (documentClassifierDetails.get() != null) {
            String classifierId = documentClassifierDetails.get().getClassifierId();
            dataRunner((data, dataLength) -> {
                SyncPoller<OperationResult, AnalyzeResult> syncPoller
                    = analysisClient
                        .beginClassifyDocument(documentClassifierDetails.get().getClassifierId(),
                            BinaryData.fromStream(data, dataLength), Context.NONE)
                        .setPollInterval(durationTestMode);
                AnalyzeResult analyzeResult = syncPoller.getFinalResult();
                Assertions.assertNotNull(analyzeResult);
                Assertions.assertEquals(3, analyzeResult.getDocuments().size());
                Assertions.assertEquals(analyzeResult.getModelId(), classifierId);
            }, IRS_1040);
        }
    }

    @RecordWithoutRequestBody
    @Test
    @Disabled("https://github.com/Azure/azure-sdk-for-java/issues/41027")
    public void testClassifyAnalyze() {
        AtomicReference<DocumentClassifierDetails> documentClassifierDetails = new AtomicReference<>();
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
                = adminClient.beginBuildDocumentClassifier(documentTypeDetailsMap).setPollInterval(durationTestMode);
            buildModelPoller.waitForCompletion();
            documentClassifierDetails.set(buildModelPoller.getFinalResult());

        });

        if (documentClassifierDetails.get() != null) {
            String classifierId = documentClassifierDetails.get().getClassifierId();
            dataRunner((data, dataLength) -> {
                SyncPoller<OperationResult, AnalyzeResult> syncPoller
                    = analysisClient
                        .beginClassifyDocument(documentClassifierDetails.get().getClassifierId(),
                            BinaryData.fromStream(data, dataLength), Context.NONE)
                        .setPollInterval(durationTestMode);
                AnalyzeResult analyzeResult = syncPoller.getFinalResult();
                Assertions.assertNotNull(analyzeResult);
                Assertions.assertEquals(3, analyzeResult.getDocuments().size());
                Assertions.assertEquals(analyzeResult.getModelId(), classifierId);
            }, IRS_1040);
        }
    }
}
