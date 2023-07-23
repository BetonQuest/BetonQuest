---
icon: material/rocket-launch
---
How to write addons for BetonQuest.

## Ensuring that BetonQuest is loaded
Declare BetonQuest as a soft dependency or hard dependency inside your plugin's *plugin.yml* file.
A hard dependency will prevent your plugin from loading if BetonQuest is not installed. If your plugin is just a 
BetonQuest addon, you should use a hard dependency. If your plugin is a standalone plugin that can work without
BetonQuest, you should use a soft dependency.

=== "BetonQuest as a required dependency"
    
    ```yaml title="plugin.yml"
    name: "My BetonQuest Addon"
    depend:
      - BetonQuest
    # ...
    ```

=== "BetonQuest as an optional dependency"
    
    ```yaml title="plugin.yml"
    name: "My Standalone Plugin"
    softdepend:
      - BetonQuest
    # ...
    ```
