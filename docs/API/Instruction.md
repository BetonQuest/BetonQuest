---
icon: material/text-account
---
@snippet:api-state:unfinished@

# Instruction Overview

The **Instruction** refers to the user-defined string that specifies conditions, events, items, and similar elements.

!!! info Package
    The `org.betonquest.betonquest.api.instruction` package contains the `Instruction` interface and related objects.

## Reading the `Instruction` Object

The `Instruction` object is responsible for parsing the instruction string provided by the user and splitting it into 
arguments. You can retrieve required arguments or optional key-value arguments one at a time through a parser chain. 
Required arguments are those specified at the beginning of an instruction string, 
such as `add someTag` in the `tag` event.

If the instruction string contains an argument formatted as `arg:something` and you request the optional `arg`, 
it will return `something`. If there is no optional argument by that name, 
it will return an empty Optional for that argument. 

If an error occurs the `Instruction` object will automatically throw a `QuestException`.
This may happen whenever there are no more arguments in the user's instruction or if the argument cannot 
be parsed into the requested type.

## Instruction Chain

The instruction chain offers java stream-like methods to read, parse, convert and 
ultimately retrieve a argument from an instruction. 
It minimally consists of two steps: the argument parsing step and the argument retrieval step.

To offer an overview before explaing all details, the following examples show excerpts from factories in BetonQuest:

