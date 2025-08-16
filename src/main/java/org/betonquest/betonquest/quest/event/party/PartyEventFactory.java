package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;

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
    private final QuestTypeApi questTypeApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates a PartyEventFactory instance.
     *
     * @param loggerFactory   the logger factory to create a logger for the events
     * @param questTypeApi    the Quest Type API
     * @param profileProvider the profile provider instance
     */
    public PartyEventFactory(final BetonQuestLoggerFactory loggerFactory, final QuestTypeApi questTypeApi, final ProfileProvider profileProvider) {
        this.loggerFactory = loggerFactory;
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> range = instruction.get(Argument.NUMBER);
        final Variable<Number> amount = instruction.getValue("amount", Argument.NUMBER);
        final Variable<List<ConditionID>> conditions = instruction.getList(ConditionID::new);
        final Variable<List<EventID>> events = instruction.getList(EventID::new);
        return new OnlineEventAdapter(
                new PartyEvent(questTypeApi, profileProvider, range, amount, conditions, events),
                loggerFactory.create(PartyEvent.class),
                instruction.getPackage()
        );
    }
}
