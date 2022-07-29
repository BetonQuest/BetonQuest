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

## Config Versions
The versions in the patch file have four digits (`1.2.3.4`). The first three are the semantic version of the BetonQuest 
version that this patch updates the config to. The last digit is used to version multiple patches during the
development phase of a semantic versioning release. 

The config's version is shown inside each config as the value of the `configVersion` key. It is automatically set by the patcher.
It uses a slightly different format: `1.2.3.4` in the patch file is `1.2.3-CONFIG-4` in the config.

Example:

* `2.0.0.1`: Patch that updates the config to a state required for a `2.0.0` dev build.
* `2.0.0.2`: Patch that updates the config to a state required for a `2.0.0` dev build.
* `2.0.0` is released. Therefore `2.0.0-CONFIG-2` becomes the final config version of `2.0.0`.
* `2.0.1.1`: Patch that updates the config to a state required for a `2.0.1` dev build.
* `2.0.1` is released. Therefore `2.0.1-CONFIG-1` becomes the final config version of `2.0.1`.

## Transformer Types
### SET

``` YAML title="Syntax"
- type: SET
  key: journalLocked
  value: true
```

### KEY_RENAME

``` YAML title="Syntax"
- type: KEY_RENAME
  oldKey: journalLocked
  newKey: journalLockedOnSlot
```

### LIST_ENTRY_ADD

``` YAML title="Syntax"
- type: LIST_ENTRY_ADD
  key: section.myList
  entry: newEntry
  position: LAST #(1)!
```

1. Can be `FIRST` or `LAST`. Default value is `LAST`.

### LIST_ENTRY_RENAME

``` YAML title="Syntax"
- type: LIST_ENTRY_RENAME
  key: section.myList
  oldEntryRegex: currentEntry
  newEntry: newEntry
```

### LIST_ENTRY_REMOVE

``` YAML title="Syntax"
- type: LIST_ENTRY_REMOVE
  key: section.myList
  entry: removedEntry
```

### VALUE_RENAME

``` YAML title="Syntax" 
- type: VALUE_RENAME
  key: section.testKey
  oldValueRegex: test
  newValue: newTest
```

### REMOVE

``` YAML title="Syntax"
- type: REMOVE
  key: section.myList
```