Compare [experience](../Documentation/Scripting/Building-Blocks/Conditions-List.md#experience-experience) condition.
```JAVA title="ExperienceConditionFactory.java"
public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> amount = instruction.number().get();
        
        //creating the condition object and returning it...
}
```

Compare [itemdurability](../Documentation/Scripting/Building-Blocks/Events-List.md#item-durability-itemdurability) event.
```JAVA title="ItemDurabilityEventFactory.java"
public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
    final Argument<EquipmentSlot> slot = instruction.enumeration(EquipmentSlot.class).get();
    final Argument<PointType> operation = instruction.enumeration(PointType.class).get();
    final Argument<Number> amount = instruction.number().get();
    final boolean ignoreUnbreakable = instruction.hasArgument("ignoreUnbreakable");
    final boolean ignoreEvents = instruction.hasArgument("ignoreEvents");
    
    //creating event object and returning it...
}
```

Compare [delay](../Documentation/Scripting/Building-Blocks/Objectives-List.md#wait-delay) objective.
```java title="DelayObjectiveFactory.java"
public Objective parseInstruction(final Instruction instruction) throws QuestException {
    final Argument<Number> delay = instruction.number().atLeast(0).get();
    final Argument<Number> interval = instruction.number().atLeast(1).get("interval", 20 * 10);
    //creating objective object and returning it...
}
```

### Argument Parsing
In the argument parsing step the method of converting a string into a java type is decided.
The instruction chain may be accessed conveniently, starting directly from any `Instruction` instance.

| Call                                 | Description                                                                                                                                                                                            |
|:-------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
 | `.number()`                          | Default parser for `java.lang.Number` covering both integer and floating point values                                                                                                                  |
 | `.string()`                          | Default parser for `java.lang.String`                                                                                                                                                                  |
 | `.bool()`                            | Default parser for `java.lang.Boolean`                                                                                                                                                                 |
 | `.location()`                        | Default parser for `org.bukkit.Location`                                                                                                                                                               |
 | `.world()`                           | Default parser for `org.bukkit.World`                                                                                                                                                                  |
 | `.item()`                            | Default parser for `org.betonquest.betonquest.api.instruction.type.ItemWrapper` representing items defined in BetonQuest                                                                               |
 | `.blockSelector()`                   | Default parser for `org.betonquest.betonquest.api.instruction.type.BlockSelector` representing a matcher for a group of bukkit materials                                                               |
 | `.vector()`                          | Default parser for `org.bukkit.util.Vector`                                                                                                                                                            |
 | `.uuid()`                            | Default parser for `java.util.UUID`                                                                                                                                                                    |
 | `.component()`                       | Default parser for `net.kyori.adventure.text.Component`                                                                                                                                                |
 | `.packageIdentifier()`               | Default parser for package identifiers producing a `java.lang.String`. This parser simply expands the existing string value to a full package identifier using the instruction's package if necessary. |
 | `.namespacedKey()`                   | Default parser for `org.bukkit.NamespacedKey`                                                                                                                                                          |
 | <nobr>`.enumeration(Enum<E>)`</nobr> | Default parser for an enum of the given type                                                                                                                                                           |
 | `.parse(Parser<P>)`                  | Using a custom parser matching the functional interfaces `InstructionArgumentParser` or `SimpleArgumentParser`                                                                                         |  

### Argument Retrieval
The argument retrieval step is required after the argument parsing and represents the wrapping into a `Argument<T>` 
instance to be carried on into events, conditions and objectives.
To have valid calls the `Number` parser is used as an example, but naturally any parser will do.

| Call                                                                  |                Type                | Description                                                                                                                                           |
|:----------------------------------------------------------------------|:----------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------|
| `.get()`                                                              |         `Argument<Number>`         | Retrieves an argument of the next value in order from the instruction                                                                                 |
| `.get("amount")`                                                      |    `Optional<Argument<Number>>`    | Retrieves an optional argument of the value with the key `amount` from the instruction                                                                |
| `.get("amount", 10)`                                                  |         `Argument<Number>`         | Retrieves an optional argument of the value with the key `amount` from the instruction or gets an argument with default value                         |
| `.getList()`                                                          |      `Argument<List<Number>>`      | Retrieves an argument of the next value in order from the instruction parsed as list                                                                  |
| `.getList("amounts")`                                                 | `Optional<Argument<List<Number>>>` | Retrieves an optional argument of the value with the key `amounts` from the instruction parsed as list                                                |
| <nobr>`.getList("amounts",`</nobr><br><nobr>`List.of(1,5,10))`</nobr> |      `Argument<List<Number>>`      | Retrieves an optional argument of the value with the key `amounts` from the instruction parsed as list or gets an argument with default list as value |
| `.getFlag("repeat", 10)`                                              |       `FlagArgument<Number>`       | Retrieves an optional flag argument of the value with the key `repeat` from the instruction using the default value for its undefined state           |

### Advanced Argument Parsing
Parsers via the chain offer more functionality than just parsing a string into a specific type.
By chaining different kinds of operations, the outcome can be modified in certain ways.

#### Validations
You can validate an argument using the `validate(ValueValidator<T>)` or `validate(ValueValidator<T>, String)` method.
This method will throw a `QuestException` if the predicate does not match the argument (aka returns `false`).
It may be used to check if a value is within a certain range or if a value satisfies a certain condition 
outside matching just the type.
In the error message a single `%s` maybe used to inline the wrong value.  
The `invalidate` counterparts handle the validation in the exact opposite way.
Examples:
??? example "Even Number"
    The example below checks if the parsed number is even and throws an error if it is not.
    ```JAVA title="even number validate example"
    instruction.number().validate(i -> i.intValue() % 2)
    instruction.number().validate(i -> i.intValue() % 2, "Number must be even, but was %s.")
    ```
??? example "Non-Empty-String"
    The example below checks if the parsed string is not empty and throws an error if it is empty.
    ```JAVA title="non-empty string validate example"
    instruction.string().validate(s -> !s.isEmpty())
    instruction.string().validate(s -> !s.isEmpty(), "Empty strings are not permitted.")
    instruction.string().invalidate(String::isEmpty)
    ```

#### Prefilter
You can use prefilters to modify the argument's parsing result without actually parsing it.
This can be useful to parse additional cases that are not covered by the default parsers.
Use the `prefilter(String expected, T fixedValue)` method to decorate the parser with a prefilter.
The matcher to find the expected value uses `String#equalsIgnoreCase(String)`.  
Examples:
??? example "`infinity` Number Value"
    The example below parses the string "infinity" into the number `Double.POSITIVE_INFINITY` while still allowing other 
    number values.
    ```JAVA title="infinity prefilter example"
    instruction.number().prefilter("infinity", Double.POSITIVE_INFINITY)
    ```
??? example "`any` Value"
    The example below parses the string "any" into the enum EntityType without EntityType allowing `any` as value.
    ```JAVA title="any prefilter example"
        instruction.enumeration(EntityType.class).prefilterOptional("any", null)
    ```
    !!! warning
        The usage of `prefilter(String expected, @Nullable T fixedValue)` is required here, 
        since the chain has a strict non-null policy to prevent exceptions. 
        This method will capsule the result in an Optional. To retrieve the `any` value you are safe to assume
        that an Optional#isEmpty() corresponds to `any` while a present value represents a parsed known enum constant.
        Another unknown value will cause it to throw an exception. 

#### Map
You can also modify the value or map it to a different type after parsing without reading it from the argument.
Use the `map(QuestionFunction<T,U>)` method to decorate the parser with such a mapping function.  
Example:
??? example "Explicit Integer Value"
    The examples below show two ways of parsing an explicit integer value. Both come with advantages and disadvantages.
    While the first one parses all kinds of numbers and fits them into an integer, 
    the second one fails when trying to parse anything other than a valid integer value. 
    ```JAVA title="infinity prefilter example"
    instruction.number().map(Number::intValue)
    instruction.parse(Integer::parseInt)
    ```

#### Collectors
You can interpret any argument's value as a list of comma-separated values and collect them into any type of `java.util.Collection`.  
Use `collect(Collector<T,?,R>)` to handle the collection process identically as java streams.  
The method `list()` offers a common overload to support `java.util.List`.

#### Special Case: List
The `instruction.list()` parser offers a more convenient way to create a list argument with additional parsing options.  
The option `notEmpty()` will throw an error if the list is empty.  
The option `distinct()` will check for duplicate values in the list and throw an error if any are found.
Another implementation `distinct(Function<T,U>)` requires a `java.util.Function` to map the list elements to a unique key to
ensure the uniqueness of the list elements.

#### Special Case: Number
The `instruction.number()` parser offers a more convenient way to create a number argument with range limits.

Additional methods are available to set the minimum and maximum values of the number.
While the methods `atLeast(int)` and `atMost(int)` are using inclusive bounds, `inRange(int,int)` is using an exclusive 
upper bound as commonly used in other Java classes.
```JAVA title="DecoratedNumberArgument example"
instruction.number().atLeast(0); // value has to be 0 or greater
instruction.number().atMost(10); // value has to be 10 or less
instruction.number().atLeast(1).atMost(10); // value has to be in the interval [1,10]
instruction.number().inRange(0, 100); // value has to be in the interval [0,99]
```  
