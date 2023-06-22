package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.bukkit.command.SilentCommandSender;
import org.betonquest.betonquest.api.bukkit.command.SilentConsoleCommandSender;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Fires a list of commands for the player.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CommandEvent extends QuestEvent {
    private final CommandSender silentSender;

    private final VariableString[] commands;

    public CommandEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.silentSender = new SilentConsoleCommandSender(BetonQuest.getInstance().getLoggerFactory().create(SilentCommandSender.class, "CommandEvent"), Bukkit.getConsoleSender());
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
        commands = new VariableString[rawCommands.length];
        for (int i = 0; i < rawCommands.length; i++) {
            commands[i] = new VariableString(instruction.getPackage(), rawCommands[i]);
        }
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    @Override
    protected Void execute(final Profile profile) {
        for (final VariableString variableCommand : commands) {
            if (!variableCommand.containsVariables()) {
                final String command = variableCommand.getString(profile);
                Bukkit.getServer().dispatchCommand(silentSender, command);
                continue;
            }
            if (profile == null) {
                for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final String command = variableCommand.getString(onlineProfile);
                    Bukkit.getServer().dispatchCommand(silentSender, command);
                }
                continue;
            }
            if (profile.getOnlineProfile().isPresent()) {
                final String command = variableCommand.getString(profile);
                Bukkit.getServer().dispatchCommand(silentSender, command);
                continue;
            }
            final String name = profile.getPlayer().getName();
            if (name == null) {
                continue;
            }
            final String command = variableCommand.getString(profile).replaceAll("%player%", name);
            Bukkit.getServer().dispatchCommand(silentSender, command);
        }
        return null;
    }
}
