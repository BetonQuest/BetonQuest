package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
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
        final Argument<Number> range = instruction.number().get();
        final Argument<Number> amount = instruction.number().get("amount").orElse(null);
        final Argument<List<ConditionID>> conditions = instruction.parse(ConditionID::new).list().get();
        final Argument<List<ActionID>> events = instruction.parse(ActionID::new).list().get();
        return new OnlineEventAdapter(
                new PartyEvent(questTypeApi, profileProvider, range, amount, conditions, events),
                loggerFactory.create(PartyEvent.class),
                instruction.getPackage()
        );
    }
}
