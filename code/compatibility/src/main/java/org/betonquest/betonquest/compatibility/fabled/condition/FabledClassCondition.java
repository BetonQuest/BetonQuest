package org.betonquest.betonquest.compatibility.fabled.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;

/**
 * Checks if the player has a specific class.
 */
public class FabledClassCondition implements PlayerCondition {

    /**
     * The class name.
     */
    private final Argument<String> className;

    /**
     * If the class check should be exact.
     */
    private final FlagArgument<Boolean> exact;

    /**
     * Create a new {@link FabledClassCondition}.
     *
     * @param className the class name.
     * @param exact     if the class check should be exact.
     */
    public FabledClassCondition(final Argument<String> className, final FlagArgument<Boolean> exact) {
        this.className = className;
        this.exact = exact;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final String className = this.className.getValue(profile);
        if (!Fabled.isClassRegistered(className)) {
            throw new QuestException("Class '" + className + "' is not registered");
        }

        final PlayerData data = Fabled.getData(profile.getPlayer());
        if (exact.getValue(profile).orElse(false)) {
            return data.isExactClass(Fabled.getClass(className));
        }
        return data.isClass(Fabled.getClass(className));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
