---
icon: octicons/codescan-checkmark-16
---
@snippet:api-state:draft@

??? abstract "Objective API Classes"
    * `org.betonquest.betonquest.api.service.objective.Objectives`
    * `org.betonquest.betonquest.api.service.objective.ObjectiveManager`
    * `org.betonquest.betonquest.api.service.objective.ObjectiveRegistry`
    * `org.betonquest.betonquest.api.quest.objective.Objective`
    * `org.betonquest.betonquest.api.quest.objective.ObjectiveFactory`

## Introduction

This page covers the Objective API of BetonQuest.

!!! warning "You should have viewed these pages"
    - [How to access the api](../Obtaining-API.md)
    - [How to use the factory system](./Overview.md)
    - [How to work with Instructions](../Tools/Instruction.md)

<div class="grid" markdown>

!!! abstract "What this page covers"
    - [How to create an objective](#how-to-create-an-objective)
    - [How to create a factory for an objective](#how-to-create-a-factory-for-an-objective)
    - [Details about the ObjectiveService](#details-about-the-objectiveservice)
    - [How to register an objective and its factory with BetonQuest](#how-to-register-an-objective-and-its-factory-with-betonquest)
    - [How to access an objective](#how-to-access-an-objective)
    - [How a full example looks like](#how-a-full-example-looks-like)
    - [What limitations are there](#what-limitations-are-there)

!!! info "What this page does not cover"
    - What an objective is
    - [Which objectives are available](../../Documentation/Reference/Objectives-List.md)
    - [How to work with objectives in scripting](../../Tutorials/Getting-Started/Basics/Objectives.md)

</div>

## How to create an objective

Creating an objective will be explained in the following sections using the `InventoryObjective` as an example.

### Comparison: Helper class

Before we start creating our objective, we will take a look at the `DefaultObjective` class.
This class is a helper class in the library package that is used to create objectives and might offer some useful methods.
The current implementation of the `DefaultObjective` class just stores the required `ObjectiveService` in a field as required by `Objective` anyways.

Those examples are identical in their behavior, but in the examples below, only the `DefaultObjective` class is used:

```java title="SimpleObjective.java"
public class SimpleObjective implements Objective {
    private final ObjectiveService objectiveService;
    
    public SimpleObjective(final ObjectiveService objectiveService) {
      this.objectiveService = objectiveService;
    }
    
    @Override
    public ObjectiveService getService() {
      return objectiveService;
    }
}
```

```java title="SimpleObjective.java"
public class SimpleObjective extends DefaultObjective {
    
    public SimpleObjective(final ObjectiveService objectiveService) {
      super(objectiveService);    
    }
}
```

### Approach 1: A simple objective

In the first approach, we will try to create a simple objective that will complete if the player opens an inventory.

- The `onOpenInventory` will be called when the player with a certain profile opens an inventory (as later defined in the factory). 
The name is freely chosen.
- The `ObjectiveService` is used for objective-related tasks inside of an objective.
- The `complete` method is called when the objective is completed for a certain profile.

```java title="Approach #1: InventoryObjective.java"
public class InventoryObjective extends DefaultObjective {
    
  public InventoryObjective(final ObjectiveService objectiveService) {
    super(objectiveService);
  }

  public void onOpenInventory(final InventoryOpenEvent event, 
          final OnlineProfile onlineProfile) throws QuestException {
    getService().complete(onlineProfile);
  }
  
}
```

And that's it for the objective itself! After registering the objective with its factory, 
the player will be able to complete the objective by opening any inventory.

### Approach 2: Adding parameters

The logical next step is to add parameters to the objective and make it more configurable and therefore more useful.
Let's add a parameter to the objective that will be used to allow only a specific type of inventory to be opened.

The `Argument` class is used to define arguments for an objective that can be resolved for a profile 
and thereby resolve any contained placeholders for that profile.

```java title="Approach #2: InventoryObjective.java"
public class InventoryObjective extends DefaultObjective {

  private final Argument<InventoryType> inventoryType;

  public InventoryObjective(final ObjectiveService objectiveService, final Argument<InventoryType> inventoryType) {
    super(objectiveService);
    this.inventoryType = inventoryType;
  }

  public void onOpenInventory(final InventoryOpenEvent event, 
          final OnlineProfile onlineProfile) throws QuestException {
    if (event.getInventory().getType() != inventoryType.getValue(onlineProfile)) {
      return;
    }
    getService().complete(onlineProfile);
  }
  
}
```

### Approach 3: Adding more events

Suppose we want to add more events to the objective.
For example, we want the player to open an inventory of a certain type and then close it again.
We can do this by adding a new event to the objective in a similar way as we did in the previous approaches, and the name is freely chosen again.

```java title="Approach #3: InventoryObjective.java"
public class InventoryObjective extends DefaultObjective {

  private final Argument<InventoryType> inventoryType;
  private final Set<UUID> openInventories;

  public InventoryObjective(final ObjectiveService objectiveService, final Argument<InventoryType> inventoryType) {
    super(objectiveService);
    this.inventoryType = inventoryType;
    this.openInventories = new HashSet<>();
  }

  public void onOpenInventory(final InventoryOpenEvent event, 
          final OnlineProfile onlineProfile) throws QuestException {
    if (event.getInventory().getType() != inventoryType.getValue(onlineProfile)) {
      return;
    }
    openInventories.add(onlineProfile.getPlayerUUID());
  }
  
  public void onCloseInventory(final InventoryCloseEvent event, 
            final OnlineProfile onlineProfile) throws QuestException {
    if (!openInventories.remove(onlineProfile.getPlayerUUID())) {
      return;
    }
    getService().complete(onlineProfile);
  }
  
}
```

### Summary

To create an objective, we need to create a class that implements the `Objective` interface.
Alternatively, we can extend the `DefaultObjective` class instead to reduce the amount of boilerplate code we need to 
write.

To include events, we need to add methods that are called when the corresponding event is fired.
Those methods contain the event instance itself and optionally the profile that is involved.

You may:

- Include no profile at all
- Include a `Profile`
- Include an `OnlineProfile`

Using `Argument`s allows us to parameterize the objective and to allow placeholders in those parameters.

## How to create a factory for an objective

After creating an objective, we have to create a factory for it.
Factories are used to create instances of the objective for a specific instruction.

In the following we are going to create factories for each of the `InventoryObjective` objective approaches.

### Approach #1: A simple factory
See [Approach #1](#approach-1-a-simple-objective).

To implement a factory, we need to create a class that implements the `ObjectiveFactory` interface.
In the factory, the provided instruction is parsed to create the objective instance 
and register its events with the `ObjectiveService`.

```java title="Approach #1: InventoryObjectiveFactory.java"
public class InventoryObjectiveFactory implements ObjectiveFactory {
  
  @Override
  public Objective parseInstruction(final Instruction instruction, 
        final ObjectiveService service) throws QuestException {
    final InventoryObjective objective = new InventoryObjective(service); //(1)!
    service.request(InventoryOpenEvent.class) //(2)!
        .priority(EventPriority.LOWEST) //(3)!
        .onlineHandler(objective::onOpenInventory) //(4)!
        .entity(InventoryOpenEvent::getPlayer) //(5)!
        .subscribe(true); //(6)!
    return objective; //(7)!
  }
  
}
```

1. Create an instance of the objective.
2. Request the `InventoryOpenEvent` event for the objective.
3. Set the event's priority as commonly done in listeners of the bukkit API.
4. Set the event handler of the `InventoryOpenEvent` for the objective. This expects a consumer of the objective; 
usually a method reference.
5. To parse the online profile automatically, we have to tell the service where to retrieve it from.
6. Subscribe the event to the service. This concludes the registration of the current event request.
7. Return the objective instance.

??? example "YAML usage examples"
    ```yaml
    objectives:
      trackInv: "inventory"
    ```

### Approach #2: Parameterized factory
See [Approach #2](#approach-2-adding-parameters).

Reading a parameter in addition to registering the event is done in the same way as in action, conditions and other factories.

```java title="Approach #2: InventoryObjectiveFactory.java"
public class InventoryObjectiveFactory implements ObjectiveFactory {
  
  @Override
  public Objective parseInstruction(final Instruction instruction, 
        final ObjectiveService service) throws QuestException {
    final Argument<InventoryType> inventoryType = instruction.enumeration(InventoryType.class).get(); //(1)!
    final InventoryObjective objective = new InventoryObjective(service, inventoryType); //(2)!
    service.request(InventoryOpenEvent.class) //(3)!
        .priority(EventPriority.LOWEST) //(4)!
        .onlineHandler(objective::onOpenInventory) //(5)!
        .entity(InventoryOpenEvent::getPlayer) //(6)!
        .subscribe(true); //(7)!
    return objective; //(8)!
  }
  
}
```

1. Parse the first parameter of the instruction as an `InventoryType` argument.
2. Create an instance of the objective.
3. Request the `InventoryOpenEvent` event for the objective.
4. Set the event's priority as commonly done in listeners of the bukkit API.
5. Set the event handler of the `InventoryOpenEvent` for the objective. This expects a consumer of the objective; 
usually a method reference.
6. To parse the online profile automatically, we have to tell the service where to retrieve it from.
7. Subscribe the event to the service. This concludes the registration of the current event request.
8. Return the objective instance.

??? example "YAML usage examples"
    ```yaml
    objectives:
      trackInv: "inventory CHEST"
    ```

### Approach #3: Multiple events
See [Approach #3](#approach-3-adding-more-events).

To register multiple events, we can simply add more event requests to the service.

```java title="Approach #3: InventoryObjectiveFactory.java"
public class InventoryObjectiveFactory implements ObjectiveFactory {
  
  @Override
  public Objective parseInstruction(final Instruction instruction, 
        final ObjectiveService service) throws QuestException {
    final Argument<InventoryType> inventoryType = instruction.enumeration(InventoryType.class).get();
    final InventoryObjective objective = new InventoryObjective(service, inventoryType);
    service.request(InventoryOpenEvent.class) 
        .priority(EventPriority.LOWEST) 
        .onlineHandler(objective::onOpenInventory)
        .entity(InventoryOpenEvent::getPlayer) 
        .subscribe(true);
    service.request(InventoryCloseEvent.class) //(1)!
        .onlineHandler(objective::onCloseInventory) //(2)!
        .entity(InventoryCloseEvent::getPlayer) //(2)!
        .subscribe(true);
    return objective;
  }
  
}
```

1. Make sure to request the `InventoryCloseEvent` event for the objective the same way as above.
2. Make sure to reference the correct method!

??? example "YAML usage examples"
    ```yaml
    objectives:
      trackInv: "inventory CHEST"
    ```

### Summary

To create a factory for an objective, we need to create a class that implements the `ObjectiveFactory` interface.
In the factory, you may parse the instruction to obtain arguments to create a parameterized objective instance. 
Register its events with the `ObjectiveService`.

## Details about the ObjectiveService

Since the `ObjectiveService` is central to interact with BetonQuest, we will go through its methods in more detail.

Some methods are only useful in the context of the factory for the objective, some are only useful in the context of the objective itself.

??? abstract "Factory methods"
    Methods that are only useful in the context of the factory for the objective.
    
    | Method                     | Description                                                         |
    |:---------------------------|:--------------------------------------------------------------------|
    | `request(Class<Event>)`    | Requests an event of the given type.                                |
        
??? abstract "Objective methods"
    Methods that are only useful in the context of the objective itself.
    
    | Method                     | Description                                                         |
    |:---------------------------|:--------------------------------------------------------------------|
    | `getProperties()`          | Access the properties of the objective.                             |
    | `complete(Profile)`        | Completes the objective for the given profile.                      |
    | `getExceptionHandler()`    | Retrieves the exception handler for the objective.                  |
    | `containsProfile(Profile)` | Checks if the given profile is currently involved in the objective. |
    | `callActions(Profile)`     | Calls the objective's actions for the given profile.                |
    | `checkConditions(Profile)` | Checks if the objective's conditions are met for the given profile. |
    
??? abstract "General methods"
    Methods that can be useful in both contexts.
    
    | Method                     | Description                                                         |
    |:---------------------------|:--------------------------------------------------------------------|
    | `getServiceDataProvider()` | Access the service data provider of the objective.                  |
    | `getObjectiveID()`         | Retrieves the objective ID of the objective.                        |
    | `getProfileProvider()`     | Access the profile provider of BetonQuest.                          |
    | `getLogger()`              | Retrieves the logger for the objective.                             |

### Event subscription with the request builder

The `request(Class<Event>)` method of the `ObjectiveService` returns a builder that allows us to configure the event request.
Each request must fulfill certain requirements to be registered successfully.

??? abstract "Event subscription methods"

    | Method                       | Description                                                | Info          |
    |:-----------------------------|:-----------------------------------------------------------|:--------------|
    | `handler(NonProfileHandler)` | Define how the event is handled.                           |               |
    | `handler(ProfileHandler)`    | Define how the event is handled.                           |               |
    | `onlinehandler(Handler)`     | Define how the event is handled.                           |               |
    | `uuid(Extractor)`            | Extract a players `UUID` from the event.                   |               |
    | `offlinePlayer(Extractor)`   | Extract an `OfflinePlayer` from the event.                 |               |
    | `player(Extractor)`          | Extract a `Player` from the event.                         |               |
    | `entity(Extractor)`          | Extract an `Entity` from the event that may be a `Player`. |               |
    | `profile(Extractor)`         | Extract a `Profile` from the event.                        |               |
    | `priority(EventPriority)`    | Sets the priority for the event request.                   | def: `NORMAL` |
    | `ignoreConditions()`         | Ignores conditions set for the objective for this event.   | def: unset    |
    | `subscribe(boolean)`         | Finalize event subscription and define `ignoreCancelled`   |               |

The following examples show how to subscribe to an event with the request builder:

??? example "Minimal example: `VehicleMoveEvent`"
    
    ```java title="Objective: onVehicleMove method"
    public void onVehicleMove(final VehicleMoveEvent event) throws QuestException {
      // ...
    }
    ```

    ```java title="ObjectiveFactory: subscription"
    service.request(VehicleMoveEvent.class) //(1)!
        .handler(objective::onVehicleMove) //(2)!
        .subscribe(true); //(3)!
    ```
    
    1. Request the `VehicleMoveEvent` event.
    2. Define the event handler for the objective that does **not** require a profile.
    3. Finalize the event subscription and set `ignoreCancelled` to `true` skipping cancelled events.
    
??? example "Minimal example: `PlayerJumpEvent`"

    ```java title="Objective: onPlayerJump method"
    public void onPlayerJump(final PlayerJumpEvent event, 
        final OnlineProfile onlineProfile) throws QuestException {
      // ...
    }
    ```

    ```java title="ObjectiveFactory: subscription"
    service.request(PlayerJumpEvent.class) //(1)!
        .onlineHandler(objective::onPlayerJump) //(2)!
        .player(PlayerJumpEvent::getPlayer) //(3)!
        .subscribe(true); //(4)!
    ```
    
    1. Request the `PlayerJumpEvent` event.
    2. Define the event handler for the objective that **does** require a profile.
    3. Extract the player from the event that the profile should be parsed from.
    4. Finalize the event subscription and set `ignoreCancelled` to `true` skipping cancelled events.

??? example "Minimal example: `PlayerConversationStartEvent`"

    ```java title="Objective: onConversationStart method"
    public void onConversationStart(final PlayerConversationStartEvent event, 
        final Profile profile) throws QuestException {
      // ...
    }
    ```

    ```java title="ObjectiveFactory: subscription"
    service.request(PlayerConversationStartEvent.class) //(1)!
        .handler(objective::onConversationStart) //(2)!
        .profile(PlayerConversationStartEvent::getProfile) //(3)!
        .subscribe(true); //(4)!
    ```
    
    1. Request the `PlayerConversationStartEvent` event.
    2. Define the event handler for the objective that **does** require a profile.
    3. Extract the profile from the event directly.
    4. Finalize the event subscription and set `ignoreCancelled` to `true` skipping cancelled events.

### Objective properties

To provide access to the objective's properties via an objective placeholder, we can define properties in the objective's constructor.

```java title="InventoryObjective.java"
public class InventoryObjective extends DefaultObjective {

  private final Argument<InventoryType> inventoryType;
  private final Set<UUID> openInventories;

  public InventoryObjective(final ObjectiveService objectiveService, final Argument<InventoryType> inventoryType) {
    super(objectiveService);
    this.inventoryType = inventoryType;
    this.openInventories = new HashSet<>();
    final QuestFunction<Profile, String> property = profile -> inventoryType.getValue(profile).name(); //(1)!
    service.getProperties().setProperty("type", property); //(2)!
  }

  // ...
  
}
```

1. Define a property for the inventory type by resolving the inventory type argument for the profile.
2. Set the `type` property of the objective.

## How to register an objective and its factory with BetonQuest

After creating an objective and its factory, we can register them with BetonQuest.
The name of the objective used in the script is now defined in the registration process.
Given the api is already obtained, we can access the objective registry and register the factory as follows:

```java title="Register InventoryObjectiveFactory"
public class MyPlugin extends JavaPlugin {
  
  @Override
  public void onEnable() {
    // ...
    betonQuestApi.objectives().registry().register("inventory", new InventoryObjectiveFactory()); //(1)!
    // ...
  }
  
}
```

1. Register the factory for `inventory` to the objective registry.

## How to access an objective

To access objectives, we can use the `ObjectiveManager` in the api.
The following examples are put together even though they are not related to each other.

```java title="Resolving the weather placeholder with 'create'"
public class MyFeature {
  private final BetonQuestApi betonQuestApi;
  
  public void foo(final Profile profile, final ObjectiveIdentifier identifier) throws QuestException {
    final ObjectiveManager objectiveManager = betonQuestApi.objectives().manager();
    final List<Objective> objectives = objectiveManager.getForProfile(profile); //(1)!
    final Objective objective = objectiveManager.getObjective(identifier); //(2)!
    
    objectiveManager.start(profile, identifier); //(3)!
    objectiveManager.cancel(profile, identifier); //(4)!
  }
  
}
```

1. Obtain all objectives for the given profile.
2. Obtain the objective with the given identifier.
3. Start the objective for the given profile.
4. Cancel the objective for the given profile.

## How a full example looks like

??? example "InventoryObjective.java"
    ```java title="InventoryObjective.java"
    public class InventoryObjective extends DefaultObjective {
    
      private final Argument<InventoryType> inventoryType;
      private final Set<UUID> openInventories;
    
      public InventoryObjective(final ObjectiveService objectiveService, final Argument<InventoryType> inventoryType) {
        super(objectiveService);
        this.inventoryType = inventoryType;
        this.openInventories = new HashSet<>();
      }
    
      public void onOpenInventory(final InventoryOpenEvent event, 
              final OnlineProfile onlineProfile) throws QuestException {
        if (event.getInventory().getType() != inventoryType.getValue(onlineProfile)) {
          return;
        }
        openInventories.add(onlineProfile.getPlayerUUID());
      }
      
      public void onCloseInventory(final InventoryCloseEvent event, 
                final OnlineProfile onlineProfile) throws QuestException {
        if (!openInventories.remove(onlineProfile.getPlayerUUID())) {
          return;
        }
        getService().complete(onlineProfile);
      }
      
    }
    ```

??? example "InventoryObjectiveFactory.java"
    ```java title="InventoryObjectiveFactory.java"
    public class InventoryObjectiveFactory implements ObjectiveFactory {
      
      @Override
      public Objective parseInstruction(final Instruction instruction, 
            final ObjectiveService service) throws QuestException {
        final Argument<InventoryType> inventoryType = instruction.enumeration(InventoryType.class).get();
        final InventoryObjective objective = new InventoryObjective(service, inventoryType);
        service.request(InventoryOpenEvent.class) 
            .priority(EventPriority.LOWEST) 
            .onlineHandler(objective::onOpenInventory)
            .entity(InventoryOpenEvent::getPlayer) 
            .subscribe(true);
        service.request(InventoryCloseEvent.class) //(1)!
            .onlineHandler(objective::onCloseInventory) //(2)!
            .entity(InventoryCloseEvent::getPlayer) //(2)!
            .subscribe(true);
        return objective;
      }
      
    }
    ```

??? example "YAML usage examples"
    ```yaml
    objectives:
      trackInv: "inventory CHEST"
    ```

## What limitations are there

### Synchronous objective completion

All objectives retain their context by default. But since most bukkit events are mostly synchronous, objectives 
usually are too.

### Legacy counting objective

There is a legacy `org.betonquest.betonquest.api.CountObjective` that is **not recommended** to use.
It is internally used to measure numerical progress on objectives. 
It is explicitly not intended to be used by integrations as it is not part of the official API and scheduled for removal.
