package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;

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
        int index = string.indexOf("conditions:");
        index = index == -1 ? string.length() : index;
        final String command = (String) string.subSequence(0, index);
        // Split commands by | but allow one to use \| to represent a pipe character
        final String[] rawCommands = Arrays.stream(command.substring(command.indexOf(' ') + 1).split("(?<!\\\\)\\|"))
                .map(s -> s.replace("\\|", "|"))
                .map(String::trim)
                .toArray(String[]::new);
        commands = new Command[rawCommands.length];
        for (int i = 0; i < rawCommands.length; i++) {
            commands[i] = new Command(rawCommands[i]);
        }
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    @Override
    protected Void execute(final Profile profile) {
        for (final Command command : commands) {
            if (command.variables.isEmpty()) {
                // if there are no variables, this is a global command
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.command);
            } else {
                if (profile == null) {
                    // this is a static command, run for each player
                    for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                        String com = command.command;
                        for (final String var : command.variables) {
                            com = com.replace(var, BetonQuest.getInstance().getVariableValue(
                                    instruction.getPackage().getPackagePath(), var, onlineProfile));
                        }
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), com);
                    }
                } else {
                    if (profile.getPlayer().isEmpty()) {
                        // the player is offline, cannot resolve variables, at least replace %player%
                        final String name = profile.getOfflinePlayer().getName();
                        if (name == null) {
                            // this should never happen, but just in case
                            continue;
                        }
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.command
                                .replaceAll("%player%", name));
                    } else {
                        // run the command for the single player
                        String com = command.command;
                        for (final String var : command.variables) {
                            com = com.replace(var, BetonQuest.getInstance().getVariableValue(
                                    instruction.getPackage().getPackagePath(), var, profile));
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
