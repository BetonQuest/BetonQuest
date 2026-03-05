package org.betonquest.betonquest.quest.action.compass;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.QuestCompassTargetChangeEvent;
import org.betonquest.betonquest.api.compass.QuestCompass;
import org.betonquest.betonquest.api.data.Persistence;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.service.compass.CompassManager;
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
     * The compass manager.
     */
    private final CompassManager compassManager;

    /**
     * Persistence api to get the offline player data.
     */
    private final Persistence persistence;

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
     * @param compassManager the compass manager
     * @param persistence    the persistence api to get offline player data
     * @param action         the action to perform
     * @param compassId      the compass point
     */
    public CompassAction(final CompassManager compassManager, final Persistence persistence,
                         final Argument<CompassTargetOperation> action, final Argument<CompassIdentifier> compassId) {
        this.compassManager = compassManager;
        this.persistence = persistence;
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
                final QuestCompass compass = compassManager.get(compassId);
                final Location location = compass.location().getValue(profile);
                if (profile.getOnlineProfile().isPresent() && new QuestCompassTargetChangeEvent(profile, location).callEvent()) {
                    profile.getOnlineProfile().get().getPlayer().setCompassTarget(location);
                }
            }
        }
    }

    private void changeTag(final TagChanger tagChanger, final Profile profile) throws QuestException {
        new TagAction(offline -> persistence.of(offline).tags(), tagChanger).execute(profile);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
