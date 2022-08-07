---
icon: material/note-edit
---

## Usage
The config patcher automatically updates BetonQuest's main plugin config or those of BetonQuest's addons.
This is needed when changes are made to the existing config format.
This patcher only works on configuration files! It's not used for files that contain quests.


## The Patch File
Whenever a resource file is loaded using BetonQuest's `ConfigurationFile` API, a "_resourceFilename.patch.yml"_ file 
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

## Config Versions
The versions in the patch file have four digits (`1.2.3.4`). The first three are the semantic version of the BetonQuest 
version that this patch updates the config to. The last digit is used to version multiple patches during the
development phase of a semantic versioning release. 

The config's version is shown inside each config as the value of the `configVersion` key. It is automatically set by the patcher.
It uses a slightly different format: `1.2.3.4` in the patch file is `1.2.3-CONFIG-4` in the config.

!!! info "Example development cycle:"
    * `2.0.0` is in development...
        - A change to the config is introduced in a dev build :arrow_right: `configVersion: "2.0.0-CONFIG-1"`
        - A change to the config is introduced in another dev build :arrow_right: `configVersion: "2.0.0-CONFIG-2"`
        - `2.0.0` is released. Therefore `2.0.0-CONFIG-2` becomes the final config version of `2.0.0`.
    * `2.0.1` is in development...
        - A change to the config is introduced :arrow_right: `configVersion: "2.0.1-CONFIG-1"`
        - `2.0.1` is released. Therefore `2.0.1-CONFIG-1` becomes the final config version of `2.0.1`.
    * `2.0.2` is in development...
        - No changes to the config are introduced.
        - `2.0.2` is released. `2.0.1-CONFIG-1` is still the config version of the `2.0.2` release as no changes have been 
           introduced to the config.

The patcher will also automatically set the version to the newest available patch version if the `configVersion` is an empty 
string. Therefore, setting the `configVersion` to an empty string in your config's resource file is recommended. The
patcher will make sure it's always up-to-date. 

## Transformer Types

By default, the transformers down below are available. 
 
If you want to use your own transformers, you can pass them to the create method in the form of a `PatchTransformationRegisterer`.
This is just a functional interface, that registers additional transformers.
Utilizing this possibility will however override the default transformers. You need to re-add them explicitly. 

```JAVA title="Anonymous PatchTransformationRegisterer Example"
config = ConfigurationFile.create(configFile, MyPlugin.getInstance(), "config.yml",
    new PatchTransformationRegisterer() {
        @Override
        public void registerTransformations(final Patcher patcher) {
            PatchTransformationRegisterer.super.registerTransformations(patcher); //(1)!
            // Register your own transformers here:
            patcher.registerTransformer("myTransformer", new MyTransformer()); 
        }
    });
```
  
1. Call this if you want to use the default transformers alongside your own.

### SET

Sets a key to the given value. Already set keys will be overridden if `override` is set to `true`.
``` YAML title="Syntax"
- type: SET
  key: journalLocked
  value: true
  override: true
```

### KEY_RENAME

Renames a key while preserving the value.
``` YAML title="Syntax"
- type: KEY_RENAME
  oldKey: journalLocked
  newKey: journalLockedOnSlot
```

### LIST_ENTRY_ADD

Adds an entry to the given list. The list will be created if it did not exist so far.
``` YAML title="Syntax"
- type: LIST_ENTRY_ADD
  key: section.myList
  entry: newEntry
  position: LAST #(1)!
```

1. Can be `FIRST` or `LAST`. Default value is `LAST`.

### LIST_ENTRY_RENAME

Renames all list entries that match the given regex.
``` YAML title="Syntax"
- type: LIST_ENTRY_RENAME
  key: section.myList
  oldEntryRegex: currentEntry
  newEntry: newEntry
```

### LIST_ENTRY_REMOVE

Removes all list entries that match the given regex.
``` YAML title="Syntax"
- type: LIST_ENTRY_REMOVE
  key: section.myList
  entry: removedEntry
```

### VALUE_RENAME

Renames the key's value if it matches the given regex.
``` YAML title="Syntax" 
- type: VALUE_RENAME
  key: section.testKey
  oldValueRegex: test
  newValue: newTest
```

### REMOVE

Removes both sections and keys (including all nested contents).
``` YAML title="Syntax"
- type: REMOVE
  key: section.myList
```
