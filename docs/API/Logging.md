--8<-- "API-State/Unfinished.md"

Here you find everything that you need to know about the BetonQuest logger, no matter if you are working on BetonQuest 
itself or an integration / addon.

## Why a custom Logger?
The main advantage is that it is **easier to use**.
Especially for contributors that are not familiar with our logging conventions. This helps to keep the log consistent.

### Advantages
These advantages are mainly for BetonQuest, but it is also very useful for other plugins, that integrate with BetonQuest. 

??? info "Debug Logging"
    First BetonQuest has an own `log` folder and if logging is enabled, a `latest.log` file will be written.
    This log fle only contains BetonQuest related log messages and messages from other plugins, that use our logger.
    The main advantage of our log is, that it also contains debug messages.
    And if you use our logger in your plugin, you can set the things you log in relation to BetonQuest mechanics.
    This also allows us to set things you do in reaction to BetonQuest.
    All this mainly helps to trace more complicated bugs on both sides.

??? info "Log History"
    We keep an eye on the "Debug Logging".
    It happens very often, that a user gets an error, and then he needs the debug log, but logging was not enabled.
    To prevent to reproduce an unknown situation it is possible to log the last 10 minutes afterwards.
    So if you enable the "Debug Logging", the history will be written automatically to the `latest.log`.

??? info "In-game logging"
    One more thing that helps not only devs but also users, is the in-game logging.
    You can see all log messages in-game, and you can filter for quest packages and for the log level.
    With that you and users can easily track what's happening in-game. 

??? info "Logger Topics"
    You can use a feature called topics. With topics, you can give your log messages a prefix like `(Database)`.
    You can use a topic for each class or of each BetonQuestLogger instance.
    Topics are mainly made to give important parts of your plugin log messages extra attention by marking them with a topic.

## Get a BetonQuestLogger
There are two ways to get a logger:

=== "Using Lombock"
    Using Lombock enables you to use the handy
    <a href="https://projectlombok.org/features/log" target="_blank">@CustomLog</a>
    annotation on each class you want a logger for.

    !!! abstract "1. Setup"
        The first step is to install a Lombok plugin in your IDE. IntelliJ contains it by default.

        All 3rd party plugins need to create a new file named 'lombok.config' in their projects root.
        Copy the following to the file:
        ````linenums="1"
        lombok.log.custom.declaration = org.betonquest.betonquest.api.BetonQuestLogger org.betonquest.betonquest.api.BetonQuestLogger.create(TYPE)(TYPE,TOPIC)
        lombok.log.fieldName = LOG
        ````

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
---

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

## Use a BetonQuestLogger
Once you [Get a BetonQuestLogger](#get-a-betonquestlogger) you can use the variable `LOG`,
that is an instance of the class BetonQuestLogger.
You have a bunch of methods there, to log what ever you want or need.

There are mainly the method types `debug`, `info`, `warning`, `error` and `reportException`.
All these methods are available with and without a package.
If you can provide a package, you should, otherwise filtering for packages is not available.
All methods have explicit javadocs, that explain how you use them and what they exactly do.
The important behaviours are, that `warning` with an exception log the exception in the debug log,
and `reportException` should only be called, if you are in an edge case, that can normally never occur.
