package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Catches the next chat message of a player.
 */
public class ChatObjective extends Objective implements Listener {
    /**
     * The amount of parts to parse for the ObjectiveID and key.
     */
    private static final int OBJECTIVE_FORMAT_LENGTH = 2;

    /**
     * Custom {@link BetonQuestLogger} for this class.
     */
    private final BetonQuestLogger log;

    /**
     * If the chat event should be cancelled.
     */
    private final boolean cancel;

    /**
     * A {@link VariableObjective} and key where the chat message should be stored.
     */
    @Nullable
    private final Map.Entry<ObjectiveID, String> variable;

    /**
     * Create a new Chat Objective from an Instruction string.
     *
     * @param instruction the user provided instruction string
     * @throws InstructionParseException when the Instruction is invalid or the VariableObjective does not exist
     */
    public ChatObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        cancel = instruction.hasArgument("cancel");
        variable = parseVariable(instruction.getOptional("variable"));
    }

    @Nullable
    private Map.Entry<ObjectiveID, String> parseVariable(@Nullable final String variableString) throws InstructionParseException {
        if (variableString == null) {
            return null;
        }
        final String[] split = variableString.split("#");
        if (split.length != OBJECTIVE_FORMAT_LENGTH) {
            throw new InstructionParseException("Invalid variable '" + variableString + "' does not contain ID and Key!");
        }
        final ObjectiveID objectiveID;
        try {
            objectiveID = new ObjectiveID(instruction.getPackage(), split[0]);
        } catch (final ObjectNotFoundException exception) {
            throw new InstructionParseException("Variable '" + split[0] + "' does not exist!", exception);
        }
        return Map.entry(objectiveID, split[1]);
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }

    /**
     * Intercepts a player message to eventually store it.
     *
     * @param event the event to listen to
     */
    @EventHandler(ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }

        if (cancel) {
            event.setCancelled(true);
        }

        if (variable != null) {
            if (BetonQuest.getInstance().getObjective(variable.getKey()) instanceof final VariableObjective variableObjective) {
                if (!variableObjective.store(onlineProfile, variable.getValue(), event.getMessage())) {
                    log.warn("Can't store value in variable objective '" + variable.getKey().getFullID()
                            + "' because it is not active for the player!");
                }
            } else {
                log.warn("Can't store value in variable objective '" + variable.getKey().getFullID()
                        + "' because it is not an variable objective!");
            }
        }

        completeObjective(onlineProfile);
    }
}
