---
icon: material/note
---

BetonQuest provides a simple API to load, reload, save and delete configuration files. It's used by BetonQuest itself to load its main config.
It also provides a [Config Patcher](ConfigPatcher.md) that automatically updates the config to
the newest version.

## Loading a config

By creating a ConfigurationFile you either load an existing config or create the default one from your plugin's resources.
```java
Plugin plugin = MyBQAddonPlugin.getInstance();
File configFile = new File(plugin.getDataFolder(), "config.yml"); // (1)!
ConfigurationFile config = new ConfigurationFile(configFile, plugin, "defaultConfig.yml");
```

1. This is the location the config will be saved to. In this case it's a file named "_config.yml_" in your plugin's folder.

Additionally, the ConfigurationFile will attempt to patch itself with a patch file. This patch file must exist in the
same directory as the resourceFile. Its name is the one of the resourceFile but with '.patch' inserted between the file 
name and the file extension. For example, "_config.yml_" & "_config.patch.yml_".

The default `PatchTransformers` are used when the ConfigurationFile is loaded using this method.
See the documentation on the [Config Patcher](ConfigPatcher.md#transformer-types) for information on how to register
custom transformers.

## Working with the ConfigurationFile

You can reload, save and delete the ConfigurationFile by calling it's corresponding `reload()`, `save()` and `delete()`
methods.

## Updating ConfigurationFiles

The ConfigurationFile API provides a [Config Patcher](ConfigPatcher.md) that automatically updates the config to the newest version.
