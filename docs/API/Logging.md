---
icon: material/text-box-outline
---
@snippet:api-state:stable@

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

You should always use dependency injection to provide a `BetonQuestLogger` instance to your class.
This is a simple example:


```java linenums="1"
public class MyAddon extends JavaPlugin {

    private BetonQuestLoggerFactory loggerFactory;

    @Override
    public void onEnable() { //(2)!
        loggerFactory = Bukkit.getServicesManager().load(BetonQuestLoggerFactory.class); //(1)!
        new MyFeature(loggerFactory.create(MyFeature.class));
    }
}
```

1. Make sure [BetonQuest is loaded](./Overview.md#ensuring-that-betonquest-is-loaded) before using this code!
2. The earliest point to obtain a `BetonQuestLoggerFactory` is in the `onEnable()` method of your plugin.

```java linenums="1"
public class MyFeature {

    private final BetonQuestLogger log;

    public MyFeature(final BetonQuestLogger log) {
        this.log = log;
    }
}
```



!!! warning "Getting the logger in a class that extends `Plugin`"
    The methods described above don't work for your plugin's main class (or any other class that extends `Plugin`). 
    Create the logger instance in the `onEnable()` method instead like this:

    ```java linenums="1"
    public class BetonQuestAddon extends JavaPlugin {

        private BetonQuestLoggerFactory loggerFactory; 
        private BetonQuestLogger log;

        @Override
        public void onEnable() {
            loggerFactory = Bukkit.getServicesManager().load(BetonQuestLoggerFactory.class);
            log = BetonQuestLoggerFactory.create(this);
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
final BetonQuestLogger logger = loggerFactory.create(MyClass.class, "MyCustomTopic");
```

## Using the BetonQuestLogger
A BetonQuestLogger will be available as the variable `log` once you [obtained a BetonQuestLogger instance](#obtaining-a-betonquestlogger-instance). 
It has a bunch of methods for all use cases. Its JavaDocs explain when and how to use these.
Make sure to give the JavaDocs a quick read!

The usage then look like this:
````java linenums="1"
log.info("Hello Log!");
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
