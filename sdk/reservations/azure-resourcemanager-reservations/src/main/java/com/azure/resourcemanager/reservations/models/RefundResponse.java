// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.reservations.models;

import com.azure.resourcemanager.reservations.fluent.models.RefundResponseInner;

/** An immutable client-side representation of RefundResponse. */
public interface RefundResponse {
    /**
     * Gets the id property: Fully qualified identifier of the reservation being returned.
     *
     * @return the id value.
     */
    String id();

    /**
     * Gets the properties property: The refund properties of reservation.
     *
     * @return the properties value.
     */
    RefundResponseProperties properties();

    /**
     * Gets the inner com.azure.resourcemanager.reservations.fluent.models.RefundResponseInner object.
     *
     * @return the inner object.
     */
    RefundResponseInner innerModel();
}