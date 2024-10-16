# Release History

## 1.0.0-beta.5 (Unreleased)

### Features Added

### Breaking Changes

### Bugs Fixed

### Other Changes

## 1.0.0-beta.4 (2023-01-16)

- Azure Resource Manager MicrosoftDatadog client library for Java. This package contains Microsoft Azure SDK for MicrosoftDatadog Management SDK.  Package tag package-2021-03. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).

### Features Added

#### `MicrosoftDatadogManager$Configurable` was modified

* `withScope(java.lang.String)` was added
* `withRetryOptions(com.azure.core.http.policy.RetryOptions)` was added

#### `models.DatadogMonitorResource` was modified

* `resourceGroupName()` was added

#### `models.DatadogSingleSignOnResource` was modified

* `resourceGroupName()` was added

#### `models.MonitoringTagRules` was modified

* `resourceGroupName()` was added

#### `MicrosoftDatadogManager` was modified

* `authenticate(com.azure.core.http.HttpPipeline,com.azure.core.management.profile.AzureProfile)` was added

## 1.0.0-beta.3 (2021-05-31)

- Azure Resource Manager MicrosoftDatadog client library for Java. This package contains Microsoft Azure SDK for MicrosoftDatadog Management SDK.  Package tag package-2021-03. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).

### Breaking Change

#### `models.MonitorProperties` was modified

* `withProvisioningState(models.ProvisioningState)` was removed

#### `models.DatadogSingleSignOnProperties` was modified

* `withProvisioningState(models.ProvisioningState)` was removed

#### `models.MonitoringTagRulesProperties` was modified

* `withProvisioningState(models.ProvisioningState)` was removed

### New Feature

#### `models.DatadogMonitorResourceUpdateParameters` was modified

* `withSku(models.ResourceSku)` was added
* `sku()` was added

#### `models.DatadogMonitorResource$Update` was modified

* `withSku(models.ResourceSku)` was added

## 1.0.0-beta.2 (2021-03-30)

- Azure Resource Manager MicrosoftDatadog client library for Java. This package contains Microsoft Azure SDK for MicrosoftDatadog Management SDK.  Package tag package-2021-03. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).

### New Feature

#### `models.DatadogMonitorResource` was modified

* `systemData()` was added

#### `models.DatadogSingleSignOnResource` was modified

* `systemData()` was added

#### `models.MonitoringTagRules` was modified

* `systemData()` was added

#### `models.DatadogAgreementResource` was modified

* `systemData()` was added

## 1.0.0-beta.1 (2021-03-08)

- Azure Resource Manager MicrosoftDatadog client library for Java. This package contains Microsoft Azure SDK for MicrosoftDatadog Management SDK.  Package tag package-2020-02-preview. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).

