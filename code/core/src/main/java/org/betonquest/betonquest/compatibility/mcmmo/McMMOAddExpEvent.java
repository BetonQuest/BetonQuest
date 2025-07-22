package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Adds experience in specified skill.
 */
public class McMMOAddExpEvent implements OnlineEvent {

    /**
     * Name of skill to add xp to.
     */
    private final Variable<String> skillType;

    /**
     * Amount to add.
     */
    private final Variable<Number> exp;

    /**
     * Create a new add exp event.
     *
     * @param skillType the type to add xp to
     * @param exp       the amount to add
     */
    public McMMOAddExpEvent(final Variable<String> skillType, final Variable<Number> exp) {
        this.skillType = skillType;
        this.exp = exp;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final String skillType = this.skillType.getValue(profile);
        ExperienceAPI.addRawXP(profile.getPlayer(), skillType, exp.getValue(profile).intValue(), "UNKNOWN");
    }
}
