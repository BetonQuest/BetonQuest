---
icon: material/variable-box
---
@snippet:api-state:draft@

??? abstract "Placeholder API Classes"
    * `org.betonquest.betonquest.api.service.placeholder.Placeholders`
    * `org.betonquest.betonquest.api.service.placeholder.PlaceholderManager`
    * `org.betonquest.betonquest.api.service.placeholder.PlaceholderRegistry`
    * `org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory`
    * `org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory`
    * `org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder`
    * `org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder`
    * `org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholder`
    * `org.betonquest.betonquest.api.quest.placeholder.OnlinePlaceholder`

## Introduction

This page covers the Placeholder API of BetonQuest.

!!! warning "You should have viewed these pages"
    - [How to access the api](../Obtaining-API.md)
    - [How to use the factory system](./Overview.md)
    - [How to work with Instructions](../Tools/Instruction.md)

<div class="grid" markdown>

!!! abstract "What this page covers"
    - [How to create a placeholder](#how-to-create-a-placeholder)
    - [How to create a factory for a placeholder](#how-to-create-a-factory-for-a-placeholder)
    - [How to register a placeholder and its factory with BetonQuest](#how-to-register-a-placeholder-with-betonquest)
    - [How to resolve a placeholder](#how-to-resolve-a-placeholder)
    - [How a full example looks like](#how-a-full-example-looks-like)
    - [What limitations are there](#what-limitations-are-there)

!!! info "What this page does not cover"
    - What a placeholder is
    - [Which placeholders are available](../../Documentation/Scripting/Building-Blocks/Placeholders-List.md)
    - How to work with placeholders in scripting

</div>

## How to create a placeholder

Creating a placeholder will be explained in the following sections using the `WeatherPlaceholder` placeholder as an example.

### Approach #1: A simple implementation

The first approach is by far the simplest one. We expect the placeholder to be a simple string representation of the 
weather in the world `world`. Since there is no `weather` value directly present in the API of paper as of now, we will 
need to create a custom implementation for the value. To reduce the amount of boilerplate code in the following examples, 
we will extract that value from the world in a separate method.  
An example implementation of that method could look like this:

```java title="getWeather"
private String getWeather(final World world) {
  if(world.isThundering()) {
    return "thunder";
  }
  if(world.hasStorm()) {
    return "rain";
  }
  return "clear";
}
```

Now, to create a placeholder, we need to implement the `PlayerlessPlaceholder` interface.

```java title="Approach #1: WeatherPlaceholder.java"
public class WeatherPlaceholder implements PlayerlessPlaceholder {
  
  public WeatherPlaceholder() {
  }
  
  @Override
  public String getValue() {
    final World world = Bukkit.getWorld("world"); //(1)!
    return getWeather(world); //(2)!
  }
  
}
```

1. To get the world named `world`.
2. Call the `getWeather` method to get the weather value and return it.

### Approach #2: Using a profile

To make the placeholder more useful, we might want to use the world of the player for which the placeholder is being 
resolved. For that, we will have to implement the `OnlinePlaceholder` interface instead of `PlayerlessPlaceholder`.

```java title="Approach #2: WeatherPlaceholder.java"
public class WeatherPlaceholder implements OnlinePlaceholder {
  
  public WeatherPlaceholder() {
  }
  
  @Override
  public String getValue(final OnlineProfile onlineProfile) {
    final World world = onlineProfile.getPlayer().getWorld(); //(1)!
    return getWeather(world); //(2)!
  }
  
}
```

1. To get the world of the player the placeholder is being resolved for.
2. Call the `getWeather` method to get the weather value and return it.

### Approach #3: Adding a parameter

To increase the versatility of the placeholder, we might want to add a parameter to it to manually define the world 
instead of relying on the world of the player. This also allows us to use the placeholder in an independent context.
For that, we will have to implement the `NullablePlaceholder` interface instead of `OnlinePlaceholder`.

```java title="Approach #3: WeatherPlaceholder.java"
public class WeatherPlaceholder implements NullablePlaceholder {
  
  @Nullable
  private final Argument<World> world;
  
  public WeatherPlaceholder(@Nullable final Argument<World> world) {
    this.world = world;
  }
  
  @Override
  public String getValue(final Profile profile) throws QuestException {
    return getWeather(world.getValue(profile)); //(1)!
  }
  
}
```

1. Call the `getWeather` method with the resolved world value to get the weather value and return it.

### Summary

To create a placeholder, we need to implement one of the following interfaces:

- `OnlinePlaceholder` to require the player the placeholder is being resolved for to be online.
- `PlayerPlaceholder` to require a player the placeholder is being resolved for.
- `PlayerlessPlaceholder` to not require a player and resolve the placeholder independently.
- `NullablePlaceholder` to allow the placeholder to be resolved with or without a player.

Using `Argument`s allows us to parameterize the placeholder and even use further placeholders as parameters.

## How to create a factory for a placeholder

After creating a placeholder, we have to create a factory for it.
Factories are used to create instances of a placeholder for a specific instruction.

In the following we are going to create factories for each of the `WeatherPlaceholder` placeholder approaches.

### Approach #1: A simple factory
See [Approach #1](#approach-1-a-simple-implementation).

The easiest way to create a factory is to extend the `PlayerlessPlaceholderFactory` or `PlayerPlaceholderFactory` class.
Those simple factories can be implemented inline during the registration of the placeholder. However, it is recommended 
to create a separate class for each factory to keep readability and maintainability and avoid sneaky bugs.

In our case, we will create a `WeatherPlaceholderFactory` class implementing the `PlayerlessPlaceholderFactory` 
class, since our placeholder is a `PlayerlessPlaceholder`.

```java title="Approach #1: WeatherPlaceholderFactory.java"
public class WeatherPlaceholderFactory implements PlayerlessPlaceholderFactory {
  
  @Override
  public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
    return new WeatherPlaceholder();
  }
  
}
```

??? example "YAML usage examples"
    ```yaml
    actions:
      logWeather: "log current weather in world is %weather%"
    ```

### Approach #2: Using a different factory
See [Approach #2](#approach-2-using-a-profile).

The factory for our second approach is almost the same as the first one.
Since we are using the `OnlinePlaceholder` interface for our placeholder, we will have to implement the 
`PlayerPlaceholderFactory` class instead and wrap our instance in an `OnlinePlaceholderAdapter`.

It might be hard to see the difference in the usage examples. In [approach #1](#approach-1-a-simple-factory) the 
placeholder is independent of the player and might therefore be used in an independent context. In our second approach, 
on the other hand, it is required to use the player, and therefore the action will fail if it is used in an independent 
context.

```java title="Approach #2: WeatherPlaceholderFactory.java"
public class WeatherPlaceholderFactory implements PlayerPlaceholderFactory {
  
  @Override
  public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
    return new OnlinePlaceholderAdapter(new WeatherPlaceholder());
  }
  
}
```

??? example "YAML usage examples"
    ```yaml
    actions:
      logWeather: "log current weather in world is %weather%"
    ```

### Approach #3: Adding versatility
See [Approach #3](#approach-3-adding-a-parameter).

The placeholder we created in the third approach now requires a parameter to be specified.
We also used the `NullablePlaceholder` interface to allow the placeholder to be resolved with or without a player.
Therefore, we will have to implement both the `PlayerlessPlaceholderFactory` and `PlayerPlaceholderFactory` 
interfaces and wrap our instance in a `NullablePlaceholderAdapter` after parsing the world argument.

```java title="Approach #3: WeatherPlaceholderFactory.java"
public class WeatherPlaceholderFactory implements PlayerlessPlaceholderFactory, PlayerPlaceholderFactory {
  
  @Override
  public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
    return parse(instruction);
  }
  
  @Override
  public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
    return parse(instruction);
  }
  
  private NullablePlaceholderAdapter parse(final Instruction instruction) throws QuestException {
    final Argument<World> world = instruction.world().get("world").orElse(DefaultArguments.PLAYER_WORLD); //(1)!
    return new NullablePlaceholderAdapter(new WeatherPlaceholder(world));
  }
}
```

1. The `world` argument of the `weather` placeholder is optional and defaults to the player's world.

??? example "YAML usage examples"
    ```yaml
    actions:
      logWeather: "log current weather in world is %weather%" #(1)!
      logWeatherInWorld: "log current weather in world 'somewhere' is %weather.world:somewhere%" #(2)!
    ```
    
    1. The `world` argument of the `weather` placeholder is optional and defaults to the player's world. This will 
    require the player to be online to resolve the placeholder.
    2. The `world` argument of the `weather` placeholder is given and this may be used in an independent context.

### Summary

To create a factory for a placeholder, we need to implement one of the following interfaces:

- `PlayerPlaceholderFactory` to create a placeholder that requires a player.
- `PlayerlessPlaceholderFactory` to create a placeholder that does not require a player.

For `NullablePlaceholder`s, we need to implement both the `PlayerlessPlaceholderFactory` and `PlayerPlaceholderFactory` 
interfaces and wrap our instance in a `NullablePlaceholderAdapter`.

Since `OnlinePlaceholder`s require a player to be online, we need to implement the `PlayerPlaceholderFactory` interface
and wrap our instance in an `OnlinePlaceholderAdapter`.

## How to register a placeholder with BetonQuest

After creating a placeholder and its factory, we have to register them with BetonQuest.  
The name of the placeholder used in the script is now defined in the registration process.  
Given the api is already obtained, we can access the placeholder registry and register the factory as follows:

```java title="Register WeatherPlaceholderFactory.java"
public class MyPlugin extends JavaPlugin {
  
  @Override
  public void onEnable() {
    // ...
    betonQuestApi.placeholders().registry().register("weather", new WeatherPlaceholderFactory()); //(1)!
    // ...
  }
  
}
```

1. Only viable for approaches #1 and #2.

Since a `NullablePlaceholder` requires both a `PlayerlessPlaceholderFactory` and a `PlayerPlaceholderFactory`, we 
need to register them combined:

```java title="Register WeatherPlaceholderFactory.java"
public class MyPlugin extends JavaPlugin {
  
  @Override
  public void onEnable() {
    // ...
    betonQuestApi.placeholders().registry().registerCombined("weather", new WeatherPlaceholderFactory()); //(1)!
    // ...
  }
  
}
```

1. Only viable for approach #3

## How to resolve a placeholder

In comparison to actions and conditions, there are multiple ways to resolve placeholders.
We can't resolve existing ones defined in the script, but we can resolve strings representing placeholders.

The most reliable way to resolve a placeholder is to create an `Argument` for it using the `PlaceholderManager` class.
But it is also possible to use the `getValue` method of the `PlaceholderManager` class to directly resolve the 
placeholder if there is no need to create an `Argument` instance for dynamic resolution.

The following examples contain an `instruction` representing the entire placeholder string including the `%` symbols:
 
- `%weather%`
- `%weather.world:somewhere%` 

```java title="Resolving the weather placeholder with 'create'"
public class MyFeature {
  private final BetonQuestApi betonQuestApi;
  
  public String resolve(@Nullable final QuestPack pack, final String instruction, @Nullable final Profile profile) {
    final Argument<String> resolved = betonQuestApi.placeholders().manager().create(pack, instruction); //(1)!
    return resolved.getValue(profile); //(2)!
  }
  
}
```

1. The `instruction` is parsed to create an `Argument` instance.
2. The value of the `Argument` is resolved using the `profile`.

```java title="Resolving the weather placeholder with 'getValue'"
public class MyFeature {
  private final BetonQuestApi betonQuestApi;
  
  public String resolve(final QuestPack pack, final String instruction, @Nullable final Profile profile) {
    return betonQuestApi.placeholders().manager().getValue(pack, instruction, profile); //(1)!
  }
  
}
```

1. The `instruction` is parsed using the `profile` to resolve the placeholder.

## How a full example looks like

??? example "WeatherPlaceholder.java"
    ```java title="WeatherPlaceholder.java"
    public class WeatherPlaceholder implements NullablePlaceholder {
      
      @Nullable
      private final Argument<World> world;
      
      public WeatherPlaceholder(@Nullable final Argument<World> world) {
        this.world = world;
      }
      
      @Override
      public String getValue(final Profile profile) throws QuestException {
        return getWeather(world.getValue(profile)); //(1)!
      }
      
      private String getWeather(final World world) {
        if(world.isThundering()) {
          return "thunder";
        }
        if(world.hasStorm()) {
          return "rain";
        }
        return "clear";
      }
      
    }
    ```
    
    1. Call the `getWeather` method with the resolved world value to get the weather value and return it.

??? example "WeatherPlaceholderFactory.java"    
    ```java title="WeatherPlaceholderFactory.java"
    public class WeatherPlaceholderFactory implements PlayerlessPlaceholderFactory, PlayerPlaceholderFactory {
      
      @Override
      public PlayerlessPlaceholder parsePlayerless(Instruction instruction) throws QuestException {
        return parse(instruction);
      }
      
      @Override
      public PlayerPlaceholder parsePlayer(Instruction instruction) throws QuestException {
        return parse(instruction);
      }
      
      private NullablePlaceholderAdapter parse(final Instruction instruction) throws QuestException {
        final Argument<World> world = instruction.world().get("world").orElse(DefaultArguments.PLAYER_WORLD); //(1)!
        return new NullablePlaceholderAdapter(new WeatherPlaceholder(world));
      }
    }
    ```
    
    1. The `world` argument of the `weather` placeholder is optional and defaults to the player's world.

??? example "YAML usage examples"
    ```yaml
    actions:
      logWeather: "log current weather in world is %weather%" #(1)!
      logWeatherInWorld: "log current weather in world 'somewhere' is %weather.world:somewhere%" #(2)!
    ```
    
    1. The `world` argument of the `weather` placeholder is optional and defaults to the player's world. This will 
    require the player to be online to resolve the placeholder.
    2. The `world` argument of the `weather` placeholder is given and this may be used in an independent context.

## What limitations are there

### Force synchronous placeholder resolution

Generally, BetonQuest tries to resolve placeholders asynchronously if their parent actions, conditions, etc. are in an 
asynchronous context. In some contexts, especially if a placeholder interacts with the bukkit api, it may be 
necessary to resolve it synchronously by itself.

To mark a placeholder as required to resolve in sync with the main thread, overwrite the `isPrimaryThreadEnforced()` 
method inherented from the `PrimaryThreadEnforceable` interface, which is implemented by all placeholders by default:

```java title="MySyncPlaceholder.java"
public class MySyncPlaceholder implements PlayerPlaceholder {

  public MySyncPlaceholder() {
  }
  
  @Override
  public String getValue(final Profile profile) {
    // ...
  }
  
  @Override
  public boolean isPrimaryThreadEnforced() {
    return true; //(1)!
  }
  
}
```

1. This method returns `false` by default, so all placeholders are tested asynchronously if not defined otherwise.
