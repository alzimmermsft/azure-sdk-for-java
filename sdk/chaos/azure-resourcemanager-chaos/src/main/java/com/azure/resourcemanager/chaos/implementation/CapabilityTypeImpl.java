// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.chaos.implementation;

import com.azure.core.management.SystemData;
import com.azure.resourcemanager.chaos.fluent.models.CapabilityTypeInner;
import com.azure.resourcemanager.chaos.models.CapabilityType;
import com.azure.resourcemanager.chaos.models.CapabilityTypePropertiesRuntimeProperties;
import java.util.Collections;
import java.util.List;

public final class CapabilityTypeImpl implements CapabilityType {
    private CapabilityTypeInner innerObject;

    private final com.azure.resourcemanager.chaos.ChaosManager serviceManager;

    CapabilityTypeImpl(CapabilityTypeInner innerObject, com.azure.resourcemanager.chaos.ChaosManager serviceManager) {
        this.innerObject = innerObject;
        this.serviceManager = serviceManager;
    }

    public String id() {
        return this.innerModel().id();
    }

    public String name() {
        return this.innerModel().name();
    }

    public String type() {
        return this.innerModel().type();
    }

    public SystemData systemData() {
        return this.innerModel().systemData();
    }

    public String publisher() {
        return this.innerModel().publisher();
    }

    public String targetType() {
        return this.innerModel().targetType();
    }

    public String displayName() {
        return this.innerModel().displayName();
    }

    public String description() {
        return this.innerModel().description();
    }

    public String parametersSchema() {
        return this.innerModel().parametersSchema();
    }

    public String urn() {
        return this.innerModel().urn();
    }

    public String kind() {
        return this.innerModel().kind();
    }

    public List<String> azureRbacActions() {
        List<String> inner = this.innerModel().azureRbacActions();
        if (inner != null) {
            return Collections.unmodifiableList(inner);
        } else {
            return Collections.emptyList();
        }
    }

    public List<String> azureRbacDataActions() {
        List<String> inner = this.innerModel().azureRbacDataActions();
        if (inner != null) {
            return Collections.unmodifiableList(inner);
        } else {
            return Collections.emptyList();
        }
    }

    public List<String> requiredAzureRoleDefinitionIds() {
        List<String> inner = this.innerModel().requiredAzureRoleDefinitionIds();
        if (inner != null) {
            return Collections.unmodifiableList(inner);
        } else {
            return Collections.emptyList();
        }
    }

    public CapabilityTypePropertiesRuntimeProperties runtimeProperties() {
        return this.innerModel().runtimeProperties();
    }

    public CapabilityTypeInner innerModel() {
        return this.innerObject;
    }

    private com.azure.resourcemanager.chaos.ChaosManager manager() {
        return this.serviceManager;
    }
}
