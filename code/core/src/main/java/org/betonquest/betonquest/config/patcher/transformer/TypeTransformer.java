package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.patcher.PatchException;
import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.api.config.patcher.PatcherOptions;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Locale;

/**
 * Changes the data type of existing value.
 */
public class TypeTransformer implements PatchTransformer {

    /**
     * Default constructor.
     */
    public TypeTransformer() {
    }

    @Override
    public void transform(final PatcherOptions options, final ConfigurationSection config) throws PatchException {
        final String key = options.getString("key");
        final String type = options.getString("newType");

        final String value = config.getString(key);
        if (value == null) {
            throw new PatchException("Value is not set, skipping transformation.");
        }
        setValue(config, type, key, value);
    }

    private void setValue(final ConfigurationSection config, final String type, final String key, final String valueString) throws PatchException {
        switch (type.toLowerCase(Locale.ROOT)) {
            case "boolean" -> config.set(key, Boolean.valueOf(valueString));
            case "integer" -> config.set(key, Integer.valueOf(valueString));
            case "double" -> config.set(key, Double.valueOf(valueString));
            case "float" -> config.set(key, Float.valueOf(valueString));
            case "string" -> config.set(key, valueString);
            default -> throw new PatchException("Unknown type '" + type + "', skipping transformation.");
        }
    }
}
