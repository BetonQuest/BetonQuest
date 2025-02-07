package org.betonquest.betonquest.compatibility.fabled.condition;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;

import java.util.Optional;

/**
 * Checks the level of the player in Fabled.
 */
public class FabledLevelCondition implements PlayerCondition {
    /**
     * The {@link VariableString} for the class name.
     */
    private final VariableString classNameVar;

    /**
     * The {@link VariableNumber} for the level.
     */
    private final VariableNumber levelVar;

    /**
     * Create a new {@link FabledLevelCondition}.
     *
     * @param classNameVar the {@link VariableString} for the class name.
     * @param levelVar     the {@link VariableNumber} for the level.
     */
    public FabledLevelCondition(final VariableString classNameVar, final VariableNumber levelVar) {
        this.classNameVar = classNameVar;
        this.levelVar = levelVar;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final String className = classNameVar.getValue(profile);
        if (!Fabled.isClassRegistered(className)) {
            throw new QuestException("Class '" + className + "' is not registered");
        }

        final int level = levelVar.getValue(profile).intValue();
        final PlayerData data = Fabled.getData(profile.getPlayer());
        final Optional<PlayerClass> playerClass = data
                .getClasses()
                .stream()
                .filter(c -> c.getData().getName().equalsIgnoreCase(className))
                .findAny();
        return playerClass.filter(aClass -> level <= aClass.getLevel()).isPresent();
    }
}
