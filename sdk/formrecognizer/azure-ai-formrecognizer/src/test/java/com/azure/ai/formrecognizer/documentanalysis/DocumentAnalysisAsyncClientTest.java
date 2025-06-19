// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.formrecognizer.documentanalysis;

import com.azure.ai.formrecognizer.documentanalysis.administration.DocumentModelAdministrationAsyncClient;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.BlobContentSource;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.ClassifierDocumentTypeDetails;
import com.azure.ai.formrecognizer.documentanalysis.administration.models.DocumentClassifierDetails;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeDocumentOptions;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.CurrencyValue;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentAnalysisFeature;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentBarcode;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentBarcodeKind;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentField;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentFormula;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentFormulaKind;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentStyle;
import com.azure.ai.formrecognizer.documentanalysis.models.FontStyle;
import com.azure.ai.formrecognizer.documentanalysis.models.OperationResult;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpClient;
import com.azure.core.models.ResponseError;
import com.azure.core.test.annotation.DoNotRecord;
import com.azure.core.test.annotation.RecordWithoutRequestBody;
import com.azure.core.test.http.AssertingHttpClientBuilder;
import com.azure.core.util.BinaryData;
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
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.BARCODE_TIF;
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
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.FORMULA_JPG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.GERMAN_PNG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.INVOICE_6_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.INVOICE_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.IRS_1040;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.LICENSE_PNG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.MULTIPAGE_BUSINESS_CARD_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.MULTIPAGE_INVOICE_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.MULTIPAGE_RECEIPT_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.MULTIPAGE_VENDOR_INVOICE_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.RECEIPT_CONTOSO_JPG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.RECEIPT_CONTOSO_PNG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.SELECTION_MARK_PDF;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.STYLE_PNG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.W2_JPG;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.damagedPdfDataRunner;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.encodedBlankSpaceSourceUrlRunner;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.getContentDetectionFileData;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.invalidSourceUrlRunner;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.localFilePathRunner;
import static com.azure.ai.formrecognizer.documentanalysis.TestUtils.urlRunner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ParameterizedClass(name = DISPLAY_NAME_WITH_ARGUMENTS)
@MethodSource("com.azure.ai.formrecognizer.documentanalysis.TestUtils#getTestParameters")
public class DocumentAnalysisAsyncClientTest extends DocumentAnalysisClientTestBase {
    private final HttpClient httpClient;
    private final DocumentAnalysisServiceVersion serviceVersion;

    private DocumentAnalysisAsyncClient analysisClient;
    private DocumentModelAdministrationAsyncClient adminClient;

    public DocumentAnalysisAsyncClientTest(HttpClient httpClient, DocumentAnalysisServiceVersion serviceVersion) {
        this.httpClient = httpClient;
        this.serviceVersion = serviceVersion;
    }

    @BeforeEach
    public void createClient() {
        this.analysisClient = getDocumentAnalysisAsyncClient();
        this.adminClient = DocumentModelAdministrationAsyncClient();
    }

    private HttpClient buildAsyncAssertingClient(HttpClient httpClient) {
        return new AssertingHttpClientBuilder(httpClient).skipRequest((ignored1, ignored2) -> false)
            .assertAsync()
            .build();
    }

    private DocumentAnalysisAsyncClient getDocumentAnalysisAsyncClient() {
        return getDocumentAnalysisBuilder(
            buildAsyncAssertingClient(
                interceptorManager.isPlaybackMode() ? interceptorManager.getPlaybackClient() : httpClient),
            serviceVersion).buildAsyncClient();
    }

    private DocumentModelAdministrationAsyncClient DocumentModelAdministrationAsyncClient() {
        return getDocumentModelAdminClientBuilder(
            buildAsyncAssertingClient(
                interceptorManager.isPlaybackMode() ? interceptorManager.getPlaybackClient() : httpClient),
            serviceVersion).buildAsyncClient();
    }

    // Receipt recognition

    // Receipt - non-URL

