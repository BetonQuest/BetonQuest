package org.betonquest.betonquest.quest.event.compass;

import org.betonquest.betonquest.api.bukkit.event.QuestCompassTargetChangeEvent;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.quest.event.tag.AddTagChanger;
import org.betonquest.betonquest.quest.event.tag.DeleteTagChanger;
import org.betonquest.betonquest.quest.event.tag.TagChanger;
import org.betonquest.betonquest.quest.event.tag.TagEvent;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;

/**
 * Event to set a compass target and manage compass points.
 */
public class CompassEvent implements Event {
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
     * The action to perform on the compass.
     */
    private final CompassTargetAction action;

    /**
     * The compass point to set.
     */
    private final CompassID compassId;

    /**
     * Create the compass event.
     *
     * @param featureAPI    the Feature API
     * @param storage       the storage to get the offline player data
     * @param pluginManager the plugin manager to call the {@link QuestCompassTargetChangeEvent}
     * @param action        the action to perform
     * @param compassId     the compass point
     */
    public CompassEvent(final FeatureAPI featureAPI, final PlayerDataStorage storage, final PluginManager pluginManager,
                        final CompassTargetAction action, final CompassID compassId) {
        this.featureAPI = featureAPI;
        this.dataStorage = storage;
        this.pluginManager = pluginManager;
        this.action = action;
        this.compassId = compassId;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        switch (action) {
            case ADD -> changeTag(new AddTagChanger(compassId.getCompassTag()), profile);
            case DEL -> changeTag(new DeleteTagChanger(compassId.getCompassTag()), profile);
            case SET -> {
                final QuestCompass compass = featureAPI.getCompasses().get(compassId);
                if (compass == null) {
                    throw new QuestException("No compass found for id '" + compassId.getFullID() + "' found.");
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

    private void changeTag(final TagChanger tagChanger, final Profile profile) {
        new TagEvent(dataStorage::getOffline, tagChanger).execute(profile);
    }
}
