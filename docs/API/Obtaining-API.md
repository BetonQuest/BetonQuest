---
icon: material/widgets
---
@snippet:api-state:draft@

To obtain the API or a part of the API there are currently two ways, the new way and the legacy way.

## Obtaining the API (new way)
!!! note "New way"
    The new way is the recommended way to get the redesigned parts of the API.  
    It is not yet available for all parts of the API, but will be in the future.

The new API is designed to be modular and extensible.
To obtain a module of the API you use the `org.bukkit.plugin.ServiceManager`.
The `ServiceManager` is Bukkit API that allows plugins to provide services to other plugins:

``` Java title="Get a module"
BetonQuestLoggerFactory loggerFactory = getServer().getServicesManager().load(BetonQuestLoggerFactory.class);
```
This can only be called after the `onEnable` method of BetonQuest has been called.
This is usually the case when your plugin's `onEnable` method is called by Bukkit,
assuming you defined a dependency on BetonQuest.

BetonQuest always registers its default implementation with the `Lowest` ServicePriority.
If you want to override a module you can register your own implementation with a higher priority.
You also need to register your implementation before the `onEnable` method of BetonQuset is called,
so usually in the `onLoad` method of your plugin:
``` Java title="Register a module"
getServer().getServicesManager().register(BetonQuestLoggerFactory.class, new MyLoggerFactory(), this, ServicePriority.Normal);
```

## Legacy way
!!! note "Legacy way"
    The legacy way is the way that was used before the API was redesigned.
    It is usually the only way to get those parts of the API that have not been redesigned yet.  
    It still will be available for the foreseeable future,
    but you should not use it when writing new code working with API that has already been redesigned.

The old API uses the `BetonQuest` class as the entry point.
Most methods are static and can be accessed directly.
For those methods that need to be called on a `BetonQuest` instance
you can obtain it by calling the static `BetonQuest.getInstance()` method. 

All the old API is documented on the [Legacy API](Legacy-API.md) page.
