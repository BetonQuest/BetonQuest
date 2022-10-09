package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.SkillAPI;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.Locale;

/**
 * Adds experience in specified skill.
 */
@SuppressWarnings("PMD.CommentRequired")
public class McMMOAddExpEvent extends QuestEvent {

    private final String skillType;
    private final VariableNumber exp;

    public McMMOAddExpEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        skillType = instruction.next().toUpperCase(Locale.ROOT);
        if (!SkillAPI.getSkills().contains(skillType)) {
            throw new InstructionParseException("Invalid skill name");
        }
        exp = instruction.getVarNum();
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        ExperienceAPI.addRawXP(profile.getOnlineProfile().getOnlinePlayer(), skillType, exp.getInt(profile), "UNKNOWN");
        return null;
    }

}
