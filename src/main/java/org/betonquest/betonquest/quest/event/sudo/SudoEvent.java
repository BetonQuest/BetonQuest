package org.betonquest.betonquest.quest.event.sudo;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

/**
 * Forces the player to run commands.
 */
public class SudoEvent implements Event {

    /**
     * The quest package this event is in.
     */
    private final QuestPackage questPackage;

    /**
     * The commands to run.
     */
    private final Command[] commands;

    /**
     * Creates a new SudoEvent.
     *
     * @param questPackage the quest package this event is in
     * @param commands     the commands to run
     */
    public SudoEvent(final QuestPackage questPackage, final Command... commands) {
        this.questPackage = questPackage;
        this.commands = commands.clone();
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        for (final Command command : commands) {
            player.performCommand(resolveVariables(profile, command));
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
