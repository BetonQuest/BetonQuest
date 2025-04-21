package org.betonquest.betonquest.quest.event.cancel;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for the cancel event.
 */
public class CancelEventFactory implements PlayerEventFactory {
    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * Creates a new cancel event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param featureAPI    the feature API
     */
    public CancelEventFactory(final BetonQuestLoggerFactory loggerFactory, final FeatureAPI featureAPI) {
        this.loggerFactory = loggerFactory;
        this.featureAPI = featureAPI;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<QuestCancelerID> cancelerID = instruction.get(IDArgument.ofSingle(QuestCancelerID::new));
        final boolean bypass = instruction.hasArgument("bypass");
        return new OnlineEventAdapter(new CancelEvent(featureAPI, cancelerID, bypass),
                loggerFactory.create(CancelEvent.class), instruction.getPackage());
    }
}
