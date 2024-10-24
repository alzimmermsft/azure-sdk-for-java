// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
//
// Code generated by Microsoft (R) AutoRest Code Generator.
// Changes may cause incorrect behavior and will be lost if the code is regenerated.

package com.azure.search.documents.indexes.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** An object that contains information about the matches that were found, and related metadata. */
@Fluent
public final class CustomEntity implements JsonSerializable<CustomEntity> {
    /*
     * The top-level entity descriptor. Matches in the skill output will be grouped by this name, and it should
     * represent the "normalized" form of the text being found.
     */
    private final String name;

    /*
     * This field can be used as a passthrough for custom metadata about the matched text(s). The value of this field
     * will appear with every match of its entity in the skill output.
     */
    private String description;

    /*
     * This field can be used as a passthrough for custom metadata about the matched text(s). The value of this field
     * will appear with every match of its entity in the skill output.
     */
    private String type;

    /*
     * This field can be used as a passthrough for custom metadata about the matched text(s). The value of this field
     * will appear with every match of its entity in the skill output.
     */
    private String subtype;

    /*
     * This field can be used as a passthrough for custom metadata about the matched text(s). The value of this field
     * will appear with every match of its entity in the skill output.
     */
    private String id;

    /*
     * Defaults to false. Boolean value denoting whether comparisons with the entity name should be sensitive to
     * character casing. Sample case insensitive matches of "Microsoft" could be: microsoft, microSoft, MICROSOFT.
     */
    private Boolean caseSensitive;

    /*
     * Defaults to false. Boolean value denoting whether comparisons with the entity name should be sensitive to
     * accent.
     */
    private Boolean accentSensitive;

    /*
     * Defaults to 0. Maximum value of 5. Denotes the acceptable number of divergent characters that would still
     * constitute a match with the entity name. The smallest possible fuzziness for any given match is returned. For
     * instance, if the edit distance is set to 3, "Windows10" would still match "Windows", "Windows10" and "Windows
     * 7". When case sensitivity is set to false, case differences do NOT count towards fuzziness tolerance, but
     * otherwise do.
     */
    private Integer fuzzyEditDistance;

    /*
     * Changes the default case sensitivity value for this entity. It be used to change the default value of all
     * aliases caseSensitive values.
     */
    private Boolean defaultCaseSensitive;

    /*
     * Changes the default accent sensitivity value for this entity. It be used to change the default value of all
     * aliases accentSensitive values.
     */
    private Boolean defaultAccentSensitive;

    /*
     * Changes the default fuzzy edit distance value for this entity. It can be used to change the default value of all
     * aliases fuzzyEditDistance values.
     */
    private Integer defaultFuzzyEditDistance;

    /*
     * An array of complex objects that can be used to specify alternative spellings or synonyms to the root entity
     * name.
     */
    private List<CustomEntityAlias> aliases;

    /**
     * Creates an instance of CustomEntity class.
     *
     * @param name the name value to set.
     */
    public CustomEntity(String name) {
        this.name = name;
    }

    /**
     * Get the name property: The top-level entity descriptor. Matches in the skill output will be grouped by this name,
     * and it should represent the "normalized" form of the text being found.
     *
     * @return the name value.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the description property: This field can be used as a passthrough for custom metadata about the matched
     * text(s). The value of this field will appear with every match of its entity in the skill output.
     *
     * @return the description value.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description property: This field can be used as a passthrough for custom metadata about the matched
     * text(s). The value of this field will appear with every match of its entity in the skill output.
     *
     * @param description the description value to set.
     * @return the CustomEntity object itself.
     */
    public CustomEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get the type property: This field can be used as a passthrough for custom metadata about the matched text(s). The
     * value of this field will appear with every match of its entity in the skill output.
     *
     * @return the type value.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Set the type property: This field can be used as a passthrough for custom metadata about the matched text(s). The
     * value of this field will appear with every match of its entity in the skill output.
     *
     * @param type the type value to set.
     * @return the CustomEntity object itself.
     */
    public CustomEntity setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Get the subtype property: This field can be used as a passthrough for custom metadata about the matched text(s).
     * The value of this field will appear with every match of its entity in the skill output.
     *
     * @return the subtype value.
     */
    public String getSubtype() {
        return this.subtype;
    }

    /**
     * Set the subtype property: This field can be used as a passthrough for custom metadata about the matched text(s).
     * The value of this field will appear with every match of its entity in the skill output.
     *
     * @param subtype the subtype value to set.
     * @return the CustomEntity object itself.
     */
    public CustomEntity setSubtype(String subtype) {
        this.subtype = subtype;
        return this;
    }

