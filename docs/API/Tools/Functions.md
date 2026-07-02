---
icon: material/function-variant
status: new
---
@snippet:api-state:draft@

??? abstract "Function API Classes"
    * `org.betonquest.betonquest.api.service.function.Functions`
    * `org.betonquest.betonquest.api.function.FunctionProvider`
    * `org.betonquest.betonquest.api.identifier.FunctionIdentifier`
    * `org.betonquest.betonquest.api.function.MathFunction`
    * `org.betonquest.betonquest.api.function.FunctionDefinition`
    * `org.betonquest.betonquest.api.function.FunctionExpression`
    * `org.betonquest.betonquest.api.function.FunctionAssignment`
    * `org.betonquest.betonquest.lib.function.FunctionParser`
    * `org.betonquest.betonquest.lib.function.token.FunctionTokenizer`

# Functions Overview

This page covers the functions API and the underlying concepts.

## Introduction

Functions solve a problem that is not common in BetonQuest's scripting language, but not having them is a huge pain in specific situations.
They combine logic with math and are a powerful tool for creating complex behaviors 
that otherwise would be very verbose with just actions and conditions or completely impossible in the first place.

## Accessing Functions

!!! danger "API currently not backed"
    Functions are currently not available in the BetonQuest scripting language and therefore cannot be accessed.
    The discription below is still valid as the API endpoints are technically implemented. They just won't give any results yet.
    
Functions are accessed through the `Functions` interface. It allows you to find and evaluate functions.
```java
public void myFunction(final Functions functions, final FunctionIdentifier identifier) throws QuestException {
    final MathFunction mathFunction = functions.getFunction(identifier); //(1)!
    final FunctionAssignment result = mathFunction.evaluate(functions, List.of(new NumberSourceAssignment(10)); //(2)!
    final Number value = result.asNumber(); //(3)!
}
```

1. `getFunction` returns a `MathFunction` object that can be used to evaluate the function. This may throw a `QuestException` if the function is not found.
2. `evaluate` takes a list of `FunctionAssignment` objects that are used to evaluate the function. Functions can be evaluated with any number of arguments, but
the required number of arguments is defined by the function definition. Missing arguments might result in an error, too many arguments might be just ignored and have no effect.
3. Functions produce their result as `FunctionAssignment`s that can be passed to other functions or resolved to java values. 
`asNumber` (as an example) returns the result of the function as a `Number` object.

You can also skip the `getFunction` step and use the `evaluate` method directly.

```java
public void myFunction(final Functions functions, final FunctionIdentifier identifier) throws QuestException {
    final FunctionAssignment result = functions
        .evaluate(identifier, List.of(new NumberSourceAssignment(10))); //(1)!
    final Number value = result.asNumber();
}
```

1. `evaluate` is essentially a shortcut for `getFunction(identifier).evaluate(functions, arguments)` if you don't need to access the `MathFunction` object itself.

### Registering Subroutines

Subroutines are functions that can be called within other functions. They are registered with the `Functions` interface before functions can use them.

!!! warning "Availablity"
    Subroutines are only available once registered. If you define functions in the script that use subroutines, 
    they will cause an error when evaluated without the subroutine being registered beforehand.
    Since subroutines are not checked at parse-time, they may be registered after the script has been loaded.

```java
public void myFunction(final Functions functions, final MathFunction subroutine) {
    functions.registerSubroutine("foo", subroutine);
}
```

## Parsing Functions

Functions are parsed from `String` by the `FunctionTokenizer` and `FunctionParser` classes.
They are currently not exposed to the API and only available in the library.

```java
public MathFunction parseFunction(final String functionAsString) throws QuestException {
    final FunctionTokenizer tokenizer = new FunctionTokenizer(); //(1)!
    final FunctionParser parser = new FunctionParser(); //(2)!
    final List<FunctionToken> tokens = tokenizer.tokenize(functionAsString); //(3)!
    return parser.parse(tokens); //(4)!
}
```

1. `FunctionTokenizer` is used to tokenize the function string into a list of `FunctionToken`s.
2. `FunctionParser` is used to parse the list of `FunctionToken`s into a `MathFunction`.
3. Invalid strings will cause a `QuestException` to be thrown.
4. Invalid tokens will cause a `QuestException` to be thrown.
