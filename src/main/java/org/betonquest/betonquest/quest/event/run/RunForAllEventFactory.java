package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.BetonQuestAPI;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.PlayerConverter;

import java.util.List;

/**
 * Create new {@link RunForAllEvent} from instruction.
 */
public class RunForAllEventFactory implements StaticEventFactory {

    /**
     * BetonQuest API.
     */
    private final BetonQuestAPI questAPI;

    /**
     * Create new {@link RunForAllEventFactory}.
     *
     * @param questAPI the BetonQuest API
     */
    public RunForAllEventFactory(final BetonQuestAPI questAPI) {
        this.questAPI = questAPI;
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final List<EventID> events = instruction.getList(instruction.getOptional("events"), instruction::getEvent);
        final List<ConditionID> conditions = instruction.getList(instruction.getOptional("where"), instruction::getCondition);
        return new RunForAllEvent(PlayerConverter::getOnlineProfiles, questAPI, events, conditions);
    }
}
