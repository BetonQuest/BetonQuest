---
icon: material/text-box-outline
---
@snippet:api-state:draft@

!!! abstract "[ServicesManager](Obtaining-API.md) API Classes"
    * `org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory`
---

This page shows you everything you need to know about the BetonQuest logger, no matter if you are working on BetonQuest 
itself or an integration / addon.

## Why a custom Logger?
The main advantage is that it is **easier to use**.
It provides an easy interface that enables custom logging features and respects our logging conventions. 
This helps to provide a great user experience and keeps the log consistent.

### Advantages
These features were mainly made for BetonQuest, but are also very useful for 3rd party integrations. 


??? info "In-Game Logging"
    Users can see all log messages send, using the BetonQuestLogger in-game.
    Additionally, these messages can be filtered by quest package and log level.

??? info "Debug Logging"
    BetonQuest has its own `log` folder in which a `latest.log` file is written if debug logging is enabled.
    It contains our own log messages and messages from 3rd party integrations.
    Additional debug messages are logged next to everything that is displayed on the console already.
    You can send debug log messages directly to that log when you use the BetonQuestLogger in your addon.
    This will make it a lot easier to see how your plugin integrates with BetonQuest's mechanics if a bug occurs.

??? info "Log History"
    It happens very often that a user experiences a bug while debug logging is not enabled.
    We keep the last `x` configured minutes of the debug log history saved in memory.
    Therefore, the history will be written to `latest.log` once you enable "Debug Logging" via command. 

??? info "Logger Topics"
    The BetonQuestLogger supports topics, which give your log messages a prefix like `(Database)`.
    You can use a topic for each class or for each BetonQuestLogger instance.
    Topics are supposed to give important log messages extra attention by making them stand out.
    The naming convention is to use _PascalCase_ for topics.

## Obtaining a BetonQuestLogger
Use the `BetonQuestLoggerFactory.create()` method to obtain a new BetonQuestLogger instance:
```java linenums="1"
BetonQuestLoggerFactory loggerFactory; //(1)!
BetonQuestLogger logger = loggerFactory.create(MyFeature.class);
```

1. Obtained via the ServicesManager, see the [Obtaining API](Obtaining-API.md) page.


!!! hint "Getting the logger in a class that extends `Plugin`"
    A class extends `Plugin` can not get an injected `BetonQuestLogger` instance,
    and can also not use the above `create` method.
    Therefore, you need to create the logger instance in the `onEnable()` method instead like this:

    ```java linenums="1"
    public class BetonQuestAddon extends JavaPlugin {

        private BetonQuestLoggerFactory betonQuestLoggerFactory; 
        private BetonQuestLogger log;

        @Override
        public void onEnable() {
            betonQuestLoggerFactory = Bukkit.getServicesManager().load(BetonQuestLoggerFactory.class);
            log = betonQuestLoggerFactory.create(this);
        }
    }
    ```

??? info "`BetonQuestLoggerFactory` additional background implementation information"
    As the BetonQuestLoggerFactory is a service, it is not guaranteed that the instance you get
    is the one BetonQuest created by default. But here we explain the behavior of the default BetonQuestLoggerFactory.
    
    First there is the `DefaultBetonQuestLoggerFactory` class,
    which is the default implementation of the `BetonQuestLoggerFactory` interface.
    It simply creates a child logger for the given class using the Logger of your plugin.
    This is done by checking which plugin loaded the class.
    
    This default implementation is wrapped into the `CachingBetonQuestLoggerFactory`.
    This class can be used to cache any implementation of the `BetonQuestLoggerFactory` interface.
    It returns always the same instance for the same class.
    There is one special behavior if the BetonQuestLogger is created with a topic. 
    In that case the `CachingBetonQuestLoggerFactory` will create a new instance for each different topic,
    but it will still cache the instances for the same topic or without a topic.
    
## Logging with Topics
This is useful if you want to give your log messages a prefix like `(Database)`.
Mainly _PascalCase_ should be used for topics and they should be short and meaningful to the user. 

```java linenums="1"
BetonQuestLoggerFactory loggerFactory;
BetonQuestLogger logger = loggerFactory.create(MyFeature.class, "MyCustomTopic");
```

## Using the BetonQuestLogger
The BetonQuestLogger has a bunch of methods for all use cases. Its JavaDocs explain when and how to use these.
Make sure to give the JavaDocs a quick read!

The usage might look like this:
````java linenums="1"
logger.info("Hello Log!");
````

### Method Overview
All methods come in multiple variants. Always provide a package if possible, as this allows the user to filter log
messages.
 
| Name                              | Use Case                                                                                                                                                   | Example                                                                                             |
|-----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| :shushing_face: Debug             | Used to display internal states or events that may be beneficial for bug-fixing. These messages are only be visible in the debug log.                      | An event has been fired.                                                                            |
| :information_source: Info         | Use this for normal log information in the server's console.                                                                                               | A new integration was successfully hooked.                                                          |
| :warning: Warning                 | You can provide useful information how to fix the underlying problem.                                                                                      | The user wrote an event with syntax errors.                                                         |
| :x: Error                         | The underlying problem affects the servers security or functionality. Usage is also allowed if you don't know how the user can fix the underlying problem. | An error occurred while loading an integration.                                                     |
| :rotating_light: Report Exception | Only use this in cases that should never occur and indicate an error that must be reported to the projects issue tracker.                                  | You need to catch an exception that you know should never occur unless something is horribly wrong. | 
