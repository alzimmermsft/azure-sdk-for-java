// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.resources.samples;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.Azure;
import com.azure.resourcemanager.resources.models.Deployment;
import com.azure.resourcemanager.resources.models.DeploymentMode;
import com.azure.resourcemanager.resources.fluentcore.arm.Region;
import com.azure.resourcemanager.resources.fluentcore.profile.AzureProfile;
import com.azure.resourcemanager.resources.fluentcore.utils.SdkContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;

/**
 * Azure Resource sample for deploying resources using an ARM template and
 * showing progress.
 */

public final class DeployUsingARMTemplateWithProgress {

    /**
     * Main function which runs the actual sample.
     *
     * @param azure instance of the azure client
     * @return true if sample runs successfully
     */
    public static boolean runSample(Azure azure) {
        final String rgName = azure.sdkContext().randomResourceName("rgRSAP", 24);
        final String deploymentName = azure.sdkContext().randomResourceName("dpRSAP", 24);
        try {
            String templateJson = DeployUsingARMTemplateWithProgress.getTemplate(azure);

            //=============================================================
            // Create resource group.

            System.out.println("Creating a resource group with name: " + rgName);

            azure.resourceGroups().define(rgName)
                    .withRegion(Region.US_WEST)
                    .create();

            System.out.println("Created a resource group with name: " + rgName);


            //=============================================================
            // Create a deployment for an Azure App Service via an ARM
            // template.

            System.out.println("Starting a deployment for an Azure App Service: " + deploymentName);

            azure.deployments().define(deploymentName)
                    .withExistingResourceGroup(rgName)
                    .withTemplate(templateJson)
                    .withParameters("{}")
                    .withMode(DeploymentMode.INCREMENTAL)
                    .beginCreate();

            System.out.println("Started a deployment for an Azure App Service: " + deploymentName);

            Deployment deployment = azure.deployments().getByResourceGroup(rgName, deploymentName);
            System.out.println("Current deployment status : " + deployment.provisioningState());

            while (!(deployment.provisioningState().equalsIgnoreCase("Succeeded")
                    || deployment.provisioningState().equalsIgnoreCase("Failed")
                    || deployment.provisioningState().equalsIgnoreCase("Cancelled"))) {
                SdkContext.sleep(10000);
                deployment = azure.deployments().getByResourceGroup(rgName, deploymentName);
                System.out.println("Current deployment status : " + deployment.provisioningState());
            }
            return true;
        } catch (Exception f) {

            System.out.println(f.getMessage());
            f.printStackTrace();

        } finally {
            try {
                System.out.println("Deleting Resource Group: " + rgName);
                azure.resourceGroups().beginDeleteByName(rgName);
                System.out.println("Deleted Resource Group: " + rgName);
            } catch (NullPointerException npe) {
                System.out.println("Did not create any resources in Azure. No clean up is necessary");
            } catch (Exception g) {
                g.printStackTrace();
            }

        }
        return false;
    }

    /**
     * Main entry point.
     *
     * @param args the parameters
     */
    public static void main(String[] args) {
        try {
            //=================================================================
            // Authenticate

            final AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
            final TokenCredential credential = new DefaultAzureCredentialBuilder()
                .build();

            Azure azure = Azure
                .configure()
                .withLogLevel(HttpLogDetailLevel.BASIC)
                .authenticate(credential, profile)
                .withDefaultSubscription();

            runSample(azure);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    private static String getTemplate(Azure azure) throws IllegalAccessException, JsonProcessingException, IOException {
        final String hostingPlanName = azure.sdkContext().randomResourceName("hpRSAT", 24);
        final String webappName = azure.sdkContext().randomResourceName("wnRSAT", 24);

        try (InputStream embeddedTemplate = DeployUsingARMTemplateWithProgress.class.getResourceAsStream("/templateValue.json")) {

            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode tmp = mapper.readTree(embeddedTemplate);

            DeployUsingARMTemplateWithProgress.validateAndAddFieldValue("string", hostingPlanName, "hostingPlanName", null, tmp);
            DeployUsingARMTemplateWithProgress.validateAndAddFieldValue("string", webappName, "webSiteName", null, tmp);
            DeployUsingARMTemplateWithProgress.validateAndAddFieldValue("string", "F1", "skuName", null, tmp);
            DeployUsingARMTemplateWithProgress.validateAndAddFieldValue("int", "1", "skuCapacity", null, tmp);

            return tmp.toString();
        }
    }

    private static void validateAndAddFieldValue(String type, String fieldValue, String fieldName, String errorMessage,
                                                 JsonNode tmp) throws IllegalAccessException {
        // Add count variable for loop....
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode parameter = mapper.createObjectNode();
        parameter.put("type", type);
        if ("int".equals(type)) {
            parameter.put("defaultValue", Integer.parseInt(fieldValue));
        } else {
            parameter.put("defaultValue", fieldValue);
        }
        ObjectNode.class.cast(tmp.get("parameters")).replace(fieldName, parameter);
    }

    private DeployUsingARMTemplateWithProgress() {

    }
}
