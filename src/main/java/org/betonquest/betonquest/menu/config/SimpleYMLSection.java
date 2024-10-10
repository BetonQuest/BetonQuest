package org.betonquest.betonquest.menu.config;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.VariableBoolean;
import org.betonquest.betonquest.instruction.variable.VariableEnum;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

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
        if (config.getKeys(false).isEmpty()) {
            throw new InvalidSimpleConfigException("RPGMenuConfig is invalid or empty!");
        }
    }

    private String getRawString(final String key) throws Missing {
        final String string = config.getString(key);
        if (string == null) {
            throw new Missing(key);
        } else {
            return string;
        }
    }

    /**
     * Parse string from config file
     *
     * @param key where to search
     * @return the {@link VariableString} from the config
     * @throws Missing                   if string is not given
     * @throws InstructionParseException If the {@link VariableString} could not be created
     */
    protected final VariableString getString(final String key) throws Missing, InstructionParseException {
        return new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, getRawString(key));
    }

    /**
     * Parse a list of strings from config file
     *
     * @param key where to search
     * @return the list of {@link VariableString} from the config
     * @throws Missing                   if no list is not given
     * @throws InstructionParseException If one {@link VariableString} could not be created
     */
    protected final List<VariableString> getStringList(final String key) throws Missing, InstructionParseException {
        final List<String> stringList = config.getStringList(key);
        if (stringList.isEmpty()) {
            throw new Missing(key);
        } else {
            final List<VariableString> list = new ArrayList<>();
            for (final String string : stringList) {
                list.add(new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, string));
            }
            return list;
        }
    }

    /**
     * Parse a list of multiple strings, separated by ',' from config file
     *
     * @param key where to search
     * @return the list of {@link VariableString} from the config
     * @throws Missing                   if no strings are given
     * @throws InstructionParseException If one {@link VariableString} could not be created
     */
    protected final List<VariableString> getStrings(final String key) throws Missing, InstructionParseException {
        final List<VariableString> list = new ArrayList<>();
        final String string = getRawString(key);
        final String[] args = string.split(",");
        for (final String arg : args) {
            final String argTrim = arg.trim();
            if (!argTrim.isEmpty()) {
                list.add(new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, argTrim));
            }
        }
        return list;
    }

    /**
     * Parse an integer from config file
     *
     * @param key where to search
     * @return the {@link VariableNumber} from the config
     * @throws Missing                   if nothing is given
     * @throws InstructionParseException If the {@link VariableNumber} could not be created
     */
    protected final VariableNumber getNumber(final String key) throws Missing, InstructionParseException {
        final String stringInt = getRawString(key);
        return new VariableNumber(BetonQuest.getInstance().getVariableProcessor(), pack, stringInt);
    }

    /**
     * Parse a boolean from config file
     *
     * @param key where to search
     * @return the {@link VariableBoolean} from the config
     * @throws Missing                   if nothing is given
     * @throws InstructionParseException If the {@link VariableBoolean} could not be created
     */
    protected final VariableBoolean getBoolean(final String key) throws Missing, InstructionParseException {
        final String stringBoolean = getRawString(key).trim();
        return new VariableBoolean(BetonQuest.getInstance().getVariableProcessor(), pack, stringBoolean);
    }

    /**
     * Parse an enum value from config file
     *
     * @param key      where to search
     * @param enumType type of the enum
     * @param <T>      the type of the enum
     * @return the {@link VariableEnum} from the config
     * @throws Missing                   if nothing is given
     * @throws InstructionParseException If the {@link VariableEnum} could not be created
     */
    protected <T extends Enum<T>> VariableEnum<T> getEnum(final String key, final Class<T> enumType) throws Missing, InstructionParseException {
        final String enumString = getRawString(key);
        return new VariableEnum<>(BetonQuest.getInstance().getVariableProcessor(), pack, enumString, enumType);
    }

    /**
     * Parse a material from config file
     *
     * @param key where to search
     * @throws Missing if nothing is given
     * @throws Invalid if given string is not a material
     */
    protected final Material getMaterial(final String key) throws Missing, Invalid {
        if (key.trim().matches("\\d+")) {
            throw new Invalid(key, "Material numbers can no longer be supported! Please use the names instead.");
        }
        final String stringMaterial = this.getRawString(key);
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
    protected final List<EventID> getEvents(final String key, final QuestPackage pack) throws Missing, Invalid {
        final List<VariableString> strings;
        try {
            strings = getStrings(key);
        } catch (final InstructionParseException e) {
            throw new Invalid(key, e);
        }
        final List<EventID> events = new ArrayList<>(strings.size());
        for (final VariableString string : strings) {
            try {
                events.add(new EventID(pack, string.getValue(null)));
            } catch (final ObjectNotFoundException | QuestRuntimeException e) {
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
    protected final List<ConditionID> getConditions(final String key, final QuestPackage pack) throws Missing, Invalid {
        final List<VariableString> strings;
        try {
            strings = getStrings(key);
        } catch (final InstructionParseException e) {
            throw new Invalid(key, e);
        }
        final List<ConditionID> conditions = new ArrayList<>(strings.size());
        for (final VariableString string : strings) {
            try {
                conditions.add(new ConditionID(pack, string.getValue(null)));
            } catch (final ObjectNotFoundException | QuestRuntimeException e) {
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

        private final T value;

        @SuppressWarnings({"PMD.ConstructorCallsOverridableMethod", "PMD.LocalVariableCouldBeFinal"})
        public DefaultSetting(final T defaultValue) throws Invalid {
            T configValue;
            try {
                configValue = of();
            } catch (final Missing missing) {
                configValue = defaultValue;
            }
            value = configValue;
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
        @Nullable
        private final T optional;

        @SuppressWarnings({"PMD.ConstructorCallsOverridableMethod", "PMD.LocalVariableCouldBeFinal"})
        public OptionalSetting() throws Invalid {
            T optionalSetting;
            try {
                optionalSetting = of();
            } catch (final Missing missing) {
                optionalSetting = null;
            }
            optional = optionalSetting;
        }

        @SuppressWarnings("PMD.ShortMethodName")
        protected abstract T of() throws Missing, Invalid;

        @Nullable
        public final T get() {
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

        @Nullable
        private final String cause;

        public InvalidSimpleConfigException(@Nullable final String cause) {
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
