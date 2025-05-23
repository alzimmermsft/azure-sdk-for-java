// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicessiterecovery.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;
import java.util.List;

/**
 * The IpConfigDetails model.
 */
@Fluent
public final class IpConfigDetails implements JsonSerializable<IpConfigDetails> {
    /*
     * The name property.
     */
    private String name;

    /*
     * The isPrimary property.
     */
    private Boolean isPrimary;

    /*
     * The subnetName property.
     */
    private String subnetName;

    /*
     * The staticIPAddress property.
     */
    private String staticIpAddress;

    /*
     * The ipAddressType property.
     */
    private String ipAddressType;

    /*
     * The isSeletedForFailover property.
     */
    private Boolean isSeletedForFailover;

    /*
     * The recoverySubnetName property.
     */
    private String recoverySubnetName;

    /*
     * The recoveryStaticIPAddress property.
     */
    private String recoveryStaticIpAddress;

    /*
     * The recoveryIPAddressType property.
     */
    private String recoveryIpAddressType;

    /*
     * The recoveryPublicIPAddressId property.
     */
    private String recoveryPublicIpAddressId;

    /*
     * The recoveryLBBackendAddressPoolIds property.
     */
    private List<String> recoveryLBBackendAddressPoolIds;

    /*
     * The tfoSubnetName property.
     */
    private String tfoSubnetName;

    /*
     * The tfoStaticIPAddress property.
     */
    private String tfoStaticIpAddress;

    /*
     * The tfoPublicIPAddressId property.
     */
    private String tfoPublicIpAddressId;

    /*
     * The tfoLBBackendAddressPoolIds property.
     */
    private List<String> tfoLBBackendAddressPoolIds;

    /**
     * Creates an instance of IpConfigDetails class.
     */
    public IpConfigDetails() {
    }

    /**
     * Get the name property: The name property.
     * 
     * @return the name value.
     */
    public String name() {
        return this.name;
    }

    /**
     * Set the name property: The name property.
     * 
     * @param name the name value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the isPrimary property: The isPrimary property.
     * 
     * @return the isPrimary value.
     */
    public Boolean isPrimary() {
        return this.isPrimary;
    }

    /**
     * Set the isPrimary property: The isPrimary property.
     * 
     * @param isPrimary the isPrimary value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
        return this;
    }

    /**
     * Get the subnetName property: The subnetName property.
     * 
     * @return the subnetName value.
     */
    public String subnetName() {
        return this.subnetName;
    }

    /**
     * Set the subnetName property: The subnetName property.
     * 
     * @param subnetName the subnetName value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withSubnetName(String subnetName) {
        this.subnetName = subnetName;
        return this;
    }

    /**
     * Get the staticIpAddress property: The staticIPAddress property.
     * 
     * @return the staticIpAddress value.
     */
    public String staticIpAddress() {
        return this.staticIpAddress;
    }

    /**
     * Set the staticIpAddress property: The staticIPAddress property.
     * 
     * @param staticIpAddress the staticIpAddress value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withStaticIpAddress(String staticIpAddress) {
        this.staticIpAddress = staticIpAddress;
        return this;
    }

    /**
     * Get the ipAddressType property: The ipAddressType property.
     * 
     * @return the ipAddressType value.
     */
    public String ipAddressType() {
        return this.ipAddressType;
    }

    /**
     * Set the ipAddressType property: The ipAddressType property.
     * 
     * @param ipAddressType the ipAddressType value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withIpAddressType(String ipAddressType) {
        this.ipAddressType = ipAddressType;
        return this;
    }

    /**
     * Get the isSeletedForFailover property: The isSeletedForFailover property.
     * 
     * @return the isSeletedForFailover value.
     */
    public Boolean isSeletedForFailover() {
        return this.isSeletedForFailover;
    }

    /**
     * Set the isSeletedForFailover property: The isSeletedForFailover property.
     * 
     * @param isSeletedForFailover the isSeletedForFailover value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withIsSeletedForFailover(Boolean isSeletedForFailover) {
        this.isSeletedForFailover = isSeletedForFailover;
        return this;
    }

    /**
     * Get the recoverySubnetName property: The recoverySubnetName property.
     * 
     * @return the recoverySubnetName value.
     */
    public String recoverySubnetName() {
        return this.recoverySubnetName;
    }

    /**
     * Set the recoverySubnetName property: The recoverySubnetName property.
     * 
     * @param recoverySubnetName the recoverySubnetName value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withRecoverySubnetName(String recoverySubnetName) {
        this.recoverySubnetName = recoverySubnetName;
        return this;
    }

    /**
     * Get the recoveryStaticIpAddress property: The recoveryStaticIPAddress property.
     * 
     * @return the recoveryStaticIpAddress value.
     */
    public String recoveryStaticIpAddress() {
        return this.recoveryStaticIpAddress;
    }

