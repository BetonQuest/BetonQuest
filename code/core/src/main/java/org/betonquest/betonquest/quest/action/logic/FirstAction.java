package org.betonquest.betonquest.quest.action.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.nullable.NullableAction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The First action. Similar to the folder, except it runs linearly through a list of actions and
 * stops after the first one succeeds. This is intended to be used with condition: syntax in actions.
 */
public class FirstAction implements NullableAction {

    /**
     * The actions to run.
     */
    private final Argument<List<ActionIdentifier>> actions;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Makes a new first action.
     *
     * @param actions      A list of actions to execute in order.
     * @param questTypeApi the Quest Type API
     */
    public FirstAction(final Argument<List<ActionIdentifier>> actions, final QuestTypeApi questTypeApi) {
        this.actions = actions;
        this.questTypeApi = questTypeApi;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        for (final ActionIdentifier action : actions.getValue(profile)) {
            if (questTypeApi.action(profile, action)) {
                break;
            }
        }
    }
}
