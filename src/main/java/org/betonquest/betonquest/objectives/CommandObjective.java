package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

/**
 * Requires the player to execute a specific command.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CommandObjective extends Objective implements Listener {

    private final String command;
    private final List<String> commandVariables;

    public CommandObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        command = instruction.next().replaceAll("_", " ");
        commandVariables = BetonQuest.resolveVariables(command);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID) && checkConditions(playerID)) {
            final String commandExecuted = event.getMessage();
            final String replaceCommand = getCommandWithVariablesReplaced(playerID);
            if (commandExecuted.startsWith(replaceCommand)) {
                completeObjective(playerID);
            }
        }
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
    public String getProperty(final String name, final String playerID) {
        return "";
    }

    private String getCommandWithVariablesReplaced(final String playerID) {
        String replaceCommand = command;
        for (final String variable : commandVariables) {
            replaceCommand = replaceCommand.replace(
                    variable,
                    BetonQuest.getInstance().getVariableValue(instruction.getPackage().getName(), variable, playerID)
            );
        }
        return replaceCommand;
    }

}
