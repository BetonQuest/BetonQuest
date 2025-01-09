package org.betonquest.betonquest.quest.event.compass;

import org.betonquest.betonquest.api.bukkit.events.QuestCompassTargetChangeEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.event.tag.AddTagChanger;
import org.betonquest.betonquest.quest.event.tag.DeleteTagChanger;
import org.betonquest.betonquest.quest.event.tag.TagChanger;
import org.betonquest.betonquest.quest.event.tag.TagEvent;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;

/**
 * Event to set a compass target and manage compass points.
 */
public class CompassEvent implements Event {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

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
    private final String compass;

    /**
     * The location to set the compass to.
     */
    private final VariableLocation compassLocation;

    /**
     * The quest package to use for logging.
     */
    private final QuestPackage questPackage;

    /**
     * Create the compass event.
     *
     * @param log             the logger
     * @param storage         the storage to get the offline player data
     * @param pluginManager   the plugin manager to call the {@link QuestCompassTargetChangeEvent}
     * @param action          the action to perform
     * @param compass         the compass point
     * @param compassLocation the location to set the compass to
     * @param questPackage    the quest package
     */
    public CompassEvent(final BetonQuestLogger log, final PlayerDataStorage storage, final PluginManager pluginManager,
                        final CompassTargetAction action, final String compass, final VariableLocation compassLocation,
                        final QuestPackage questPackage) {
        this.log = log;
        this.dataStorage = storage;
        this.pluginManager = pluginManager;
        this.action = action;
        this.compass = compass;
        this.compassLocation = compassLocation;
        this.questPackage = questPackage;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        switch (action) {
            case ADD -> changeTag(new AddTagChanger(getPackagedCompass()), profile);
            case DEL -> changeTag(new DeleteTagChanger(getPackagedCompass()), profile);
            case SET -> {
                try {
                    final Location location = compassLocation.getValue(profile);
                    if (profile.getOnlineProfile().isPresent()) {
                        final QuestCompassTargetChangeEvent event = new QuestCompassTargetChangeEvent(profile, location);
                        pluginManager.callEvent(event);
                        if (!event.isCancelled()) {
                            profile.getOnlineProfile().get().getPlayer().setCompassTarget(location);
                        }
                    }
                } catch (final QuestException e) {
                    log.warn(questPackage, "Failed to set compass: " + compass, e);
                }
            }
        }
    }

    private String getPackagedCompass() {
        return Utils.addPackage(questPackage, "compass-" + compass);
    }

    private void changeTag(final TagChanger tagChanger, final Profile profile) {
        new TagEvent(dataStorage::getOffline, tagChanger).execute(profile);
    }
}
