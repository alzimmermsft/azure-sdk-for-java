// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.compute.fluent.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import com.azure.resourcemanager.compute.models.BootDiagnosticsInstanceView;
import com.azure.resourcemanager.compute.models.DiskInstanceView;
import com.azure.resourcemanager.compute.models.HyperVGenerationType;
import com.azure.resourcemanager.compute.models.InstanceViewStatus;
import com.azure.resourcemanager.compute.models.MaintenanceRedeployStatus;
import com.azure.resourcemanager.compute.models.VirtualMachineAgentInstanceView;
import com.azure.resourcemanager.compute.models.VirtualMachineExtensionInstanceView;
import com.azure.resourcemanager.compute.models.VirtualMachineHealthStatus;
import com.azure.resourcemanager.compute.models.VirtualMachinePatchStatus;
import java.io.IOException;
import java.util.List;

/**
 * The instance view of a virtual machine.
 */
@Fluent
public final class VirtualMachineInstanceViewInner implements JsonSerializable<VirtualMachineInstanceViewInner> {
    /*
     * Specifies the update domain of the virtual machine.
     */
    private Integer platformUpdateDomain;

    /*
     * Specifies the fault domain of the virtual machine.
     */
    private Integer platformFaultDomain;

    /*
     * The computer name assigned to the virtual machine.
     */
    private String computerName;

    /*
     * The Operating System running on the virtual machine.
     */
    private String osName;

    /*
     * The version of Operating System running on the virtual machine.
     */
    private String osVersion;

    /*
     * Specifies the HyperVGeneration Type associated with a resource
     */
    private HyperVGenerationType hyperVGeneration;

    /*
     * The Remote desktop certificate thumbprint.
     */
    private String rdpThumbPrint;

    /*
     * The VM Agent running on the virtual machine.
     */
    private VirtualMachineAgentInstanceView vmAgent;

    /*
     * The Maintenance Operation status on the virtual machine.
     */
    private MaintenanceRedeployStatus maintenanceRedeployStatus;

    /*
     * The virtual machine disk information.
     */
    private List<DiskInstanceView> disks;

    /*
     * The extensions information.
     */
    private List<VirtualMachineExtensionInstanceView> extensions;

    /*
     * The application health status for the VM, provided through Application Health Extension.
     */
    private VirtualMachineHealthStatus vmHealth;

    /*
     * Boot Diagnostics is a debugging feature which allows you to view Console Output and Screenshot to diagnose VM
     * status. You can easily view the output of your console log. Azure also enables you to see a screenshot of the VM
     * from the hypervisor.
     */
    private BootDiagnosticsInstanceView bootDiagnostics;

    /*
     * Resource id of the dedicated host, on which the virtual machine is allocated through automatic placement, when
     * the virtual machine is associated with a dedicated host group that has automatic placement enabled. Minimum
     * api-version: 2020-06-01.
     */
    private String assignedHost;

    /*
     * The resource status information.
     */
    private List<InstanceViewStatus> statuses;

    /*
     * [Preview Feature] The status of virtual machine patch operations.
     */
    private VirtualMachinePatchStatus patchStatus;

    /*
     * [Preview Feature] Specifies whether the VM is currently in or out of the Standby Pool.
     */
    private Boolean isVMInStandbyPool;

    /**
     * Creates an instance of VirtualMachineInstanceViewInner class.
     */
    public VirtualMachineInstanceViewInner() {
    }

    /**
     * Get the platformUpdateDomain property: Specifies the update domain of the virtual machine.
     * 
     * @return the platformUpdateDomain value.
     */
    public Integer platformUpdateDomain() {
        return this.platformUpdateDomain;
    }

    /**
     * Set the platformUpdateDomain property: Specifies the update domain of the virtual machine.
     * 
     * @param platformUpdateDomain the platformUpdateDomain value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withPlatformUpdateDomain(Integer platformUpdateDomain) {
        this.platformUpdateDomain = platformUpdateDomain;
        return this;
    }

    /**
     * Get the platformFaultDomain property: Specifies the fault domain of the virtual machine.
     * 
     * @return the platformFaultDomain value.
     */
    public Integer platformFaultDomain() {
        return this.platformFaultDomain;
    }

