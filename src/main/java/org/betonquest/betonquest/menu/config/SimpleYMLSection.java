package org.betonquest.betonquest.menu.config;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Abstract class to help parsing of yml config files
 */
@SuppressWarnings({"PMD.PreserveStackTrace", "PMD.AbstractClassWithoutAbstractMethod", "PMD.CommentRequired",
        "PMD.TooManyMethods"})
public abstract class SimpleYMLSection {
    public static final String RPG_MENU_CONFIG_SETTING = "RPGMenuConfig setting ";

    protected final QuestPackage pack;

    protected final ConfigurationSection config;

    protected final String name;

    public SimpleYMLSection(final QuestPackage pack, final String name, final ConfigurationSection config) throws InvalidConfigurationException {
        this.pack = pack;
        this.config = config;
        this.name = name;
        //noinspection ConstantValue
        if (config == null || config.getKeys(false).size() == 0) {
            throw new InvalidSimpleConfigException("RPGMenuConfig is invalid or empty!");
        }
    }

    private String resolveGlobalVariable(final String string) {
        if (pack == null) {
            return string;
        }
        return GlobalVariableResolver.resolve(pack, string);
    }

    /**
     * Parse string from config file
     *
     * @param key where to search
     * @throws Missing if string is not given
     */
    protected String getString(final String key) throws Missing {
        final String string = config.getString(key);
        if (string == null) {
            throw new Missing(key);
        } else {
            return resolveGlobalVariable(string);
        }
    }

    /********************************************************
     *                                                      *
     *                  STRING LISTS                        *
     *                                                      *
     ********************************************************/

    /**
     * Parse a list of strings from config file
     *
     * @param key where to search
     * @throws Missing if no list is not given
     */
    protected List<String> getStringList(final String key) throws Missing {
        final List<String> list = config.getStringList(key);
        if (list.isEmpty()) {
            throw new Missing(key);
        } else {
            return list.stream().map(this::resolveGlobalVariable).toList();
        }
    }

    /**
     * Parse a list of multiple strings, separated by ',' from config file
     *
     * @param key where to search
     * @throws Missing if no strings are given
     */
    protected List<String> getStrings(final String key) throws Missing {
        final List<String> list = new ArrayList<>();
        final String[] args = getString(key).split(",");
        for (final String arg : args) {
            final String argTrim = arg.trim();
            if (argTrim.length() != 0) {
                list.add(argTrim);
            }
        }
        return list;
    }

    /********************************************************
     *                                                      *
     *                       NUMBERS                        *
     *                                                      *
     ********************************************************/

    /**
     * Parse an integer from config file
     *
     * @param key where to search
     * @throws Missing if nothing is given
     * @throws Invalid if given string is not an integer
     */
    protected int getInt(final String key) throws Missing, Invalid {
        final String stringInt = this.getString(key);
        try {
            return Integer.parseInt(stringInt);
        } catch (final NumberFormatException e) {
            throw new Invalid(key, "Invalid number format for '" + stringInt + "'");
        }
    }

    /**
     * Parse a double from config file
     *
     * @param key where to search
     * @throws Missing if nothing is given
     * @throws Invalid if given string is not a double
     */
    protected double getDouble(final String key) throws Missing, Invalid {
        final String stringDouble = this.getString(key);
        try {
            return Double.parseDouble(stringDouble);
        } catch (final NumberFormatException e) {
            throw new Invalid(key, "Invalid number format for '" + stringDouble + "'");
        }
    }

    /**
     * Parse a long from config file
     *
     * @param key where to search
     * @throws Missing if nothing is given
     * @throws Invalid if given string is not a long
     */
    protected long getLong(final String key) throws Missing, Invalid {
        final String stringLong = this.getString(key);
        try {
            return Long.parseLong(stringLong);
        } catch (final NumberFormatException e) {
            throw new Invalid(key, "Invalid number format for '" + stringLong + "'");
        }
    }

    /********************************************************
     *                                                      *
     *                      OTHER                           *
     *                                                      *
     ********************************************************/

    /**
     * Parse a boolean from config file
     *
     * @param key where to search
     * @throws Missing if nothing is given
     * @throws Invalid if given string is not a boolean
     */
    protected boolean getBoolean(final String key) throws Missing, Invalid {
        final String stringBoolean = this.getString(key).trim();
        if ("true".equalsIgnoreCase(stringBoolean)) {
            return true;
        } else if ("false".equalsIgnoreCase(stringBoolean)) {
            return false;
        } else {
            throw new Invalid(key);
        }
    }

    /**
     * Parse an enum value from config file
     *
     * @param key      where to search
     * @param enumType type of the enum
     * @throws Missing if nothing is given
     * @throws Invalid if given string is not of given type
     */
    protected <T extends Enum<T>> T getEnum(final String key, final Class<T> enumType) throws Missing, Invalid {
        final String stringEnum = this.getString(key).toUpperCase(Locale.ROOT).replace(" ", "_");
        try {
            return Enum.valueOf(enumType, stringEnum);
        } catch (final IllegalArgumentException e) {
            throw new Invalid(key, "'" + stringEnum + "' isn't a " + enumType.getName());
        }
    }

