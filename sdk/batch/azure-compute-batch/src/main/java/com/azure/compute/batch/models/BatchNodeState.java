// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.
package com.azure.compute.batch.models;

import com.azure.core.annotation.Generated;
import com.azure.core.util.ExpandableStringEnum;
import java.util.Collection;

/**
 * BatchNodeState enums.
 */
public final class BatchNodeState extends ExpandableStringEnum<BatchNodeState> {

    /**
     * The Compute Node is not currently running a Task.
     */
    @Generated
    public static final BatchNodeState IDLE = fromString("idle");

    /**
     * The Compute Node is rebooting.
     */
    @Generated
    public static final BatchNodeState REBOOTING = fromString("rebooting");

    /**
     * The Compute Node is reimaging.
     */
    @Generated
    public static final BatchNodeState REIMAGING = fromString("reimaging");

    /**
     * The Compute Node is running one or more Tasks (other than a StartTask).
     */
    @Generated
    public static final BatchNodeState RUNNING = fromString("running");

    /**
     * The Compute Node cannot be used for Task execution due to errors.
     */
    @Generated
    public static final BatchNodeState UNUSABLE = fromString("unusable");

    /**
     * The Batch service has obtained the underlying virtual machine from Azure Compute, but it has not yet started to
     * join the Pool.
     */
    @Generated
    public static final BatchNodeState CREATING = fromString("creating");

    /**
     * The Batch service is starting on the underlying virtual machine.
     */
    @Generated
    public static final BatchNodeState STARTING = fromString("starting");

    /**
     * The StartTask has started running on the Compute Node, but waitForSuccess is set and the StartTask has not yet
     * completed.
     */
    @Generated
    public static final BatchNodeState WAITING_FOR_START_TASK = fromString("waitingforstarttask");

    /**
     * The StartTask has failed on the Compute Node (and exhausted all retries), and waitForSuccess is set. The Compute
     * Node is not usable for running Tasks.
     */
    @Generated
    public static final BatchNodeState START_TASK_FAILED = fromString("starttaskfailed");

    /**
     * The Batch service has lost contact with the Compute Node, and does not know its true state.
     */
    @Generated
    public static final BatchNodeState UNKNOWN = fromString("unknown");

    /**
     * The Compute Node is leaving the Pool, either because the user explicitly removed it or because the Pool is
     * resizing or autoscaling down.
     */
    @Generated
    public static final BatchNodeState LEAVING_POOL = fromString("leavingpool");

    /**
     * The Compute Node is not currently running a Task, and scheduling of new Tasks to the Compute Node is disabled.
     */
    @Generated
    public static final BatchNodeState OFFLINE = fromString("offline");

    /**
     * The Spot/Low-priority Compute Node has been preempted. Tasks which were running on the Compute Node when it was
     * preempted will be rescheduled when another Compute Node becomes available.
     */
    @Generated
    public static final BatchNodeState PREEMPTED = fromString("preempted");

    /**
     * The Compute Node is undergoing an OS upgrade operation.
     */
    @Generated
    public static final BatchNodeState UPGRADING_OS = fromString("upgradingos");

    /**
     * The Compute Node is deallocated.
     */
    @Generated
    public static final BatchNodeState DEALLOCATED = fromString("deallocated");

    /**
     * The Compute Node is deallocating.
     */
    @Generated
    public static final BatchNodeState DEALLOCATING = fromString("deallocating");

    /**
     * Creates a new instance of BatchNodeState value.
     *
     * @deprecated Use the {@link #fromString(String)} factory method.
     */
    @Generated
    @Deprecated
    public BatchNodeState() {
    }

    /**
     * Creates or finds a BatchNodeState from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding BatchNodeState.
     */
    @Generated
    public static BatchNodeState fromString(String name) {
        return fromString(name, BatchNodeState.class);
    }

    /**
     * Gets known BatchNodeState values.
     *
     * @return known BatchNodeState values.
     */
    @Generated
    public static Collection<BatchNodeState> values() {
        return values(BatchNodeState.class);
    }
}
