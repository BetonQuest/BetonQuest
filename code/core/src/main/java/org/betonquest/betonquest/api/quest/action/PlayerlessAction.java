package org.betonquest.betonquest.api.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;

/**
 * Interface for playerless quest-actions.
 * It represents the playerless action as described in the BetonQuest user documentation.
 * They may act on all players, only online player or even no player at all; this is an implementation detail.
 * For the normal action variant see {@link PlayerAction}.
 */
@FunctionalInterface
public interface PlayerlessAction extends PrimaryThreadEnforceable {

    /**
     * Executes the playerless action.
     *
     * @throws QuestException when the action execution fails
     */
    void execute() throws QuestException;
}