    /**
     * Verifies receipt data from a document using file data as source.
     */
    @Test
    public void analyzeReceiptData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-receipt", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateJpegReceiptData(syncPoller.getFinalResult());
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies content type will be auto-detected when using custom form API with input stream data overload.
     */
    @Test
    public void analyzeReceiptDataWithContentTypeAutoDetection() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocument("prebuilt-receipt",
                    BinaryData.fromStream(getContentDetectionFileData(filePath), dataLength))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateJpegReceiptData(syncPoller.getFinalResult());
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies receipt data from a document using PNG file data as source and including element reference details.
     */
    @Test
    public void analyzeReceiptDataWithPngFile() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-receipt", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    @Test
    public void analyzeReceiptFromDataMultiPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-receipt", BinaryData.fromStream(data, dataLength))
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
    public void analyzeReceiptFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocument("prebuilt-receipt", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());
            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    // Receipt - URL

    /**
     * Verifies receipt data for a document using source as file url.
     */
    @Test
    public void analyzeReceiptSourceUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-receipt", sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-receipt", sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());

            validateEncodedUrlExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void analyzeReceiptInvalidSourceUrl() {
        invalidSourceUrlRunner((invalidSourceUrl) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-receipt", invalidSourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());

            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    /**
     * Verifies receipt data for a document using source as PNG file url.
     */
    @Test
    public void analyzeReceiptSourceUrlWithPngFile() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-receipt", sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validatePngReceiptData(syncPoller.getFinalResult());
        }, RECEIPT_CONTOSO_PNG);
    }

    @Test
    public void analyzeReceiptFromUrlMultiPage() {
        urlRunner(documentUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-receipt", documentUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateMultipageReceiptData(syncPoller.getFinalResult());
        }, MULTIPAGE_RECEIPT_PDF);
    }

    // Content Recognition

    // Content - non-URL

    /**
     * Verifies layout data for a document using source as input stream data.
     */
    @Test
    public void analyzeContent() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateContentData(syncPoller.getFinalResult());
        }, CONTENT_FORM_JPG);
    }

    /**
     * Verifies content type will be auto-detected when using content/layout API with input stream data overload.
     */
    @Test
    public void analyzeContentResultWithContentTypeAutoDetection() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocument("prebuilt-layout",
                    BinaryData.fromStream(getContentDetectionFileData(filePath), dataLength))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateBlankPdfData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    @Test
    public void analyzeContentFromDataMultiPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());
            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    @Test
    public void analyzeContentWithSelectionMarks() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();

            validateSelectionMarkContentData(syncPoller.getFinalResult());
        }, SELECTION_MARK_PDF);
    }

    @Test
    public void analyzeContentWithPage() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength),
                    new AnalyzeDocumentOptions().setPages(Collections.singletonList("1")))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                    new AnalyzeDocumentOptions().setPages(Arrays.asList("1", "2")))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertEquals(2, analyzeResult.getPages().size());
        }, MULTIPAGE_INVOICE_PDF);
    }

    @Test
    public void analyzeContentWithPageRange() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocument("prebuilt-layout", BinaryData.fromStream(data, dataLength),
                    new AnalyzeDocumentOptions().setPages(Arrays.asList("1-2", "3")))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertEquals(3, analyzeResult.getPages().size());
        }, MULTIPAGE_INVOICE_PDF);
    }

    // Content - URL

    /**
     * Verifies layout data for a document using source as input stream data.
     */
    @Test
    public void analyzeContentFromUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-layout", sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-layout", sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validatePdfContentData(syncPoller.getFinalResult());
        }, INVOICE_6_PDF);
    }

    /**
     * Verifies that an exception is thrown for invalid status model Id.
     */
    @Test
    public void analyzeContentInvalidSourceUrl() {
        invalidSourceUrlRunner((invalidSourceUrl) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-layout", invalidSourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());
            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    @Test
    public void analyzeContentFromUrlMultiPage() {
        urlRunner((documentUrl) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-layout", documentUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateSelectionMarkContentData(syncPoller.getFinalResult());
        }, SELECTION_MARK_PDF);
    }

    @Test
    public void analyzeGermanContentFromUrl() {
        testingContainerUrlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocumentFromUrl("prebuilt-layout", sourceUrl, new AnalyzeDocumentOptions().setLocale("de"))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateGermanContentData(syncPoller.getFinalResult());
        }, CONTENT_GERMAN_PDF);
    }

    // Business Card Recognition

    /**
     * Verifies business card data from a document using file data as source.
     */
    @Test
    public void analyzeBusinessCardData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-businessCard", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult());
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies content type will be auto-detected when using custom form API with input stream data overload.
     */
    @Test
    public void analyzeBusinessCardDataWithContentTypeAutoDetection() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocument("prebuilt-businessCard",
                    BinaryData.fromStream(getContentDetectionFileData(filePath), dataLength))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            validateBusinessCardData(syncPoller.getFinalResult());
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies business card data from a document using PNG file data as source and including element reference details.
     */
    @Test
    public void analyzeBusinessCardDataWithPngFile() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-businessCard", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
    public void analyzeBusinessCardFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient
                    .beginAnalyzeDocument("prebuilt-businessCard", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
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
    public void analyzeBusinessCardSourceUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-businessCard", sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();

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
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-businessCard", sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());
            validateEncodedUrlExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void analyzeBusinessCardInvalidSourceUrl() {
        invalidSourceUrlRunner((invalidSourceUrl) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-businessCard", invalidSourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());

            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    /**
     * Verifies business card data for a document using source as PNG file url.
     */
    @Test
    public void analyzeBusinessCardSourceUrlWithPngFile() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-businessCard", sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();

            validateMultipageBusinessData(syncPoller.getFinalResult());
        }, MULTIPAGE_BUSINESS_CARD_PDF);
    }

    /**
     * Verify pages parameter passed when specified by user.
     */
    @Test
    public void receiptWithPage() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocumentFromUrl("prebuilt-receipt", sourceUrl,
                    new AnalyzeDocumentOptions().setPages(Collections.singletonList("1")))
                .setPollInterval(durationTestMode)
                .getSyncPoller();

            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertEquals(1, analyzeResult.getPages().size());
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verify pages parameter passed when specified by user for business cards API.
     */
    @Test
    public void businessCardWithPage() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocumentFromUrl("prebuilt-businessCard", sourceUrl,
                    new AnalyzeDocumentOptions().setPages(Collections.singletonList("1")))
                .setPollInterval(durationTestMode)
                .getSyncPoller();

            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertEquals(1, analyzeResult.getPages().size());
        }, BUSINESS_CARD_JPG);
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocument("prebuilt-invoice",
                    BinaryData.fromStream(getContentDetectionFileData(filePath), dataLength))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
    public void analyzeInvoiceFromDamagedPdf() {
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocument("prebuilt-invoice", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
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
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-invoice", BinaryData.fromStream(data, dataLength))
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
    @Disabled("until service regression is fixed #33187")
    public void analyzeInvoiceSourceUrl() {
        urlRunner((sourceUrl) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-invoice", sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-invoice", sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller());
            validateEncodedUrlExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @Test
    public void analyzeInvoiceInvalidSourceUrl() {
        invalidSourceUrlRunner((sourceUrl) -> assertThrows(HttpResponseException.class,
            () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-invoice", sourceUrl)
                .setPollInterval(durationTestMode)
                .getSyncPoller()));
    }

    /**
     * Verify locale parameter passed when specified by user.
     */
    @Test
    @Disabled("until service regression is fixed #33187")
    public void invoiceValidLocale() {
        urlRunner(sourceUrl -> {
            final SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient
                    .beginAnalyzeDocumentFromUrl("prebuilt-invoice", sourceUrl,
                        new AnalyzeDocumentOptions().setLocale("en-US"))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.getFinalResult();
            validateInvoiceData(syncPoller.getFinalResult());
        }, INVOICE_PDF);
    }

    @Test
    @Disabled("Deserialization error as amount missing in CurrencyValue")
    public void analyzeInvoiceWithPage() {
        urlRunner(sourceUrl -> {
            final SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocumentFromUrl("prebuilt-invoice", sourceUrl,
                    new AnalyzeDocumentOptions().setLocale("en-US").setPages(Collections.singletonList("1")))
                .setPollInterval(durationTestMode)
                .getSyncPoller();

            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertEquals(1, analyzeResult.getPages().size());
            Map<String, DocumentField> invoicePage1Fields = analyzeResult.getDocuments().get(0).getFields();
            CurrencyValue invoiceTotalField = invoicePage1Fields.get("InvoiceTotal").getValueAsCurrency();
            assertEquals(56651.49, invoiceTotalField.getAmount());
            assertEquals("$", invoiceTotalField.getSymbol());
            assertEquals("USD", invoiceTotalField.getCode());
        }, INVOICE_PDF);
    }

    // identity document Recognition

    /**
     * Verifies license card data from a document using file data as source.
     */
    @Test
    public void analyzeLicenseCardData() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-idDocument", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();

            validateIdentityData(syncPoller.getFinalResult());
        }, LICENSE_PNG);
    }

    /**
     * Verifies content type will be auto-detected when using custom form API with input stream data overload.
     */
    @Test
    public void analyzeLicenseDataWithContentTypeAutoDetection() {
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocument("prebuilt-idDocument",
                    BinaryData.fromStream(getContentDetectionFileData(filePath), dataLength))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient
                    .beginAnalyzeDocument("prebuilt-idDocument", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());

            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    // Identity document - URL

    /**
     * Verifies business card data for a document using source as file url.
     */
    @Test
    public void analyzeLicenseSourceUrl() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-idDocument", sourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> analysisClient.beginAnalyzeDocumentFromUrl("prebuilt-idDocument", invalidSourceUrl)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller()
                    .getFinalResult());
            ResponseError responseError = (ResponseError) httpResponseException.getValue();
            Assertions.assertEquals("InvalidRequest", responseError.getCode());
        });
    }

    /**
     * Verifies that languages are returned on analyze result when using "prebuilt-read".
     */
    @Test
    public void testDocumentLanguagePrebuiltRead() {
        urlRunner(sourceUrl -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocumentFromUrl("prebuilt-read", sourceUrl,
                    new AnalyzeDocumentOptions()
                        .setDocumentAnalysisFeatures(Collections.singletonList(DocumentAnalysisFeature.LANGUAGES)))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertNotNull(analyzeResult);
            Assertions.assertNotNull(analyzeResult.getLanguages());
        }, INVOICE_PDF);
    }

    @Test
    public void testGermanDocumentLanguagePrebuiltRead() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocument("prebuilt-read", BinaryData.fromStream(data, dataLength),
                    new AnalyzeDocumentOptions()
                        .setDocumentAnalysisFeatures(Collections.singletonList(DocumentAnalysisFeature.LANGUAGES)))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertNotNull(analyzeResult);
            // TODO (alzimmer): This test to be recorded again as this wasn't actually checking the language locale
            //  and was just checking that "de" was non-null which is always true.
            // Assertions.assertEquals("de", analyzeResult.getLanguages().get(0).getLocale());
        }, GERMAN_PNG);
    }

    @Test
    public void analyzeW2Data() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-tax.us.w2", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            syncPoller.waitForCompletion();
            validateW2Data(syncPoller.getFinalResult());
        }, W2_JPG);
    }

    @Test
    @DoNotRecord(skipInPlayback = true)
    public void analyzeDocumentInvalidLength() {
        dataRunner((data, dataLength) -> {
            IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> analysisClient.beginAnalyzeDocument("prebuilt-tax.us.w2", BinaryData.fromStream(data))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller());
            Assertions.assertEquals("'document length' is required and cannot be null",
                illegalArgumentException.getMessage());
        }, W2_JPG);
    }

    /**
     * Verifies support for pptx when using "prebuilt-read".
     */
    @Test
    public void testPptDocumentPrebuiltRead() {
        dataRunner((data, dataLength) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller
                = analysisClient.beginAnalyzeDocument("prebuilt-read", BinaryData.fromStream(data, dataLength))
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
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
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertNotNull(analyzeResult);
            Assertions.assertTrue(analyzeResult.getContent().contains("This is a xlsx example."));
        }, EXAMPLE_XLSX);
    }

    @RecordWithoutRequestBody
    @Test
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
                = this.adminClient.beginBuildDocumentClassifier(documentTypeDetailsMap)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            buildModelPoller.waitForCompletion();
            documentClassifierDetails.set(buildModelPoller.getFinalResult());

        });

        if (documentClassifierDetails.get() != null) {
            String classifierId = documentClassifierDetails.get().getClassifierId();
            dataRunner((data, dataLength) -> {
                SyncPoller<OperationResult, AnalyzeResult> syncPoller
                    = analysisClient.beginClassifyDocument(classifierId, BinaryData.fromStream(data, dataLength))
                        .setPollInterval(durationTestMode)
                        .getSyncPoller();
                AnalyzeResult analyzeResult = syncPoller.getFinalResult();
                Assertions.assertNotNull(analyzeResult);
                Assertions.assertEquals(3, analyzeResult.getDocuments().size());
                Assertions.assertEquals(analyzeResult.getModelId(), classifierId);
            }, IRS_1040);
        }
    }

    @RecordWithoutRequestBody
    @Test
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
                = this.adminClient.beginBuildDocumentClassifier(documentTypeDetailsMap)
                    .setPollInterval(durationTestMode)
                    .getSyncPoller();
            buildModelPoller.waitForCompletion();
            documentClassifierDetails.set(buildModelPoller.getFinalResult());

        });

        if (documentClassifierDetails.get() != null) {
            String classifierId = documentClassifierDetails.get().getClassifierId();
            dataRunner((data, dataLength) -> {
                SyncPoller<OperationResult, AnalyzeResult> syncPoller
                    = analysisClient
                        .beginClassifyDocument(documentClassifierDetails.get().getClassifierId(),
                            BinaryData.fromStream(data, dataLength))
                        .setPollInterval(durationTestMode)
                        .getSyncPoller();
                AnalyzeResult analyzeResult = syncPoller.getFinalResult();
                Assertions.assertNotNull(analyzeResult);
                Assertions.assertEquals(3, analyzeResult.getDocuments().size());
                Assertions.assertEquals(analyzeResult.getModelId(), classifierId);
            }, IRS_1040);
        }
    }

    @Test
    public void testFormulaPrebuiltRead() {
        testingContainerUrlRunner((sourceUrl) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocumentFromUrl("prebuilt-read", sourceUrl,
                    new AnalyzeDocumentOptions()
                        .setDocumentAnalysisFeatures(Collections.singletonList(DocumentAnalysisFeature.FORMULAS)))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertNotNull(analyzeResult.getPages());
            DocumentFormula formula = analyzeResult.getPages().get(0).getFormulas().get(0);
            Assertions.assertEquals(DocumentFormulaKind.INLINE, formula.getKind());
            Assertions.assertTrue(formula.getValue().startsWith("a + b ="));
        }, FORMULA_JPG);
    }

    @Test
    public void testBarcodePrebuiltRead() {
        testingContainerUrlRunner((sourceUrl) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocumentFromUrl("prebuilt-layout", sourceUrl,
                    new AnalyzeDocumentOptions()
                        .setDocumentAnalysisFeatures(Collections.singletonList(DocumentAnalysisFeature.BARCODES)))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertNotNull(analyzeResult.getPages());

            DocumentBarcode barcode = analyzeResult.getPages().get(0).getBarcodes().get(0);
            Assertions.assertEquals(DocumentBarcodeKind.CODE_39, barcode.getKind());
        }, BARCODE_TIF);
    }

    @Test
    public void testStyleFeatureFlagPrebuiltLayout() {
        testingContainerUrlRunner((sourceUrl) -> {
            SyncPoller<OperationResult, AnalyzeResult> syncPoller = analysisClient
                .beginAnalyzeDocumentFromUrl("prebuilt-layout", sourceUrl,
                    new AnalyzeDocumentOptions()
                        .setDocumentAnalysisFeatures(Collections.singletonList(DocumentAnalysisFeature.STYLE_FONT)))
                .setPollInterval(durationTestMode)
                .getSyncPoller();
            syncPoller.waitForCompletion();
            AnalyzeResult analyzeResult = syncPoller.getFinalResult();
            Assertions.assertNotNull(analyzeResult.getPages());
            DocumentStyle style = analyzeResult.getStyles().get(3);
            Assertions.assertEquals(FontStyle.ITALIC, style.getFontStyle());
        }, STYLE_PNG);
    }
}
