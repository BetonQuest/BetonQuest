---
icon: material/puzzle-edit
---
@snippet:api-state:stable@

We implemented multiple different `org.bukkit.configuration.ConfigurationSection`'s and `Configuration`'s to make it 
easier to work with configurations and to implement some new features.

We have the following implementations:

## Decorator
Basically a wrapper for a `ConfigurationSection` simply delegating all calls to the wrapped section.
In that way, you can override specific methods without having to override all methods for custom implementations.
Mainly used for internal purposes, but can be used to create even more custom implementations.

- `org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator`

## Handle
Based on `Decorator`. Handles any modifications to the configuration and wraps all values that are an instance
of `ConfigurationSection` with the same implementation. This should prevent any modifications to the configuration
that are not done through the handler. This is the implementation actually used to create more custom implementations.

- `org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleModificationConfiguration`
- `org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleModificationConfigurationSection`

## Unmodifiable
Based on `Handle`. Makes the configuration unmodifiable. This means no values can be added, removed or modified.
Also, no new Sections can be created. All modifications throw a `UnsupportedOperationException`.

- `org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable.UnmodifiableConfiguration`
- `org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable.UnmodifiableConfigurationSection`

## Lazy
Based on `Handle`.
This configuration does only create a MemorySection that is not registered to the parent, until a value is set.
This means it can be read without creating a new section in the parent configuration.

- `org.betonquest.betonquest.api.bukkit.config.custom.lazy.LazyConfigurationSection`

## Fallback
This is a configuration buildup from two `ConfigurationSection`'s.
One is the original where all modifications are done, and the other one is the fallback configuration.
The fallback configuration is used to look up values that are not contained in the original configuration.
This is useful for default values or global configurations.

- `org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfiguration`
- `org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSection`

## Multi
This is not a ConfigurationSection of bukkit, it is an additional interface.
This interface adds methods to manage configs that are build from **multiple** `ConfigurationSection`'s.
The MultiSectionConfiguration is an implementation of this interface that implements a merging method that does not
allow any conflicting values resulting in a `KeyConflictException`.
If mismatching states or settings are found in one `ConfigurationSection` a `InvalidSubConfigurationException` is thrown.

- `org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration`
- `org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration`
- `org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException`
- `org.betonquest.betonquest.api.bukkit.config.custom.multi.InvalidSubConfigurationException`

## Multi Fallback
This is a Fallback configuration that also implements the MultiConfiguration interface.
In this way, a multi configuration can have the advantage of a fallback configuration to look up values that are not
contained in the original configuration.

- `org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback.MultiFallbackConfiguration`
