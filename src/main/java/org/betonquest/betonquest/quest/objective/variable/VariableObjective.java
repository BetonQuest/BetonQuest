package org.betonquest.betonquest.quest.objective.variable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
 * Creates variables based on what player is typing.
 * Will not run any events, will not check any conditions.
 * The only way to remove it is using "objective cancel" event.
 */
public class VariableObjective extends Objective implements Listener {
    /**
     * Pattern to match the chat variable format.
     */
    public static final Pattern CHAT_VARIABLE_PATTERN = Pattern.compile("^(?<key>[a-zA-Z]+): (?<value>.+)$");

    /**
     * Deactivates the chat input for this objective.
     */
    private final boolean noChat;

    /**
     * Creates a new VariableObjective instance.
     *
     * @param instruction the instruction that created this objective
     * @param noChat      whether to disable chat input for this objective
     * @throws QuestException if there is an error in the instruction
     */
    public VariableObjective(final Instruction instruction, final boolean noChat) throws QuestException {
        super(instruction, VariableData.class);
        this.noChat = noChat;
    }

    @Override
    public void start() {
        if (!noChat) {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }
    }

    @Override
    public void stop() {
        if (!noChat) {
            HandlerList.unregisterAll(this);
        }
    }

    /**
     * Handles chat input.
     *
     * @param event the AsyncPlayerChatEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(onlineProfile)) {
            return;
        }
        final Matcher chatVariableMatcher = CHAT_VARIABLE_PATTERN.matcher(event.getMessage());
        if (chatVariableMatcher.matches()) {
            event.setCancelled(true);
            final String key = chatVariableMatcher.group("key").toLowerCase(Locale.ROOT);
            final String value = chatVariableMatcher.group("value");
            getVariableData(onlineProfile).add(key, value);
            event.getPlayer().sendMessage(Component.text("✓", NamedTextColor.DARK_GREEN, TextDecoration.BOLD));
        }
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
            getVariableData(profile).add(key.toLowerCase(Locale.ROOT), value);
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
        final String key = name.toLowerCase(Locale.ROOT);
        final String value = getVariableData(profile).get(key);
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

    private VariableData getVariableData(final Profile profile) {
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
         * Constructs a mew {@link org.betonquest.betonquest.api.Objective.ObjectiveData} for this objective.
         *
         * @param instruction the data of the objective
         * @param profile     the profile of the player
         * @param objID       the ID of the objective
         */
        public VariableData(final String instruction, final Profile profile, final String objID) {
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
            return variables.get(key);
        }

        /**
         * Adds a new variable to the map.
         *
         * @param key   key of the variable
         * @param value value of the variable
         */
        public void add(final String key, @Nullable final String value) {
            if (value == null || value.isEmpty()) {
                variables.remove(key);
            } else {
                variables.put(key, value);
            }
            update();
        }

        @Override
        public String toString() {
            return serializeData(variables);
        }
    }
}
