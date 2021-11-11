Here you can find a summary of useful information, how you write JUnit tests in BetonQuest.
In generale you should already know how to write JUnit tests and, you may need basic knowledge about mocking.

## Generale
It is a mayor goal to write JUnit tests for the most parts of BetonQuest.

**There are things, where you definitely want as many tests as possible1:**

- Everything in API should be Tested as good as possible
- Core concepts of the plugin should also be Tested
- Components where a bunche of other code is build on top of it
- Parts of the plugin, that could lead into major issues when the behavior is not correct
- Utility classes

**But there are also some parts, where we do not want Tests at all:**

- If a core concept has manny implementations, the implementations itself should be not tested
- If code is too close to Minecraft and you need to mock things, if this take too much time no tests are necessary

## Need to know
By default, all classes and methods are executed `CONCURRENT`.
This means that it will run the tests parallel, to save time.
There are tests that can not be executed parallel,
for that case you need to add the following annotation to the related methods or the complete class
````java linenums="1"
@Execution(ExecutionMode.SAME_THREAD)
````

### Handle Logging
To understand this, you may need to read [Logging](../../../API/Logging.md) before.

Everytime the `@CustomLog` annotation is used in a class, you may see something like this:
```
Cannot invoke "org.bukkit.Server.getPluginManager()" because "org.bukkit.Bukkit.server" is null
```
If this is the case, you need the `BetonQuestLoggerValidationProvider`.
Simply add the following annotation to the class:
````java linenums="1"
@ExtendWith(BetonQuestLoggerValidationProvider.class)
public class TestFeature {
````
After you did that, the test should work as intended.

**But what happen, if you add this annotation?**  
Now everytime the `@CustomLog` annotation is used, a new anonymous logger is created.
All these loggers have a silent parent logger, so now you don't see any log messages at all.
This parent logger now enables new possibilities.

#### LogValidator
You can now write the following optional argument on any test method:  
````java linenums="1"
@ExtendWith(BetonQuestLoggerValidationProvider.class)
public class TestFeature {
    @Test
    public void testCustom(LogValidator validator) {
````
This `LogValidator` is created by the `BetonQuestLoggerValidationProvider` and contains the possibility to assert,
that a log message was printed.
As mentioned before, there is a parent silent Logger, but all the messages are passed to this `LogValidator`.
The simplest method is `assertLogEntry(Level level, String message)`,
that you can use to check that a specified message with a related level was logged.
You can also check that there is no left log message in the `LogValidator` by calling `assertEmpty()`.

!!! warning "Use `ExecutionMode.SAME_THREAD`"
    When you use the `LogValidator`, you try to validate that log messages are logged in the correct order.
    This means that if you leave it by default (`CONCURRENT`) the test will fail,
    because the log messages do not have a predictable order when executed parallel.
    ````java linenums="1"
    @Execution(ExecutionMode.SAME_THREAD)
    ````

#### Logger & BetonQuestLogger
You can also use two more arguments now:
````java linenums="1"
@ExtendWith(BetonQuestLoggerValidationProvider.class)
public class TestFeature {
    @Test
    public void testCustom(LogValidator validator, Logger logger, BetonQuestLogger log) {
````
While `Logger` is the silent logger, so you can do things with the parent logger if needed,
`BetonQuestLogger` gives you a new instance of an BetonQuestLogger, that you can use to log things in a test.
