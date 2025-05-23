// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.
package com.azure.ai.agents.persistent.models;

import com.azure.core.annotation.Generated;
import com.azure.core.util.ExpandableStringEnum;
import java.util.Collection;

/**
 * Thread operation related streaming events.
 */
public final class ThreadStreamEvent extends ExpandableStringEnum<ThreadStreamEvent> {

    /**
     * Event sent when a new thread is created. The data of this event is of type AgentThread.
     */
    @Generated
    public static final ThreadStreamEvent THREAD_CREATED = fromString("thread.created");

    /**
     * Creates a new instance of ThreadStreamEvent value.
     *
     * @deprecated Use the {@link #fromString(String)} factory method.
     */
    @Generated
    @Deprecated
    public ThreadStreamEvent() {
    }

    /**
     * Creates or finds a ThreadStreamEvent from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding ThreadStreamEvent.
     */
    @Generated
    public static ThreadStreamEvent fromString(String name) {
        return fromString(name, ThreadStreamEvent.class);
    }

    /**
     * Gets known ThreadStreamEvent values.
     *
     * @return known ThreadStreamEvent values.
     */
    @Generated
    public static Collection<ThreadStreamEvent> values() {
        return values(ThreadStreamEvent.class);
    }
}