    /**
     * Set the recoveryStaticIpAddress property: The recoveryStaticIPAddress property.
     * 
     * @param recoveryStaticIpAddress the recoveryStaticIpAddress value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withRecoveryStaticIpAddress(String recoveryStaticIpAddress) {
        this.recoveryStaticIpAddress = recoveryStaticIpAddress;
        return this;
    }

    /**
     * Get the recoveryIpAddressType property: The recoveryIPAddressType property.
     * 
     * @return the recoveryIpAddressType value.
     */
    public String recoveryIpAddressType() {
        return this.recoveryIpAddressType;
    }

    /**
     * Set the recoveryIpAddressType property: The recoveryIPAddressType property.
     * 
     * @param recoveryIpAddressType the recoveryIpAddressType value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withRecoveryIpAddressType(String recoveryIpAddressType) {
        this.recoveryIpAddressType = recoveryIpAddressType;
        return this;
    }

    /**
     * Get the recoveryPublicIpAddressId property: The recoveryPublicIPAddressId property.
     * 
     * @return the recoveryPublicIpAddressId value.
     */
    public String recoveryPublicIpAddressId() {
        return this.recoveryPublicIpAddressId;
    }

    /**
     * Set the recoveryPublicIpAddressId property: The recoveryPublicIPAddressId property.
     * 
     * @param recoveryPublicIpAddressId the recoveryPublicIpAddressId value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withRecoveryPublicIpAddressId(String recoveryPublicIpAddressId) {
        this.recoveryPublicIpAddressId = recoveryPublicIpAddressId;
        return this;
    }

    /**
     * Get the recoveryLBBackendAddressPoolIds property: The recoveryLBBackendAddressPoolIds property.
     * 
     * @return the recoveryLBBackendAddressPoolIds value.
     */
    public List<String> recoveryLBBackendAddressPoolIds() {
        return this.recoveryLBBackendAddressPoolIds;
    }

    /**
     * Set the recoveryLBBackendAddressPoolIds property: The recoveryLBBackendAddressPoolIds property.
     * 
     * @param recoveryLBBackendAddressPoolIds the recoveryLBBackendAddressPoolIds value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withRecoveryLBBackendAddressPoolIds(List<String> recoveryLBBackendAddressPoolIds) {
        this.recoveryLBBackendAddressPoolIds = recoveryLBBackendAddressPoolIds;
        return this;
    }

    /**
     * Get the tfoSubnetName property: The tfoSubnetName property.
     * 
     * @return the tfoSubnetName value.
     */
    public String tfoSubnetName() {
        return this.tfoSubnetName;
    }

    /**
     * Set the tfoSubnetName property: The tfoSubnetName property.
     * 
     * @param tfoSubnetName the tfoSubnetName value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withTfoSubnetName(String tfoSubnetName) {
        this.tfoSubnetName = tfoSubnetName;
        return this;
    }

    /**
     * Get the tfoStaticIpAddress property: The tfoStaticIPAddress property.
     * 
     * @return the tfoStaticIpAddress value.
     */
    public String tfoStaticIpAddress() {
        return this.tfoStaticIpAddress;
    }

    /**
     * Set the tfoStaticIpAddress property: The tfoStaticIPAddress property.
     * 
     * @param tfoStaticIpAddress the tfoStaticIpAddress value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withTfoStaticIpAddress(String tfoStaticIpAddress) {
        this.tfoStaticIpAddress = tfoStaticIpAddress;
        return this;
    }

    /**
     * Get the tfoPublicIpAddressId property: The tfoPublicIPAddressId property.
     * 
     * @return the tfoPublicIpAddressId value.
     */
    public String tfoPublicIpAddressId() {
        return this.tfoPublicIpAddressId;
    }

    /**
     * Set the tfoPublicIpAddressId property: The tfoPublicIPAddressId property.
     * 
     * @param tfoPublicIpAddressId the tfoPublicIpAddressId value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withTfoPublicIpAddressId(String tfoPublicIpAddressId) {
        this.tfoPublicIpAddressId = tfoPublicIpAddressId;
        return this;
    }

    /**
     * Get the tfoLBBackendAddressPoolIds property: The tfoLBBackendAddressPoolIds property.
     * 
     * @return the tfoLBBackendAddressPoolIds value.
     */
    public List<String> tfoLBBackendAddressPoolIds() {
        return this.tfoLBBackendAddressPoolIds;
    }

