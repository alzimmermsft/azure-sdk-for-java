// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.apimanagement.fluent.models;

import com.azure.core.annotation.Immutable;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import com.azure.resourcemanager.apimanagement.models.GatewaySku;
import com.azure.resourcemanager.apimanagement.models.GatewaySkuCapacity;
import java.io.IOException;

/**
 * Describes an available API Management gateway SKU.
 */
@Immutable
public final class GatewayResourceSkuResultInner implements JsonSerializable<GatewayResourceSkuResultInner> {
    /*
     * The type of resource the SKU applies to.
     */
    private String resourceType;

    /*
     * Specifies API Management gateway SKU.
     */
    private GatewaySku sku;

    /*
     * Specifies the number of API Management gateway units.
     */
    private GatewaySkuCapacity capacity;

    /**
     * Creates an instance of GatewayResourceSkuResultInner class.
     */
    public GatewayResourceSkuResultInner() {
    }

    /**
     * Get the resourceType property: The type of resource the SKU applies to.
     * 
     * @return the resourceType value.
     */
    public String resourceType() {
        return this.resourceType;
    }

    /**
     * Get the sku property: Specifies API Management gateway SKU.
     * 
     * @return the sku value.
     */
    public GatewaySku sku() {
        return this.sku;
    }

    /**
     * Get the capacity property: Specifies the number of API Management gateway units.
     * 
     * @return the capacity value.
     */
    public GatewaySkuCapacity capacity() {
        return this.capacity;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (sku() != null) {
            sku().validate();
        }
        if (capacity() != null) {
            capacity().validate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of GatewayResourceSkuResultInner from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of GatewayResourceSkuResultInner if the JsonReader was pointing to an instance of it, or null
     * if it was pointing to JSON null.
     * @throws IOException If an error occurs while reading the GatewayResourceSkuResultInner.
     */
    public static GatewayResourceSkuResultInner fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            GatewayResourceSkuResultInner deserializedGatewayResourceSkuResultInner
                = new GatewayResourceSkuResultInner();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("resourceType".equals(fieldName)) {
                    deserializedGatewayResourceSkuResultInner.resourceType = reader.getString();
                } else if ("sku".equals(fieldName)) {
                    deserializedGatewayResourceSkuResultInner.sku = GatewaySku.fromJson(reader);
                } else if ("capacity".equals(fieldName)) {
                    deserializedGatewayResourceSkuResultInner.capacity = GatewaySkuCapacity.fromJson(reader);
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedGatewayResourceSkuResultInner;
        });
    }
}
