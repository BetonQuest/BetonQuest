package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.util.PlayerConverter;

import java.util.List;

/**
 * Create new {@link RunForAllEvent} from instruction.
 */
public class RunForAllEventFactory implements StaticEventFactory {

    /**
     * Create new {@link RunForAllEventFactory}.
     */
    public RunForAllEventFactory() {
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final List<EventID> events = instruction.getIDList(instruction.getOptional("events"), EventID::new);
        final List<ConditionID> conditions = instruction.getIDList(instruction.getOptional("where"), ConditionID::new);
        return new RunForAllEvent(PlayerConverter::getOnlineProfiles, events, conditions);
    }
}
