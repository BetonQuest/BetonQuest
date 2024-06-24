package org.betonquest.betonquest.compatibility.skillapi;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.Optional;

/**
 * Checks the level of the player in SkillAPI.
 */
@SuppressWarnings("PMD.CommentRequired")
public class SkillAPILevelCondition extends Condition {
    private final String className;

    private final VariableNumber level;

    public SkillAPILevelCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        className = instruction.next();
        if (!SkillAPI.isClassRegistered(className)) {
            throw new InstructionParseException("Class '" + className + "' is not registered");
        }
        level = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final PlayerData data = SkillAPI.getPlayerData(profile.getPlayer());
        final Optional<PlayerClass> playerClass = data
                .getClasses()
                .stream()
                .filter(c -> c.getData().getName().equalsIgnoreCase(className))
                .findAny();
        return playerClass.filter(aClass -> level.getInt(profile) <= aClass.getLevel()).isPresent();
    }

}
