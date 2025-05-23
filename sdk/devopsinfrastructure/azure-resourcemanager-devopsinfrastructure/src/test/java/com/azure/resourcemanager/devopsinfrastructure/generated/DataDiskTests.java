// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.devopsinfrastructure.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.devopsinfrastructure.models.CachingType;
import com.azure.resourcemanager.devopsinfrastructure.models.DataDisk;
import com.azure.resourcemanager.devopsinfrastructure.models.StorageAccountType;
import org.junit.jupiter.api.Assertions;

public final class DataDiskTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        DataDisk model = BinaryData.fromString(
            "{\"caching\":\"None\",\"diskSizeGiB\":778150976,\"storageAccountType\":\"Standard_LRS\",\"driveLetter\":\"wdzuhtymwisd\"}")
            .toObject(DataDisk.class);
        Assertions.assertEquals(CachingType.NONE, model.caching());
        Assertions.assertEquals(778150976, model.diskSizeGiB());
        Assertions.assertEquals(StorageAccountType.STANDARD_LRS, model.storageAccountType());
        Assertions.assertEquals("wdzuhtymwisd", model.driveLetter());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        DataDisk model = new DataDisk().withCaching(CachingType.NONE)
            .withDiskSizeGiB(778150976)
            .withStorageAccountType(StorageAccountType.STANDARD_LRS)
            .withDriveLetter("wdzuhtymwisd");
        model = BinaryData.fromObject(model).toObject(DataDisk.class);
        Assertions.assertEquals(CachingType.NONE, model.caching());
        Assertions.assertEquals(778150976, model.diskSizeGiB());
        Assertions.assertEquals(StorageAccountType.STANDARD_LRS, model.storageAccountType());
        Assertions.assertEquals("wdzuhtymwisd", model.driveLetter());
    }
}
