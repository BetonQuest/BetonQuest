package org.betonquest.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class OpSudoEvent extends QuestEvent {

    private final Command[] commands;

    public OpSudoEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.getInstruction().trim();
        int index = string.indexOf("conditions:");
        index = index == -1 ? string.length() : index;
        final String[] rawCommands = string.substring(string.indexOf(' ') + 1, index).split("\\|");

        commands = new Command[rawCommands.length];
        for (int i = 0; i < rawCommands.length; i++) {
            commands[i] = new Command(rawCommands[i]);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final Profile profile) {
        final Player player = profile.getOnlineProfile().getOnlinePlayer();
        final boolean previousOp = player.isOp();
        try {
            player.setOp(true);
            for (final Command command : commands) {
                String com = command.command;
                for (final String var : command.variables) {
                    com = com.replace(var, BetonQuest.getInstance().getVariableValue(
                            instruction.getPackage().getPackagePath(), var, profile));
                }
                player.performCommand(com);
            }
        } finally {
            player.setOp(previousOp);
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
