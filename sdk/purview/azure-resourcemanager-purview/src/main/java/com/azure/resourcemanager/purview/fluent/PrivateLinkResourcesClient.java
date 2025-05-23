// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.purview.fluent;

import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.resourcemanager.purview.fluent.models.PrivateLinkResourceInner;

/**
 * An instance of this class provides access to all the operations defined in PrivateLinkResourcesClient.
 */
public interface PrivateLinkResourcesClient {
    /**
     * Gets a list of privately linkable resources for an account.
     * 
     * Gets a list of privately linkable resources for an account.
     * 
     * @param resourceGroupName The resource group name.
     * @param accountName The name of the account.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a list of privately linkable resources for an account as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<PrivateLinkResourceInner> listByAccount(String resourceGroupName, String accountName);

    /**
     * Gets a list of privately linkable resources for an account.
     * 
     * Gets a list of privately linkable resources for an account.
     * 
     * @param resourceGroupName The resource group name.
     * @param accountName The name of the account.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a list of privately linkable resources for an account as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<PrivateLinkResourceInner> listByAccount(String resourceGroupName, String accountName,
        Context context);

    /**
     * Gets a privately linkable resources for an account with given group identifier.
     * 
     * Gets a privately linkable resources for an account with given group identifier.
     * 
     * @param resourceGroupName The resource group name.
     * @param accountName The name of the account.
     * @param groupId The group identifier.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a privately linkable resources for an account with given group identifier along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<PrivateLinkResourceInner> getByGroupIdWithResponse(String resourceGroupName, String accountName,
        String groupId, Context context);

    /**
     * Gets a privately linkable resources for an account with given group identifier.
     * 
     * Gets a privately linkable resources for an account with given group identifier.
     * 
     * @param resourceGroupName The resource group name.
     * @param accountName The name of the account.
     * @param groupId The group identifier.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a privately linkable resources for an account with given group identifier.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    PrivateLinkResourceInner getByGroupId(String resourceGroupName, String accountName, String groupId);
}
