package org.betonquest.betonquest.mc_1_21_4.api.instruction.argument.type;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

/**
 * Parses a registry key from a string.
 * Can be used for previous enum types that are now accessed via registry keys.
 *
 * @param <T> the type of the registry key
 */
public class RegistryKeyParser<T extends Keyed> implements Argument<T> {

    /**
     * The registry key.
     */
    private final RegistryKey<T> key;

    /**
     * Creates a new parser for the given registry key.
     *
     * @param key the registry key
     */
    public RegistryKeyParser(final RegistryKey<T> key) {
        this.key = key;
    }

    @Override
    public T apply(final String string) throws QuestException {
        final NamespacedKey namespacedKey = NamespacedKey.fromString(string);
        if (namespacedKey == null) {
            throw new QuestException("Invalid namespaced key: " + string);
        }
        final T value = RegistryAccess.registryAccess().getRegistry(this.key).get(namespacedKey);
        if (value == null) {
            throw new QuestException("Can't find '" + namespacedKey + "' in registry '" + key + "'");
        }
        return value;
    }
}
