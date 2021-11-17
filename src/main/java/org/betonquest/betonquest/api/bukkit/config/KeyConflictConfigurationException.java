package org.betonquest.betonquest.api.bukkit.config;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyConflictConfigurationException extends InvalidConfigurationException {

    private static final long serialVersionUID = 3529254335908123119L;

    @Getter
    private final Map<String, List<ConfigurationSection>> duplicates;

    public KeyConflictConfigurationException(final Map<String, List<ConfigurationSection>> duplicates) {
        super();
        this.duplicates = toUnmodifiable(duplicates);
    }

    public KeyConflictConfigurationException(final String msg, final Map<String, List<ConfigurationSection>> duplicates) {
        super(msg);
        this.duplicates = toUnmodifiable(duplicates);
    }

    @NotNull
    private Map<String, List<ConfigurationSection>> toUnmodifiable(final Map<String, List<ConfigurationSection>> duplicates) {
        final Map<String, List<ConfigurationSection>> unmodifiable = new HashMap<>();
        duplicates.forEach((key, value) -> unmodifiable.put(key, Collections.unmodifiableList(value)));
        return Collections.unmodifiableMap(unmodifiable);
    }
}
