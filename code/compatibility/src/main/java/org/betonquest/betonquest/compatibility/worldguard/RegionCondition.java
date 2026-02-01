package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlineCondition;

/**
 * Checks if the player is in specified region.
 */
public class RegionCondition implements OnlineCondition {

    /**
     * Region name.
     */
    private final Argument<String> name;

    /**
     * Creates a new region condition.
     *
     * @param name the name of the region
     */
    public RegionCondition(final Argument<String> name) {
        this.name = name;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return WorldGuardIntegrator.isInsideRegion(profile.getPlayer().getLocation(), name.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
