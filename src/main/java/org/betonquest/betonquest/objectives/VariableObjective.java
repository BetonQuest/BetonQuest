package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Creates variables based on what player is typing.
 * Will not run any events, will not check any conditions.
 * The only way to remove it is using "objective cancel" event.
 */
@SuppressWarnings("PMD.CommentRequired")
public class VariableObjective extends Objective implements Listener {

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
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID)) {
            return;
        }
        if (event.getMessage().matches("[a-zA-Z]*: .*")) {
            event.setCancelled(true);
            final String message = event.getMessage();
            final int index = message.indexOf(':');
            final String key = message.substring(0, index).trim();
            final String value = message.substring(index + 1).trim();
            ((VariableData) dataMap.get(playerID)).add(key, value);
            event.getPlayer().sendMessage("§2§l\u2713");
        }
    }

    /**
     * Stores specified string in this objective.
     *
     * @param playerID ID of the player
     * @param key      key of the variable
     * @param value    string to store
     * @return true if it was stored, false if the player doesn't have this
     * objective
     */
    public boolean store(final String playerID, final String key, final String value) {
        final VariableData data = (VariableData) dataMap.get(playerID);
        if (data == null) {
            return false;
        }
        data.add(key, value);
        return true;
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        final String value = ((VariableData) dataMap.get(playerID)).get(name);
        return value == null ? "" : value;
    }

    public static class VariableData extends ObjectiveData {

        private final Map<String, String> variables = new HashMap<>();

        public VariableData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            final String[] rawVariables = instruction.split("\n");
            for (final String rawVariable : rawVariables) {
                if (rawVariable.contains(":")) {
                    final String[] parts = rawVariable.split(":");
                    variables.put(parts[0], parts[1]);
                }
            }
        }

        public String get(final String key) {
            return variables.get(key.toLowerCase(Locale.ROOT));
        }

        public void add(final String key, final String value) {
            variables.put(key, value);
            update();
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            for (final Entry<String, String> entry : variables.entrySet()) {
                builder.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
            }
            return builder.toString().trim();
        }

    }

}
