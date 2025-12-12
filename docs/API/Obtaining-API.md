---
icon: material/widgets
---
@snippet:api-state:unfinished@

To obtain the API or a part of the API there are currently two ways, the new way and the legacy way.

## Obtaining the API
!!! note "New method"
    The new way is the recommended way to get the redesigned parts of the API.  
    It is not yet available for all parts of the API, but will be in the future.

The new API is designed to be modular and extensible.
To obtain a module of the API you use the `org.bukkit.plugin.ServicesManager`.
The `ServicesManager` is Bukkit API that allows plugins to provide services to other plugins:

``` Java title="Get a module"
BetonQuestLoggerFactory loggerFactory = getServer().getServicesManager().load(BetonQuestLoggerFactory.class);
```
This can only be called after the `onEnable` method of BetonQuest has been called.
This is usually the case when your plugin's `onEnable` method is called by Bukkit,
assuming you defined a dependency on BetonQuest.

BetonQuest always registers its default implementation with the `Lowest` ServicePriority.
If you want to override a module you can register your own implementation with a higher priority.
You also need to register your implementation before the `onEnable` method of BetonQuest is called,
so usually in the `onLoad` method of your plugin:
``` Java title="Register a module"
getServer().getServicesManager().register(BetonQuestLoggerFactory.class, new MyLoggerFactory(), this, ServicePriority.Normal);
```

### Legacy API
!!! note "Old method"
    The legacy API is how you could interact with BetonQuest in the past before the API was redesigned.
    For most systems that we haven't been able to improve, it is still the only option.  
    While it will still be available for the foreseeable future,
    you should not use it when writing new code working with API that has already been redesigned.

The old API uses the `BetonQuest` class as the entry point.
Most methods are static and can be accessed directly.
For those methods that need to be called on a `BetonQuest` instance
you can obtain it by calling the static `BetonQuest.getInstance()` method. 

All the old API is documented on the [Legacy API](Legacy-API.md) page.

## The ServicesManager Hint
The following hint can be found on many API pages:

!!! abstract "[ServicesManager](Obtaining-API.md) API Classes"
    * `org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory`

It lists all interfaces that are related to the API described on that page.
Every one of them can be obtained by using the `ServicesManager` as described below.

## Working with the API
We recommend that you inject instances you obtained from the `ServicesManager` into your classes when they need them.
You might want to learn about "Dependency Injection" as a programming technique,
but as a quick start here's a simple example:

This plugin injects an instance of `BetonQuestLogger` that was created by a `BetonQuestLoggerFactory` into a class implementing some feature.

```java linenums="1"
public class MyAddon extends JavaPlugin {
    private BetonQuestLoggerFactory loggerFactory;

    @Override
    public void onEnable() {
        loggerFactory = Bukkit.getServicesManager().load(BetonQuestLoggerFactory.class);
        new MyFeature(loggerFactory.create(MyFeature.class));
    }
}

public class MyFeature {
    private final BetonQuestLogger log;

    public MyFeature(final BetonQuestLogger log) {
        this.log = log;
    }
}
```
