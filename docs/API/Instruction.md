---
icon: material/text-account
---
@snippet:api-state:draft@

# Instruction Overview

The **Instruction** refers to the user-defined string that specifies conditions, events, items, and similar elements.

!!! info Package
    The `org.betonquest.betonquest.instruction` package contains the `Instruction` class and related objects.

## Reading the `Instruction` Object

The `Instruction` object is responsible for parsing the instruction string provided by the user and splitting it into 
arguments. You can retrieve required arguments one at a time using the `next()` method or through a parser method
like `get(Argument<T>)`. Required arguments are those specified at the beginning of an instruction string, 
such as `add someTag` in the `tag` event.

If an error occurs—such as when there are no more arguments in the user's instruction or if the argument cannot 
be parsed into the requested type—the `Instruction` object will automatically throw a `QuestException`.

You can also request optional arguments. If the instruction string contains an argument formatted as `arg:something`, 
and you request the optional `arg`, it will return `something`. If there is no optional argument, it will return `null`. 
You can safely pass this `null` to parser methods like `get(String, Argument<T>)`, which will not throw an error 
but will simply return `null`.

## Parser

Instead of having separate methods like `getLocation()` or `getLocation(String)` that return a `VariableLocation` 
object, as well as methods for fetching various `ID`s, you can provide these directly within the `get` method.

All methods are overloaded to accept either a provided string as the first argument to parse, or, to fetch the next
argument using `next()`, directly an argument.

### Primitive & Enum

To retrieve a primitive number, you can use the `getInt()` and `getDouble()` methods.
Parsing Enums is straightforward; simply pass the desired class into the `getEnum` method. 
Ensure that the enum values adhere to the default Java naming conventions,
meaning all letters used for the enum values must be uppercase.

```JAVA title="Own parsing vs. getEnum(Enum)"
try {
  EntityType entity = EntityType.valueOf(instruction.next().toUpperCase(Locale.ROOT));
} catch (IllegalArgumentException e) {
  throw new QuestException("Unknown mob type: " + mob);
}
  
EntityType entity = instruction.getEnum(EntityType.class);
```

### Argument

The `Argument<T>` interface provides a simple way to construct an object from a single string.
Using `get(Argument<T>)` is equivalent to `Argument<T>.apply(next())`, but it allows for in-method construction 
in `getList(Argument<T>)`, which splits the string by `,` and converts each part to the requested type `T`.

```JAVA title="Getting a List of Primitives"
List<String> strings = instruction.getList(string -> string);
List<Integer> ints = instruction.getList(string -> instruction.getInt(string, 0));
```

```JAVA title="Own parsing vs. getList(Argument)"
List<EntityType> entities = new ArrayList<>();
for (String mob : instruction.next().split(",")) {
  entities.add(instruction.getEnum(mob, EntityType.class));
}

List<EntityType> entities = instruction.getList(mob -> instruction.getEnum(mob, EntityType.class));
```

!!! warning Overloaded Methods in Lambdas
    Be cautious to use the method that accepts a string as the first argument within the lambda; otherwise, 
    the method will call `next()` for each split segment, (potentially) resulting in incorrect conversions.

### VariableArgument

The `VariableArgument` interface provides an easy way to parse variables (from the `instruction.variable` package) 
by utilizing the constructor as a method reference (as shown in line 2 of the example below).

You can create a location parser manually, but it's unnecessary since you can simply use the 
`get(VariableLocation::new)` or `get(String, VariableLocation::new)` methods to obtain a `VariableLocation` object. 
The former method is effectively `get(next(), VariableLocation::new)`.

```JAVA title="Own parsing vs. get(VariableArgument)"
VariableLocation location = new VariableLocation(variableProcessor, instruction.getPackage(), instruction.next());
VariableLocation location = instruction.get(VariableLocation::new);
```

Additionally, there are common non-standard constructor implementations in the `VariableArgument` that can also be 
passed as arguments. The following example demonstrates how a number can be validated to ensure it is positive or zero.

```JAVA title="Example for number validation"
VariableNumber number = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ZERO);
VariableNumber number = instruction.get((variableProcessor, pack, input) ->
  new VariableNumber(variableProcessor, pack, input, value -> {
    if (value.doubleValue() < 0) {
      throw new QuestException("Value must be greater than or equal to 0: " + value);
    }
  }));
```

Utilizing these various options simplifies the process of parsing, for instance, a list of `VariableLocation` objects:

```JAVA title="Equivalant calls"
List<VariableLocation> locs = instruction.getList(instruction.next(), string -> instruction.get(string, VariableLocation::new)); 
List<VariableLocation> locs = instruction.getList(string -> instruction.get(string, VariableLocation::new)); 
List<VariableLocation> locs = instruction.getList(instruction.next(), VariableLocation::new); 
List<VariableLocation> locs = instruction.getList(VariableLocation::new); 
```

Common implementations are also available within the `VariableArgument` interface:

```JAVA title="Number above 0"
VariableNumber number = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
```

## Custom Parsing

If your instruction is more complex and the `Instruction` class does not provide the necessary methods, you can still 
parse the instruction string manually. The already split parts are accessible through the `getParts()` method, 
and the raw instruction can be retrieved using the `toString()` method. 

Remember to throw a `QuestException` with an informative message if the instruction provided by the user is incorrect; 
BetonQuest will handle these exceptions appropriately and display them in the console.
