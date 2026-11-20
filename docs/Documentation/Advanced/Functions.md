---
icon: material/function-variant
---

# Functions

Functions allow you to define reusable calculations in a quest package. 
They take arguments, assign them to named parameters, and return the result of an expression.
Functions are useful when the calculation or decision logic is complicated or needed multiple times in a script.

## Where functions are defined

Functions are defined in the `functions` section of a quest package script.

Each entry in this section has:

- a **function identifier** as the YAML key
- a **function definition and expression** as the value

```yaml 
functions: 
  add_numbers: 'f(a,b) = a + b' 
  double: 'f(value) = value * 2' 
  is_positive: 'b(value) = value > 0'
```

The identifier on the left side, for example `add_numbers`, is the name used to reference the function from other places in the package.
The function name inside the definition, usually `f`, is part of the function syntax but can be chosen freely as it carries no semantic meaning for now.

## Basic syntax

A function consists of two parts: A definition and an expression separated by the first `=`.
The basic definition starts with a function name, followed by a non-empty list of parameter names in parentheses.
The expression is any valid expression that can use the parameters.
The syntax is similar to how a function is defined in a programming language or mathematical notation:

```text 
<name>(<parameter1>, <parameter2>, ..., <parameterN>) = <expression>
```

Example:

```yaml 
functions: 
  add_numbers: 'f(a,b) = a + b'
```

This function:

- defines two parameters: `a` and `b`
- evaluates the expression `a + b`
- returns the result

If the function is called with the arguments `3` and `5`, the result is `8`.

## Definition 

The first part of a function is the definition.
It consists of a function name and a list of parameters in parentheses.

??? abstract "Syntax in backus-naur form"
    ```bnf
    <value> ::= <number> | <string> | <boolean>
    <parameter> ::= <qualifier> | <qualifier> ':' <value>
    <parameter-list> ::= <parameter> | <parameter> ',' <parameter-list>
    <definition> ::= <qualifier>(<parameter-list>)
    ```

```text 
<name>(<parameter1>, <parameter2>, ..., <parameterN>)
```

### Parameters

Parameters are defined inside parentheses after the function name and separated by commas.
Their names are used to reference them in the expression.

```yaml 
functions: 
  reward: f(base,multiplier) = base * multiplier
```

### Default parameter values

Parameters can define default values using `:`.

```yaml 
functions: 
  add_bonus: 'f(value,bonus:10) = value + bonus'
```

If the function is called without a second argument, `bonus` uses the default value `10`.
Default arguments can only be trailing arguments, defining them as prefixes to the parameter list won't work.
Since there always has to be at least one parameter, the first parameter can't reasonably have a default argument, 
although it is syntactically valid.

## Expression

The second part of a function is the expression.
It is any valid mathematical or logical expression that can use the parameters.

??? abstract "Syntax in backus-naur form"

    ```bnf
    <expr> ::= <ternary>
    
    <ternary> ::= <disjunction> | <disjunction> '?' <expr> ':' <ternary>
    
    <disjunction> ::= <conjunction> {'|' <conjunction>}
    
    <conjunction> ::= <comparison> {'&' <comparison>}
    
    <compare-symbol> ::= '<' | '>' | '<=' | '>=' | '=' | '!='
    <comparison> ::= <add> | <add> <compare-symbol> <add>
    
    <sum> ::= '+' | '-'
    <add> ::= <product> {<sum> <product>}
    
    <factor> ::= '*' | '/' | '%'
    <product> ::= <unary> {<factor> <unary>}
    
    <unary> ::= <exponent> | '-' <unary> | '!' <unary>
    
    <exponent> ::= <primary> | <primary> '^' <exponent>
    
    <primary> ::= <value> | <function-call> | '(' <expr> ')'
    
    <expr-list> ::= <expr> {',' <expr>}
    <function-call> ::= <identifier> '(' <expr-list> ')' | <qualifier> '(' <expr-list> ')'
    <value> ::= <number> | <string> | <boolean> | <qualifier>
    ```

