##YAML Syntax
Before we can start you need to understand the fundamentals of YAML.
You have no idea what that is? Well YAML is the format most plugins store their config data in.

Its `key:value` based which means that you will always have to name the thing that you write down before you actually
write it down. Let me show you an example:

!!! example "YAML Key:Value"
        ```YAML
        key: "value"
        name: "your thing"
        ```

These keys and values can also be nested into each other by indenting them with a few spaces:

!!! example "Nested YAML"
        ```YAML
        outerName:
          innerName: "innerValue"
          anotherInnerName: "BetonQuest is great!"
        ```



YAML also cares a lot about spaces and tabs! The rules are pretty complicated.
It's best not to use tabs in your YAML files at all. Use spaces instead. 
 
**Next step: [Learn BetonQuest](Learn-BetonQuest.md)**
