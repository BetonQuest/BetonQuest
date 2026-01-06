package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;

/**
 * Adds experience in specified skill.
 */
public class McMMOAddExpAction implements OnlineAction {

    /**
     * Name of skill to add xp to.
     */
    private final Argument<String> skillType;

    /**
     * Amount to add.
     */
    private final Argument<Number> exp;

    /**
     * Create a new add exp action.
     *
     * @param skillType the type to add xp to
     * @param exp       the amount to add
     */
    public McMMOAddExpAction(final Argument<String> skillType, final Argument<Number> exp) {
        this.skillType = skillType;
        this.exp = exp;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final String skillType = this.skillType.getValue(profile);
        ExperienceAPI.addRawXP(profile.getPlayer(), skillType, exp.getValue(profile).intValue(), "UNKNOWN");
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
