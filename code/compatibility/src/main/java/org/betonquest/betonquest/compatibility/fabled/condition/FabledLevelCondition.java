package org.betonquest.betonquest.compatibility.fabled.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerClass;
import studio.magemonkey.fabled.api.player.PlayerData;

import java.util.Optional;

/**
 * Checks the level of the player in Fabled.
 */
public class FabledLevelCondition implements PlayerCondition {

    /**
     * The class name.
     */
    private final Argument<String> className;

    /**
     * The level.
     */
    private final Argument<Number> level;

    /**
     * Create a new {@link FabledLevelCondition}.
     *
     * @param className the class name.
     * @param level     the level.
     */
    public FabledLevelCondition(final Argument<String> className, final Argument<Number> level) {
        this.className = className;
        this.level = level;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final String className = this.className.getValue(profile);
        if (!Fabled.isClassRegistered(className)) {
            throw new QuestException("Class '" + className + "' is not registered");
        }

        final int level = this.level.getValue(profile).intValue();
        final PlayerData data = Fabled.getData(profile.getPlayer());
        final Optional<PlayerClass> playerClass = data
                .getClasses()
                .stream()
                .filter(c -> c.getData().getName().equalsIgnoreCase(className))
                .findAny();
        return playerClass.filter(aClass -> level <= aClass.getLevel()).isPresent();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
