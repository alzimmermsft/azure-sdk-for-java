// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.keyvault.fluent.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.management.ProxyResource;
import com.azure.core.util.logging.ClientLogger;
import com.azure.json.JsonReader;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import com.azure.resourcemanager.keyvault.models.VaultAccessPolicyProperties;
import java.io.IOException;

/**
 * Parameters for updating the access policy in a vault.
 */
@Fluent
public final class VaultAccessPolicyParametersInner extends ProxyResource {
    /*
     * The resource type of the access policy.
     */
    private String location;

    /*
     * Properties of the access policy
     */
    private VaultAccessPolicyProperties properties;

    /*
     * The type of the resource.
     */
    private String type;

    /*
     * The name of the resource.
     */
    private String name;

    /*
     * Fully qualified resource Id for the resource.
     */
    private String id;

    /**
     * Creates an instance of VaultAccessPolicyParametersInner class.
     */
    public VaultAccessPolicyParametersInner() {
    }

    /**
     * Get the location property: The resource type of the access policy.
     * 
     * @return the location value.
     */
    public String location() {
        return this.location;
    }

    /**
     * Get the properties property: Properties of the access policy.
     * 
     * @return the properties value.
     */
    public VaultAccessPolicyProperties properties() {
        return this.properties;
    }

    /**
     * Set the properties property: Properties of the access policy.
     * 
     * @param properties the properties value to set.
     * @return the VaultAccessPolicyParametersInner object itself.
     */
    public VaultAccessPolicyParametersInner withProperties(VaultAccessPolicyProperties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Get the type property: The type of the resource.
     * 
     * @return the type value.
     */
    @Override
    public String type() {
        return this.type;
    }

    /**
     * Get the name property: The name of the resource.
     * 
     * @return the name value.
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Get the id property: Fully qualified resource Id for the resource.
     * 
     * @return the id value.
     */
    @Override
    public String id() {
        return this.id;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (properties() == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException(
                    "Missing required property properties in model VaultAccessPolicyParametersInner"));
        } else {
            properties().validate();
        }
    }

    private static final ClientLogger LOGGER = new ClientLogger(VaultAccessPolicyParametersInner.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeJsonField("properties", this.properties);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of VaultAccessPolicyParametersInner from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of VaultAccessPolicyParametersInner if the JsonReader was pointing to an instance of it, or
     * null if it was pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the VaultAccessPolicyParametersInner.
     */
    public static VaultAccessPolicyParametersInner fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            VaultAccessPolicyParametersInner deserializedVaultAccessPolicyParametersInner
                = new VaultAccessPolicyParametersInner();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("id".equals(fieldName)) {
                    deserializedVaultAccessPolicyParametersInner.id = reader.getString();
                } else if ("name".equals(fieldName)) {
                    deserializedVaultAccessPolicyParametersInner.name = reader.getString();
                } else if ("type".equals(fieldName)) {
                    deserializedVaultAccessPolicyParametersInner.type = reader.getString();
                } else if ("properties".equals(fieldName)) {
                    deserializedVaultAccessPolicyParametersInner.properties
                        = VaultAccessPolicyProperties.fromJson(reader);
                } else if ("location".equals(fieldName)) {
                    deserializedVaultAccessPolicyParametersInner.location = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedVaultAccessPolicyParametersInner;
        });
    }
}