    /**
     * Set the platformFaultDomain property: Specifies the fault domain of the virtual machine.
     * 
     * @param platformFaultDomain the platformFaultDomain value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withPlatformFaultDomain(Integer platformFaultDomain) {
        this.platformFaultDomain = platformFaultDomain;
        return this;
    }

    /**
     * Get the computerName property: The computer name assigned to the virtual machine.
     * 
     * @return the computerName value.
     */
    public String computerName() {
        return this.computerName;
    }

    /**
     * Set the computerName property: The computer name assigned to the virtual machine.
     * 
     * @param computerName the computerName value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withComputerName(String computerName) {
        this.computerName = computerName;
        return this;
    }

    /**
     * Get the osName property: The Operating System running on the virtual machine.
     * 
     * @return the osName value.
     */
    public String osName() {
        return this.osName;
    }

    /**
     * Set the osName property: The Operating System running on the virtual machine.
     * 
     * @param osName the osName value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withOsName(String osName) {
        this.osName = osName;
        return this;
    }

    /**
     * Get the osVersion property: The version of Operating System running on the virtual machine.
     * 
     * @return the osVersion value.
     */
    public String osVersion() {
        return this.osVersion;
    }

    /**
     * Set the osVersion property: The version of Operating System running on the virtual machine.
     * 
     * @param osVersion the osVersion value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withOsVersion(String osVersion) {
        this.osVersion = osVersion;
        return this;
    }

    /**
     * Get the hyperVGeneration property: Specifies the HyperVGeneration Type associated with a resource.
     * 
     * @return the hyperVGeneration value.
     */
    public HyperVGenerationType hyperVGeneration() {
        return this.hyperVGeneration;
    }

    /**
     * Set the hyperVGeneration property: Specifies the HyperVGeneration Type associated with a resource.
     * 
     * @param hyperVGeneration the hyperVGeneration value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withHyperVGeneration(HyperVGenerationType hyperVGeneration) {
        this.hyperVGeneration = hyperVGeneration;
        return this;
    }

    /**
     * Get the rdpThumbPrint property: The Remote desktop certificate thumbprint.
     * 
     * @return the rdpThumbPrint value.
     */
    public String rdpThumbPrint() {
        return this.rdpThumbPrint;
    }

    /**
     * Set the rdpThumbPrint property: The Remote desktop certificate thumbprint.
     * 
     * @param rdpThumbPrint the rdpThumbPrint value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withRdpThumbPrint(String rdpThumbPrint) {
        this.rdpThumbPrint = rdpThumbPrint;
        return this;
    }

    /**
     * Get the vmAgent property: The VM Agent running on the virtual machine.
     * 
     * @return the vmAgent value.
     */
    public VirtualMachineAgentInstanceView vmAgent() {
        return this.vmAgent;
    }

    /**
     * Set the vmAgent property: The VM Agent running on the virtual machine.
     * 
     * @param vmAgent the vmAgent value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withVmAgent(VirtualMachineAgentInstanceView vmAgent) {
        this.vmAgent = vmAgent;
        return this;
    }

    /**
     * Get the maintenanceRedeployStatus property: The Maintenance Operation status on the virtual machine.
     * 
     * @return the maintenanceRedeployStatus value.
     */
    public MaintenanceRedeployStatus maintenanceRedeployStatus() {
        return this.maintenanceRedeployStatus;
    }

    /**
     * Set the maintenanceRedeployStatus property: The Maintenance Operation status on the virtual machine.
     * 
     * @param maintenanceRedeployStatus the maintenanceRedeployStatus value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner
        withMaintenanceRedeployStatus(MaintenanceRedeployStatus maintenanceRedeployStatus) {
        this.maintenanceRedeployStatus = maintenanceRedeployStatus;
        return this;
    }

    /**
     * Get the disks property: The virtual machine disk information.
     * 
     * @return the disks value.
     */
    public List<DiskInstanceView> disks() {
        return this.disks;
    }

