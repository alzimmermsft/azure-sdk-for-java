// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.dell.storage.implementation;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.core.util.Context;
import com.azure.core.util.logging.ClientLogger;
import com.azure.resourcemanager.dell.storage.fluent.FileSystemsClient;
import com.azure.resourcemanager.dell.storage.fluent.models.FileSystemResourceInner;
import com.azure.resourcemanager.dell.storage.models.FileSystemResource;
import com.azure.resourcemanager.dell.storage.models.FileSystems;

public final class FileSystemsImpl implements FileSystems {
    private static final ClientLogger LOGGER = new ClientLogger(FileSystemsImpl.class);

    private final FileSystemsClient innerClient;

    private final com.azure.resourcemanager.dell.storage.DellStorageManager serviceManager;

    public FileSystemsImpl(FileSystemsClient innerClient,
        com.azure.resourcemanager.dell.storage.DellStorageManager serviceManager) {
        this.innerClient = innerClient;
        this.serviceManager = serviceManager;
    }

    public Response<FileSystemResource> getByResourceGroupWithResponse(String resourceGroupName, String filesystemName,
        Context context) {
        Response<FileSystemResourceInner> inner
            = this.serviceClient().getByResourceGroupWithResponse(resourceGroupName, filesystemName, context);
        if (inner != null) {
            return new SimpleResponse<>(inner.getRequest(), inner.getStatusCode(), inner.getHeaders(),
                new FileSystemResourceImpl(inner.getValue(), this.manager()));
        } else {
            return null;
        }
    }

    public FileSystemResource getByResourceGroup(String resourceGroupName, String filesystemName) {
        FileSystemResourceInner inner = this.serviceClient().getByResourceGroup(resourceGroupName, filesystemName);
        if (inner != null) {
            return new FileSystemResourceImpl(inner, this.manager());
        } else {
            return null;
        }
    }

    public void deleteByResourceGroup(String resourceGroupName, String filesystemName) {
        this.serviceClient().delete(resourceGroupName, filesystemName);
    }

    public void delete(String resourceGroupName, String filesystemName, Context context) {
        this.serviceClient().delete(resourceGroupName, filesystemName, context);
    }

    public PagedIterable<FileSystemResource> listByResourceGroup(String resourceGroupName) {
        PagedIterable<FileSystemResourceInner> inner = this.serviceClient().listByResourceGroup(resourceGroupName);
        return ResourceManagerUtils.mapPage(inner, inner1 -> new FileSystemResourceImpl(inner1, this.manager()));
    }

    public PagedIterable<FileSystemResource> listByResourceGroup(String resourceGroupName, Context context) {
        PagedIterable<FileSystemResourceInner> inner
            = this.serviceClient().listByResourceGroup(resourceGroupName, context);
        return ResourceManagerUtils.mapPage(inner, inner1 -> new FileSystemResourceImpl(inner1, this.manager()));
    }

    public PagedIterable<FileSystemResource> list() {
        PagedIterable<FileSystemResourceInner> inner = this.serviceClient().list();
        return ResourceManagerUtils.mapPage(inner, inner1 -> new FileSystemResourceImpl(inner1, this.manager()));
    }

    public PagedIterable<FileSystemResource> list(Context context) {
        PagedIterable<FileSystemResourceInner> inner = this.serviceClient().list(context);
        return ResourceManagerUtils.mapPage(inner, inner1 -> new FileSystemResourceImpl(inner1, this.manager()));
    }

    public FileSystemResource getById(String id) {
        String resourceGroupName = ResourceManagerUtils.getValueFromIdByName(id, "resourceGroups");
        if (resourceGroupName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'resourceGroups'.", id)));
        }
        String filesystemName = ResourceManagerUtils.getValueFromIdByName(id, "filesystems");
        if (filesystemName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'filesystems'.", id)));
        }
        return this.getByResourceGroupWithResponse(resourceGroupName, filesystemName, Context.NONE).getValue();
    }

    public Response<FileSystemResource> getByIdWithResponse(String id, Context context) {
        String resourceGroupName = ResourceManagerUtils.getValueFromIdByName(id, "resourceGroups");
        if (resourceGroupName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'resourceGroups'.", id)));
        }
        String filesystemName = ResourceManagerUtils.getValueFromIdByName(id, "filesystems");
        if (filesystemName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'filesystems'.", id)));
        }
        return this.getByResourceGroupWithResponse(resourceGroupName, filesystemName, context);
    }

    public void deleteById(String id) {
        String resourceGroupName = ResourceManagerUtils.getValueFromIdByName(id, "resourceGroups");
        if (resourceGroupName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'resourceGroups'.", id)));
        }
        String filesystemName = ResourceManagerUtils.getValueFromIdByName(id, "filesystems");
        if (filesystemName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'filesystems'.", id)));
        }
        this.delete(resourceGroupName, filesystemName, Context.NONE);
    }

    public void deleteByIdWithResponse(String id, Context context) {
        String resourceGroupName = ResourceManagerUtils.getValueFromIdByName(id, "resourceGroups");
        if (resourceGroupName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'resourceGroups'.", id)));
        }
        String filesystemName = ResourceManagerUtils.getValueFromIdByName(id, "filesystems");
        if (filesystemName == null) {
            throw LOGGER.logExceptionAsError(new IllegalArgumentException(
                String.format("The resource ID '%s' is not valid. Missing path segment 'filesystems'.", id)));
        }
        this.delete(resourceGroupName, filesystemName, context);
    }

    private FileSystemsClient serviceClient() {
        return this.innerClient;
    }

    private com.azure.resourcemanager.dell.storage.DellStorageManager manager() {
        return this.serviceManager;
    }

    public FileSystemResourceImpl define(String name) {
        return new FileSystemResourceImpl(name, this.manager());
    }
}
