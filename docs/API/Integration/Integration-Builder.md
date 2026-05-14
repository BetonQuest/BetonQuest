---
icon: material/application-export
status: new
---
# IntegrationBuilder

@snippet:api-state:unfinished@

??? abstract "Integration API Classes"
    * `org.betonquest.betonquest.api.integration.IntegrationBuilder`

## Introduction

The integration API offers a custom integration builder to create custom integration implementations without 
defining a class.
It is recommended to use this API if you want to integrate a bunch of smaller independent features from multiple 
plugins or sources.

If you want to create a custom implementation, have a look at the [Integration](./Implementation.md) interface.

!!! warning "You should have viewed these pages"
    - [How to access the api](../Obtaining-API.md)
    - [How to use the API](./Overview.md)
    - [How to access the integration service](./Overview.md#access)
    - [How to define policies](./Policies.md)
    
<div class="grid" markdown>
!!! success "Use Case"
    The integration builder is best used if you want to integrate a bunch of smaller independent features from multiple 
    plugins or sources.
    
!!! failure "Do Not Use"
    Custom integration implementations are recommended if you want to integrate the entirety of a plugin with 
    BetonQuest.
    For custom integrations, take a look at the [Integration](./Implementation.md) interface first.
</div>

## Quick Demonstration

The IntegrationBuilder is used to create and register integration features on the spot in a stream-like call.

```java title="IntegrationBuilder"
final IntegrationPolicy integrationPolicy; //(1)!
integrationPolicy.builder() //(2)!
  .enable(/* ... */) //(3)!
  .postEnable(/* ... */) //(4)!
  .disable(/* ... */) //(5)!
  .integrate(pluginInstance); //(6)!
```

1. The integration policy is required to create a new builder.
2. The builder call creates a new `IntegrationBuilder` instance.
3. Define the function called when the integration is enabled.
4. Define the function called when the integration is post-enabled. (after BetonQuest has been enabled, before the first server tick)
5. Define the function called when the integration is disabled.
6. Integrate using a plugin instance. The integration will deactivate if the plugin is deactivated.

## Method details

The minimal required methods for a valid integration builder registration are either
`enable()` or `postEnable()` and the builder must be finalized with `integrate()`.

The following methods are available and may be called once at most per server start in the given order:

- `enable(QuestConsumer<BetonQuestApi>)`: Called when the integration is enabled, 
early in the integration process of BetonQuest.
- `postEnable(QuestConsumer<BetonQuestApi>)`: Called when the integration is post-enabled, 
after BetonQuest, all integrations and all its features have been fully enabled, but before the first server tick.
- `disable(QuestRunnable)`: Called when the integration is disabled. An integration can be disabled by 
disabling BetonQuest or by the plugin integrating it disabling itself. An integration may only be disabled if it is enabled before.

The `integrate(Plugin)` method is required to register the integration with BetonQuest. The plugin provided must be 
a valid and loaded plugin instance, usually the instance of the plugin that is providing the integration.
The integrating plugin does not need to be identical to any plugin required by the integration(s). 

The methods `enable()` and `postEnable()` are provided a BetonQuest API instance to allow the integration to 
register features and services. Please do not inject another API instance into the integration methods from outside.

## An Example

Suppose you want to integrate a custom action implementation `MyAction` using the integration builder, since it's the only feature 
you want to integrate and there is no need to create a custom integration implementation.

```java title="IntegrationBuilder"
final IntegrationPolicy integrationPolicy; //(1)!
integrationPolicy.builder() //(2)!
  .enable(api -> api.actions().registry().register("myaction", MyAction.class)) //(3)!
  .integrate(pluginInstance); //(4)!
```

1. The integration policy is required to create a new builder.
2. The builder call creates a new `IntegrationBuilder` instance.
3. Register the action implementation with the action registry.
4. Integrate using a plugin instance.
