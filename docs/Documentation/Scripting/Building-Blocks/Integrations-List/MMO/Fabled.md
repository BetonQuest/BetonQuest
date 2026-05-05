# [Fabled](https://www.spigotmc.org/resources/91913/)

@snippet:versions:minimum@ _1.0.2-R1_

## Conditions

### `FabledClass`

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `fabledclass <name>`  
__Description__: Whether the player has the specified class or a child class of the specified one.

The first argument is simply the name of a class.
You can add `exact` argument if you want to check for that exact class, without checking child classes.

```YAML title="Example"
conditions:
  isWarrior: "fabledclass warrior"
```

### `FabledLevel`

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `fabledlevel <name> <level>`  
__Description__: Whether the player has the specified or greater level than the specified class level.

The first argument is class name, the second one is the required level.

```YAML title="Example"
conditions:
  isWarrior3: "fabledlevel warrior 3"
```