    /**
     * Get the id property: This field can be used as a passthrough for custom metadata about the matched text(s). The
     * value of this field will appear with every match of its entity in the skill output.
     *
     * @return the id value.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the id property: This field can be used as a passthrough for custom metadata about the matched text(s). The
     * value of this field will appear with every match of its entity in the skill output.
     *
     * @param id the id value to set.
     * @return the CustomEntity object itself.
     */
    public CustomEntity setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Get the caseSensitive property: Defaults to false. Boolean value denoting whether comparisons with the entity
     * name should be sensitive to character casing. Sample case insensitive matches of "Microsoft" could be: microsoft,
     * microSoft, MICROSOFT.
     *
     * @return the caseSensitive value.
     */
    public Boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    /**
     * Set the caseSensitive property: Defaults to false. Boolean value denoting whether comparisons with the entity
     * name should be sensitive to character casing. Sample case insensitive matches of "Microsoft" could be: microsoft,
     * microSoft, MICROSOFT.
     *
     * @param caseSensitive the caseSensitive value to set.
     * @return the CustomEntity object itself.
     */
    public CustomEntity setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        return this;
    }

    /**
     * Get the accentSensitive property: Defaults to false. Boolean value denoting whether comparisons with the entity
     * name should be sensitive to accent.
     *
     * @return the accentSensitive value.
     */
    public Boolean isAccentSensitive() {
        return this.accentSensitive;
    }

    /**
     * Set the accentSensitive property: Defaults to false. Boolean value denoting whether comparisons with the entity
     * name should be sensitive to accent.
     *
     * @param accentSensitive the accentSensitive value to set.
     * @return the CustomEntity object itself.
     */
    public CustomEntity setAccentSensitive(Boolean accentSensitive) {
        this.accentSensitive = accentSensitive;
        return this;
    }

    /**
     * Get the fuzzyEditDistance property: Defaults to 0. Maximum value of 5. Denotes the acceptable number of divergent
     * characters that would still constitute a match with the entity name. The smallest possible fuzziness for any
     * given match is returned. For instance, if the edit distance is set to 3, "Windows10" would still match "Windows",
     * "Windows10" and "Windows 7". When case sensitivity is set to false, case differences do NOT count towards
     * fuzziness tolerance, but otherwise do.
     *
     * @return the fuzzyEditDistance value.
     */
    public Integer getFuzzyEditDistance() {
        return this.fuzzyEditDistance;
    }

    /**
     * Set the fuzzyEditDistance property: Defaults to 0. Maximum value of 5. Denotes the acceptable number of divergent
     * characters that would still constitute a match with the entity name. The smallest possible fuzziness for any
     * given match is returned. For instance, if the edit distance is set to 3, "Windows10" would still match "Windows",
     * "Windows10" and "Windows 7". When case sensitivity is set to false, case differences do NOT count towards
     * fuzziness tolerance, but otherwise do.
     *
     * @param fuzzyEditDistance the fuzzyEditDistance value to set.
     * @return the CustomEntity object itself.
     */
    public CustomEntity setFuzzyEditDistance(Integer fuzzyEditDistance) {
        this.fuzzyEditDistance = fuzzyEditDistance;
        return this;
    }

    /**
     * Get the defaultCaseSensitive property: Changes the default case sensitivity value for this entity. It be used to
     * change the default value of all aliases caseSensitive values.
     *
     * @return the defaultCaseSensitive value.
     */
    public Boolean isDefaultCaseSensitive() {
        return this.defaultCaseSensitive;
    }

    /**
     * Set the defaultCaseSensitive property: Changes the default case sensitivity value for this entity. It be used to
     * change the default value of all aliases caseSensitive values.
     *
     * @param defaultCaseSensitive the defaultCaseSensitive value to set.
     * @return the CustomEntity object itself.
     */
    public CustomEntity setDefaultCaseSensitive(Boolean defaultCaseSensitive) {
        this.defaultCaseSensitive = defaultCaseSensitive;
        return this;
    }

    /**
     * Get the defaultAccentSensitive property: Changes the default accent sensitivity value for this entity. It be used
     * to change the default value of all aliases accentSensitive values.
     *
     * @return the defaultAccentSensitive value.
     */
    public Boolean isDefaultAccentSensitive() {
        return this.defaultAccentSensitive;
    }

    /**
     * Set the defaultAccentSensitive property: Changes the default accent sensitivity value for this entity. It be used
     * to change the default value of all aliases accentSensitive values.
     *
     * @param defaultAccentSensitive the defaultAccentSensitive value to set.
     * @return the CustomEntity object itself.
     */
    public CustomEntity setDefaultAccentSensitive(Boolean defaultAccentSensitive) {
        this.defaultAccentSensitive = defaultAccentSensitive;
        return this;
    }

    /**
     * Get the defaultFuzzyEditDistance property: Changes the default fuzzy edit distance value for this entity. It can
     * be used to change the default value of all aliases fuzzyEditDistance values.
     *
     * @return the defaultFuzzyEditDistance value.
     */
    public Integer getDefaultFuzzyEditDistance() {
        return this.defaultFuzzyEditDistance;
    }

    /**
     * Set the defaultFuzzyEditDistance property: Changes the default fuzzy edit distance value for this entity. It can
     * be used to change the default value of all aliases fuzzyEditDistance values.
     *
     * @param defaultFuzzyEditDistance the defaultFuzzyEditDistance value to set.
     * @return the CustomEntity object itself.
     */
    public CustomEntity setDefaultFuzzyEditDistance(Integer defaultFuzzyEditDistance) {
        this.defaultFuzzyEditDistance = defaultFuzzyEditDistance;
        return this;
    }

    /**
     * Get the aliases property: An array of complex objects that can be used to specify alternative spellings or
     * synonyms to the root entity name.
     *
     * @return the aliases value.
     */
    public List<CustomEntityAlias> getAliases() {
        return this.aliases;
    }

    /**
     * Set the aliases property: An array of complex objects that can be used to specify alternative spellings or
     * synonyms to the root entity name.
     *
     * @param aliases the aliases value to set.
     * @return the CustomEntity object itself.
     */
    public CustomEntity setAliases(List<CustomEntityAlias> aliases) {
        this.aliases = aliases;
        return this;
    }

    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("name", this.name);
        jsonWriter.writeStringField("description", this.description);
        jsonWriter.writeStringField("type", this.type);
        jsonWriter.writeStringField("subtype", this.subtype);
        jsonWriter.writeStringField("id", this.id);
        jsonWriter.writeBooleanField("caseSensitive", this.caseSensitive);
        jsonWriter.writeBooleanField("accentSensitive", this.accentSensitive);
        jsonWriter.writeNumberField("fuzzyEditDistance", this.fuzzyEditDistance);
        jsonWriter.writeBooleanField("defaultCaseSensitive", this.defaultCaseSensitive);
        jsonWriter.writeBooleanField("defaultAccentSensitive", this.defaultAccentSensitive);
        jsonWriter.writeNumberField("defaultFuzzyEditDistance", this.defaultFuzzyEditDistance);
        jsonWriter.writeArrayField("aliases", this.aliases, (writer, element) -> writer.writeJson(element));
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of CustomEntity from the JsonReader.
     *
     * @param jsonReader The JsonReader being read.
     * @return An instance of CustomEntity if the JsonReader was pointing to an instance of it, or null if it was
     *     pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the CustomEntity.
     */
    public static CustomEntity fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(
                reader -> {
                    boolean nameFound = false;
                    String name = null;
                    String description = null;
                    String type = null;
                    String subtype = null;
                    String id = null;
                    Boolean caseSensitive = null;
                    Boolean accentSensitive = null;
                    Integer fuzzyEditDistance = null;
                    Boolean defaultCaseSensitive = null;
                    Boolean defaultAccentSensitive = null;
                    Integer defaultFuzzyEditDistance = null;
                    List<CustomEntityAlias> aliases = null;
                    while (reader.nextToken() != JsonToken.END_OBJECT) {
                        String fieldName = reader.getFieldName();
                        reader.nextToken();

                        if ("name".equals(fieldName)) {
                            name = reader.getString();
                            nameFound = true;
                        } else if ("description".equals(fieldName)) {
                            description = reader.getString();
                        } else if ("type".equals(fieldName)) {
                            type = reader.getString();
                        } else if ("subtype".equals(fieldName)) {
                            subtype = reader.getString();
                        } else if ("id".equals(fieldName)) {
                            id = reader.getString();
                        } else if ("caseSensitive".equals(fieldName)) {
                            caseSensitive = reader.getNullable(JsonReader::getBoolean);
                        } else if ("accentSensitive".equals(fieldName)) {
                            accentSensitive = reader.getNullable(JsonReader::getBoolean);
                        } else if ("fuzzyEditDistance".equals(fieldName)) {
                            fuzzyEditDistance = reader.getNullable(JsonReader::getInt);
                        } else if ("defaultCaseSensitive".equals(fieldName)) {
                            defaultCaseSensitive = reader.getNullable(JsonReader::getBoolean);
                        } else if ("defaultAccentSensitive".equals(fieldName)) {
                            defaultAccentSensitive = reader.getNullable(JsonReader::getBoolean);
                        } else if ("defaultFuzzyEditDistance".equals(fieldName)) {
                            defaultFuzzyEditDistance = reader.getNullable(JsonReader::getInt);
                        } else if ("aliases".equals(fieldName)) {
                            aliases = reader.readArray(reader1 -> CustomEntityAlias.fromJson(reader1));
                        } else {
                            reader.skipChildren();
                        }
                    }
                    if (nameFound) {
                        CustomEntity deserializedValue = new CustomEntity(name);
                        deserializedValue.description = description;
                        deserializedValue.type = type;
                        deserializedValue.subtype = subtype;
                        deserializedValue.id = id;
                        deserializedValue.caseSensitive = caseSensitive;
                        deserializedValue.accentSensitive = accentSensitive;
                        deserializedValue.fuzzyEditDistance = fuzzyEditDistance;
                        deserializedValue.defaultCaseSensitive = defaultCaseSensitive;
                        deserializedValue.defaultAccentSensitive = defaultAccentSensitive;
                        deserializedValue.defaultFuzzyEditDistance = defaultFuzzyEditDistance;
                        deserializedValue.aliases = aliases;

                        return deserializedValue;
                    }
                    List<String> missingProperties = new ArrayList<>();
                    if (!nameFound) {
                        missingProperties.add("name");
                    }

                    throw new IllegalStateException(
                            "Missing required property/properties: " + String.join(", ", missingProperties));
                });
    }
}
