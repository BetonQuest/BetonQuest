---
icon: simple/yaml
---
# YAML Basics

This plugin uses the YAML (`.yml`) format for all files. You may already know it from other plugins as most use this format.

Unfortunately, you'll likely soon experience that your quest is not working because you made a YAML syntax error.
This is the case because it is quite easy to make a mistake in YAML syntax.

So let's briefly talk about what YAML is and how it works.

<div class="grid" markdown>
!!! danger "Requirements"
    * No further requirements

!!! example "Related Docs"
    * [Handling YAML Errors](../../Tools/YAML-Errors.md)
</div>

## What is YAML?

YAML is a data-serialization language that is readable by humans. It is frequently used for Minecraft related configuration files.

YAML is `key: "value"` based. This means you use a :octicons-key-16: to get a certain value. Values should be surrounded by double quotes (`"..."`).
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


[:octicons-arrow-right-16: Next Step: Conversations](../Basics/Conversations.md){ .md-button .md-button--primary }
