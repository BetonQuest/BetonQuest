---
icon: material/widgets
---
@snippet:api-state:draft@

To obtain the API or a part of the API there are currently two ways, the new way and the legacy way.

## Obtaining the API (new way)
!!! note "New way"
    The new way is the recommended way to get the redesigned parts of the API.  
    It is not yet available for all parts of the API, but will be in the future.

The new API is designed to be modular and replaceable.
To obtain a module of the API you use the `org.bukkit.plugin.ServiceManager`.
The `ServiceManager` is a Bukkit API that allows plugins to provide services to other plugins.
BetonQuest always registers a default implementation in the `Lowest` ServicePriority.

``` Java title="Get a module"
BetonQuestLoggerFactory loggerFactory = getServer().getServicesManager().load(BetonQuestLoggerFactory.class);
```
This can only be called after the `onEnable` method of your plugin has been called.

If you want to override a module you can register your own implementation with a higher priority than `Lowest`.
You also need to register your implementation before the `onEnable` method of your plugin is called.
``` Java title="Register a module"
getServer().getServicesManager().register(BetonQuestLoggerFactory.class, new MyLoggerFactory(), this, ServicePriority.Normal);
```

## Legacy way
!!! note "Legacy way"
    The legacy way is the way that was used before the API was redesigned.
    It is the only way to get some parts of the API that have not yet been redesigned.  
    It is still available and will be for the foreseeable future.
    It is not recommended to use this method for redesigned parts of the API.

The old API use the `BetonQuest` class as an entry point to the API.
The most methods are static and can be accessed directly. Other methods are accessed through the `BetonQuest` instance.
It is possible to obtain the `BetonQuest` instance by using the `BetonQuest.getInstance()` method.

All the old API is documented on the [Legacy API](Legacy-API.md) page.
