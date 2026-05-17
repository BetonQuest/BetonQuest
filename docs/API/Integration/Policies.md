---
icon: material/script-text-key-outline
status: new
---
# Policies

@snippet:api-state:unfinished@

??? abstract "Integration API Classes"
    * `org.betonquest.betonquest.api.integration.policy.Policy`
    * `org.betonquest.betonquest.api.integration.IntegrationPolicy`
    * `org.betonquest.betonquest.lib.integration.policy.Policies`

## Introduction

Policies are used to control the activation of certain integrations based on common conditions.

!!! warning "You should have viewed these pages"
    - [How to access the api](../Obtaining-API.md)
    - [How to use the API](./Overview.md)
    - [How to access the integration service](./Overview.md#access)
    
<div class="grid" markdown>
!!! success "Use Case"
    Integrations usually require a minimum version, an installed plugin or any plugin-specific condition to be 
    loaded correctly. All those conditions can be represented by policies and common conditions do not require manual 
    implementation.
    
!!! failure "Do Not Use"
    If your integration has no additional requirements.
</div>

## Quick Demonstration

Policies are defined per `IntegrationPolicy` instance retrieved from the integration service.

```java title="IntegrationPolicy"
final IntegrationService apiService; //(1)!
final Policy mcVersionPolicy = Policies.minimalVanillaVersion("1.21.11"); //(2)!
final IntegrationPolicy integrationPolicy = apiService.withPolicies(mcVersionPolicy); //(3)!
```

1. The integration service is obtained from the API.
2. A policy that requires a minimum version of Minecraft (as an example).
3. An integration policy instance with the policy applied is retrieved from the integration service.

## Policies

A policy acts as a prerequisite that must be satisfied for all integrations registered with it to load successfully. 
Each policy includes a description that explains its requirements in human-readable form.

The `Policies` class provides a number of pre-defined policies that can be used to quickly implement common 
requirements. Those include:

- Minecraft (vanilla) version requirements
- Plugin dependency requirements
- Plugin version requirements
- Java class requirements
- Custom boolean requirements

All version requirements offer:

- `exact` version matching
- `minimal` version matching (greater than or equal to)
- `maximal` version matching (less than or equal to)
- `range` version matching (min and max with inclusive bounds)

If all aforementioned pre-defined policies are not sufficient, you can create your own policies by implementing the 
`Policy` interface.

## Elaborated Demonstration

Suppose you want to integrate your plugin with BetonQuest.
You got version 1.0 and version 2.0 of your plugin with varying requirements and different environmental prerequisites.

Let's say, version 1.0 requires a minimum version of Minecraft 1.20 and version 2.0 requires a minimum version of 
Minecraft 1.21.11. However, version 1.0 does no longer support Minecraft 1.21.11 and above.
And since the name of your plugin is "MyTestPlugin" and does not change between versions, but the main class does, 
you want to register your plugin using its name.

It may look like this:

```java title="Registration MyTestPlugin v1.0"
final IntegrationService apiService; //(1)!
final Policy[] vanillaVersionRange = 
    Policies.vanillaVersionRange("1.20", "1.21.10"); //(2)!
final Version minimalPluginVersion = 
    VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, "1.0"); //(3)!
final Policy pluginVersion = 
    Policies.minimalPluginVersion("MyTestPlugin", minimalPluginVersion); //(4)!
final IntegrationPolicy integrationPolicy = 
    apiService.withPolicies(mcVersionPolicy).withPolicies(pluginVersion); //(5)!
integrationPolicy.register(pluginInstance, () -> new CustomIntegrationImplementation()); //(6)!
```

1. The integration service is obtained from the API.
2. A policy that requires a version of Minecraft in the range `1.20` to `1.21.10`.
3. Parses the version string `1.0` into a `Version` instance using a default version type.
4. A policy that requires a minimum version of the plugin "MyTestPlugin" with the version `1.0`.
5. An `IntegrationPolicy` instance with the policies applied is retrieved from the integration service.
6. The integration is registered with the integration service. More details on how to register integrations can be 
found on the [Integration](./Implementation.md) page.

```java title="Registration MyTestPlugin v2.0"
final IntegrationService apiService; //(1)!
final Policy minimalVanillaVersion = 
    Policies.minimalVanillaVersion("1.21.10"); //(2)!
final Version minimalPluginVersion = 
    VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, "2.0"); //(3)!
final Policy pluginVersion = 
    Policies.minimalPluginVersion("MyTestPlugin", minimalPluginVersion); //(4)!
final IntegrationPolicy integrationPolicy = 
    apiService.withPolicies(mcVersionPolicy, pluginVersion); //(5)!
integrationPolicy.register(pluginInstance, () -> new CustomIntegrationImplementation()); //(6)!
```

1. The integration service is obtained from the API.
2. A policy that requires a minimal version of Minecraft `1.21.11`.
3. Parses the version string `2.0` into a `Version` instance using a default version type.
4. A policy that requires a minimum version of the plugin "MyTestPlugin" with the version `2.0`.
5. An `IntegrationPolicy` instance with the policies applied is retrieved from the integration service.
6. The integration is registered with the integration service. More details on how to register integrations can be 
found on the [Integration](./Implementation.md) page.

??? abstract "Why is the version parsing that complex?"
    The version parser is a utility class that can parse version strings into `Version` instances.
    To allow for more flexible version formats, version comparison and variation in general, a version object 
    is not a simple string. Standard versions may look like `1.20` or `1.3.5`, but BetonQuest itself has versions 
    ranging from like `2.2.1` to `3.0.0-DEV-345` and other compatibility integrations have even more complex version 
    formats. To support all those, enabling flawless comparison among them while keeping the API simple and 
    consistent, the version parser and version types are needed. 
    More details on how the versioning works can be found on the [Versioning](../Tools/Versioning.md) page.
