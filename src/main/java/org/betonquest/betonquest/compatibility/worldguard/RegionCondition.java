package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Checks if the player is in specified region
 */
@SuppressWarnings("PMD.CommentRequired")
public class RegionCondition extends Condition {

    private final String name;

    public RegionCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);
        name = instruction.next();
    }

    @Override
    protected Boolean execute(final Profile profile) {
        return WorldGuardIntegrator.isInsideRegion(profile.getOnlineProfile().get().getPlayer().getLocation(), name);
    }
}
