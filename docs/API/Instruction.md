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
arguments. You can retrieve required arguments one at a time through a parser method like `get(Argument<T>)` for 
required arguments or `getValue(String, Argument<T>)` for optional or key-value arguments. 
Required arguments are those specified at the beginning of an instruction string, 
such as `add someTag` in the `tag` event.

If the instruction string contains an argument formatted as `arg:something` and you request the optional `arg`, 
it will return `something`. If there is no optional argument, it will return `null`. 
You can safely pass this `null` to parser methods like `get(String, Argument<T>)`, which will not throw an error 
but will simply return `null`.

If an error occurs the `Instruction` object will automatically throw a `QuestException`.
This may happen whenever there are no more arguments in the user's instruction or if the argument cannot 
be parsed into the requested type.

## Argument Parsing

The `Argument` interface defines a method for parsing an argument into a specific type.
Implementations for custom types can be created by implementing the `Argument` interface, 
which also satisfies the single-method-requirement for a functional interface and can be used directly as a lambda.
```JAVA title="simple example"
Argument<String> stringArgument = s -> s;
Argument<Integer> integerArgument = Integer::parseInt;
```
Since there are frequently used types like `String`, `Number`, `Boolean` and so on, 
there are predefined implementations for these types in the `ArgumentParsers` class.
Those may be accessed through the `Instruction` instance that is present most of the time 
when parsing using an Argument.
```JAVA title="accessing predefined parsers"
Argument<String> stringArgument = instruction.getParsers().string();
Argument<Number> numberArgument = instruction.getParsers().number();
```

To offer a better overview, the following examples show excerpts from factories in BetonQuest:

Compare [experience](../Documentation/Scripting/Building-Blocks/Conditions-List.md#experience-experience) condition.
```JAVA title="ExperienceConditionFactory.java"
public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> amount = instruction.number().get();
        
        //creating the condition object and returning it...
}
```

Compare [itemdurability](../Documentation/Scripting/Building-Blocks/Events-List.md#item-durability-itemdurability) event.
```JAVA title="ItemDurabilityEventFactory.java"
public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
    final Variable<EquipmentSlot> slot = instruction.get(instruction.getParsers().forEnum(EquipmentSlot.class));
    final Variable<PointType> operation = instruction.get(instruction.getParsers().forEnum(PointType.class));
    final Variable<Number> amount = instruction.number().get();
    final boolean ignoreUnbreakable = instruction.hasArgument("ignoreUnbreakable");
    final boolean ignoreEvents = instruction.hasArgument("ignoreEvents");
    
    //creating event object and returning it...
}
```

### Advanced Argument Parsing
Default implementations from ArgumentParsers offer more functionality than just parsing a string into a specific type.

#### Validations
You can validate an argument using the `validate(ValueValidator<T>)` or `validate(ValueValidator<T>, String)` method.
This method will throw a `QuestException` if the predicate does not match the argument.
It may be used to check if a value is within a certain range or if a value satisfies a certain condition 
outside matching just the type.

The example below checks if the parsed number is even and throws an error if it is not.
```JAVA title="validate example"
DecoratedArgument<Number> evenArgument = instruction.getParsers().number().validate(i -> i.intValue() % 2);
DecoratedArgument<Number> evenArgument2 = instruction.getParsers().number().validate(i -> i.intValue() % 2, 
"Number must be even, but was %s.");
```

#### Prefilter
You can use prefilters to modify the argument's parsing result without actually parsing it.
This can be useful to parse additional cases that are not covered by the default parsers.
Use the `prefilter(String expected, T fixedValue)` method to decorate the argument with a prefilter.

The example below parses the string "infinity" into the number `Double.POSITIVE_INFINITY` while still allowing other 
number values.
```JAVA title="prefilter example"
DecoratedArgument<Number> numberArgument = instruction.getParsers().number().prefilter("infinity", Double.POSITIVE_INFINITY);
```

#### DecoratedNumberArgument
The `DecoratedNumberArgument` class offers a more convenient way to create a number argument with range limits.
It is retrieved through the `ArgumentParsers` class as default number argument parser wrapping `DecoratedArgument<Number>`.

Additional methods are available to set the minimum and maximum values of the number.
While the methods `atLeast(int)` and `atMost(int)` are using inclusive bounds, `bounds(int,int)` is using an exclusive 
upper bound as commonly used in other Java classes.
```JAVA title="DecoratedNumberArgument example"
DecoratedNumberArgument numberArgument = ArgumentParsers.number().atLeast(0);
DecoratedNumberArgument numberArgument = ArgumentParsers.number().atLeast(1).atMost(10);
DecoratedNumberArgument numberArgument2 = ArgumentParsers.number().bounds(0, 100);
```  
