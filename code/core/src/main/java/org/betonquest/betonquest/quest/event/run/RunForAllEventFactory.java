package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.condition.ConditionID;

import java.util.Collections;
import java.util.List;

/**
 * Create new {@link RunForAllEvent} from instruction.
 */
public class RunForAllEventFactory implements PlayerlessActionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create new {@link RunForAllEventFactory}.
     *
     * @param questTypeApi    the Quest Type API
     * @param profileProvider the profile provider instance
     */
    public RunForAllEventFactory(final QuestTypeApi questTypeApi, final ProfileProvider profileProvider) {
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        final Argument<List<ActionID>> events = instruction.parse(ActionID::new)
                .list().get("actions", Collections.emptyList());
        final Argument<List<ConditionID>> conditions = instruction.parse(ConditionID::new)
                .list().get("where", Collections.emptyList());
        return new RunForAllEvent(profileProvider::getOnlineProfiles, questTypeApi, events, conditions);
    }
}
