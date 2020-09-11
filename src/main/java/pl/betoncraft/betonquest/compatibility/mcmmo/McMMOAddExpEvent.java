package pl.betoncraft.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.SkillAPI;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Adds experience in specified skill.
 */
public class McMMOAddExpEvent extends QuestEvent {

    private final String skillType;
    private final VariableNumber exp;

    public McMMOAddExpEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        skillType = instruction.next().toUpperCase();
        if (!SkillAPI.getSkills().contains(skillType)) {
            throw new InstructionParseException("Invalid skill name");
        }
        exp = instruction.getVarNum();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        ExperienceAPI.addRawXP(PlayerConverter.getPlayer(playerID), skillType, exp.getInt(playerID), "UNKNOWN");
        return null;
    }

}
