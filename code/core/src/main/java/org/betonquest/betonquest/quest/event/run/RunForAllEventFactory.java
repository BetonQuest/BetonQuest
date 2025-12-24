package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;

import java.util.Collections;
import java.util.List;

/**
 * Create new {@link RunForAllEvent} from instruction.
 */
public class RunForAllEventFactory implements PlayerlessEventFactory {

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
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        final Variable<List<EventID>> events = instruction.parse(EventID::new)
                .getList("events", Collections.emptyList());
        final Variable<List<ConditionID>> conditions = instruction.parse(ConditionID::new)
                .getList("where", Collections.emptyList());
        return new RunForAllEvent(profileProvider::getOnlineProfiles, questTypeApi, events, conditions);
    }
}
