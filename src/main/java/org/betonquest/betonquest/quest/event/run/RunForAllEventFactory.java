package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.List;

/**
 * Create new {@link RunForAllEvent} from instruction.
 */
public class RunForAllEventFactory implements StaticEventFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create new {@link RunForAllEventFactory}.
     *
     * @param questTypeAPI    the Quest Type API
     * @param profileProvider the profile provider instance
     */
    public RunForAllEventFactory(final QuestTypeAPI questTypeAPI, final ProfileProvider profileProvider) {
        this.questTypeAPI = questTypeAPI;
        this.profileProvider = profileProvider;
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final List<EventID> events = instruction.getIDList(instruction.getOptional("events"), EventID::new);
        final List<ConditionID> conditions = instruction.getIDList(instruction.getOptional("where"), ConditionID::new);
        return new RunForAllEvent(profileProvider::getOnlineProfiles, questTypeAPI, events, conditions);
    }
}
