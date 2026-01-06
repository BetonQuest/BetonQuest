package org.betonquest.betonquest.api.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;

/**
 * Interface for quest-events that are executed for a profile. It represents the player event as described in the
 * BetonQuest user documentation. It does not represent the playerless variant though, see {@link PlayerlessAction}.
 */
@FunctionalInterface
public interface PlayerAction extends PrimaryThreadEnforceable {

    /**
     * Executes the event.
     *
     * @param profile the {@link Profile} the event is executed for
     * @throws QuestException when the event execution fails
     */
    void execute(Profile profile) throws QuestException;
}
