package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.SkillAPI;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.Locale;

/**
 * Adds experience in specified skill.
 */
@SuppressWarnings("PMD.CommentRequired")
public class McMMOAddExpEvent extends QuestEvent {
    private final String skillType;

    private final VariableNumber exp;

    public McMMOAddExpEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);
        skillType = instruction.next().toUpperCase(Locale.ROOT);
        if (!SkillAPI.getSkills().contains(skillType)) {
            throw new QuestException("Invalid skill name");
        }
        exp = instruction.get(VariableNumber::new);
    }

    @Override
    protected Void execute(final Profile profile) throws QuestException {
        ExperienceAPI.addRawXP(profile.getOnlineProfile().get().getPlayer(), skillType, exp.getInt(profile), "UNKNOWN");
        return null;
    }
}
