--8<-- "API-State/Stable.md"

Here you find everything you need to know, how you can use our own logger.  
As this is a part of the API, you can also use this logger in your own plugin.

## Why we have our own Logger
The first and main advantage is, it is __simpler to use__ if you want to log something.
Especially for contributors, that don't know how we like to log things.
This also helps to log things in a consistent way.

### Advantages
These advantages are mainly for BetonQuest, but it is also very useful for other plugins, that integrates with BetonQuest. 

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

## Set up the Logging
If you want to use <a href="https://projectlombok.org/" target="_blank">Lombok</a>'s annotation
<a href="https://projectlombok.org/features/log" target="_blank">@CustomLog</a> you need to do the following steps.
First you need to install Lombok in your IDE, if it is not already installed natively.
Now you need to create a new file named 'lombok.config' in your projects root. Copy or adapt the following to the file:
````linenums="1"
lombok.log.custom.declaration = org.betonquest.betonquest.api.BetonQuestLogger org.betonquest.betonquest.api.BetonQuestLogger.create(TYPE)(TYPE,TOPIC)
lombok.log.fieldName = LOG
````

## Get a BetonQuestLogger
There are two ways to get a logger.

!!! abstract ""
    The first one only works, if you did [Set up the Logging](#set-up-the-logging).
    If you did that you can simply add the `@CustomLog` annotation at the class definition.
    ````java linenums="1"
    @CustomLog
    public final class MyCustomEvent {
    ````
    ````java linenums="1"
    @CustomLog(topic = "MyCustomTopic")
    public final class MyCustomEvent {
    ````

!!! abstract ""
    The second method can be used without Lombok.
    You simply create a BetonQuestLogger instance.
    ````java linenums="1"
    public final class MyCustomEvent {
        private final static BetonQuestLogger LOG = BetonQuestLogger.create(MyCustomEvent.class);
    ````
    ````java linenums="1"
    public final class MyCustomEvent {
        private final static BetonQuestLogger LOG = BetonQuestLogger.create(MyCustomEvent.class, "MyCustomTopic");
    ````

!!! warning "Get the logger in the JavaPlugin class"
    For you main class, you need to create your logger in the constructor of your class.
    ````java linenums="1"
    public final class BetonQuestAddon extends JavaPlugin {
        private static BetonQuestLogger log;
        public BetonQuestAddon() {
            log = new BetonQuestLoggerImpl(this, this.getLogger(), this.getClass(), null);
    ````
    ````java linenums="1"
    public final class BetonQuestAddon extends JavaPlugin {
        private static BetonQuestLogger log;
        public BetonQuestAddon() {
            log = new BetonQuestLoggerImpl(this, this.getLogger(), this.getClass(), "MyCustomTopic");
    ````

## Use a BetonQuestLogger
Once you [Get a BetonQuestLogger](#get-a-betonquestlogger) you can use the variable `LOG`,
that is an instance of the class BetonQuestLogger.
You have a bunche of methods there, to log what ever you want or need.

There are mainly the method types `debug`, `info`, `warning`, `error` and `reportException`.
All these methods are available with and without a package.
If you can provide a package, you should, otherwise filtering for packages is not available.
All methods have explicit javadocs, that explain how you use them and what they exactly do.
The important behaviours are, that `warning` with an exception log the exception in the debug log,
and `reportException` should only be called, if you are in an edge case, that can normally never occur.
