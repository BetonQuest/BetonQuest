package org.betonquest.betonquest.quest.event.sudo;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Forces the player to run commands.
 */
public class OpSudoEvent implements Event {

    /**
     * The commands to run.
     */
    private final Command[] commands;

    /**
     * The quest package this event is in.
     */
    private final QuestPackage questPackage;

    /**
     * Creates a new SudoEvent.
     *
     * @param commands     the commands to run
     * @param questPackage the quest package this event is in
     */
    public OpSudoEvent(final Command[] commands, final QuestPackage questPackage) {
        this.commands = Arrays.copyOf(commands, commands.length);
        this.questPackage = questPackage;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final boolean previousOp = player.isOp();
        try {
            player.setOp(true);
            for (final Command command : commands) {
                player.performCommand(resolveVariables(profile, command));
            }
        } finally {
            player.setOp(previousOp);
        }
    }

    /**
     * Resolves the variables in the command.
     *
     * @param profile the profile to resolve the variables for
     * @param command the command to resolve the variables in
     * @return the command with the variables resolved
     */
    private String resolveVariables(final Profile profile, final Command command) {
        String com = command.command();
        for (final String var : command.variables()) {
            com = com.replace(var, BetonQuest.getInstance().getVariableValue(
                    questPackage.getQuestPath(), var, profile));
        }
        return com;
    }
}
