package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.condition.ConditionID;

import java.util.List;

/**
 * Fires specified events for every player in the party.
 */
public class PartyActionFactory implements PlayerActionFactory {

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
     * Creates a PartyActionFactory instance.
     *
     * @param loggerFactory   the logger factory to create a logger for the events
     * @param questTypeApi    the Quest Type API
     * @param profileProvider the profile provider instance
     */
    public PartyActionFactory(final BetonQuestLoggerFactory loggerFactory, final QuestTypeApi questTypeApi, final ProfileProvider profileProvider) {
        this.loggerFactory = loggerFactory;
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> range = instruction.number().get();
        final Argument<Number> amount = instruction.number().get("amount").orElse(null);
        final Argument<List<ConditionID>> conditions = instruction.parse(ConditionID::new).list().get();
        final Argument<List<ActionID>> events = instruction.parse(ActionID::new).list().get();
        return new OnlineActionAdapter(
                new PartyAction(questTypeApi, profileProvider, range, amount, conditions, events),
                loggerFactory.create(PartyAction.class),
                instruction.getPackage()
        );
    }
}
