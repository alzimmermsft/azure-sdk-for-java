// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.purestorageblock.fluent.models;

import com.azure.core.annotation.Immutable;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import com.azure.resourcemanager.purestorageblock.models.ServiceInitializationHandle;
import java.io.IOException;

/**
 * Transient information about an on-going connection to an AVS instance.
 */
@Immutable
public final class AvsConnectionInner implements JsonSerializable<AvsConnectionInner> {
    /*
     * Indicates whether service initialization is complete
     */
    private boolean serviceInitializationCompleted;

    /*
     * Encoded service account credentials alongside connection information
     */
    private String serviceInitializationHandleEnc;

    /*
     * Explicit service account credentials
     */
    private ServiceInitializationHandle serviceInitializationHandle;

    /**
     * Creates an instance of AvsConnectionInner class.
     */
    private AvsConnectionInner() {
    }

    /**
     * Get the serviceInitializationCompleted property: Indicates whether service initialization is complete.
     * 
     * @return the serviceInitializationCompleted value.
     */
    public boolean serviceInitializationCompleted() {
        return this.serviceInitializationCompleted;
    }

    /**
     * Get the serviceInitializationHandleEnc property: Encoded service account credentials alongside connection
     * information.
     * 
     * @return the serviceInitializationHandleEnc value.
     */
    public String serviceInitializationHandleEnc() {
        return this.serviceInitializationHandleEnc;
    }

    /**
     * Get the serviceInitializationHandle property: Explicit service account credentials.
     * 
     * @return the serviceInitializationHandle value.
     */
    public ServiceInitializationHandle serviceInitializationHandle() {
        return this.serviceInitializationHandle;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (serviceInitializationHandle() != null) {
            serviceInitializationHandle().validate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeBooleanField("serviceInitializationCompleted", this.serviceInitializationCompleted);
        jsonWriter.writeStringField("serviceInitializationHandleEnc", this.serviceInitializationHandleEnc);
        jsonWriter.writeJsonField("serviceInitializationHandle", this.serviceInitializationHandle);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of AvsConnectionInner from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of AvsConnectionInner if the JsonReader was pointing to an instance of it, or null if it was
     * pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the AvsConnectionInner.
     */
    public static AvsConnectionInner fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            AvsConnectionInner deserializedAvsConnectionInner = new AvsConnectionInner();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("serviceInitializationCompleted".equals(fieldName)) {
                    deserializedAvsConnectionInner.serviceInitializationCompleted = reader.getBoolean();
                } else if ("serviceInitializationHandleEnc".equals(fieldName)) {
                    deserializedAvsConnectionInner.serviceInitializationHandleEnc = reader.getString();
                } else if ("serviceInitializationHandle".equals(fieldName)) {
                    deserializedAvsConnectionInner.serviceInitializationHandle
                        = ServiceInitializationHandle.fromJson(reader);
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedAvsConnectionInner;
        });
    }
}
