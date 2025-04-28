package org.betonquest.betonquest.quest.event.compass;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.plugin.PluginManager;

/**
 * The compass event factory.
 */
public class CompassEventFactory implements PlayerEventFactory {
    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * Storage to get the offline player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Plugin manager to use to call the event.
     */
    private final PluginManager pluginManager;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the compass event factory.
     *
     * @param featureAPI    the Feature API
     * @param dataStorage   the storage for used player data
     * @param pluginManager plugin manager to use
     * @param data          the data for primary server thread access
     */
    public CompassEventFactory(final FeatureAPI featureAPI, final PlayerDataStorage dataStorage,
                               final PluginManager pluginManager, final PrimaryServerThreadData data) {
        this.featureAPI = featureAPI;
        this.dataStorage = dataStorage;
        this.pluginManager = pluginManager;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<CompassTargetAction> action = instruction.get(Argument.ENUM(CompassTargetAction.class));
        final Variable<CompassID> compassId = instruction.get(CompassID::new);
        return new PrimaryServerThreadEvent(
                new CompassEvent(featureAPI, dataStorage, pluginManager, action, compassId),
                data);
    }
}
