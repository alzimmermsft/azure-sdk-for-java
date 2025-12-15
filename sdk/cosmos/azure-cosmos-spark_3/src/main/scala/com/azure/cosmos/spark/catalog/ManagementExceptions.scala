// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.spark.catalog

import com.azure.core.management.exception.ManagementException

private[spark] object ManagementExceptions {
    private val notFoundCode = "NotFound"
    private val badRequestCode = "BadRequest"

    def isNotFoundException(throwable: Throwable): Boolean = {
        throwable match {
            case managementException: ManagementException => notFoundCode.equalsIgnoreCase(managementException.getValue.getCode)
            case _ => false
        }
    }

    def isBadRequestException(throwable: Throwable): Boolean = {
        throwable match {
            case managementException: ManagementException => badRequestCode.equalsIgnoreCase(managementException.getValue.getCode)
            case _ => false
        }
    }
}
