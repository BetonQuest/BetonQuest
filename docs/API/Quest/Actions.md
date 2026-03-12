---
icon: material/application-export
status: new
---
@snippet:api-state:draft@

??? abstract "Action API Classes"
    * `org.betonquest.betonquest.api.service.action.Actions`
    * `org.betonquest.betonquest.api.service.action.ActionManager`
    * `org.betonquest.betonquest.api.service.action.ActionRegistry`
    * `org.betonquest.betonquest.api.quest.action.PlayerActionFactory`
    * `org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory`
    * `org.betonquest.betonquest.api.quest.action.PlayerAction`
    * `org.betonquest.betonquest.api.quest.action.PlayerlessAction`
    * `org.betonquest.betonquest.api.quest.action.NullableAction`
    * `org.betonquest.betonquest.api.quest.action.OnlineAction`

## Introduction

This page covers the Actions API of BetonQuest.

!!! warning "You should have viewed these pages"
    - [How to access the api](../Obtaining-API.md)
    - [How to use the factory system](./Quest-API.md)
    - [How to work with Instructions](../Instruction.md)

<div class="grid" markdown>

!!! abstract "What this page covers"
    - [How to create an action](#how-to-create-an-action)
    - [How to create a factory for an action](#how-to-create-a-factory-for-an-action)
    - [How to register an action and its factory](#how-to-register-an-action-and-its-factory-with-betonquest)
    - [How to run an action](#how-to-run-an-action)
    - [How a full example looks like](#how-a-full-example-looks-like)
    - [Additional notes](#additional-notes)

!!! info "What this page does not cover"
    - What an action is
    - [Which actions are available](../../Documentation/Scripting/Building-Blocks/Actions-List.md)
    - [How work with actions in scripting](../../Tutorials/Getting-Started/Basics/Actions.md)
    
</div>

## How to create an action

Creating an action will be explained in the following sections using the `MyBanAction` class as an example.
The example classes aim to be as simple but versatile as possible.

### Approach #1: A simple implementation

This first approach is the easiest to understand. It is a simple action that bans the player with a fixed reason.  
Any Action that implements the `OnlineAction` interface will be executed on the player's online profile and 
therefore requires the player to be online to execute.  
The `execute` method is called when the action is executed and provides the player's online profile the action is 
supposed to be executed on.

```java title="Approach #1: MyBanAction.java"
public class MyBanAction implements OnlineAction {

    public MyBanAction() {
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        profile.getPlayer().banPlayer("You have been banned by a custom action!");
    }
    
}
```

Now that we know what our action should do, we might wanna make it more versatile by parameterizing it.  
Currently, it just bans the player it's executed on with a fixed reason, but we can make that configurable.
We can make it more versatile by adding a `reason` parameter to the action:

### Approach #2: Adding a parameter to the action

To implement this, we have to make the message configurable and add a field to the action:
```java title="Approach #2: MyBanAction.java"
public class MyBanAction implements OnlineAction {

  private final String reason;

  public MyBanAction(final String reason) {
    this.reason = reason;
  }
  
  @Override
  public void execute(final OnlineProfile profile) throws QuestException {
    profile.getPlayer().banPlayer(this.reason);
  }
  
}
```

This will allow the reason message to be passed to the action. However, the message will be only plainly configurable this way.
To make it more versatile, allowing placeholders to be used in the message, we have to utilize the Argument container.

### Approach #3: Using the `Argument` container

The Argument container allows us to pass arguments to the action in such a way that they can be resolved for the profile.
```java title="Approach #3: MyBanAction.java"
public class MyBanAction implements OnlineAction {
  
  private final Argument<String> reason;
  
  public MyBanAction(final Argument<String> reason) {
    this.reason = reason;
  }
  
  @Override
  public void execute(final OnlineProfile profile) throws QuestException {
    profile.getPlayer().banPlayer(this.reason.getValue(profile));
  }
  
}
```

Now the message is fully configurable and can contain placeholders, but it still requires the player to be online to execute.
And there are more optional parameters that we can use to ban a player.

### Approach #4: Making the action independent

To make our action independent of the player, we can use the `PlayerlessAction` interface and use an Argument for the player.
```java title="Approach #4: MyBanAction.java"
public class MyBanAction implements PlayerlessAction {
  
  private final Argument<UUID> playerUID;
  private final Argument<String> reason;
  
  public MyBanAction(final Argument<UUID> playerUID, final Argument<String> reason) {
    this.playerUID = playerUID;
    this.reason = reason;
  }
  
  @Override
  public void execute() throws QuestException {
    final OfflinePlayer playerToBan = Bukkit.getOfflinePlayer(this.playerUID.getValue(null));
    playerToBan.banPlayer(this.reason.getValue(null));
  }
  
}
```

This action is now independent of the executing player and can be executed with a players UUID as a parameter instead.

### Approach #5: Making the action nullable

Since our action is technically independent of the player, but still does work with a player, we can use the `NullableAction` interface.
This no longer requires a player to execute the action. However, based on the profile executing the action, placeholders will be resolved differently.

```java title="Approach #5: MyBanAction.java"
public class MyBanAction implements NullableAction {
  
  private final Argument<UUID> playerUID;
  private final Argument<String> reason;
  
  public MyBanAction(final Argument<UUID> playerUID, final Argument<String> reason) {
    this.playerUID = playerUID;
    this.reason = reason;
  }
  
  @Override
  public void execute(@Nullable final Profile profile) throws QuestException {
    final OfflinePlayer playerToBan = Bukkit.getOfflinePlayer(this.playerUID.getValue(profile));
    playerToBan.banPlayer(this.reason.getValue(profile));
  }
  
}
```

### Summary

To create an action, we have to implement one of the following interfaces:

- `OnlineAction` to require the executing player to be online
- `PlayerAction` to require at least an offline player
- `PlayerlessAction` to not require a player at all
- `NullableAction` to allow the action to be executed with or without a player

Using `Argument`s allows us to use placeholders in any value passed to the action.

## How to create a factory for an action

After creating an action, we have to create a factory for it.
Factories are used to create instances of an action for a specific instruction.

In the following we are going to learn how to create a factory for the different `MyBanAction` approaches.

### Approach #1: A simple factory
See [Approach #1](#approach-1-a-simple-implementation).

This is the easiest approach to create a factory for an action and may even be done inlined in the [registration process](#how-to-register-an-action-and-its-factory-with-betonquest).
However, this approach is not very versatile and therefore not recommended in most cases.

Creating a factory for an action requires implementing the `PlayerActionFactory` or `PlayerlessActionFactory` interface.
There are no interfaces for the `NullableAction` and `OnlineAction` directly.

In this example, our `MyBanAction` is a `OnlineAction` and therefore requires a `PlayerActionFactory`.
The factory is created by implementing the `create` method and returning an instance of `OnlineActionAdapter` to adapt the action to the `PlayerAction` interface.
Since we entirely ignore the instruction, we can just return a new instance of the action, which leads to identical behavior for all instructions of this action.

```java title="Approach #1: MyBanActionFactory.java"
public class MyBanActionFactory implements PlayerActionFactory {
  
  @Override
  public PlayerAction parsePlayer(final Instruction instruction) {
    return new OnlineActionAdapter(new MyBanAction());
  }
  
}
```

??? example "YAML examples"
    ```yaml
      actions:
        banAction: ban
    ```

### Approach #2: Without `Argument` container
See [Approach #2](#approach-2-adding-a-parameter-to-the-action).

Adding a parameter to the action is a good approach to make the action more versatile.
In the second approach, we made the message configurable by adding a field to the action.
Now we still have to pass the message to the action. This is done inside the factory.

```java title="Approach #2: MyBanActionFactory.java"
public class MyBanActionFactory implements PlayerActionFactory {
  
  @Override
  public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
    final String message = instruction.string().get().getValue(null); //(1)!
    return new OnlineActionAdapter(new MyBanAction(message));
  }
  
}
```

1. Get the message from the instruction using the `string()` method to declare the type of the argument.

??? example "YAML examples"
    ```yaml
      actions:
        banAction: ban "You have been banned by a custom action!"
        banAction2: ban "You have been banned with a different message!"
    ```

### Approach #3: With `Argument` container
See [Approach #3](#approach-3-using-the-argument-container).

Having the message configurable is good, but using the `Argument` container is even better.
In the third approach, we passed the message to the action as an `Argument` and the `Instruction` 
is designed to make it as easy as possible to create an `Argument` for a specific value.
The factory is very similar to the second approach, pay close attention to the `Argument` creation.

```java title="Approach #3: MyBanActionFactory.java"
public class MyBanActionFactory implements PlayerActionFactory {
  
  @Override
  public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
    final Argument<String> message = instruction.string().get(); //(1)!
    return new OnlineActionAdapter(new MyBanAction(message));
  }
  
}
```

1. Get the message from the instruction using the `string()` method to declare the type of the argument.

??? example "YAML examples"
    ```yaml
      constants:
        bannedForTrespassing: You have been banned for Trespassing!
      actions:
        banAction: ban "You have been banned by a custom action!"
        banAction2: ban %constant.bannedForTrespassing%
    ```

### Approach #4: Independent action factory
See [Approach #4](#approach-4-making-the-action-independent).

In the fourth approach, we made the action independent of the player.
Now we have to use the `PlayerlessActionFactory` interface to create an action factory.
Since we are no longer using an `OnlineAction` but a `PlayerlessAction` instead, 
we may also drop the `OnlineActionAdapter` and just return the action directly.

```java title="Approach #4: MyBanActionFactory.java"
public class MyBanActionFactory implements PlayerlessActionFactory {
  
  @Override
  public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
    final Argument<UUID> playerUID = instruction.uuid().get(); //(1)!
    final Argument<String> message = instruction.string().get(); //(2)!
    return new MyBanAction(playerUID, message);
  }
  
}
```

1. Get the player UID from the instruction using the `uuid()` method to declare the type of the argument.
2. Get the message from the instruction using the `string()` method to declare the type of the argument.

??? example "YAML examples"
    ```yaml
      constants:
        bannedForTrespassing: You have been banned for Trespassing!
      actions:
        banAction: ban 49085e80-eb73-4e2e-b14d-6bd5f5f76062 "You have been banned by a custom action!"
        banAction2: ban 49085e80-eb73-4e2e-b14d-6bd5f5f76062 %constant.bannedForTrespassing%
    ```

### Approach #5: Nullable action factory
See [Approach #5](#approach-5-making-the-action-nullable).

Nullable actions require both `PlayerActionFactory` and `PlayerlessActionFactory` to be implemented.
In most cases, we will create a single method that creates the action for both cases.
But if the action has different behavior for player and playerless calls, we can implement both methods separately.
We do recommend creating two separate actions and factories if we plan to have different behavior for player and 
playerless calls. This will ensure that users are less confused about the behavior of the action.

We have to use the `NullableActionAdapter` to adapt the action using the `NullableAction` interface to the `PlayerAction` and `PlayerlessAction` interfaces.

```java title="Approach #4: MyBanActionFactory.java"
public class MyBanActionFactory implements PlayerActionFactory, PlayerlessActionFactory {
  
  @Override
  public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
    return parse(instruction);
  }
  
  @Override
  public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
    return parse(instruction);
  }
  
  private NullableActionAdapter parse(final Instruction instruction) throws QuestException {
    final Argument<UUID> playerUID = instruction.uuid().get(); //(1)!
    final Argument<String> message = instruction.string().get(); //(2)!
    return new NullableActionAdapter(new MyBanAction(playerUID, message));
  }
  
}
```

1. Get the player UID from the instruction using the `uuid()` method to declare the type of the argument.
2. Get the message from the instruction using the `string()` method to declare the type of the argument.

??? example "YAML examples"
    The last example requires a player to execute the action, since the placeholder `%player.name%` is used. 
    ```yaml
      constants:
        bannedForTrespassing: You have been banned for Trespassing!
      actions:
        banAction: ban 49085e80-eb73-4e2e-b14d-6bd5f5f76062 "You have been banned by a custom action!"
        banAction2: ban 49085e80-eb73-4e2e-b14d-6bd5f5f76062 %constant.bannedForTrespassing%
        banActionWithPlayer: ban 49085e80-eb73-4e2e-b14d-6bd5f5f76062 "You have been banned by %player.name%!"
    ```

### Optional parameters

If we want to have optional parameters in our action, for example, the expiration time of a ban, we have to get the 
`Argument` from the instruction with an optional parameter name.  
A more detailed explanation of this can be found in the documentation for [Instructions](../Instruction.md).


??? example
    [Approach #5](#approach-5-nullable-action-factory) modified to include an optional expiration time as an 
    example: See the full example [here](#how-a-full-example-looks-like).

### Summary

To create a factory for an action, we have to implement one of the following interfaces:

- `PlayerActionFactory` to create an action for a `PlayerAction` or `OnlineAction`
- `PlayerlessActionFactory` to create an action for a `PlayerlessAction`

We have to implement both interfaces for a `NullableAction`.

Since `OnlineAction` is not a `PlayerAction`, we have to use the `OnlineActionAdapter` to adapt the action to the 
`PlayerAction` interface.  
Since `NullableAction` is neither a `PlayerAction` nor a `PlayerlessAction`, we have to use the `NullableActionAdapter` 
to adapt the action to the `PlayerAction` and `PlayerlessAction` interfaces.


## How to register an action and its factory with BetonQuest

After creating an action and its factory, we have to register them with BetonQuest.  
The name of the action used in the script is now defined in the registration process.  
Given the api is already obtained, we can access the action registry and register the factory as follows:

```java title="Register MyBanActionFactory.java"
public class MyPlugin extends JavaPlugin {
  
  @Override
  public void onEnable() {
    // ...
    betonQuestApi.actions().registry().register("ban", new MyBanActionFactory()); //(1)!
    // ...
  }
  
}
```

1. Only viable for approaches #1-#4

Since `NullableAction` requires both `PlayerActionFactory` and `PlayerlessActionFactory`, we have to use the `registry().registerCombined(...)` method to register both factories at once:

```java title="Register MyBanActionFactory.java"
public class MyPlugin extends JavaPlugin {
  
  @Override
  public void onEnable() {
    // ...
    betonQuestApi.actions().registry().registerCombined("ban", new MyBanActionFactory()); //(1)!
    // ...
  }
  
}
```

1. Only viable for approach #5

## How to run an action

Instead of creating new action types and their factories, we can also use existing ones and run them.
This is useful if we want to reuse a loaded action in another action or just for a feature inside an addon.
Loaded action instances are generally referenced by an `ActionIdentifier` consisting of their name and the package they are defined in.
`ActionIdentifier`s are unique since you cannot have two actions with the same name in the same package.
They can be obtained from an `Instruction` using the `identifier(ActionIdentifier.class)` type declaring method (See [Instruction](../Instruction.md#argument-parsing)).

You can run an action as follows:

```java title="Run an action"
public class MyFeature {
  private final BetonQuestApi betonQuestApi;
  
  public void foo() {
    final ActionIdentifier action; //(1)!
    final Profile profile; //(2)!
    betonQuestApi.actions().manager().run(profile, action); //(3)!
  }
  
}
```

1. The `ActionIdentifier` is used to identify the action to be executed.
2. The `Profile` is required for actions that require a player. May also be an `OnlineProfile`.
3. The action is executed for the provided profile.

You can also run an action without a profile, requiring the action to be independent:

```java title="Run an action"
public class MyFeature {
  private final BetonQuestApi betonQuestApi;
  
  public void foo() {
    final ActionIdentifier action; //(1)!
    betonQuestApi.actions().manager().run(null, action); //(2)!
  }
  
}
```

1. The `ActionIdentifier` is used to identify the action to be executed.
2. In this case, it _may_ fail if no `Profile` is provided, but the action is _not_ independent.

Lastly, running multiple actions at once is also possible:

```java title="Run an action"
public class MyFeature {
  private final BetonQuestApi betonQuestApi;
  
  public void foo() {
    final Set<ActionIdentifier> actions; //(1)!
    betonQuestApi.actions().manager().run(null, actions); //(2)!
  }
  
}
```

1. Any `java.util.Collection` type is supported
2. The order of the actions is _not_ guaranteed to be preserved. In this case, it may fail if no profile is provided, but any action is not independent.

## How a full example looks like

??? example "MyBanAction.java"
    ```java title="MyBanAction.java"
    public class MyBanAction implements NullableAction {
      
      private final Argument<UUID> playerUID;
      private final Argument<String> reason;
      private final Argument<TimeUnit> timeUnit;
      @Nullable //(1)!
      private final Argument<Number> duration;
      
      public MyBanAction(final Argument<UUID> playerUID, final Argument<String> reason, 
                         final Argument<TimeUnit> timeUnit, @Nullable final Argument<Number> duration) {
        this.playerUID = playerUID;
        this.reason = reason;
        this.timeUnit = timeUnit;
        this.duration = duration;
      }
      
      @Override
      public void execute(@Nullable Profile profile) throws QuestException {
        final OfflinePlayer playerToBan = Bukkit.getOfflinePlayer(this.playerUID.getValue(profile));
        final String reason = this.reason.getValue(profile);
        if (this.duration != null) { //(2)!
          final long expirationMillis = this.timeUnit.getValue(profile).toMillis(this.duration.getValue(profile));
          final Date expirationDate = new Date(System.currentTimeMillis() + expirationMillis); //(3)!
          playerToBan.banPlayer(reason, expirationDate);
        } else {
          playerToBan.banPlayer(reason);
        }
      }
      
    }
    ```
    
    1. The `duration` argument is nullable as marker for its presence.
    2. If the `duration` argument is present, the player will be banned for the specified duration.
    3. The expiration date is calculated using the `timeUnit` and the `duration` argument applied to the current time.
    
??? example "MyBanActionFactory.java"
    ```java title="MyBanActionFactory.java"
    public class MyBanActionFactory implements PlayerActionFactory, PlayerlessActionFactory {
      
      @Override
      public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return parse(instruction);
      }
      
      @Override
      public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return parse(instruction);
      }
      
      @Override
      public NullableActionAdapter parse(final Instruction instruction) throws QuestException {
        final Argument<UUID> playerUID = instruction.uuid().get(); //(1)!
        final Argument<String> message = instruction.string().get(); //(2)!
        final Argument<TimeUnit> unit = instruction.enumeration(TimeUnit.class).get("unit", TimeUnit.DAYS); //(3)!
        final Optional<Argument<Number>> duration = instruction.number().get("time"); //(4)!
        return new NullableActionAdapter(new MyBanAction(playerUID, message, unit, duration.orElse(null)));
      }
      
    }
    ```
    
    1. The `playerUID` argument is required as first argument.
    2. The `message` argument is required as second argument.
    3. The `unit` argument is optional and can be omitted resolving to `DAYS` if not present.
    4. The `duration` argument is optional and can be omitted resolving to `null` if not present.

??? example "actions.yml"
    ```yaml title="actions.yml"
      actions:
        banAction1: ban 49085e80-eb73-4e2e-b14d-6bd5f5f76062 "You have been permanently banned"
        banAction2: ban 49085e80-eb73-4e2e-b14d-6bd5f5f76062 "You have been temporarily banned for 10 days" duration:10
        banAction3: ban 49085e80-eb73-4e2e-b14d-6bd5f5f76062 "You have been temporarily banned for 48 hours" duration:48 unit:hours
    ```

## Additional notes

### Force synchronous action execution

Generally, BetonQuest tries to run actions asynchronously. In some contexts, especially if an action interacts with 
the bukkit api, it may be necessary to run the action synchronously.

To mark an action as required to run in sync with the main thread, overwrite the `isPrimaryThreadEnforced()` method 
inherented from the `PrimaryThreadEnforceable` interface, which is implemented by all actions by default:

```java title="MySyncAction.java"
public class MySyncAction implements PlayerAction {

  public MySyncAction() {
  }
  
  @Override
  public void execute(final Profile profile) {
    // ...
  }
  
  @Override
  public boolean isPrimaryThreadEnforced() {
    return true; //(1)!
  }
  
}
```

1. This method returns `false` by default, so all actions are executed asynchronously if not defined otherwise.
