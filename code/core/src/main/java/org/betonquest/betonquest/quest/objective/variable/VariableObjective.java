package org.betonquest.betonquest.quest.objective.variable;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveData;
import org.betonquest.betonquest.api.quest.objective.ObjectiveDataFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores {@code key:value} pairs for a player.
 * Will not run any events, will not check any conditions.
 * The only way to remove it is using "objective cancel" event.
 */
public class VariableObjective extends Objective {

    /**
     * The Factory for the Variable Data.
     */
    private static final ObjectiveDataFactory VARIABLE_FACTORY = VariableData::new;

    /**
     * Creates a new VariableObjective instance.
     *
     * @param instruction the instruction that created this objective
     * @throws QuestException if there is an error in the instruction
     */
    public VariableObjective(final Instruction instruction) throws QuestException {
        super(instruction, VARIABLE_FACTORY);
    }

    /**
     * Stores specified string in this objective.
     *
     * @param profile the {@link Profile} of the player
     * @param key     key of the variable
     * @param value   string to store
     * @return true if it was stored, false if the player doesn't have this
     * objective
     */
    public boolean store(final Profile profile, final String key, @Nullable final String value) {
        if (containsPlayer(profile)) {
            getVariableData(profile).add(key, value);
            return true;
        }
        return false;
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        final String value = getVariableData(profile).get(name);
        return value == null ? "" : value;
    }

    /**
     * Gets the currently stored variables.
     *
     * @param profile the {@link Profile} of the player
     * @return the profile's variables as unmodifiable map; or null if the objective is not active for the profile
     */
    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    @Nullable
    public Map<String, String> getProperties(final Profile profile) {
        if (containsPlayer(profile)) {
            return Collections.unmodifiableMap(getVariableData(profile).variables);
        }
        return null;
    }

    /* default */ VariableData getVariableData(final Profile profile) {
        return Objects.requireNonNull((VariableData) dataMap.get(profile));
    }

    /**
     * The data class for the {@link VariableObjective}.
     */
    public static class VariableData extends ObjectiveData {
        /**
         * Pattern to split the serialized data into key-value pairs.
         */
        private static final Pattern VARIABLE_SPLIT_PATTERN = Pattern.compile("(\n)(?=(?:(?:[^\n]*?[^\\\\\n])?(?:\\\\\\\\)+?|[^\n]*?[^\\\\\n]):)");

        /**
         * Pattern to match the key-value pair in the serialized data.
         */
        private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("^(?<key>(?:[^\n]*?[^\\\\\n])?(?:\\\\\\\\)+?|[^\n]*?[^\\\\\n]):(?<value>.*)$", Pattern.DOTALL);

        /**
         * Pattern to match escaped characters in the serialized data.
         */
        private static final Pattern DESERIALIZE_PATTERN = Pattern.compile("\\\\(?<escaped>.)");

        /**
         * The map containing the variables.
         */
        private final Map<String, String> variables;

        /**
         * Constructs a mew {@link ObjectiveData} for this objective.
         *
         * @param instruction the data of the objective
         * @param profile     the profile of the player
         * @param objID       the ID of the objective
         */
        public VariableData(final String instruction, final Profile profile, final ObjectiveID objID) {
            super(instruction, profile, objID);
            variables = deserializeData(instruction);
        }

        /**
         * The static serialize method for the data.
         *
         * @param values the map of variables to serialize
         * @return the serialized string
         */
        public static String serializeData(final Map<String, String> values) {
            final StringBuilder builder = new StringBuilder();
            for (final Entry<String, String> entry : values.entrySet()) {
                builder
                        .append(serializePart(entry.getKey()))
                        .append(':')
                        .append(serializePart(entry.getValue()))
                        .append('\n');
            }
            if (!builder.isEmpty()) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString();
        }

        private static String serializePart(final String part) {
            return part
                    .replace("\\", "\\\\")
                    .replace(":", "\\:")
                    .replace("\n", "\\n");
        }

        /**
         * The static deserialize method for the data.
         *
         * @param data the data to deserialize
         * @return the deserialized map of variables
         */
        public static Map<String, String> deserializeData(final String data) {
            final Map<String, String> variables = new LinkedHashMap<>();
            final String[] rawVariables = VARIABLE_SPLIT_PATTERN.split(data);
            for (final String rawVariable : rawVariables) {
                final Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(rawVariable);
                if (keyValueMatcher.matches()) {
                    final String key = deserializePart(keyValueMatcher.group("key"));
                    final String value = deserializePart(keyValueMatcher.group("value"));
                    variables.put(key, value);
                }
            }
            return variables;
        }

        private static String deserializePart(final String part) {
            final Matcher matcher = DESERIALIZE_PATTERN.matcher(part);
            final StringBuilder deserialized = new StringBuilder(part.length());
            while (matcher.find()) {
                switch (matcher.group("escaped")) {
                    case "n" -> matcher.appendReplacement(deserialized, "\n");
                    case ":", "\\" -> matcher.appendReplacement(deserialized, "${escaped}");
                    default -> matcher.appendReplacement(deserialized, "\\\\${escaped}");
                }
            }
            return matcher.appendTail(deserialized).toString();
        }

        /**
         * Returns the value of the variable with the specified key.
         *
         * @param key key of the variable
         * @return value of the variable or null if it doesn't exist
         */
        @Nullable
        public String get(final String key) {
            return variables.get(key.toLowerCase(Locale.ROOT));
        }

        /**
         * Adds a new variable to the map.
         *
         * @param key   key of the variable
         * @param value value of the variable
         */
        public void add(final String key, @Nullable final String value) {
            if (value == null || value.isEmpty()) {
                variables.remove(key.toLowerCase(Locale.ROOT));
            } else {
                variables.put(key.toLowerCase(Locale.ROOT), value);
            }
            update();
        }

        @Override
        public String toString() {
            return serializeData(variables);
        }
    }
}