    /**
     * Parse a material from config file
     *
     * @param key where to search
     * @throws Missing if nothing is given
     * @throws Invalid if given string is not a material
     */
    protected Material getMaterial(final String key) throws Missing, Invalid {
        if (key.trim().matches("\\d+")) {
            throw new Invalid(key, "Material numbers can no longer be supported! Please use the names instead.");
        }
        final String stringMaterial = this.getString(key);
        Material material = Material.matchMaterial(stringMaterial.replace(" ", "_"));
        if (material == null) {
            material = Material.matchMaterial(stringMaterial.replace(" ", "_"), true);
        }
        if (material == null) {
            throw new Invalid(key, "'" + stringMaterial + "' isn't a material");
        } else {
            return material;
        }
    }

    /**
     * Parse a list of events from config file
     *
     * @param key  where to search
     * @param pack configuration package of this file
     * @throws Missing if nothing is given
     * @throws Invalid if one of the events can't be found
     */
    protected List<EventID> getEvents(final String key, final QuestPackage pack) throws Missing, Invalid {
        final List<String> strings = getStrings(key);
        final List<EventID> events = new ArrayList<>(strings.size());
        for (final String string : strings) {
            try {
                events.add(new EventID(pack, string));
            } catch (final ObjectNotFoundException e) {
                throw new Invalid(key, e);
            }
        }
        return events;
    }

    /**
     * Parse a list of conditions from config file
     *
     * @param key  where to search
     * @param pack configuration package of this file
     * @throws Missing if nothing is given
     * @throws Invalid if one of the conditions can't be found
     */
    protected List<ConditionID> getConditions(final String key, final QuestPackage pack) throws Missing, Invalid {
        final List<String> strings = getStrings(key);
        final List<ConditionID> conditions = new ArrayList<>(strings.size());
        for (final String string : strings) {
            try {
                conditions.add(new ConditionID(pack, string));
            } catch (final ObjectNotFoundException e) {
                throw new Invalid(key, e);
            }
        }
        return conditions;
    }

    /**
     * A config setting which uses a given default value if not set
     *
     * @param <T> the type of the setting
     */
    protected abstract class DefaultSetting<T> {

        private T value;

        public DefaultSetting(final T defaultValue) throws Invalid {
            try {
                value = of();
            } catch (final Missing missing) {
                value = defaultValue;
            }
        }

        @SuppressWarnings("PMD.ShortMethodName")
        protected abstract T of() throws Missing, Invalid;

        public final T get() {
            return value;
        }
    }

    /**
     * A config setting which doesn't throw a Missing exception if not specified
     *
     * @param <T> the type of the setting
     */
    protected abstract class OptionalSetting<T> {

        private Optional<T> optional;

        public OptionalSetting() throws Invalid {
            try {
                optional = Optional.of(of());
            } catch (final Missing missing) {
                optional = Optional.empty();
            }
        }

        @SuppressWarnings("PMD.ShortMethodName")
        protected abstract T of() throws Missing, Invalid;

        public final Optional<T> get() {
            return optional;
        }
    }

    /**
     * Thrown when the config could not be loaded due to an error
     */
    public class InvalidSimpleConfigException extends InvalidConfigurationException {
        @Serial
        private static final long serialVersionUID = 5231741827329435199L;

        private final String message;

        private final String cause;

        public InvalidSimpleConfigException(final String cause) {
            super();
            this.cause = cause;
            this.message = "Could not load '" + name + "':" + this.cause;
        }

        public InvalidSimpleConfigException(final InvalidSimpleConfigException exception) {
            super();
            this.cause = "  Error in '" + exception.getName() + "':\n" + exception.cause;
            this.message = "Could not load '" + getName() + "'\n" + this.cause;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        public final String getName() {
            return name;
        }
    }

    /**
     * Thrown when a setting is missing
     */
    public class Missing extends InvalidSimpleConfigException {
        @Serial
        private static final long serialVersionUID = 1827433702663413827L;

        public Missing(final String missingSetting) {
            super(RPG_MENU_CONFIG_SETTING + missingSetting + " is missing!");
        }
    }

    /**
     * Thrown when a setting is invalid
     */
    public class Invalid extends InvalidSimpleConfigException {
        @Serial
        private static final long serialVersionUID = -4898301219445719212L;

        public Invalid(final String invalidSetting) {
            super(RPG_MENU_CONFIG_SETTING + invalidSetting + " is invalid!");
        }

        public Invalid(final String invalidSetting, final String cause) {
            super(RPG_MENU_CONFIG_SETTING + invalidSetting + " is invalid: " + cause);
        }

        public Invalid(final String invalidSetting, final Throwable cause) {
            super(RPG_MENU_CONFIG_SETTING + invalidSetting + " is invalid: " + cause.getMessage());
        }
    }
}