    /**
     * Set the disks property: The virtual machine disk information.
     * 
     * @param disks the disks value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withDisks(List<DiskInstanceView> disks) {
        this.disks = disks;
        return this;
    }

    /**
     * Get the extensions property: The extensions information.
     * 
     * @return the extensions value.
     */
    public List<VirtualMachineExtensionInstanceView> extensions() {
        return this.extensions;
    }

    /**
     * Set the extensions property: The extensions information.
     * 
     * @param extensions the extensions value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withExtensions(List<VirtualMachineExtensionInstanceView> extensions) {
        this.extensions = extensions;
        return this;
    }

    /**
     * Get the vmHealth property: The application health status for the VM, provided through Application Health
     * Extension.
     * 
     * @return the vmHealth value.
     */
    public VirtualMachineHealthStatus vmHealth() {
        return this.vmHealth;
    }

    /**
     * Get the bootDiagnostics property: Boot Diagnostics is a debugging feature which allows you to view Console Output
     * and Screenshot to diagnose VM status. You can easily view the output of your console log. Azure also enables you
     * to see a screenshot of the VM from the hypervisor.
     * 
     * @return the bootDiagnostics value.
     */
    public BootDiagnosticsInstanceView bootDiagnostics() {
        return this.bootDiagnostics;
    }

    /**
     * Set the bootDiagnostics property: Boot Diagnostics is a debugging feature which allows you to view Console Output
     * and Screenshot to diagnose VM status. You can easily view the output of your console log. Azure also enables you
     * to see a screenshot of the VM from the hypervisor.
     * 
     * @param bootDiagnostics the bootDiagnostics value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withBootDiagnostics(BootDiagnosticsInstanceView bootDiagnostics) {
        this.bootDiagnostics = bootDiagnostics;
        return this;
    }

    /**
     * Get the assignedHost property: Resource id of the dedicated host, on which the virtual machine is allocated
     * through automatic placement, when the virtual machine is associated with a dedicated host group that has
     * automatic placement enabled. Minimum api-version: 2020-06-01.
     * 
     * @return the assignedHost value.
     */
    public String assignedHost() {
        return this.assignedHost;
    }

    /**
     * Get the statuses property: The resource status information.
     * 
     * @return the statuses value.
     */
    public List<InstanceViewStatus> statuses() {
        return this.statuses;
    }

    /**
     * Set the statuses property: The resource status information.
     * 
     * @param statuses the statuses value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withStatuses(List<InstanceViewStatus> statuses) {
        this.statuses = statuses;
        return this;
    }

    /**
     * Get the patchStatus property: [Preview Feature] The status of virtual machine patch operations.
     * 
     * @return the patchStatus value.
     */
    public VirtualMachinePatchStatus patchStatus() {
        return this.patchStatus;
    }

    /**
     * Set the patchStatus property: [Preview Feature] The status of virtual machine patch operations.
     * 
     * @param patchStatus the patchStatus value to set.
     * @return the VirtualMachineInstanceViewInner object itself.
     */
    public VirtualMachineInstanceViewInner withPatchStatus(VirtualMachinePatchStatus patchStatus) {
        this.patchStatus = patchStatus;
        return this;
    }

