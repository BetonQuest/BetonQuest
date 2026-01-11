package org.betonquest.betonquest.quest.action.compass;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.QuestCompassTargetChangeEvent;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.betonquest.betonquest.quest.action.tag.AddTagChanger;
import org.betonquest.betonquest.quest.action.tag.DeleteTagChanger;
import org.betonquest.betonquest.quest.action.tag.TagAction;
import org.betonquest.betonquest.quest.action.tag.TagChanger;
import org.bukkit.Location;

import java.util.List;

/**
 * Action to set a compass target and manage compass points.
 */
public class CompassAction implements PlayerAction {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Storage to get the offline player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The action to perform on the compass.
     */
    private final Argument<CompassTargetOperation> action;

    /**
     * The compass point to set.
     */
    private final Argument<CompassIdentifier> compassId;

    /**
     * Create the compass action.
     *
     * @param featureApi the Feature API
     * @param storage    the storage to get the offline player data
     * @param action     the action to perform
     * @param compassId  the compass point
     */
    public CompassAction(final FeatureApi featureApi, final PlayerDataStorage storage,
                         final Argument<CompassTargetOperation> action, final Argument<CompassIdentifier> compassId) {
        this.featureApi = featureApi;
        this.dataStorage = storage;
        this.action = action;
        this.compassId = compassId;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final CompassIdentifier compassId = this.compassId.getValue(profile);
        switch (action.getValue(profile)) {
            case ADD -> changeTag(new AddTagChanger(new DefaultArgument<>(List.of(compassId.getTag()))), profile);
            case DEL -> changeTag(new DeleteTagChanger(new DefaultArgument<>(List.of(compassId.getTag()))), profile);
            case SET -> {
                final QuestCompass compass = featureApi.getCompasses().get(compassId);
                if (compass == null) {
                    throw new QuestException("No compass found for id '" + compassId + "' found.");
                }
                final Location location = compass.location().getValue(profile);
                if (profile.getOnlineProfile().isPresent() && new QuestCompassTargetChangeEvent(profile, location).callEvent()) {
                    profile.getOnlineProfile().get().getPlayer().setCompassTarget(location);
                }
            }
        }
    }

    private void changeTag(final TagChanger tagChanger, final Profile profile) throws QuestException {
        new TagAction(dataStorage::getOffline, tagChanger).execute(profile);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
