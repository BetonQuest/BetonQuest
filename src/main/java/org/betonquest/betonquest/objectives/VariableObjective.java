package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates variables based on what player is typing.
 * Will not run any events, will not check any conditions.
 * The only way to remove it is using "objective cancel" event.
 */
@SuppressWarnings("PMD.CommentRequired")
public class VariableObjective extends Objective implements Listener {

    public static final Pattern CHAT_VARIABLE_PATTERN = Pattern.compile("^(?<key>[a-zA-Z]+): (?<value>.+)$");

    private final boolean noChat;

    public VariableObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = VariableData.class;
        noChat = instruction.hasArgument("no-chat");
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

    @EventHandler(ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(onlineProfile)) {
            return;
        }
        final Matcher chatVariableMatcher = CHAT_VARIABLE_PATTERN.matcher(event.getMessage());
        if (chatVariableMatcher.matches()) {
            event.setCancelled(true);
            final String key = chatVariableMatcher.group("key").toLowerCase(Locale.ROOT);
            final String value = chatVariableMatcher.group("value");
            ((VariableData) dataMap.get(onlineProfile)).add(key, value);
            event.getPlayer().sendMessage("§2§l\u2713"); // send checkmark
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
    public boolean store(final Profile profile, final String key, final String value) {
        final VariableData data = (VariableData) dataMap.get(profile);
        if (data == null) {
            return false;
        }
        data.add(key.toLowerCase(Locale.ROOT), value);
        return true;
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        final String key = name.toLowerCase(Locale.ROOT);
        final String value = ((VariableData) dataMap.get(profile)).get(key);
        return value == null ? "" : value;
    }

    public static class VariableData extends ObjectiveData {

        private static final Pattern VARIABLE_SPLIT_PATTERN = Pattern.compile("(\n)(?=(?:(?:[^\n]*?[^\\\\\n])?(?:\\\\\\\\)+?|[^\n]*?[^\\\\\n]):)");

        private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("^(?<key>(?:[^\n]*?[^\\\\\n])?(?:\\\\\\\\)+?|[^\n]*?[^\\\\\n]):(?<value>.*)$", Pattern.DOTALL);

        private final Map<String, String> variables;

        public VariableData(final String instruction, final Profile profile, final String objID) {
            super(instruction, profile, objID);
            variables = deserializeData(instruction);
        }

        public String get(final String key) {
            return variables.get(key);
        }

        public void add(final String key, final String value) {
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
            return part
                    .replace("\\n", "\n")
                    .replace("\\:", ":")
                    .replace("\\\\", "\\");
        }
    }
}
