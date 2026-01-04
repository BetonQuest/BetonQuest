package org.betonquest.betonquest.quest.event.compass;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.id.CompassID;

/**
 * The compass event factory.
 */
public class CompassEventFactory implements PlayerEventFactory {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Storage to get the offline player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the compass event factory.
     *
     * @param featureApi  the Feature API
     * @param dataStorage the storage for used player data
     */
    public CompassEventFactory(final FeatureApi featureApi, final PlayerDataStorage dataStorage) {
        this.featureApi = featureApi;
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<CompassTargetOperation> action = instruction.enumeration(CompassTargetOperation.class).get();
        final Argument<CompassID> compassId = instruction.parse(
                (placeholders, packManager, pack, string)
                        -> new CompassID(packManager, pack, string)).get();
        return new CompassEvent(featureApi, dataStorage, action, compassId);
    }
}
