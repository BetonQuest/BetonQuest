package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;

import java.util.Optional;

/**
 * Checks the level of the player in SkillAPI.
 */
@SuppressWarnings("PMD.CommentRequired")
public class FabledLevelCondition extends Condition {
    private final String className;

    private final VariableNumber level;

    public FabledLevelCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);
        className = instruction.next();
        if (!Fabled.isClassRegistered(className)) {
            throw new QuestException("Class '" + className + "' is not registered");
        }
        level = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        final PlayerData data = Fabled.getData(profile.getPlayer());
        final Optional<PlayerClass> playerClass = data
                .getClasses()
                .stream()
                .filter(c -> c.getData().getName().equalsIgnoreCase(className))
                .findAny();
        return playerClass.filter(aClass -> level.getInt(profile) <= aClass.getLevel()).isPresent();
    }
}
