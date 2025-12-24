package org.betonquest.betonquest.quest.event.compass;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
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
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the compass event factory.
     *
     * @param featureApi  the Feature API
     * @param dataStorage the storage for used player data
     * @param data        the data for primary server thread access
     */
    public CompassEventFactory(final FeatureApi featureApi, final PlayerDataStorage dataStorage,
                               final PrimaryServerThreadData data) {
        this.featureApi = featureApi;
        this.dataStorage = dataStorage;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<CompassTargetAction> action = instruction.enumeration(CompassTargetAction.class).get();
        final Variable<CompassID> compassId = instruction.parse(
                (variables, packManager, pack, string)
                        -> new CompassID(packManager, pack, string)).get();
        return new PrimaryServerThreadEvent(
                new CompassEvent(featureApi, dataStorage, action, compassId),
                data);
    }
}
