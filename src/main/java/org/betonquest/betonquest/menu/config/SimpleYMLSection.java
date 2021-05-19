package org.betonquest.betonquest.menu.config;

import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract class to help parsing of yml config files
 * <p>
 * Created on 06.09.2017
 *
 * @author Jonas Blocher
 */
public abstract class SimpleYMLSection {

    protected final ConfigurationSection config;
    protected final String name;

    public SimpleYMLSection(final String name, final ConfigurationSection config) throws InvalidConfigurationException {
        this.config = config;
        this.name = name;
        if (config == null || config.getKeys(false) == null || config.getKeys(false).size() == 0)
            throw new InvalidSimpleConfigException("RPGMenuConfig is invalid or empty!");
    }

    /**
     * Parse string from config file
     *
     * @param key where to search
     * @throws Missing if string is not given
     */
    protected String getString(final String key) throws Missing {
        final String s = config.getString(key);
        if (s == null) {
            throw new Missing(key);
        } else {
            return s;
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
        if (list == null || list.size() == 0) {
            throw new Missing(key);
        } else {
            return list;
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
        for (String arg : args) {
            arg = arg.trim();
            if (arg.length() != 0) {
                list.add(arg);
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
        final String s = this.getString(key);
        try {
            return Integer.parseInt(s);
        } catch (final NumberFormatException e) {
            throw new Invalid(key, "Invalid number format for '" + s + "'");
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
        final String s = this.getString(key);
        try {
            return Double.parseDouble(s);
        } catch (final NumberFormatException e) {
            throw new Invalid(key, "Invalid number format for '" + s + "'");
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
        final String s = this.getString(key);
        try {
            return Long.parseLong(s);
        } catch (final NumberFormatException e) {
            throw new Invalid(key, "Invalid number format for '" + s + "'");
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
        final String s = this.getString(key);
        if (s.trim().equalsIgnoreCase("true")) return true;
        else if (s.trim().equalsIgnoreCase("false")) return false;
        else throw new Invalid(key);
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
        final String s = this.getString(key).toUpperCase().replace(" ", "_");
        try {
            return Enum.valueOf(enumType, s);
        } catch (final IllegalArgumentException e) {
            throw new Invalid(key, "'" + s + "' isn't a " + enumType.getName());
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
        final String s = this.getString(key);
        if (key.trim().matches("\\d+")) {
            throw new Invalid(key, "Material numbers can no longer be supported! Please use the names instead.");
        }
        Material m;
        try {
            m = Material.matchMaterial(s.replace(" ", "_"));
            if (m == null) {
                m = Material.matchMaterial(s.replace(" ", "_"), true);
            }
        } catch (final LinkageError error) {
            //pre 1.13
            m = Material.getMaterial(s.toUpperCase().replace(" ", "_"));
        }
        if (m == null) {
            throw new Invalid(key, "'" + s + "' isn't a material");
        } else {
            return m;
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
    protected List<EventID> getEvents(final String key, final ConfigPackage pack) throws Missing, Invalid {
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
    protected List<ConditionID> getConditions(final String key, final ConfigPackage pack) throws Missing, Invalid {
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

        protected abstract T of() throws Missing, Invalid;

        public final Optional<T> get() {
            return optional;
        }
    }

    /**
     * Thrown when the config could not be loaded due to an error
     */
    public class InvalidSimpleConfigException extends InvalidConfigurationException {

        private static final long serialVersionUID = 5231741827329435199L;
        private final String message;
        private final String cause;

        public InvalidSimpleConfigException(final String cause) {
            super();
            this.cause = "  §c" + cause;
            this.message = "§4Could not load §7" + getName() + "§4:\n" + this.cause;
        }

        public InvalidSimpleConfigException(final InvalidSimpleConfigException e) {
            super();
            this.cause = "  §4Error in §7" + e.getName() + "§4:\n" + e.cause;
            this.message = "Could not load §7" + getName() + "§4\n" + this.cause;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Thrown when a setting is missing
     */
    public class Missing extends InvalidSimpleConfigException {

        private static final long serialVersionUID = 1827433702663413827L;

        public Missing(final String missingSetting) {
            super("RPGMenuConfig setting §7" + missingSetting + "§c is missing!");
        }
    }

    /**
     * Thrown when a setting is invalid
     */
    public class Invalid extends InvalidSimpleConfigException {

        private static final long serialVersionUID = -4898301219445719212L;

        public Invalid(final String invalidSetting) {
            super("RPGMenuConfig setting §7" + invalidSetting + "§c is invalid!");
        }

        public Invalid(final String invalidSetting, final String cause) {
            super("RPGMenuConfig setting §7" + invalidSetting + "§c is invalid: §7" + cause);
        }

        public Invalid(final String invalidSetting, final Throwable cause) {
            super("RPGMenuConfig setting §7" + invalidSetting + "§c is invalid: §7" + cause.getMessage());
        }
    }
}