    /**
     * Set the tfoLBBackendAddressPoolIds property: The tfoLBBackendAddressPoolIds property.
     * 
     * @param tfoLBBackendAddressPoolIds the tfoLBBackendAddressPoolIds value to set.
     * @return the IpConfigDetails object itself.
     */
    public IpConfigDetails withTfoLBBackendAddressPoolIds(List<String> tfoLBBackendAddressPoolIds) {
        this.tfoLBBackendAddressPoolIds = tfoLBBackendAddressPoolIds;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("name", this.name);
        jsonWriter.writeBooleanField("isPrimary", this.isPrimary);
        jsonWriter.writeStringField("subnetName", this.subnetName);
        jsonWriter.writeStringField("staticIPAddress", this.staticIpAddress);
        jsonWriter.writeStringField("ipAddressType", this.ipAddressType);
        jsonWriter.writeBooleanField("isSeletedForFailover", this.isSeletedForFailover);
        jsonWriter.writeStringField("recoverySubnetName", this.recoverySubnetName);
        jsonWriter.writeStringField("recoveryStaticIPAddress", this.recoveryStaticIpAddress);
        jsonWriter.writeStringField("recoveryIPAddressType", this.recoveryIpAddressType);
        jsonWriter.writeStringField("recoveryPublicIPAddressId", this.recoveryPublicIpAddressId);
        jsonWriter.writeArrayField("recoveryLBBackendAddressPoolIds", this.recoveryLBBackendAddressPoolIds,
            (writer, element) -> writer.writeString(element));
        jsonWriter.writeStringField("tfoSubnetName", this.tfoSubnetName);
        jsonWriter.writeStringField("tfoStaticIPAddress", this.tfoStaticIpAddress);
        jsonWriter.writeStringField("tfoPublicIPAddressId", this.tfoPublicIpAddressId);
        jsonWriter.writeArrayField("tfoLBBackendAddressPoolIds", this.tfoLBBackendAddressPoolIds,
            (writer, element) -> writer.writeString(element));
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of IpConfigDetails from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of IpConfigDetails if the JsonReader was pointing to an instance of it, or null if it was
     * pointing to JSON null.
     * @throws IOException If an error occurs while reading the IpConfigDetails.
     */
    public static IpConfigDetails fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            IpConfigDetails deserializedIpConfigDetails = new IpConfigDetails();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("name".equals(fieldName)) {
                    deserializedIpConfigDetails.name = reader.getString();
                } else if ("isPrimary".equals(fieldName)) {
                    deserializedIpConfigDetails.isPrimary = reader.getNullable(JsonReader::getBoolean);
                } else if ("subnetName".equals(fieldName)) {
                    deserializedIpConfigDetails.subnetName = reader.getString();
                } else if ("staticIPAddress".equals(fieldName)) {
                    deserializedIpConfigDetails.staticIpAddress = reader.getString();
                } else if ("ipAddressType".equals(fieldName)) {
                    deserializedIpConfigDetails.ipAddressType = reader.getString();
                } else if ("isSeletedForFailover".equals(fieldName)) {
                    deserializedIpConfigDetails.isSeletedForFailover = reader.getNullable(JsonReader::getBoolean);
                } else if ("recoverySubnetName".equals(fieldName)) {
                    deserializedIpConfigDetails.recoverySubnetName = reader.getString();
                } else if ("recoveryStaticIPAddress".equals(fieldName)) {
                    deserializedIpConfigDetails.recoveryStaticIpAddress = reader.getString();
                } else if ("recoveryIPAddressType".equals(fieldName)) {
                    deserializedIpConfigDetails.recoveryIpAddressType = reader.getString();
                } else if ("recoveryPublicIPAddressId".equals(fieldName)) {
                    deserializedIpConfigDetails.recoveryPublicIpAddressId = reader.getString();
                } else if ("recoveryLBBackendAddressPoolIds".equals(fieldName)) {
                    List<String> recoveryLBBackendAddressPoolIds = reader.readArray(reader1 -> reader1.getString());
                    deserializedIpConfigDetails.recoveryLBBackendAddressPoolIds = recoveryLBBackendAddressPoolIds;
                } else if ("tfoSubnetName".equals(fieldName)) {
                    deserializedIpConfigDetails.tfoSubnetName = reader.getString();
                } else if ("tfoStaticIPAddress".equals(fieldName)) {
                    deserializedIpConfigDetails.tfoStaticIpAddress = reader.getString();
                } else if ("tfoPublicIPAddressId".equals(fieldName)) {
                    deserializedIpConfigDetails.tfoPublicIpAddressId = reader.getString();
                } else if ("tfoLBBackendAddressPoolIds".equals(fieldName)) {
                    List<String> tfoLBBackendAddressPoolIds = reader.readArray(reader1 -> reader1.getString());
                    deserializedIpConfigDetails.tfoLBBackendAddressPoolIds = tfoLBBackendAddressPoolIds;
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedIpConfigDetails;
        });
    }
}
