# [Fabled](https://www.spigotmc.org/resources/91913/)

### Conditions

#### Fabled Class: `fabledclass`

This condition checks if the player has specified class or a child class of the specified one.
The first argument is simply the name of a class.
You can add `exact` argument if you want to check for that exact class, without checking child classes.

```YAML title="Example"
conditions:
  isWarrior: "fabledclass warrior"
```

#### Fabled Level: `fabledlevel`

This condition checks if the player has specified or greater level than the specified class level.
The first argument is class name, the second one is the required level.

```YAML title="Example"
conditions:
  isWarrior3: "fabledlevel warrior 3"
```
