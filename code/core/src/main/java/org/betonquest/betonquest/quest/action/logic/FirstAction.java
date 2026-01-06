package org.betonquest.betonquest.quest.action.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
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
    private final Argument<List<ActionID>> events;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Makes a new first event.
     *
     * @param eventIDList  A list of events to execute in order.
     * @param questTypeApi the Quest Type API
     */
    public FirstAction(final Argument<List<ActionID>> eventIDList, final QuestTypeApi questTypeApi) {
        events = eventIDList;
        this.questTypeApi = questTypeApi;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        for (final ActionID event : events.getValue(profile)) {
            if (questTypeApi.action(profile, event)) {
                break;
            }
        }
    }
}
