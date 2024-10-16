// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
//
// Code generated by Microsoft (R) AutoRest Code Generator.
// Changes may cause incorrect behavior and will be lost if the code is regenerated.

package com.azure.search.documents.indexes.models;

import com.azure.core.annotation.Immutable;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Provides parameter values to a freshness scoring function. */
@Immutable
public final class FreshnessScoringParameters implements JsonSerializable<FreshnessScoringParameters> {
    /*
     * The expiration period after which boosting will stop for a particular document.
     */
    private final Duration boostingDuration;

    /**
     * Creates an instance of FreshnessScoringParameters class.
     *
     * @param boostingDuration the boostingDuration value to set.
     */
    public FreshnessScoringParameters(Duration boostingDuration) {
        this.boostingDuration = boostingDuration;
    }

    /**
     * Get the boostingDuration property: The expiration period after which boosting will stop for a particular
     * document.
     *
     * @return the boostingDuration value.
     */
    public Duration getBoostingDuration() {
        return this.boostingDuration;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("boostingDuration", Objects.toString(this.boostingDuration, null));
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of FreshnessScoringParameters from the JsonReader.
     *
     * @param jsonReader The JsonReader being read.
     * @return An instance of FreshnessScoringParameters if the JsonReader was pointing to an instance of it, or null if
     *     it was pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the FreshnessScoringParameters.
     */
    public static FreshnessScoringParameters fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(
                reader -> {
                    boolean boostingDurationFound = false;
                    Duration boostingDuration = null;
                    while (reader.nextToken() != JsonToken.END_OBJECT) {
                        String fieldName = reader.getFieldName();
                        reader.nextToken();

                        if ("boostingDuration".equals(fieldName)) {
                            boostingDuration =
                                    reader.getNullable(nonNullReader -> Duration.parse(nonNullReader.getString()));
                            boostingDurationFound = true;
                        } else {
                            reader.skipChildren();
                        }
                    }
                    if (boostingDurationFound) {
                        FreshnessScoringParameters deserializedValue = new FreshnessScoringParameters(boostingDuration);

                        return deserializedValue;
                    }
                    List<String> missingProperties = new ArrayList<>();
                    if (!boostingDurationFound) {
                        missingProperties.add("boostingDuration");
                    }

                    throw new IllegalStateException(
                            "Missing required property/properties: " + String.join(", ", missingProperties));
                });
    }
}
