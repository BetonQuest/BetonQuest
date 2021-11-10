--8<-- "API-State/Draft.md"

This page shows you everything you need to know about the BetonQuest logger, no matter if you are working on BetonQuest 
itself or an integration / addon.

## Why a custom Logger?
The main advantage is that it is **easier to use**.
It provides an easy interface that enables custom logging features and respects our logging conventions. 
This helps to provide a great user experience and keeps the log consistent.

### Advantages
These advantages are mainly for BetonQuest, but it is also very useful for 3rd party integrations. 


??? info "In-game logging"
    Users can see all log messages send using the BetonQuestLogger in-game.
    Additionally, these messages can be filtered by quest package and log level.

??? info "Debug Logging"
    BetonQuest has its own `log` folder in which a `latest.log` file is written if debug logging is enabled.
    It contains our own log messages and messages from 3rd party integrations.
    Additional debug messages are logged next to everything that is displayed on the console already.
    You can send debug log messages directly to that log when you use the BetonQuestLogger in your addon.
    This will make it a lot easier to see how your plugin integrates with BetonQuest's mechanics if a bug occurs.

??? info "Log History"
    It happens very often that a user experiences a bug while debug logging is not enabled.
    We keep the last 10 minutes of the debug log history saved in memory.
    Therefore, the history will be written to `latest.log` once you enable "Debug Logging" via command. 

??? info "Logger Topics"
    The BetonQuestLogger supports topics, which give your log messages a prefix like `(Database)`.
    You can use a topic for each class or for each BetonQuestLogger instance.
    Topics are supposed to give important log messages extra attention by making them stand out.

## Obtaining a BetonQuestLogger Instance

!!! note ""
    === "Using Lombock"
        Using Lombock enables you to use the handy
        <a href="https://projectlombok.org/features/log" target="_blank">@CustomLog</a>
        annotation on each class you want a logger for.
        This requires a Lombock setup in your project and in your IDE.
    
        !!! abstract "1. Setup"
            The first step is to install a Lombok plugin in your IDE. IntelliJ contains it by default.
    
            All 3rd party plugins need to create a new file named `lombok.config` in their projects root.
            Copy the following to the file:
            ````linenums="1"
            lombok.log.custom.declaration = org.betonquest.betonquest.api.BetonQuestLogger org.betonquest.betonquest.api.BetonQuestLogger.create(TYPE)(TYPE,TOPIC)
            lombok.log.fieldName = LOG
            ````
            Additionally, Lombock also needs to be setup for the project. The exact configuration depends on your project
            setup.
    
        !!! abstract "2. Usage"
            Simply add the `@CustomLog` annotation to any class definition.
    
            === "Without topic"
                ````java linenums="1"
                @CustomLog
                public final class MyCustomEvent {
                ````
            === "With topic"
                ````java linenums="1"
                @CustomLog(topic = "MyCustomTopic")
                public final class MyCustomEvent {
                ````
    
    === "Using plain Java"    
        !!! abstract ""
            This method works without Lombok.
            Simply create a BetonQuestLogger instance.
    
            === "Without topic"
                ````java linenums="1"
                public final class MyCustomEvent {
                    private final static BetonQuestLogger LOG = BetonQuestLogger.create(MyCustomEvent.class);
                ````
            
            === "With topic"
                ````java linenums="1"
                public final class MyCustomEvent {
                    private final static BetonQuestLogger LOG = BetonQuestLogger.create(MyCustomEvent.class, "MyCustomTopic");
                ````


!!! warning "Get the logger in your JavaPlugin class"
    The methods described above do not work for your plugin's main class. 
    Create the logger instance in the `onLoad()` method instead.

    === "Without topic"
        ````java linenums="1"
        public final class BetonQuestAddon extends JavaPlugin {
    
            private static BetonQuestLogger log;
    
            @Override
            public void onLoad() {
                log = new BetonQuestLoggerImpl(this, this.getLogger(), this.getClass(), null);
            }
        ````

    === "With topic"
        ````java linenums="1"
        public final class BetonQuestAddon extends JavaPlugin {
    
            private static BetonQuestLogger log;
    
            @Override
            public void onLoad() {
                log = new BetonQuestLoggerImpl(this, this.getLogger(), this.getClass(), "MyCustomTopic");
            }
        ````

## Using the BetonQuestLogger
A BetonQuestLogger will be available as the variable `LOG` once you [obtained a BetonQuestLogger instance](#obtaining-a-betonquestlogger-instance). 
It has a bunch of methods for all use cases. Its JavaDocs explain when and how to use these.
Make sure to give the JavaDocs a quick read! 

### Method Overview

All methods come in multiple variants. Always provide a package if possible, as this makes it possible to filter the log
message.
 

| Name                              	| Use Case 	                                                                                                                                                 | Example 	                                                 |
|------------------------------------ |----------------------------------------------------------------------------------------------------------------------------------------------------------- |---------------------------------------------------------- |
| :shushing_face: Debug               | Used to display internal states or events that may be beneficial for bug-fixing. These messages are only be visible in the debug log.                      | An event has been fired.         	                       |
| :information_source: Info           | Use this for normal log information in the server's console.                                                                                               | A new integration was successfully hooked.     	         |
| :warning: Warning                   | You can provide useful information how to fix the underlying problem.                                                                                      | The user wrote an event with syntax errors.               |
| :x: Error            	              | The underlying problem affects the servers security or functionality. Usage is also allowed if you don't know how the user can fix the underlying problem. | An error occurred while loading an integration.           |
| :rotating_light: Report Exception 	| Only use this in cases that should never occur and indicate an error that must be reported to the projects issue tracker.                                  | You need to catch an exception that you know should never occur unless something is horribly wrong. | 