Function expressions can use different kinds of values and atomic value sources:

- numbers
- strings
- booleans
- variables
- functions
- subroutines

Those can then be combined using operators to form more complex expressions.

### Values

Number, string, and boolean values are explicitly defined. Strings need to be quoted with `"`.

```yaml 
functions:
  compare: 'f(x) = x > 0 ? "positive" : "negative"'
```

Every value will be converted into the expected type if possible and might cause unexpected results otherwise.
Function evaluation will _rarely_ fail if the expression is not valid and instead return the string itself, `NaN` or `false`.
See [Value Interpretation](#value-interpretation) for more information.

### Variables

Parameter defined in the function definition can be used directly in the expression by name.
They simply refer to the value of the parameter passed to the function and can also be of any type.
There is no type checking, so make sure the parameters are of the correct type when using them to get the desired result.

### Operators

Any non-letter non-number symbol is recognized as an operator.

#### Arithmetic

Function expressions support common mathematical operators.

```yaml
functions: 
  add: 'f(a, b) = a + b'
  subtract: 'f(a, b) = a - b' 
  multiply: 'f(a, b) = a * b'
  divide: 'f(a, b) = a / b'
  modulo: 'f(a, b) = a % b'
  power: 'f(a, b) = a ^ b'
  is_less: 'b(a,b) = a < b' 
  is_greater: 'b(a,b) = a > b'
  is_equal: 'b(a,b) = a = b'
  is_not_equal: 'b(a,b) = a != b'
  is_less_or_equal: 'b(a,b) = a <= b'
  is_greater_or_equal: 'b(a,b) = a >= b'
```

#### Logical

Function expressions support common logical operators.

```yaml
functions: 
  is_less: 'b(a,b) = a < b' 
  is_greater: 'b(a,b) = a > b'
  is_equal: 'b(a,b) = a = b'
  is_not_equal: 'b(a,b) = a != b'
  is_less_or_equal: 'b(a,b) = a <= b'
  is_greater_or_equal: 'b(a,b) = a >= b'
```

As well as the usual `and` and `or` operators.

```yaml
functions: 
  or: 'b(a,b) = a | b'
  and: 'b(a,b) = a & b'
```

#### Parentheses

Parentheses can be used to group parts of an expression.
Without parentheses, normal operator precedence is used.

```yaml 
functions: 
  calculation: f(a,b,c) = (a + b) * c
```

#### Unary operators

Use `-` to negate a numeric value.
Use `!` to invert a boolean value.

```yaml 
functions:
  negate: 'f(a) = -a'
  invert: 'f(a) = !a'
```

#### Conditional expressions

Functions support ternary expressions.

```text 
condition ? value_if_true : value_if_false
```

Example:
```yaml 
functions: 
  reward: 'f(level) = level >= 10 ? 100 : 25'
```

If `level` is greater than or equal to `10`, the function returns `100`.
Otherwise, it returns `25`.

Ternary expressions can also use parameters and calculations:

```yaml 
functions: 
  bonus: 'f(points, multiplier) = points > 50 ? points * multiplier : points'
```

Since ternary expressions have a low precedence, other expressions within them do not require parentheses.

### Calling other functions

A function can call another function by using its identifier in curly braces.

```text 
{function_identifier}(argument1,argument2)
```

Example:

```yaml 
functions: 
  double: 'f(value) = value * 2'
  double_reward: 'f(base) = {double}(base) + 10'
```

The function `double_reward` calls the function with the identifier `double`.

Arguments passed to another function can also be full expressions:

```yaml 
functions: 
  multiply: 'multiply(a, b) = a * b'
  reward: 'reward(base, bonus) = {multiply}(base + bonus, 2)'
```

### Calling subroutines

There are a number of predefined subroutines that can be used to perform common tasks that are annoying to write out manually.
Unlike script functions, subroutines are not referenced with curly braces. Use `round(value)`, not `{round}(value)`.

```text 
subroutine(argument1,argument2)
```

Available subroutines:

| Subroutine | Parameters        | Description                                                                                                                                       | Example                                 |
|------------|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| `abs`      | `x`               | Returns the absolute value of `x`. Negative numbers become positive, positive numbers stay unchanged.                                             | `abs(-5)` returns `5`                   |
| `avg`      | at least 1        | Returns the average of all given numbers. Accepts any number of arguments. If no argument is given, `0` is used as the default value.             | `avg(2, 6)` returns `4`                 |
| `ceil`     | `x`               | Rounds `x` up to the next whole number.                                                                                                           | `ceil(2.1)` returns `3`                 |
| `clamp`    | `value, min, max` | Limits `value` to the range between `min` and `max`. Returns `min` if `value` is smaller, `max` if it is greater, otherwise `value`.              | `clamp(120, 0, 100)` returns `100`      |
| `cos`      | `x`               | Returns the cosine of `x`. The value is interpreted as radians.                                                                                   | `cos(0)` returns `1`                    |
| `exp`      | `x`               | Returns Euler's number `e` raised to the power of `x`.                                                                                            | `exp(1)` returns approximately `2.718`  |
| `floor`    | `x`               | Rounds `x` down to the previous whole number.                                                                                                     | `floor(2.9)` returns `2`                |
| `log`      | `basis, value`    | Returns the logarithm of `value` with the given `basis`.                                                                                          | `log(10, 100)` returns `2`              |
| `max`      | at least 1        | Returns the greatest number from all given arguments. Accepts any number of arguments. If no argument is given, `0` is used as the default value. | `max(3, 8, 2)` returns `8`              |
| `min`      | at least 1        | Returns the smallest number from all given arguments. Accepts any number of arguments. If no argument is given, `0` is used as the default value. | `min(3, 8, 2)` returns `2`              |
| `pi`       | `x`               | Returns `x` multiplied by the value of `pi` ($\pi$).                                                                                              | `pi(2)` returns approximately `6.283`   |
| `rnd`      | `min, max`        | Returns a random number between `min` and `max`.                                                                                                  | `rnd(1, 10)` returns a value like `5.4` |
| `round`    | `x`               | Rounds `x` to the nearest whole number.                                                                                                           | `round(2.6)` returns `3`                |
| `sin`      | `x`               | Returns the sine of `x`. The value is interpreted as radians.                                                                                     | `sin(0)` returns `0`                    |
| `sqrt`     | `x`               | Returns the square root of `x`.                                                                                                                   | `sqrt(9)` returns `3`                   |
| `sum`      | at least 1        | Returns the sum of all given numbers. Accepts any number of arguments. If no argument is given, `0` is used as the default value.                 | `sum(2, 4, 6)` returns `12`             |
| `tan`      | `x`               | Returns the tangent of `x`. The value is interpreted as radians.                                                                                  | `tan(0)` returns `0`                    |

Subroutines can be combined with other operators:

```yaml 
functions: 
  rounded_bonus: 'f(base, multiplier) = round(base * multiplier)' 
  safe_value: 'f(input) = clamp(input, 0, 100)'
  distance: 'f(x, y) = sqrt(x ^ 2 + y ^ 2)'
```

They can also be nested:

```yaml 
functions: 
  normalized_reward: 'f(value) = round(clamp(value, 0, 100))'
```

## Value Interpretation

Values are automatically converted to the expected type for specific operations and contexts.
This type coercion applies to literal values, variables, and function results, ensuring compatibility across different expression components.

The following table lists the conversions:

| From / To | Number                 | String                                                     | Boolean                           |
|-----------|------------------------|------------------------------------------------------------|-----------------------------------|
| Number    | :octicons-x-circle-16: | just the number as string, sometimes adding decimal places | `number > 0 ? true : false`       |
| String    | parse number or NaN    | :octicons-x-circle-16:                                     | `string = "true"` or `string > 0` |
| Boolean   | `bool ? 1 : 0`         | `"true"` or `"false"`                                      | :octicons-x-circle-16:            |
