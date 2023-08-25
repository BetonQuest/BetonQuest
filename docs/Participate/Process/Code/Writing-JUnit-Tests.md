---
icon: material/test-tube
---
Here you can find a summary on how to write JUnit tests for BetonQuest. In order to understand this, you need to have
basic knowledge of JUnit tests and mocking of objects and classes.

## Introduction

It is a major goal to write JUnit tests for most parts of BetonQuest.

**There are things, where you definitely want as many tests as possible:**

- API
- Utilities
- Internal application logic that is used by a bunch of other code
- Critical parts that can cause a lot of harm when bugged

**But there are also some parts, where we do not want tests at all:**

- If a core API concept has many implementations, the implementations itself should not be tested
- Some parts of the code require a lot of Bukkit API mocking. If this takes too much time no tests are
  necessary

### Naming Convention
We use `snake_case` for test method names.
The name should be descriptive and readable as the IDE will use it to display the test results
and the underscores will be replaced by spaces.

## Handling Logging

_See [Logging](../../../API/Logging.md) for general information about BetonQuest's logging._

=== "Testing"    
    You can mock the `BetonQuestLogger` directly.
    
    In case you need a `BetonQuestLoggerFactory` you must inject a `BetonQuestLogger` into the `SingletonLoggerFactory`.
    Then you can use this factory in your tests. It will always return the logger instance you injected. 

=== "Legacy Testing"
    If you write a test for a legacy class that requires a `BetonQuestLogger` or a `BetonQuestLoggerFactory` you can use the
    `BetonQuestLoggerService` like so:
    
    ````java linenums="1" hl_lines="1"
      @ExtendWith(BetonQuestLoggerService.class)
      public class TestClass {
    ````
    Now you can easily obtain a `BetonQuestLoggerFactory`, a `BetonQuestLogger` and a static mock of the `BetonQuest` instance
    by adding them as parameters into the test method's signature. They will be automatically injected.

### LogValidator

You can now add this optional argument to any test's method signature:

```java linenums="1" hl_lines="5"
@ExtendWith(BetonQuestLoggerService.class)
public class TestClass {

    @Test
    public void testCustom(LogValidator validator) {
```

The `LogValidator` is created and passed to your method by the `BetonQuestLoggerService`.
It makes it possible to assert that a log message has been logged in the silent parent logger.
The simplest method is `assertLogEntry(Level level, String message)`, that you can use to check
that the given message with the given level has been logged. You can also check that there are no additional log 
messages in the `LogValidator` by calling `assertEmpty()`.

### Advanced Features

??? note "Obtaining the parent `Logger` and a `BetonQuestLogger`"
    You can also use these two additional arguments:
    
    ```java linenums="1" hl_lines="5"
    @ExtendWith(BetonQuestLoggerService.class)

    public class TestClass {
        @Test
        public void testCustom(LogValidator validator, Logger logger, BetonQuestLogger log) {
    ```
    
    The `logger` is the silent parent `Logger`.

    The `log` is a new instance of the `BetonQuestLogger` that you can use to log things during the test.
    This logger has a topic that can be accessed via `BetonQuestLoggerService.LOGGER_TOPIC`.

## Handling BukkitScheduler

If you want to test code that only works with the `BukkitScheduler`, we even have a ready to use solution for this.
To use the `BukkitSchedulerMock` you need to create the following setup:

````java linenums="1" hl_lines="3-5"
@Test
void testMethod {
    try (BukkitSchedulerMock scheduler = new BukkitSchedulerMock()) {
        ...
    }
}
````

Now you can use the scheduler object for several things. First if you want to perform a single or multiple ticks,
you can call the methods `performTick()` or `performTicks(long)`:

````java linenums="1"
scheduler.performTick();
scheduler.performTicks(20);
````

We also have a method that allows to get the number of ticks since the `BukkitSchedulerMock` was created.

````java linenums="1"
scheduler.getCurrentTick();
````

There are some additional features of this scheduler:

````java linenums="1"
scheduler.close(); //(1)!
scheduler.waitAsyncTasksFinished(long); //(2)!
scheduler.waitAsyncTasksFinished(); //(3)!
````

1. Shuts down the scheduler. Already called thanks to with "try with resources".
2. Wait for all async tasks to finish.
3. One second timeout.

## Expanded visibility for testing
Sometimes you need a method, class or field to be accessible for your JUnit tests but not for external code.  
Generally a good way to achieve this is using the default (package-local) access modifier instead of `private`.  
Of course the unit tests must be located in the same package for this to work.

To clearly mark such elements, that are more widely visible than necessary only for use in test code, 
the `@VisibleForTesting` annotation can be added.
Make sure you import it from `org.jetbrains.annotations`, not from Google Commons or Apache.

This will also suppress the PMD rule [`CommentDefaultAccessModifier`](https://pmd.github.io/latest/pmd_rules_java_codestyle.html#commentdefaultaccessmodifier) 
which requires you to add a `/* default */` or `/* package */` comment when using default access modifier.
