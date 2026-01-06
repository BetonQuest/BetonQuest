package org.betonquest.betonquest.api.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;

/**
 * Interface for quest-actions that are executed for a profile. It represents the player action as described in the
 * BetonQuest user documentation. It does not represent the playerless variant though, see {@link PlayerlessAction}.
 */
@FunctionalInterface
public interface PlayerAction extends PrimaryThreadEnforceable {

    /**
     * Executes the action.
     *
     * @param profile the {@link Profile} the action is executed for
     * @throws QuestException when the action execution fails
     */
    void execute(Profile profile) throws QuestException;
}
