---
icon: material/note-text-outline
status: new
---
# Implementation

@snippet:api-state:unfinished@

??? abstract "Integration API Classes"
    * `org.betonquest.betonquest.api.integration.Integration`
    * `org.betonquest.betonquest.lib.integration.IntegrationTemplate`

## Introduction

The integration API requires a custom implementation of the `Integration` interface.
It is recommended to use the `IntegrationTemplate` abstract class from the library package of BetonQuest as well.

If you do not want to create a custom implementation, have a look at the [IntegrationBuilder](./Integration-Builder.md).

!!! warning "You should have viewed these pages"
    - [How to access the api](../Obtaining-API.md)
    - [How to use the API](./Overview.md)
    - [How to access the integration service](./Overview.md#access)
    - [How to define policies](./Policies.md)
    
<div class="grid" markdown>
!!! success "Use Case"
    Custom integration implementations are best used if you want to integrate the entirety of a plugin with BetonQuest.
    
!!! failure "Do Not Use"
    Custom integration implementations are not recommended if you want to integrate a single feature of a plugin with BetonQuest.
    For single feature integrations take a look at the [IntegrationBuilder](./Integration-Builder.md) first.
</div>

## Quick Demonstration

A custom integration implementation looks like this:

```java title="Integration"
public class MyIntegration implements Integration {
  
  @Override
  public void enable(final BetonQuestApi betonQuestApi) throws QuestException {
    // ...
  }
  
  @Override
  public void postEnable(final BetonQuestApi betonQuestApi) throws QuestException {
    // ...
  }
    
  @Override
  public void disable() throws QuestException {
    // ...
  }
  
}
```

And may be registered like this:

```java title="Registration"
final IntegrationPolicy integrationPolicy; //(1)!
integrationPolicy.register(pluginInstance, () -> new MyIntegration()); //(2)!
```

1. The `IntegrationPolicy` is required to register the integration.
2. The `Integration` custom implementation is provided as a lambda. Do not use a method reference here! Review Javadocs for more information.

## Details

The following methods are available and may be called once at most per server start in the given order:

- `enable(BetonQuestApi)`: Called when the integration is enabled, 
early in the integration process of BetonQuest.
- `postEnable(BetonQuestApi)`: Called when the integration is post-enabled, 
after BetonQuest, all integrations and all its features have been fully enabled, but before the first server tick.
- `disable()`: Called when the integration is disabled. An integration can be disabled by 
disabling BetonQuest or by the plugin integrating it disabling itself. An integration may only be disabled if it is enabled before.

The `integrate(Plugin, Supplier<Integration>)` method is required to register the integration with BetonQuest. The 
plugin provided must be a valid and loaded plugin instance, usually the instance of the plugin that is providing the integration.
The integrating plugin does not need to be identical to any plugin required by the integration(s). 

The methods `enable()` and `postEnable()` are provided a BetonQuest API instance to allow the integration to 
register features and services. Please do not inject another API instance into the integration methods from outside.

## An Example

Suppose you have a bunch of custom features that you want to integrate with BetonQuest.
You may want to create a custom integration implementation that enables all of these features.
The following examples show how to do the identical integration using the `Integration` and `IntegrationTemplate` 
for you to compare their advantages.

??? example "Integration"
    ```java title="Integration"
    public class MyIntegration implements Integration {
      
      @Override
      public void enable(BetonQuestApi betonQuestApi) throws QuestException {
        api.actions().registry().register("custom_action", new CustomActionFactory()); //(1)!
        api.conditions().registry().register("custom_condition", new CustomConditionFactory()); //(2)!
        api.objectives().registry().register("custom_objective", new CustomObjectiveFactory()); //(3)!
      }
      
      @Override
      public void postEnable(BetonQuestApi betonQuestApi) throws QuestException {
        // ...
      }
        
      @Override
      public void disable() throws QuestException {
        // ...
      }
      
    }
    ```
    
    1. Register the "custom_action" with the action registry.
    2. Register the "custom_condition" with the condition registry.
    3. Register the "custom_objective" with the objective registry.

??? example "IntegrationTemplate"
    ```java title="Integration"
    public class MyIntegration extends IntegrationTemplate {
      
      public MyIntegration() {
        super(); //(1)!
      }

      @Override
      public void enable(BetonQuestApi betonQuestApi) throws QuestException {
        playerAction("action", new CustomActionFactory()); //(2)!
        playerlessCondition("condition", new CustomConditionFactory(); //(3)!
        objective("objective", new CustomObjectiveFactory(); //(4)!
        registerFeatures(betonQuestApi, "custom_"); //(5)!
      }
      
      @Override
      public void postEnable(BetonQuestApi betonQuestApi) throws QuestException {
        // ...
      }
        
      @Override
      public void disable() throws QuestException {
        // ...
      }
      
    }
    ``` 
    
    1. Call the super constructor. This is very important, since it initializes the `IntegrationTemplate`.
    2. Prepare registration for the "custom_action" with the action registry.
    3. Prepare registration for the "custom_condition" with the condition registry.
    4. Prepare registration for the "custom_objective" with the objective registry.
    5. Register all features with the given prefix. Only do this once.

Advantages of the `IntegrationTemplate`:

- The major advantage of the `IntegrationTemplate` is that it reduces a lot of boilerplate code for you.
- You can focus on the actual integration of your features and leave the exact process of registering them to the 
library.
- You can use the `IntegrationTemplate` to register features with a given prefix more easily.
- The resulting integration is much more readable and more intuitive due to clear method names.
- You can still do any normal calls to the BetonQuest API.
