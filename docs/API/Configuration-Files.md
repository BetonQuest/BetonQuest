---
icon: material/note-edit
---
@snippet:api-state:draft@

!!! abstract "[ServicesManager](Obtaining-API.md) API Classes"
    * `org.betonquest.betonquest.api.config.ConfigAccessorFactory`
    * `org.betonquest.betonquest.api.config.ConfigAccessor`
    * `org.betonquest.betonquest.api.config.FileConfigAccessor`
---

BetonQuest provides the `ConfigAccessor`, a simple API to load, reload, save and delete configuration files.
It extends `ConfigurationSection` and therefore also provides the well-known Bukkit methods to access and modify the configuration.
Additionally, it takes care of patching the config whenever syntax or content changes need to be made.

## Loading a config

By creating a ConfigAccessor you either load an existing config or create the default one from your plugin's resources.
Here is an example method that loads a "_config.yml_" file from your plugin's resources:
```java
public void loadPluginConfig(Plugin plugin, ConfigAccessorFactory configAccessorFactory) { //(1)!
    File targetConfigFile = new File(plugin.getDataFolder(), "config.yml"); //(2)!
    String sourceResourceFile = "config.yml"; //(3)!
    
    ConfigAccessor config1 = configAccessorFactory.create(targetConfigFile, plugin, sourceResourceFile);
    // or
    ConfigAccessor config2 = configAccessorFactory.createPatching(targetConfigFile, plugin, sourceResourceFile);
}
```

1. Inject the `Plugin` and the `ConfigAccessorFactory`, obtained via the ServicesManager, see the [Obtaining API](Obtaining-API.md) page.
2. This is the location the config will be saved to. In this case it's a file named "_config.yml_" in your plugin's folder.
3. This is the name of the resource file in your plugin's resources. Here is is also "_config.yml_" but it could be different.

The `create` method is overloaded.
One only to access a configuration file from the resources and one to only load an actual file.
And one more to a file from the resources and save it to the file system.
None of this methods will patch the config file.

If you want to patch the config file, you can use the `createPatching` method.
This will load the config file and apply all patches that are needed.
Also this method is overloaded and has a variant to use custom patches. 

## Working with the ConfigAccessor

The `ConfigAccessor` extends `ConfigurationSection` and therefore provides all known Bukkit methods
to access and modify the configuration.
You can reload, save and delete the configuration by calling it's corresponding `reload()`, `save()` and `delete()`
methods.

Make sure to add a `configVersion` key with an empty string as it's value when adding a new config resource file,
that should get patched.

``` YAML title="Example config.yml"
configVersion: ""
language: en
# ...
```

The patcher will then automatically set the version to the newest available patch version.
This frees you from the hassle of updating the `configVersion` key in the default resource file every time you add a patch.

!!! warning "Reloading Behaviour"
    When reloading the `ConfigAccessor`, it loads a new `ConfigurationSection` from the related file and replaces the old root.
    This means that all references to old child `ConfigurationSection` in your code will be outdated and need to be updated.
    Therefore, the best way to work with the `ConfigAccessor` is to pass it to your classes. Don't pass its children.
    While querying the `ConfigAccessor` you can use child `ConfigurationSection` as usual, just don't store them.

## Updating Configurations

When you just want to add a new option to the config, you can simply add it to your config's resource file. It will 
automatically be added to the users existing config. However, if you want to edit existing options, you need to use the config patcher.

The config patcher automatically updates all configs loaded using the `ConfigAccessor` APIs `createPatching` methods.
This is needed when changes are made to the existing config format.
This patcher only works on configuration files!
It's not used for files that contain quests as these should not be loaded with the `createPatching` methods.
The patching progress is configured in a dedicated patch file per config file.

### The Patch File
Whenever a resource file is loaded using BetonQuest's `createPatching` method, a "_resourceFileName.patch.yml_" file 
is searched in the same directory the resource file is located. It contains the configuration for all patches
that need to be applied. Each patch contains configurations for "transformers" that apply changes to the resource
file before it's loaded. Let's take a look at an example:

