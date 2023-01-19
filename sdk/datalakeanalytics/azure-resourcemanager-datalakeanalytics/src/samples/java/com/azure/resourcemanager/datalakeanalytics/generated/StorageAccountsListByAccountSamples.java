// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datalakeanalytics.generated;

/** Samples for StorageAccounts ListByAccount. */
public final class StorageAccountsListByAccountSamples {
    /*
     * x-ms-original-file: specification/datalake-analytics/resource-manager/Microsoft.DataLakeAnalytics/stable/2016-11-01/examples/StorageAccounts_ListByAccount.json
     */
    /**
     * Sample code: Gets the first page of Azure Storage accounts linked to the specified Data Lake Analytics account.
     *
     * @param manager Entry point to DataLakeAnalyticsManager.
     */
    public static void getsTheFirstPageOfAzureStorageAccountsLinkedToTheSpecifiedDataLakeAnalyticsAccount(
        com.azure.resourcemanager.datalakeanalytics.DataLakeAnalyticsManager manager) {
        manager
            .storageAccounts()
            .listByAccount(
                "contosorg",
                "contosoadla",
                "test_filter",
                1,
                1,
                "test_select",
                "test_orderby",
                false,
                com.azure.core.util.Context.NONE);
    }
}