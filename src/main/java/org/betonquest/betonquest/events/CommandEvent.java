package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Fires a list of commands for the player.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CommandEvent extends QuestEvent {

    private final Command[] commands;

    public CommandEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        final String string = instruction.getInstruction().trim();
        // Split commands by | but allow one to use \| to represent a pipe character
        final String[] rawCommands = Arrays.stream(string.substring(string.indexOf(' ') + 1).split("(?<!\\\\)\\|"))
                .map(s -> s.replace("\\|", "|"))
                .map(String::trim)
                .toArray(String[]::new);
        commands = new Command[rawCommands.length];
        for (int i = 0; i < rawCommands.length; i++) {
            commands[i] = new Command(rawCommands[i]);
        }
    }

    @Override
    protected Void execute(final String playerID) {
        for (final Command command : commands) {
            if (command.variables.isEmpty()) {
                // if there are no variables, this is a global command
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.command);
            } else {
                if (playerID == null) {
                    // this is a static command, run for each player
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        String com = command.command;
                        for (final String var : command.variables) {
                            com = com.replace(var, BetonQuest.getInstance().getVariableValue(
                                    instruction.getPackage().getName(), var, PlayerConverter.getID(player)));
                        }
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), com);
                    }
                } else {
                    final Player player = PlayerConverter.getPlayer(playerID);
                    if (player == null) {
                        // the player is offline, cannot resolve variables, at least replace %player%
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.command
                                .replaceAll("%player%", PlayerConverter.getName(playerID)));
                    } else {
                        // run the command for the single player
                        String com = command.command;
                        for (final String var : command.variables) {
                            com = com.replace(var, BetonQuest.getInstance().getVariableValue(
                                    instruction.getPackage().getName(), var, playerID));
                        }
                        final String finalCom = com;
                        Bukkit.getScheduler().callSyncMethod(BetonQuest.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCom));
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
    private static class Command {

        private final String command;
        private final List<String> variables;

        public Command(final String command) {
            this.command = command;
            variables = BetonQuest.resolveVariables(command);
        }

    }
}