``` YAML title="config.patch.yml"
2.0.0.1: #(1)!
  - type: SET #(2)!
    key: defaultConversationColor
    value: BLUE
  - type: REMOVE
    key: hook.mmocore
1.12.9.1:
  - type: LIST_ENTRY_ADD #(3)!
    key: cmdBlacklist
    entry: teleport
```

1. These transformers will be applied for a config on any version older than 2.0.0-CONFIG-1
2. This is the `SET` transformer. It will set `defaultConversationColor` to `BLUE`.
3. This is the `LIST_ENTRY_ADD` transformer. It will append `teleport` to the list with the key `cmdBlacklist`.

All patches that are newer than the configs current version are applied, starting with the oldest one.    

### Config Versions
The versions in the patch file have four digits (`1.2.3.4`). The first three are the semantic version of the BetonQuest 
version that this patch updates the config to. The last digit is used to version multiple patches during the
development phase of a semantic versioning release. 

The config's version is saved in each config as the value of the `configVersion` key. It is automatically set by the patcher.
The version format is slightly different from the patch file - `1.2.3-CONFIG-4` in the config is `1.2.3.4` in the patch file .

!!! info "Example development cycle:"
    * `2.0.0` is in development...
        - A change to the config is introduced in 2.0.0-DEV-45 :arrow_right: `configVersion: "2.0.0-CONFIG-1"`
        - Another change to the config is introduced in 2.0.0-DEV-132 :arrow_right: `configVersion: "2.0.0-CONFIG-2"`
        - `2.0.0` is released. Therefore `2.0.0-CONFIG-2` becomes the final config version of `2.0.0`.
    * `2.0.1` is in development...
        - A change to the config is introduced :arrow_right: `configVersion: "2.0.1-CONFIG-1"`
        - `2.0.1` is released. Therefore `2.0.1-CONFIG-1` becomes the final config version of `2.0.1`.
    * `2.0.2` is in development...
        - No changes to the config are introduced.
        - `2.0.2` is released. `2.0.1-CONFIG-1` is still the config version of the `2.0.2` release as no changes have been 
           introduced to the config.
    
    This is how the patch file would be changed for each dev build:
    
    === "2.0.0-DEV-45"
        ```YAML title="config.patch.yml"
        2.0.0.1: #(1)!
          - type: SET
            key: journal.custom_model_data
            value: -1
        ```
        
        1. Config Version 1 of 2.0.0.
    === "2.0.0-DEV-132"
        ```YAML title="config.patch.yml"
        2.0.0.2: #(2)!
          - type: SET
            key: hook.decentholograms
            value: true
        2.0.0.1: #(1)!
          - type: SET
            key: journal.custom_model_data
            value: -1
        ```
        
        1. Config Version 1 of 2.0.0.
        2. Config Version 2 of 2.0.0. Multiple config versions per release are needed to auto-update the configs
           of dev builds users.
    === "2.0.1-DEV-1"
        ```YAML title="config.patch.yml"
        2.0.1.1: #(3)!
          - type: SET
            key: hook.cmi
            value: true
        2.0.0.2: #(2)!
          - type: SET
            key: hook.decentholograms
            value: true
        2.0.0.1: #(1)!
          - type: SET
            key: journal.custom_model_data
            value: -1
        ```
        
        1. Config Version 1 of 2.0.0.
        2. Config Version 2 of 2.0.0. Multiple config versions per release are needed to auto-update the configs
           of dev builds users.
        3. Config Version 1 of 2.0.1.
    === "2.0.2-DEV-1"
        ```YAML title="config.patch.yml"
        # As no changes were made to the config in 2.0.2 no new config version was introduced.
        2.0.1.1: #(3)!
          - type: SET
            key: hook.cmi
            value: true
        2.0.0.2: #(2)!
          - type: SET
            key: hook.decentholograms
            value: true
        2.0.0.1: #(1)!
          - type: SET
            key: journal.custom_model_data
            value: -1
        ```
        
        1. Config Version 1 of 2.0.0.
        2. Config Version 2 of 2.0.0. Multiple config versions per release are needed to auto-update the configs
           of dev builds users.
        3. Config Version 1 of 2.0.1.
  
