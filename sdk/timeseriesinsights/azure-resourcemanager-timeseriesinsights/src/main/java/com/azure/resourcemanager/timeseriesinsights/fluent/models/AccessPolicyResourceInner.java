// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.timeseriesinsights.fluent.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.management.ProxyResource;
import com.azure.json.JsonReader;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import com.azure.resourcemanager.timeseriesinsights.models.AccessPolicyRole;
import java.io.IOException;
import java.util.List;

/**
 * An access policy is used to grant users and applications access to the environment. Roles are assigned to service
 * principals in Azure Active Directory. These roles define the actions the principal can perform through the Time
 * Series Insights data plane APIs.
 */
@Fluent
public final class AccessPolicyResourceInner extends ProxyResource {
    /*
     * The properties property.
     */
    private AccessPolicyResourceProperties innerProperties;

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
     * Creates an instance of AccessPolicyResourceInner class.
     */
    public AccessPolicyResourceInner() {
    }

    /**
     * Get the innerProperties property: The properties property.
     * 
     * @return the innerProperties value.
     */
    private AccessPolicyResourceProperties innerProperties() {
        return this.innerProperties;
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
     * Get the principalObjectId property: The objectId of the principal in Azure Active Directory.
     * 
     * @return the principalObjectId value.
     */
    public String principalObjectId() {
        return this.innerProperties() == null ? null : this.innerProperties().principalObjectId();
    }

    /**
     * Set the principalObjectId property: The objectId of the principal in Azure Active Directory.
     * 
     * @param principalObjectId the principalObjectId value to set.
     * @return the AccessPolicyResourceInner object itself.
     */
    public AccessPolicyResourceInner withPrincipalObjectId(String principalObjectId) {
        if (this.innerProperties() == null) {
            this.innerProperties = new AccessPolicyResourceProperties();
        }
        this.innerProperties().withPrincipalObjectId(principalObjectId);
        return this;
    }

    /**
     * Get the description property: An description of the access policy.
     * 
     * @return the description value.
     */
    public String description() {
        return this.innerProperties() == null ? null : this.innerProperties().description();
    }

    /**
     * Set the description property: An description of the access policy.
     * 
     * @param description the description value to set.
     * @return the AccessPolicyResourceInner object itself.
     */
    public AccessPolicyResourceInner withDescription(String description) {
        if (this.innerProperties() == null) {
            this.innerProperties = new AccessPolicyResourceProperties();
        }
        this.innerProperties().withDescription(description);
        return this;
    }

    /**
     * Get the roles property: The list of roles the principal is assigned on the environment.
     * 
     * @return the roles value.
     */
    public List<AccessPolicyRole> roles() {
        return this.innerProperties() == null ? null : this.innerProperties().roles();
    }

    /**
     * Set the roles property: The list of roles the principal is assigned on the environment.
     * 
     * @param roles the roles value to set.
     * @return the AccessPolicyResourceInner object itself.
     */
    public AccessPolicyResourceInner withRoles(List<AccessPolicyRole> roles) {
        if (this.innerProperties() == null) {
            this.innerProperties = new AccessPolicyResourceProperties();
        }
        this.innerProperties().withRoles(roles);
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
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
        jsonWriter.writeJsonField("properties", this.innerProperties);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of AccessPolicyResourceInner from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of AccessPolicyResourceInner if the JsonReader was pointing to an instance of it, or null if
     * it was pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the AccessPolicyResourceInner.
     */
    public static AccessPolicyResourceInner fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            AccessPolicyResourceInner deserializedAccessPolicyResourceInner = new AccessPolicyResourceInner();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("id".equals(fieldName)) {
                    deserializedAccessPolicyResourceInner.id = reader.getString();
                } else if ("name".equals(fieldName)) {
                    deserializedAccessPolicyResourceInner.name = reader.getString();
                } else if ("type".equals(fieldName)) {
                    deserializedAccessPolicyResourceInner.type = reader.getString();
                } else if ("properties".equals(fieldName)) {
                    deserializedAccessPolicyResourceInner.innerProperties
                        = AccessPolicyResourceProperties.fromJson(reader);
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedAccessPolicyResourceInner;
        });
    }
}
