---
icon: material/newspaper
tags: 
  - YAML-Tutorials
---
# YAML Syntax
This plugin uses the YAML (`.yml`) format for all files. You may already know it from other plugins as most use this format.
Before we can start you need to understand the fundamentals of YAML.

In theory, you can edit quests with any editor. However, using the feature-packed 
Visual Studio Code is highly recommended! It will highlight any syntax errors you may make with YAML.

[:material-download: Install Visual Studio Code](https://code.visualstudio.com){ .md-button .md-button--primary .noExternalLinkIcon}

## YAML Basics

YAML is `key: "value"` based. This means you use a :octicons-key-16: to get a certain value.
Let me show you an example:

```YAML title="YAML Data Format"
key: "value"
Jack: "Some data about Jack"
```
Now you can use the :octicons-key-16: `Jack` to obtain `Some data about Jack`.

Keys and values can also be nested into each other. Then they **must** be indented with two **spaces**.

```YAML title="Nested YAML"
outerName:
  innerName: "innerValue"
  anotherInnerName: "BetonQuest is great!"
```

Tabs are not supported. Use spaces instead.

You shouldn't name anything `yes`, `no`, `on`, `off`, `null`, `true` or `false` as those names are reserved keywords in YAML.  
 
---
[:material-arrow-right: Next Step: Conversations](../Basics/Conversations.md){ .md-button .md-button--primary }
