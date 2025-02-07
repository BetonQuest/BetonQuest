package org.betonquest.betonquest.quest.event.cancel;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory for the cancel event.
 */
public class CancelEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * Creates a new cancel event factory.
     *
     * @param loggerFactory logger factory to use
     * @param featureAPI    the feature API
     */
    public CancelEventFactory(final BetonQuestLoggerFactory loggerFactory, final FeatureAPI featureAPI) {
        this.loggerFactory = loggerFactory;
        this.featureAPI = featureAPI;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final QuestPackage pack = instruction.getPackage();
        final String identifier = instruction.next();
        final QuestCancelerID cancelerID = new QuestCancelerID(pack, identifier);
        final QuestCanceler canceler = featureAPI.getCanceler().get(cancelerID);
        if (canceler == null) {
            throw new QuestException("Quest canceler '" + cancelerID.getFullID() + "' does not exist."
                    + " Ensure it was loaded without errors.");
        }
        return new OnlineEventAdapter(new CancelEvent(canceler), loggerFactory.create(CancelEvent.class), pack);
    }
}
