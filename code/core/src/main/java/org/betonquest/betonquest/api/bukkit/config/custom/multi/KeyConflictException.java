package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * This exception represents a conflict with two keys in multiple configurations.
 */
public class KeyConflictException extends InvalidConfigurationException {

    @Serial
    private static final long serialVersionUID = 3529254335908123119L;

    /**
     * This map contains all conflicting keys and a list of all related {@link ConfigurationSection}s.
     */
    private final Map<String, List<ConfigurationSection>> conflictingKeys;

    /**
     * This outer list contains all conflicting paths while the inner list contains pairs of the actual
     * paths and the related {@link ConfigurationSection} that have a conflict.
     */
    private final List<List<Pair<String, ConfigurationSection>>> conflictingPaths;

    /**
     * Creates an exception for duplicated keys.
     *
     * @param duplicates       the map of duplicated keys
     * @param conflictingPaths the list of all conflicting paths
     */
    public KeyConflictException(final Map<String, List<ConfigurationSection>> duplicates,
                                final List<List<Pair<String, ConfigurationSection>>> conflictingPaths) {
        this(null, duplicates, conflictingPaths);
    }

    /**
     * Creates an exception for duplicated keys.
     *
     * @param msg              the exception message
     * @param duplicates       the map of duplicated keys
     * @param conflictingPaths the list of all conflicting paths
     */
    public KeyConflictException(@Nullable final String msg, final Map<String, List<ConfigurationSection>> duplicates,
                                final List<List<Pair<String, ConfigurationSection>>> conflictingPaths) {
        super(msg);
        this.conflictingKeys = duplicates;
        this.conflictingPaths = conflictingPaths;
    }

    /**
     * Create a readable and more useful error message.
     *
     * @param namedConfigurations a map of the {@link ConfigurationSection}s, that you used when constructing a
     *                            {@link MultiSectionConfiguration#MultiSectionConfiguration(List)}.
     *                            Each ConfigurationSection maps to a readable message.
     * @return a formatted and helpful error message
     */
    public String resolvedMessage(final Map<ConfigurationSection, String> namedConfigurations) {
        final StringBuilder exMessage = new StringBuilder(50);
        exMessage.append("You have conflicts in your configuration files:\n");

        if (!conflictingKeys.isEmpty()) {
            resolveKeyMessage(namedConfigurations, exMessage);
        }
        if (!conflictingPaths.isEmpty()) {
            resolvePathMessage(namedConfigurations, exMessage);
        }
        return exMessage.toString();
    }

    private void resolveKeyMessage(final Map<ConfigurationSection, String> namedConfigurations, final StringBuilder exMessage) {
        exMessage.append('\n');
        final SortedMap<String, List<ConfigurationSection>> sorted = new TreeMap<>(conflictingKeys);
        for (final Map.Entry<String, List<ConfigurationSection>> entry : sorted.entrySet()) {
            exMessage.append("    The key '").append(entry.getKey()).append("' is defined multiple times in the following configs:\n");
            entry.getValue().parallelStream().map(namedConfigurations::get).sorted()
                    .forEachOrdered(config -> exMessage.append("        - ").append(config).append('\n'));
        }
    }

    @SuppressWarnings("NullAway")
    private void resolvePathMessage(final Map<ConfigurationSection, String> namedConfigurations, final StringBuilder exMessage) {
        exMessage.append('\n');
        for (final List<Pair<String, ConfigurationSection>> entry : conflictingPaths) {
            final List<Pair<String, ConfigurationSection>> sorted = entry.stream()
                    .sorted((entry1, entry2) -> {
                        final int compare = entry1.getKey().compareTo(entry2.getKey());
                        if (compare == 0) {
                            return namedConfigurations.get(entry1.getValue()).compareTo(namedConfigurations.get(entry2.getValue()));
                        }
                        return compare;
                    }).collect(Collectors.toList());
            final Pair<String, ConfigurationSection> firstEntry = sorted.get(0);
            sorted.remove(0);

            exMessage.append("    The key '").append(firstEntry.getKey()).append("' in config '")
                    .append(namedConfigurations.get(firstEntry.getValue()))
                    .append("' is a path with sub keys in at least one of the following configs:\n");
            sorted.parallelStream().map(pair -> Pair.of(namedConfigurations.get(pair.getValue()), pair.getKey())).sorted()
                    .forEachOrdered(pair -> exMessage.append("        - ").append(pair.getKey()).append(" with '").append(pair.getValue()).append("'\n"));
        }
    }
}
