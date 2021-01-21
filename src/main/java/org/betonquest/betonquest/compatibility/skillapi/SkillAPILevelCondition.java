package org.betonquest.betonquest.compatibility.skillapi;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

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
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final PlayerData data = SkillAPI.getPlayerData(PlayerConverter.getPlayer(playerID));
        final Optional<PlayerClass> playerClass = data
                .getClasses()
                .stream()
                .filter(c -> c.getData().getName().equalsIgnoreCase(className))
                .findAny();
        if (!playerClass.isPresent()) {
            return false;
        }
        return level.getInt(playerID) <= playerClass.get().getLevel();
    }

}
