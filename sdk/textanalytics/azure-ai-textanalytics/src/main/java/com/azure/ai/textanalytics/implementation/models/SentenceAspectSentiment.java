// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.ai.textanalytics.implementation.models;

import com.azure.core.util.ExpandableStringEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Collection;

/** Defines values for SentenceAspectSentiment. */
public final class SentenceAspectSentiment extends ExpandableStringEnum<SentenceAspectSentiment> {
    /** Static value positive for SentenceAspectSentiment. */
    public static final SentenceAspectSentiment POSITIVE = fromString("positive");

    /** Static value mixed for SentenceAspectSentiment. */
    public static final SentenceAspectSentiment MIXED = fromString("mixed");

    /** Static value negative for SentenceAspectSentiment. */
    public static final SentenceAspectSentiment NEGATIVE = fromString("negative");

    /**
     * Creates or finds a SentenceAspectSentiment from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding SentenceAspectSentiment.
     */
    @JsonCreator
    public static SentenceAspectSentiment fromString(String name) {
        return fromString(name, SentenceAspectSentiment.class);
    }

    /** @return known SentenceAspectSentiment values. */
    public static Collection<SentenceAspectSentiment> values() {
        return values(SentenceAspectSentiment.class);
    }
}
