// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.hybridcompute.models;

import com.azure.core.management.Region;
import com.azure.core.management.SystemData;
import com.azure.core.util.Context;
import com.azure.resourcemanager.hybridcompute.fluent.models.MachineExtensionInner;
import java.util.Map;

/** An immutable client-side representation of MachineExtension. */
public interface MachineExtension {
    /**
     * Gets the id property: Fully qualified resource Id for the resource.
     *
     * @return the id value.
     */
    String id();

    /**
     * Gets the name property: The name of the resource.
     *
     * @return the name value.
     */
    String name();

    /**
     * Gets the type property: The type of the resource.
     *
     * @return the type value.
     */
    String type();

    /**
     * Gets the location property: The geo-location where the resource lives.
     *
     * @return the location value.
     */
    String location();

    /**
     * Gets the tags property: Resource tags.
     *
     * @return the tags value.
     */
    Map<String, String> tags();

    /**
     * Gets the properties property: Describes Machine Extension Properties.
     *
     * @return the properties value.
     */
    MachineExtensionProperties properties();

    /**
     * Gets the systemData property: The system meta data relating to this resource.
     *
     * @return the systemData value.
     */
    SystemData systemData();

    /**
     * Gets the region of the resource.
     *
     * @return the region of the resource.
     */
    Region region();

    /**
     * Gets the name of the resource region.
     *
     * @return the name of the resource region.
     */
    String regionName();

    /**
     * Gets the name of the resource group.
     *
     * @return the name of the resource group.
     */
    String resourceGroupName();

    /**
     * Gets the inner com.azure.resourcemanager.hybridcompute.fluent.models.MachineExtensionInner object.
     *
     * @return the inner object.
     */
    MachineExtensionInner innerModel();

    /** The entirety of the MachineExtension definition. */
    interface Definition
        extends DefinitionStages.Blank,
            DefinitionStages.WithLocation,
            DefinitionStages.WithParentResource,
            DefinitionStages.WithCreate {
    }
    /** The MachineExtension definition stages. */
    interface DefinitionStages {
        /** The first stage of the MachineExtension definition. */
        interface Blank extends WithLocation {
        }
        /** The stage of the MachineExtension definition allowing to specify location. */
        interface WithLocation {
            /**
             * Specifies the region for the resource.
             *
             * @param location The geo-location where the resource lives.
             * @return the next definition stage.
             */
            WithParentResource withRegion(Region location);

            /**
             * Specifies the region for the resource.
             *
             * @param location The geo-location where the resource lives.
             * @return the next definition stage.
             */
            WithParentResource withRegion(String location);
        }
        /** The stage of the MachineExtension definition allowing to specify parent resource. */
        interface WithParentResource {
            /**
             * Specifies resourceGroupName, machineName.
             *
             * @param resourceGroupName The name of the resource group. The name is case insensitive.
             * @param machineName The name of the machine where the extension should be created or updated.
             * @return the next definition stage.
             */
            WithCreate withExistingMachine(String resourceGroupName, String machineName);
        }
        /**
         * The stage of the MachineExtension definition which contains all the minimum required properties for the
         * resource to be created, but also allows for any other optional properties to be specified.
         */
        interface WithCreate extends DefinitionStages.WithTags, DefinitionStages.WithProperties {
            /**
             * Executes the create request.
             *
             * @return the created resource.
             */
            MachineExtension create();

            /**
             * Executes the create request.
             *
             * @param context The context to associate with this operation.
             * @return the created resource.
             */
            MachineExtension create(Context context);
        }
        /** The stage of the MachineExtension definition allowing to specify tags. */
        interface WithTags {
            /**
             * Specifies the tags property: Resource tags..
             *
             * @param tags Resource tags.
             * @return the next definition stage.
             */
            WithCreate withTags(Map<String, String> tags);
        }
        /** The stage of the MachineExtension definition allowing to specify properties. */
        interface WithProperties {
            /**
             * Specifies the properties property: Describes Machine Extension Properties..
             *
             * @param properties Describes Machine Extension Properties.
             * @return the next definition stage.
             */
            WithCreate withProperties(MachineExtensionProperties properties);
        }
    }
    /**
     * Begins update for the MachineExtension resource.
     *
     * @return the stage of resource update.
     */
    MachineExtension.Update update();

    /** The template for MachineExtension update. */
    interface Update extends UpdateStages.WithTags, UpdateStages.WithProperties {
        /**
         * Executes the update request.
         *
         * @return the updated resource.
         */
        MachineExtension apply();

        /**
         * Executes the update request.
         *
         * @param context The context to associate with this operation.
         * @return the updated resource.
         */
        MachineExtension apply(Context context);
    }
    /** The MachineExtension update stages. */
    interface UpdateStages {
        /** The stage of the MachineExtension update allowing to specify tags. */
        interface WithTags {
            /**
             * Specifies the tags property: Resource tags.
             *
             * @param tags Resource tags.
             * @return the next definition stage.
             */
            Update withTags(Map<String, String> tags);
        }
        /** The stage of the MachineExtension update allowing to specify properties. */
        interface WithProperties {
            /**
             * Specifies the properties property: Describes Machine Extension Update Properties..
             *
             * @param properties Describes Machine Extension Update Properties.
             * @return the next definition stage.
             */
            Update withProperties(MachineExtensionUpdateProperties properties);
        }
    }
    /**
     * Refreshes the resource to sync with Azure.
     *
     * @return the refreshed resource.
     */
    MachineExtension refresh();

    /**
     * Refreshes the resource to sync with Azure.
     *
     * @param context The context to associate with this operation.
     * @return the refreshed resource.
     */
    MachineExtension refresh(Context context);
}
