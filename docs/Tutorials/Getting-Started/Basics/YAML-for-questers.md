---
icon: material/newspaper
tags: 
  - YAML-Tutorials
---
# YAML Syntax
This plugin uses the YAML format for all files. You may already know it from other plugins as most use this format.
Before we can start you need to understand the fundamentals of YAML.

YAML is `key: "value"` based. This means you use a :octicons-key-16: to get a certain value.
Let me show you an example:

```YAML title="YAML Data Format"
key: "value"
Jack: "Some data about Jack"
```
Now you can use the :octicons-key-16: `Jack` to obtain `Some data about Jack`.

Keys and values can also be nested into each other. Then they **must** be indented with two spaces.

```YAML title="Nested YAML"
outerName:
  innerName: "innerValue"
  anotherInnerName: "BetonQuest is great!"
```

It's best not to use tabs in your YAML files at all. Use spaces instead. 
