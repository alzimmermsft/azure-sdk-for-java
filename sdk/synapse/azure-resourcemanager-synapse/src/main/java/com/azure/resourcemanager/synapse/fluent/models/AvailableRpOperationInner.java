// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.fluent.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import com.azure.resourcemanager.synapse.models.AvailableRpOperationDisplayInfo;
import com.azure.resourcemanager.synapse.models.OperationMetaServiceSpecification;
import java.io.IOException;

/**
 * An operation that is available in this resource provider.
 */
@Fluent
public final class AvailableRpOperationInner implements JsonSerializable<AvailableRpOperationInner> {
    /*
     * Display properties of the operation
     */
    private AvailableRpOperationDisplayInfo display;

    /*
     * Whether this operation is a data action
     */
    private String isDataAction;

    /*
     * Operation name
     */
    private String name;

    /*
     * Operation properties
     */
    private OperationMetaPropertyInfo innerProperties;

    /*
     * Operation origin
     */
    private String origin;

    /**
     * Creates an instance of AvailableRpOperationInner class.
     */
    public AvailableRpOperationInner() {
    }

    /**
     * Get the display property: Display properties of the operation.
     * 
     * @return the display value.
     */
    public AvailableRpOperationDisplayInfo display() {
        return this.display;
    }

    /**
     * Set the display property: Display properties of the operation.
     * 
     * @param display the display value to set.
     * @return the AvailableRpOperationInner object itself.
     */
    public AvailableRpOperationInner withDisplay(AvailableRpOperationDisplayInfo display) {
        this.display = display;
        return this;
    }

    /**
     * Get the isDataAction property: Whether this operation is a data action.
     * 
     * @return the isDataAction value.
     */
    public String isDataAction() {
        return this.isDataAction;
    }

    /**
     * Set the isDataAction property: Whether this operation is a data action.
     * 
     * @param isDataAction the isDataAction value to set.
     * @return the AvailableRpOperationInner object itself.
     */
    public AvailableRpOperationInner withIsDataAction(String isDataAction) {
        this.isDataAction = isDataAction;
        return this;
    }

    /**
     * Get the name property: Operation name.
     * 
     * @return the name value.
     */
    public String name() {
        return this.name;
    }

    /**
     * Set the name property: Operation name.
     * 
     * @param name the name value to set.
     * @return the AvailableRpOperationInner object itself.
     */
    public AvailableRpOperationInner withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the innerProperties property: Operation properties.
     * 
     * @return the innerProperties value.
     */
    private OperationMetaPropertyInfo innerProperties() {
        return this.innerProperties;
    }

    /**
     * Get the origin property: Operation origin.
     * 
     * @return the origin value.
     */
    public String origin() {
        return this.origin;
    }

    /**
     * Set the origin property: Operation origin.
     * 
     * @param origin the origin value to set.
     * @return the AvailableRpOperationInner object itself.
     */
    public AvailableRpOperationInner withOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    /**
     * Get the serviceSpecification property: Operation service specification.
     * 
     * @return the serviceSpecification value.
     */
    public OperationMetaServiceSpecification serviceSpecification() {
        return this.innerProperties() == null ? null : this.innerProperties().serviceSpecification();
    }

    /**
     * Set the serviceSpecification property: Operation service specification.
     * 
     * @param serviceSpecification the serviceSpecification value to set.
     * @return the AvailableRpOperationInner object itself.
     */
    public AvailableRpOperationInner withServiceSpecification(OperationMetaServiceSpecification serviceSpecification) {
        if (this.innerProperties() == null) {
            this.innerProperties = new OperationMetaPropertyInfo();
        }
        this.innerProperties().withServiceSpecification(serviceSpecification);
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (display() != null) {
            display().validate();
        }
        if (innerProperties() != null) {
            innerProperties().validate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeJsonField("display", this.display);
        jsonWriter.writeStringField("isDataAction", this.isDataAction);
        jsonWriter.writeStringField("name", this.name);
        jsonWriter.writeJsonField("properties", this.innerProperties);
        jsonWriter.writeStringField("origin", this.origin);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of AvailableRpOperationInner from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of AvailableRpOperationInner if the JsonReader was pointing to an instance of it, or null if
     * it was pointing to JSON null.
     * @throws IOException If an error occurs while reading the AvailableRpOperationInner.
     */
    public static AvailableRpOperationInner fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            AvailableRpOperationInner deserializedAvailableRpOperationInner = new AvailableRpOperationInner();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("display".equals(fieldName)) {
                    deserializedAvailableRpOperationInner.display = AvailableRpOperationDisplayInfo.fromJson(reader);
                } else if ("isDataAction".equals(fieldName)) {
                    deserializedAvailableRpOperationInner.isDataAction = reader.getString();
                } else if ("name".equals(fieldName)) {
                    deserializedAvailableRpOperationInner.name = reader.getString();
                } else if ("properties".equals(fieldName)) {
                    deserializedAvailableRpOperationInner.innerProperties = OperationMetaPropertyInfo.fromJson(reader);
                } else if ("origin".equals(fieldName)) {
                    deserializedAvailableRpOperationInner.origin = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedAvailableRpOperationInner;
        });
    }
}
