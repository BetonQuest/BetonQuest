package org.betonquest.betonquest.quest.event.compass;

import org.betonquest.betonquest.api.bukkit.event.QuestCompassTargetChangeEvent;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.quest.event.tag.AddTagChanger;
import org.betonquest.betonquest.quest.event.tag.DeleteTagChanger;
import org.betonquest.betonquest.quest.event.tag.TagChanger;
import org.betonquest.betonquest.quest.event.tag.TagEvent;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;

/**
 * Event to set a compass target and manage compass points.
 */
public class CompassEvent implements PlayerEvent {
    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Storage to get the offline player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Plugin manager to use to call the event.
     */
    private final PluginManager pluginManager;

    /**
     * The action to perform on the compass.
     */
    private final Variable<CompassTargetAction> action;

    /**
     * The compass point to set.
     */
    private final Variable<CompassID> compassId;

    /**
     * Create the compass event.
     *
     * @param featureApi    the Feature API
     * @param storage       the storage to get the offline player data
     * @param pluginManager the plugin manager to call the {@link QuestCompassTargetChangeEvent}
     * @param action        the action to perform
     * @param compassId     the compass point
     */
    public CompassEvent(final FeatureApi featureApi, final PlayerDataStorage storage, final PluginManager pluginManager,
                        final Variable<CompassTargetAction> action, final Variable<CompassID> compassId) {
        this.featureApi = featureApi;
        this.dataStorage = storage;
        this.pluginManager = pluginManager;
        this.action = action;
        this.compassId = compassId;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final CompassID compassId = this.compassId.getValue(profile);
        switch (action.getValue(profile)) {
            case ADD -> changeTag(new AddTagChanger(new VariableList<>(compassId.getTag())), profile);
            case DEL -> changeTag(new DeleteTagChanger(new VariableList<>(compassId.getTag())), profile);
            case SET -> {
                final QuestCompass compass = featureApi.getCompasses().get(compassId);
                if (compass == null) {
                    throw new QuestException("No compass found for id '" + compassId + "' found.");
                }
                final Location location = compass.location().getValue(profile);
                if (profile.getOnlineProfile().isPresent()) {
                    final QuestCompassTargetChangeEvent event = new QuestCompassTargetChangeEvent(profile, location);
                    pluginManager.callEvent(event);
                    if (!event.isCancelled()) {
                        profile.getOnlineProfile().get().getPlayer().setCompassTarget(location);
                    }
                }
            }
        }
    }

    private void changeTag(final TagChanger tagChanger, final Profile profile) throws QuestException {
        new TagEvent(dataStorage::getOffline, tagChanger).execute(profile);
    }
}