The patcher will also automatically set the version to the newest available patch version if the `configVersion` is an empty 
string. Therefore, setting the `configVersion` to an empty string in your config's resource file is recommended. The
patcher will make sure it's always up-to-date. 

### Transformer Types

By default, these transformers are available: 

#### SET

Sets a key to the given value. Already set keys will be overridden.
Only use this to modify existing keys. Adding new ones can be done easier by just adding them to your config's resource file.
New keys will then be added automatically to the users existing config.

``` YAML title="Syntax"
- type: SET
  key: journalLocked
  value: true
```

#### KEY_RENAME

Renames a key while preserving the value.
``` YAML title="Syntax"
- type: KEY_RENAME
  oldKey: journalLocked
  newKey: journalLockedOnSlot
```

#### LIST_ENTRY_ADD

Adds an entry to the given list. The list will be created if it did not exist so far.
``` YAML title="Syntax"
- type: LIST_ENTRY_ADD
  key: section.myList
  entry: newEntry
  position: LAST #(1)!
```

1. Can be `FIRST` or `LAST`. Default value is `LAST`.

#### LIST_ENTRY_RENAME

Renames all list entries that match the given regex.
``` YAML title="Syntax"
- type: LIST_ENTRY_RENAME
  key: section.myList
  oldEntryRegex: currentEntry
  newEntry: newEntry
```

#### LIST_ENTRY_REMOVE

Removes all list entries that match the given regex.
``` YAML title="Syntax"
- type: LIST_ENTRY_REMOVE
  key: section.myList
  entry: removedEntry
```

#### VALUE_RENAME

Renames the key's value if it matches the given regex.
``` YAML title="Syntax" 
- type: VALUE_RENAME
  key: section.testKey
  oldValueRegex: test
  newValue: newTest
```

#### VALUE_REPLACE

Replace a value part with another value.
``` YAML title="Syntax"
- type: VALUE_REPLACE
  key: section.testKey
  oldValue: test
  newValue: newTest
```

#### TYPE_TRANSFORM

Transforms the data type of an existing key into the given Java data type. This will result in a different formatting
in the YAML file. E.g. strings will be surrounded by quotes, numbers and booleans won't.
``` YAML title="Syntax"
- type: TYPE_TRANSFORM
  key: section.myKey
  newType: string #(1)!
```

1. Either `string`, `integer`, `double`, `float` or `boolean`. 


#### REMOVE

Removes both sections and keys (including all nested contents).
``` YAML title="Syntax"
- type: REMOVE
  key: section.myList
```

### Adding additional Transformers
If you want to use your own transformers, you can pass them to the `createPatching` method in the form of a `PatchTransformerRegistry`.
Using this possibility will override the default transformers. You need to re-add them explicitly.
Reading them can be simplified by using the class  `DefaultPatchTransformerRegistry`, however,
this is internal code like the default patcher itself, so it's not recommended to use and also not guaranteed to work in future versions.

```JAVA title="Anonymous PatchTransformerRegisterer Example"
public void loadPluginConfig(Plugin plugin, ConfigAccessorFactory configAccessorFactory) {
    File targetConfigFile = new File(plugin.getDataFolder(), "config.yml");
    PatchTransformerRegistry patchTransformerRegistry = new MyTransformerRegistry();
    ConfigAccessor config = configAccessorFactory.createPatching(targetConfigFile, plugin, "config.yml", patchTransformerRegistry);
}

public class MyTransformerRegistry extends DefaultPatchTransformerRegistry { //(1)!
    public MyTransformerRegistry() {
        super();
        transformers.put("MY_TRANSFORMER", new MyTransformer());
    }
}
```

1. extend the `DefaultPatchTransformerRegistry` if you want to use the default transformers as well and add more in the constructor.
