---
icon: material/text-account
---
@snippet:api-state:draft@

The Instruction is the user provided string defining conditions, events, items and the like.

!!! info Package
    The `org.betonquest.betonquest.instruction` package contains the Instruction and related objects.

## Reading `Instruction` object
The `Instruction` object parses the instruction string defined by the user and splits it into arguments.
You can ask it for required arguments one by one with `next()` method or a parser method like `get(Argument<T>)`.
Required arguments are the ones specified at the very beginning of an instruction string, for example `add someTag` in `tag` event.
It will automatically throw `QuestException` for you if it encounters an error,
for example when there were no more arguments in user's instruction, or it can't parse the argument to the type you asked for.

You can also ask for optional arguments: if the instruction string contains argument `arg:something`
and you ask for optional `arg`, it will give you `something`. If there is no optional argument, it will return `null`.
Don't worry about passing that `null` to parser methods like `get(String, Argument<T>)`,
they won't throw an error, they'll simply return that `null`.

## Parser

Instead of having methods like `getLocation()` or `getLocation(String)` which return a `VariableLocation` object,
or `getBlockSelector()` and `getEventID()` and the like for all `ID`s, you provide them in the `get` method yourself.

All methods are overloaded to accept either a provided string as first argument to parse or fetch that using `next()`.

### Primitive & Enum

To get a primitive number you can use the `getInt()` and `getDouble()` methods.
Parsing Enums is also easy, just pass the class you want into the `getEnum` method.
Please make sure that the enum must follow the default Java coding style when using this method:
All letters used for the enum values must be uppercase.

```JAVA title="Own parsing vs. getEnum(Enum)"
try {
  EntityType entity = EntityType.valueOf(instruction.next().toUpperCase(Locale.ROOT));
} catch(IllegalArgumentException e) {
  throw new QuestException("Unknown mob type: " + mob);
}
  
EntityType entity = instruction.getEnum(EntityType.class);
```

### Argument

The `Argument<T>` is a simple interface which provides constructing an object from a single string.
The usage in `get(Argument<T>)` is equivalent to `Argument<T>.apply(next())` but allows the in method construction
in `getList(Argument<T>)` which splits the string by `,` and converts every split part to the requested `T`.

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
!!! warning Overloaded methods in Lambdas
    You need to pay attention to use the method which accepts the string as first argument inside the Lambda,
    otherwise the method is calling with `next()` for each split resulting in converting the wrong parts!

### VariableArgument

The `VariableArgument` interface provides a very easy way to parse Variables (from the `instruction.variable` package),
where you simply use the Constructor as Method Reference (as line 2 in the example below).

You could write a location parser for yourself, but there's no need for that,
you can just use `get(VariableLocation::new)` or `get(String, VariableLocation::new)` method and receive `VariableLocation` object.
The former method is simply `get(next(), VariableLocation::new)`.

```JAVA title="Own parsing vs. get(VariableArgument)"
VariableLocation locaction = new VariableLocation(variableProcessor, instruction.getPackage(), instruction.next());
VariableLocation locaction = instruction.get(VariableLocation::new);
```

Additionally, there are common non-standard-constructor implementation in the `VariableArgument` which can be 
simply passed as argument too.
The following example shows how a number, when resolving it, is guaranteed to be positive or 0.

```JAVA title="Example for number validation"
VariableNumber number = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ZERO);
VariableNumber number = instruction.get((variableProcessor, pack, input) ->
  new VariableNumber(variableProcessor, pack, input, (value) -> {
   if (value.doubleValue() < 0) {
       throw new QuestException("Value must be greater than or equal to 0: " + value);
   }}));
```

Utilizing the different possibilities lead to a quite easy way to parse for example a List of VariableLocations:

```JAVA title="Equivalant calls"
List<VariableLocation> locs = instruction.getList(instruction.next(), string -> instruction.get(string, VariableLocation::new)); 
List<VariableLocation> locs = instruction.getList(string -> instruction.get(string, VariableLocation::new)); 
List<VariableLocation> locs = instruction.getList(instruction.next(), VariableLocation::new); 
List<VariableLocation> locs = instruction.getList(VariableLocation::new); 
```

There are also common implementations inside the VariableArgument interface:
```JAVA title="Number above 0"
VariableNumber number = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
```

## Own parsing

If your instruction is more complicated and the `Instruction` class doesn't provide necessary methods,
you can still parse the instruction string manually.
Already split parts are available by `getParts()` and the raw instruction by `toString()` method.
Just remember to throw `QuestException` with an informative message when the instruction supplied by the user is 
incorrect,  BetonQuest will catch them properly and display it in the console.
