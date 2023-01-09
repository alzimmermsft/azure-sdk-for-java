// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicessiterecovery.models;

import com.azure.resourcemanager.recoveryservicessiterecovery.fluent.models.ReplicationApplianceInner;

/** An immutable client-side representation of ReplicationAppliance. */
public interface ReplicationAppliance {
    /**
     * Gets the properties property: Appliance related data.
     *
     * @return the properties value.
     */
    ReplicationApplianceProperties properties();

    /**
     * Gets the inner com.azure.resourcemanager.recoveryservicessiterecovery.fluent.models.ReplicationApplianceInner
     * object.
     *
     * @return the inner object.
     */
    ReplicationApplianceInner innerModel();
}