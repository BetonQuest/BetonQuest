package org.betonquest.betonquest.quest.action.party;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

import java.util.List;

/**
 * Fires specified actions for every player in the party.
 */
public class PartyActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
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
     * @param loggerFactory   the logger factory to create a logger for the actions
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
        final Argument<List<ConditionIdentifier>> conditions = instruction.identifier(ConditionIdentifier.class).list().get();
        final Argument<List<ActionIdentifier>> actions = instruction.identifier(ActionIdentifier.class).list().get();
        return new OnlineActionAdapter(
                new PartyAction(questTypeApi, profileProvider, range, amount, conditions, actions),
                loggerFactory.create(PartyAction.class),
                instruction.getPackage()
        );
    }
}
