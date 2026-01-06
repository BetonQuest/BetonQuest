package org.betonquest.betonquest.quest.action.cancel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.betonquest.betonquest.id.QuestCancelerID;

/**
 * Factory for the cancel action.
 */
public class CancelActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Creates a new cancel action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     * @param featureApi    the feature API
     */
    public CancelActionFactory(final BetonQuestLoggerFactory loggerFactory, final FeatureApi featureApi) {
        this.loggerFactory = loggerFactory;
        this.featureApi = featureApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<QuestCancelerID> cancelerID = instruction.parse(QuestCancelerID::new).get();
        final FlagArgument<Boolean> bypass = instruction.bool().getFlag("bypass", true);
        return new OnlineActionAdapter(new CancelAction(featureApi, cancelerID, bypass),
                loggerFactory.create(CancelAction.class), instruction.getPackage());
    }
}
