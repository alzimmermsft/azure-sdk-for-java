// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicesbackup.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.CoreUtils;
import com.azure.json.JsonReader;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Recovery point specific to PointInTime.
 */
@Fluent
public class AzureWorkloadPointInTimeRecoveryPoint extends AzureWorkloadRecoveryPoint {
    /*
     * This property will be used as the discriminator for deciding the specific types in the polymorphic chain of
     * types.
     */
    private String objectType = "AzureWorkloadPointInTimeRecoveryPoint";

    /*
     * List of log ranges
     */
    private List<PointInTimeRange> timeRanges;

    /**
     * Creates an instance of AzureWorkloadPointInTimeRecoveryPoint class.
     */
    public AzureWorkloadPointInTimeRecoveryPoint() {
    }

    /**
     * Get the objectType property: This property will be used as the discriminator for deciding the specific types in
     * the polymorphic chain of types.
     * 
     * @return the objectType value.
     */
    @Override
    public String objectType() {
        return this.objectType;
    }

    /**
     * Get the timeRanges property: List of log ranges.
     * 
     * @return the timeRanges value.
     */
    public List<PointInTimeRange> timeRanges() {
        return this.timeRanges;
    }

    /**
     * Set the timeRanges property: List of log ranges.
     * 
     * @param timeRanges the timeRanges value to set.
     * @return the AzureWorkloadPointInTimeRecoveryPoint object itself.
     */
    public AzureWorkloadPointInTimeRecoveryPoint withTimeRanges(List<PointInTimeRange> timeRanges) {
        this.timeRanges = timeRanges;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AzureWorkloadPointInTimeRecoveryPoint withRecoveryPointTimeInUtc(OffsetDateTime recoveryPointTimeInUtc) {
        super.withRecoveryPointTimeInUtc(recoveryPointTimeInUtc);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AzureWorkloadPointInTimeRecoveryPoint withType(RestorePointType type) {
        super.withType(type);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AzureWorkloadPointInTimeRecoveryPoint
        withRecoveryPointTierDetails(List<RecoveryPointTierInformationV2> recoveryPointTierDetails) {
        super.withRecoveryPointTierDetails(recoveryPointTierDetails);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AzureWorkloadPointInTimeRecoveryPoint
        withRecoveryPointMoveReadinessInfo(Map<String, RecoveryPointMoveReadinessInfo> recoveryPointMoveReadinessInfo) {
        super.withRecoveryPointMoveReadinessInfo(recoveryPointMoveReadinessInfo);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AzureWorkloadPointInTimeRecoveryPoint
        withRecoveryPointProperties(RecoveryPointProperties recoveryPointProperties) {
        super.withRecoveryPointProperties(recoveryPointProperties);
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    @Override
    public void validate() {
        if (timeRanges() != null) {
            timeRanges().forEach(e -> e.validate());
        }
        if (recoveryPointTierDetails() != null) {
            recoveryPointTierDetails().forEach(e -> e.validate());
        }
        if (recoveryPointMoveReadinessInfo() != null) {
            recoveryPointMoveReadinessInfo().values().forEach(e -> {
                if (e != null) {
                    e.validate();
                }
            });
        }
        if (recoveryPointProperties() != null) {
            recoveryPointProperties().validate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("recoveryPointTimeInUTC",
            recoveryPointTimeInUtc() == null
                ? null
                : DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(recoveryPointTimeInUtc()));
        jsonWriter.writeStringField("type", type() == null ? null : type().toString());
        jsonWriter.writeArrayField("recoveryPointTierDetails", recoveryPointTierDetails(),
            (writer, element) -> writer.writeJson(element));
        jsonWriter.writeMapField("recoveryPointMoveReadinessInfo", recoveryPointMoveReadinessInfo(),
            (writer, element) -> writer.writeJson(element));
        jsonWriter.writeJsonField("recoveryPointProperties", recoveryPointProperties());
        jsonWriter.writeStringField("objectType", this.objectType);
        jsonWriter.writeArrayField("timeRanges", this.timeRanges, (writer, element) -> writer.writeJson(element));
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of AzureWorkloadPointInTimeRecoveryPoint from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of AzureWorkloadPointInTimeRecoveryPoint if the JsonReader was pointing to an instance of it,
     * or null if it was pointing to JSON null.
     * @throws IOException If an error occurs while reading the AzureWorkloadPointInTimeRecoveryPoint.
     */
    public static AzureWorkloadPointInTimeRecoveryPoint fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            String discriminatorValue = null;
            try (JsonReader readerToUse = reader.bufferObject()) {
                readerToUse.nextToken(); // Prepare for reading
                while (readerToUse.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = readerToUse.getFieldName();
                    readerToUse.nextToken();
                    if ("objectType".equals(fieldName)) {
                        discriminatorValue = readerToUse.getString();
                        break;
                    } else {
                        readerToUse.skipChildren();
                    }
                }
                // Use the discriminator value to determine which subtype should be deserialized.
                if ("AzureWorkloadSAPHanaPointInTimeRecoveryPoint".equals(discriminatorValue)) {
                    return AzureWorkloadSapHanaPointInTimeRecoveryPoint.fromJson(readerToUse.reset());
                } else if ("AzureWorkloadSAPAsePointInTimeRecoveryPoint".equals(discriminatorValue)) {
                    return AzureWorkloadSapAsePointInTimeRecoveryPoint.fromJson(readerToUse.reset());
                } else {
                    return fromJsonKnownDiscriminator(readerToUse.reset());
                }
            }
        });
    }

    static AzureWorkloadPointInTimeRecoveryPoint fromJsonKnownDiscriminator(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            AzureWorkloadPointInTimeRecoveryPoint deserializedAzureWorkloadPointInTimeRecoveryPoint
                = new AzureWorkloadPointInTimeRecoveryPoint();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("recoveryPointTimeInUTC".equals(fieldName)) {
                    deserializedAzureWorkloadPointInTimeRecoveryPoint.withRecoveryPointTimeInUtc(reader
                        .getNullable(nonNullReader -> CoreUtils.parseBestOffsetDateTime(nonNullReader.getString())));
                } else if ("type".equals(fieldName)) {
                    deserializedAzureWorkloadPointInTimeRecoveryPoint
                        .withType(RestorePointType.fromString(reader.getString()));
                } else if ("recoveryPointTierDetails".equals(fieldName)) {
                    List<RecoveryPointTierInformationV2> recoveryPointTierDetails
                        = reader.readArray(reader1 -> RecoveryPointTierInformationV2.fromJson(reader1));
                    deserializedAzureWorkloadPointInTimeRecoveryPoint
                        .withRecoveryPointTierDetails(recoveryPointTierDetails);
                } else if ("recoveryPointMoveReadinessInfo".equals(fieldName)) {
                    Map<String, RecoveryPointMoveReadinessInfo> recoveryPointMoveReadinessInfo
                        = reader.readMap(reader1 -> RecoveryPointMoveReadinessInfo.fromJson(reader1));
                    deserializedAzureWorkloadPointInTimeRecoveryPoint
                        .withRecoveryPointMoveReadinessInfo(recoveryPointMoveReadinessInfo);
                } else if ("recoveryPointProperties".equals(fieldName)) {
                    deserializedAzureWorkloadPointInTimeRecoveryPoint
                        .withRecoveryPointProperties(RecoveryPointProperties.fromJson(reader));
                } else if ("objectType".equals(fieldName)) {
                    deserializedAzureWorkloadPointInTimeRecoveryPoint.objectType = reader.getString();
                } else if ("timeRanges".equals(fieldName)) {
                    List<PointInTimeRange> timeRanges = reader.readArray(reader1 -> PointInTimeRange.fromJson(reader1));
                    deserializedAzureWorkloadPointInTimeRecoveryPoint.timeRanges = timeRanges;
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedAzureWorkloadPointInTimeRecoveryPoint;
        });
    }
}
