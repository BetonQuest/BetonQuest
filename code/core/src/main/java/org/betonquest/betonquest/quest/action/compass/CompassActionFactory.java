package org.betonquest.betonquest.quest.action.compass;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.id.CompassID;

/**
 * The compass action factory.
 */
public class CompassActionFactory implements PlayerActionFactory {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Storage to get the offline player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the compass action factory.
     *
     * @param featureApi  the Feature API
     * @param dataStorage the storage for used player data
     */
    public CompassActionFactory(final FeatureApi featureApi, final PlayerDataStorage dataStorage) {
        this.featureApi = featureApi;
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<CompassTargetOperation> action = instruction.enumeration(CompassTargetOperation.class).get();
        final Argument<CompassID> compassId = instruction.parse(
                (placeholders, packManager, pack, string)
                        -> new CompassID(packManager, pack, string)).get();
        return new CompassAction(featureApi, dataStorage, action, compassId);
    }
}
