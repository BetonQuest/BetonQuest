package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

import java.util.List;

/**
 * Fires specified events for every player in the party.
 */
public class PartyEventFactory implements PlayerEventFactory {
    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates a PartyEventFactory instance.
     *
     * @param loggerFactory   the logger factory to create a logger for the events
     * @param questTypeAPI    the Quest Type API
     * @param profileProvider the profile provider instance
     */
    public PartyEventFactory(final BetonQuestLoggerFactory loggerFactory, final QuestTypeAPI questTypeAPI, final ProfileProvider profileProvider) {
        this.loggerFactory = loggerFactory;
        this.questTypeAPI = questTypeAPI;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> range = instruction.get(Argument.NUMBER);
        final Variable<Number> amount = instruction.get(instruction.getValue("amount"), Argument.NUMBER);
        final Variable<List<ConditionID>> conditions = instruction.getList(ConditionID::new);
        final Variable<List<EventID>> events = instruction.getList(EventID::new);
        return new OnlineEventAdapter(
                new PartyEvent(questTypeAPI, profileProvider, range, amount, conditions, events),
                loggerFactory.create(PartyEvent.class),
                instruction.getPackage()
        );
    }
}
