// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.kafka.connect.implementation.sink.patch;

import com.azure.cosmos.implementation.Strings;

import static com.azure.cosmos.implementation.Utils.checkArgument;
import static com.azure.cosmos.implementation.Utils.checkNotNull;

public class CosmosPatchJsonPropertyConfig {
    private final String property;
    private final KafkaCosmosPatchOperationType patchOperationType;
    private final String mappingPath;

    public CosmosPatchJsonPropertyConfig(
        String property,
        KafkaCosmosPatchOperationType patchOperationType,
        String mappingPath) {

        checkArgument(Strings.isNotEmpty(property), "Argument 'property' should not be null");
        checkArgument(Strings.isNotEmpty(mappingPath), "Argument 'mappingPath' should not be null");
        checkNotNull(patchOperationType, "Argument 'patchOperationType' should not be null");

        this.property = property;
        this.patchOperationType = patchOperationType;
        this.mappingPath = mappingPath;
    }

    public String getProperty() {
        return property;
    }

    public KafkaCosmosPatchOperationType getPatchOperationType() {
        return patchOperationType;
    }

    public String getMappingPath() {
        return mappingPath;
    }
}