    /**
     * Get the isVMInStandbyPool property: [Preview Feature] Specifies whether the VM is currently in or out of the
     * Standby Pool.
     * 
     * @return the isVMInStandbyPool value.
     */
    public Boolean isVMInStandbyPool() {
        return this.isVMInStandbyPool;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (vmAgent() != null) {
            vmAgent().validate();
        }
        if (maintenanceRedeployStatus() != null) {
            maintenanceRedeployStatus().validate();
        }
        if (disks() != null) {
            disks().forEach(e -> e.validate());
        }
        if (extensions() != null) {
            extensions().forEach(e -> e.validate());
        }
        if (vmHealth() != null) {
            vmHealth().validate();
        }
        if (bootDiagnostics() != null) {
            bootDiagnostics().validate();
        }
        if (statuses() != null) {
            statuses().forEach(e -> e.validate());
        }
        if (patchStatus() != null) {
            patchStatus().validate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeNumberField("platformUpdateDomain", this.platformUpdateDomain);
        jsonWriter.writeNumberField("platformFaultDomain", this.platformFaultDomain);
        jsonWriter.writeStringField("computerName", this.computerName);
        jsonWriter.writeStringField("osName", this.osName);
        jsonWriter.writeStringField("osVersion", this.osVersion);
        jsonWriter.writeStringField("hyperVGeneration",
            this.hyperVGeneration == null ? null : this.hyperVGeneration.toString());
        jsonWriter.writeStringField("rdpThumbPrint", this.rdpThumbPrint);
        jsonWriter.writeJsonField("vmAgent", this.vmAgent);
        jsonWriter.writeJsonField("maintenanceRedeployStatus", this.maintenanceRedeployStatus);
        jsonWriter.writeArrayField("disks", this.disks, (writer, element) -> writer.writeJson(element));
        jsonWriter.writeArrayField("extensions", this.extensions, (writer, element) -> writer.writeJson(element));
        jsonWriter.writeJsonField("bootDiagnostics", this.bootDiagnostics);
        jsonWriter.writeArrayField("statuses", this.statuses, (writer, element) -> writer.writeJson(element));
        jsonWriter.writeJsonField("patchStatus", this.patchStatus);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of VirtualMachineInstanceViewInner from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of VirtualMachineInstanceViewInner if the JsonReader was pointing to an instance of it, or
     * null if it was pointing to JSON null.
     * @throws IOException If an error occurs while reading the VirtualMachineInstanceViewInner.
     */
    public static VirtualMachineInstanceViewInner fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            VirtualMachineInstanceViewInner deserializedVirtualMachineInstanceViewInner
                = new VirtualMachineInstanceViewInner();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("platformUpdateDomain".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.platformUpdateDomain
                        = reader.getNullable(JsonReader::getInt);
                } else if ("platformFaultDomain".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.platformFaultDomain
                        = reader.getNullable(JsonReader::getInt);
                } else if ("computerName".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.computerName = reader.getString();
                } else if ("osName".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.osName = reader.getString();
                } else if ("osVersion".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.osVersion = reader.getString();
                } else if ("hyperVGeneration".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.hyperVGeneration
                        = HyperVGenerationType.fromString(reader.getString());
                } else if ("rdpThumbPrint".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.rdpThumbPrint = reader.getString();
                } else if ("vmAgent".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.vmAgent
                        = VirtualMachineAgentInstanceView.fromJson(reader);
                } else if ("maintenanceRedeployStatus".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.maintenanceRedeployStatus
                        = MaintenanceRedeployStatus.fromJson(reader);
                } else if ("disks".equals(fieldName)) {
                    List<DiskInstanceView> disks = reader.readArray(reader1 -> DiskInstanceView.fromJson(reader1));
                    deserializedVirtualMachineInstanceViewInner.disks = disks;
                } else if ("extensions".equals(fieldName)) {
                    List<VirtualMachineExtensionInstanceView> extensions
                        = reader.readArray(reader1 -> VirtualMachineExtensionInstanceView.fromJson(reader1));
                    deserializedVirtualMachineInstanceViewInner.extensions = extensions;
                } else if ("vmHealth".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.vmHealth = VirtualMachineHealthStatus.fromJson(reader);
                } else if ("bootDiagnostics".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.bootDiagnostics
                        = BootDiagnosticsInstanceView.fromJson(reader);
                } else if ("assignedHost".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.assignedHost = reader.getString();
                } else if ("statuses".equals(fieldName)) {
                    List<InstanceViewStatus> statuses
                        = reader.readArray(reader1 -> InstanceViewStatus.fromJson(reader1));
                    deserializedVirtualMachineInstanceViewInner.statuses = statuses;
                } else if ("patchStatus".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.patchStatus
                        = VirtualMachinePatchStatus.fromJson(reader);
                } else if ("isVMInStandbyPool".equals(fieldName)) {
                    deserializedVirtualMachineInstanceViewInner.isVMInStandbyPool
                        = reader.getNullable(JsonReader::getBoolean);
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedVirtualMachineInstanceViewInner;
        });
    }
}
