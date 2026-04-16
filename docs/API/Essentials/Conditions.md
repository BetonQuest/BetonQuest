---
icon: material/application-export
status: new
---
@snippet:api-state:unfinished@

??? abstract "Condition API Classes"
    * `org.betonquest.betonquest.api.service.condition.Conditions`
    * `org.betonquest.betonquest.api.service.condition.ConditionManager`
    * `org.betonquest.betonquest.api.service.condition.ConditionRegistry`
    * `org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory`
    * `org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory`
    * `org.betonquest.betonquest.api.quest.condition.PlayerCondition`
    * `org.betonquest.betonquest.api.quest.condition.PlayerlessCondition`
    * `org.betonquest.betonquest.api.quest.condition.NullableCondition`
    * `org.betonquest.betonquest.api.quest.condition.OnlineCondition`

## Introduction

This page covers the Conditions API of BetonQuest.

!!! warning "You should have viewed these pages"
    - [How to access the api](../Obtaining-API.md)
    - [How to use the factory system](./Overview.md)
    - [How to work with Instructions](../Tools/Instruction.md)

<div class="grid" markdown>

!!! abstract "What this page covers"
    - [How to create a condition](#how-to-create-a-condition)
    - [How to create a factory for a condition](#how-to-create-a-factory-for-a-condition)
    - [How to register a condition and its factory with BetonQuest](#how-to-register-a-condition-and-its-factory-with-betonquest)
    - [How to test a condition](#how-to-test-a-condition)
    - [How a full example looks like](#how-a-full-example-looks-like)
    - [Additional notes](#additional-notes)

!!! info "What this page does not cover"
    - What a condition is
    - [Which conditions are available](../../Documentation/Scripting/Building-Blocks/Conditions-List.md)
    - [How to work with conditions in scripting](../../Tutorials/Getting-Started/Basics/Conditions.md)

</div>

## How to create a condition

Creating a condition will be explained in the following sections using multiple examples.
The example classes aim to be as simple but versatile as possible.

### Approach #1: A naive implementation

The most basic implementation of a condition is a class that implements the `OnlineCondition` interface.
Any condition using this interface requires a player to be online.
Likewise, the `PlayerCondition` interface requires a player that may be offline and the `PlayerlessCondition` interface requires no player.
You are free to implement any logic you want in the `check` method, which should return `true` if the condition is met.

In the first example we want to look for a very specific name by checking if the player's name is equal to the name we want.

```java title="Approach #1: IsBiggestFanCondition.java"
public class IsBiggestFanCondition implements OnlineCondition {

    public IsBiggestFanCondition() {
    }

    @Override
    public boolean check(final OnlineProfile profile) {
      return profile.getPlayer().getName().equals("_Xx_BQ_Lover_xX_");
    }
    
}
```

### Approach #2: Adding parameters

Since there is more than one name to check, we can pass the name to the constructor and use it in the `check` method.

```java title="Approach #2: IsBiggestFanCondition.java"
public class IsBiggestFanCondition implements OnlineCondition {

    private final String name;
  
    public IsBiggestFanCondition(final String name) {
      this.name = name;
    }

    @Override
    public boolean check(final OnlineProfile profile) {
      return profile.getPlayer().getName().equals(name);
    }
    
}
```

This will allow the name to be passed to the condition. 
However, the name will be only plainly configurable this way. 
To make it more versatile, allowing placeholders to be used in the name, we have to utilize the Argument container.

### Approach #3: Using the Argument container

The `Argument` container allows us to pass arguments to the condition in such a way that they can be resolved for the profile.

```java title="Approach #3: IsBiggestFanCondition.java"
public class IsBiggestFanCondition implements OnlineCondition {

    private final Argument<String> name;
  
    public IsBiggestFanCondition(final Argument<String> name) {
      this.name = name;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
      return profile.getPlayer().getName().equals(this.name.getValue(profile));
    }
    
}
```

Now the name is fully configurable and can contain placeholders, but it still requires the player to be online to be checked.
We also might want to check the player's name against a list of names.

### Approach #4: For offline players and multiple names

To make the condition work for both online and offline players alike, we have to implement the `PlayerCondition` 
interface instead of the `OnlineCondition` interface.
We also have to pass a list of names to the constructor to check against a list of names instead of a single name.

```java title="Approach #4: IsBiggestFanCondition.java"
public class IsBiggestFanCondition implements PlayerCondition {

    private final Argument<List<String>> names;
  
    public IsBiggestFanCondition(final Argument<List<String>> names) {
      this.names = names;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
      final List<String> namesToCheck = this.names.getValue(profile);
      return namesToCheck.contains(profile.getPlayer().getName());
    }
    
}
```

This will allow the condition to work for both online and offline players and check against a list of names.

### More interfaces & Summary

To create a condition, you have to implement one of the following interfaces:

- `OnlineCondition` to require a player to be online
- `PlayerCondition` to require a player that may be offline
- `PlayerlessCondition` to not require a player at all
- `NullableCondition` to allow the condition to be checked with or without a player

Using `Argument`s allows you to pass arguments to the condition that may contain placeholders.

## How to create a factory for a condition

After creating a condition, we have to create a factory for it.
Factories are used to create instances of a condition for a specific instruction.

Creating a factory for a condition is done by implementing the `PlayerConditionFactory` or `PlayerlessConditionFactory` interface.
There are no factory interfaces equivalent to the `OnlineCondition` or `NullableCondition` interfaces.

In the following you are going to learn how to create a factory for the different `IsBiggestFanCondition` approaches.
In all approaches we will create only factories for condition classes that do require a player and therefore implement the `PlayerConditionFactory` interface.

### Approach #1: A simple factory
See [Approach #1](#approach-1-a-naive-implementation).

This is the easiest approach to create a factory for a condition and may be even done inline in the [registration](#how-to-register-a-condition-and-its-factory-with-betonquest).
However, this approach is not very versatile and therefore explicitly not recommended in most cases.

```java title="Approach #1: IsBiggestFanConditionFactory.java"
public class IsBiggestFanConditionFactory implements PlayerConditionFactory {
  
  @Override
  public PlayerCondition parsePlayer(final Instruction instruction) {
    return new OnlineConditionAdapter(new IsBiggestFanCondition());
  }
  
}
```

??? example "YAML examples"
    ```yaml
    conditions:
      biggestFan: isBiggestFan
    ```

### Approach #2: Without placeholder support
See [Approach #2](#approach-2-adding-parameters).

Adding a parameter to the condition is a good approach to make the condition more versatile.
In the second approach we made the player name configurable by passing it to the constructor.
Now we still have to pass the name to the condition. This is done inside the factory.

```java title="Approach #2: IsBiggestFanConditionFactory.java"
public class IsBiggestFanConditionFactory implements PlayerConditionFactory {
  
  @Override
  public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
    final String name = instruction.string().get().getValue(null); //(1)!
    return new OnlineConditionAdapter(new IsBiggestFanCondition(name));
  }
  
}
```

1. Get the name from the instruction using the `string()` method to declare the type of the argument.

??? example "YAML examples"
    ```yaml
    conditions:
      biggestFan1: isBiggestFan "_Xx_BQ_Lover_xX_"
      biggestFan2: isBiggestFan "Wolf2323"
    ```

### Approach #3: With placeholder support using `Argument`
See [Approach #3](#approach-3-using-the-argument-container).

Having the name configurable is good, but using the `Argument` container is even better.
In the third approach, we passed the name to the condition as an `Argument` and the `Instruction` is designed
to make it as easy as possible to create an `Argument` for a specific value.
The factory is very similar to the second approach, pay close attention to the `Argument` creation.

```java title="Approach #3: IsBiggestFanConditionFactory.java"
public class IsBiggestFanConditionFactory implements PlayerConditionFactory {
  
  @Override
  public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
    final Argument<String> name = instruction.string().get(); //(1)!
    return new OnlineConditionAdapter(new IsBiggestFanCondition(name));
  }
  
}
```

1. Get the name from the instruction using the `string()` method to declare the type of the argument.

??? example "YAML examples"
    ```yaml
    constants:
      biggestFan: "Wolf2323"
    conditions:
      biggestFan1: isBiggestFan "_Xx_BQ_Lover_xX_"
      biggestFan2: isBiggestFan %constant.biggestFan%
    ```

### Approach #4: Offline player support and a list of names
See [Approach #4](#approach-4-for-offline-players-and-multiple-names).

In the fourth approach we want to make the condition work for both online and offline players.
We also want to check against a list of names.
The factory is very similar to the third approach, pay close attention to the `Argument` creation, where we use the `list()` modifier to
create an `Argument` that can contain multiple comma-separated values.

```java title="Approach #4: IsBiggestFanConditionFactory.java"
public class IsBiggestFanConditionFactory implements PlayerConditionFactory {
  
  @Override
  public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
    final Argument<List<String>> name = instruction.string().list().get();
    return new IsBiggestFanCondition(name); //(1)!
  }
  
}
```

1. The `OnlineConditionAdapter` is no longer needed since the condition is a `PlayerCondition` itself.

??? example "YAML examples"
    ```yaml
    conditions:
      biggestFan: isBiggestFan "_Xx_BQ_Lover_xX_,Wolf2323,some_random_name"
    ```

### Optional parameters

If we want to have optional parameters in our condition, for example, to switch between names and uuids, we have to get the 
`Argument` from the instruction with an optional parameter name.  
A more detailed explanation of this can be found in the documentation for [Instructions](../Tools/Instruction.md).

### Summary

To create a factory for a condition, you have to implement one of the following interfaces:

- `PlayerConditionFactory` to create a condition that requires a player
- `PlayerlessConditionFactory` to create a condition that does not require a player

For `NullableCondition`s you have to implement both interfaces.

Since `OnlineCondition` is not a `PlayerCondition` we have to use the `OnlineConditionAdapter` to make it work.
Since `NullableCondition` is neither a `PlayerCondition` nor a `PlayerlessCondition` we have to use 
the `NullableConditionAdapter` to adapt to the `PlayerCondition` and `PlayerlessCondition` interfaces.

## How to register a condition and its factory with BetonQuest

After creating a condition and its factory, we have to register them with BetonQuest.  
The name of the condition used in the script is now defined in the registration process.  
Given the api is already obtained, we can access the condition registry and register the factory as follows:

```java title="Register IsBiggestFanConditionFactory.java"
public class MyPlugin extends JavaPlugin {
  
  @Override
  public void onEnable() {
    // ...
    betonQuestApi.conditions().registry().register("isBiggestFan", new IsBiggestFanConditionFactory()); //(1)!
    // ...
  }
  
}
```

1. For factories that implement both `PlayerConditionFactory` and `PlayerlessConditionFactory`, you should use `registerCombined` instead.

## How to test a condition

Instead of creating new condition types and their factories, we can also use existing ones and test them.
This is useful if we want to reuse loaded conditions in another condition or just for a feature inside an addon.
Loaded conditions are generally referenced by an `ConditionIdentifier` consisting of their name and the package they are defined in.
`ConditionIdentifier`s are unique since you cannot have two conditions with the same name in the same package.
They can be obtained from an `Instruction` using the `identifier(ConditionIdentifier.class)` type declaring method (See [Instruction](../Tools/Instruction.md#argument-parsing))
or directly manually via the API (See [API Overview](../Essentials/Overview.md#identifiers)).

You can test a condition as follows:

```java title="Test a condition"
public class MyFeature {
  private final BetonQuestApi betonQuestApi;
  
  public void foo() {
    final ConditionIdentifier condition; //(1)!
    final Profile profile; //(2)!
    final boolean result = betonQuestApi.conditions()
        .manager().test(profile, condition); //(3)!
  }
  
}
```

1. The `ConditionIdentifier` is used to identify the condition to be tested.
2. The `Profile` is required for conditions that require a player. May also be an `OnlineProfile`.
3. The condition is tested for the provided profile.

You can also test a condition without a profile requiring the condition to be independent:

```java title="Test a condition"
public class MyFeature {
  private final BetonQuestApi betonQuestApi;
  
  public void foo() {
    final ConditionIdentifier condition; //(1)!
    final boolean result = betonQuestApi.conditions()
        .manager().test(null, condition); //(2)!
  }
  
}
```

1. The `ConditionIdentifier` is used to identify the condition to be tested.
2. In this case, it will fail if no `Profile` is provided but the condition is _not_ independent.

Lastly, testing multiple conditions at once is also possible in either conjunction or disjunction.
In most cases not all conditions need to be tested to determine the result, this therefore improves performance.

```java title="Test conditions"
public class MyFeature {
  private final BetonQuestApi betonQuestApi;
  
  public void foo() {
    final Set<ConditionIdentifier> conditions; //(1)!
    final boolean result = betonQuestApi.conditions()
        .manager().testAll(null, condition); //(2)!
    final boolean result = betonQuestApi.conditions()
        .manager().testAny(null, condition); //(3)!
  }
  
}
```

1. Any `java.util.Collection` type is supported
2. Conjunction. The order of the conditions is _not_ guaranteed to be preserved. 
It may fast-fail if one condition is not met without checking the others.
3. Disjunction. The order of the conditions is _not_ guaranteed to be preserved. 
It may early-succeed if one condition is met without checking the others.

## How a full example looks like

??? example "IsBiggestFanCondition.java"
    ```java title="IsBiggestFanCondition.java"
    public class IsBiggestFanCondition implements PlayerCondition {
    
        private final Argument<List<String>> names;
        
        private final FlagArgument<Boolean> useUUIDs;
      
        public IsBiggestFanCondition(final Argument<List<String>> names, final FlagArgument<Boolean> useUUIDs) {
          this.names = names;
          this.useUUIDs = useUUIDs;
        }
    
        @Override
        public boolean check(final Profile profile) throws QuestException {
          final List<String> namesToCheck = this.names.getValue(profile);
          if (this.useUUIDs.getValue(profile).orElse(false)) { //(1)!
            return namesToCheck.contains(profile.getPlayer().getUniqueId().toString());
          }
          return namesToCheck.contains(profile.getPlayer().getName());
        }
        
    }
    ```
    
    1. The `useUUIDs` flag is used to switch between checking against names and uuids. It defaults to `false` if not present.

??? example "IsBiggestFanConditionFactory.java"
    ```java title="IsBiggestFanConditionFactory.java"
    public class IsBiggestFanConditionFactory implements PlayerConditionFactory {
      
      @Override
      public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<List<String>> names = instruction.string().list().get(); //(1)!
        final FlagArgument<Boolean> useUUIDs = instruction.bool().getFlag("useUUIDs", true); //(2)!
        return new IsBiggestFanCondition(names, useUUIDs);
      }
      
    }
    ```
    
    1. The list of names to check against is required as the first argument.
    2. The `useUUIDs` flag is used to switch between checking against names and uuids. It defaults to `true` if it is present but unspecified.

??? example "conditions.yml"
    ```yaml title="conditions.yml"
    conditions:
      isBiggestFan1: isBiggestFan "_Xx_BQ_Lover_xX_,Wolf2323,some_random_name"
      isBiggestFan2: isBiggestFan "49085e80-eb73-4e2e-b14d-6bd5f5f76062,2cc0d875-2501-4d14-8676-467ea522ea4f" useUUIDs
      isBiggestFan3: isBiggestFan "2cc0d875-2501-4d14-8676-467ea522ea4f,eba17d33-959d-42a7-a4d9-e9aebef5969e" useUUIDs:true
    ```

## Additional notes

### Force synchronous condition testing

Generally, BetonQuest tries to test conditions asynchronously. In some contexts, especially if a condition interacts with 
the bukkit api, it may be necessary to test it synchronously.

To mark a condition as required to test in sync with the main thread, overwrite the `isPrimaryThreadEnforced()` method 
inherented from the `PrimaryThreadEnforceable` interface, which is implemented by all conditions by default:

```java title="MySyncCondition.java"
public class MySyncCondition implements PlayerCondition {

  public MySyncCondition() {
  }
  
  @Override
  public boolean check(final Profile profile) {
    // ...
  }
  
  @Override
  public boolean isPrimaryThreadEnforced() {
    return true; //(1)!
  }
  
}
```

1. This method returns `false` by default, so all conditions are tested asynchronously if not defined otherwise.
