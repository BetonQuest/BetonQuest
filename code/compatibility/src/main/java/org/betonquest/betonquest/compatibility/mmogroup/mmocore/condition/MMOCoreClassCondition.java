package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.jetbrains.annotations.Nullable;

/**
 * Condition to check if the player has a level in a class.
 */
public class MMOCoreClassCondition implements PlayerCondition {

    /**
     * If the current class should be checked instead a specific one.
     */
    private static final String CURRENT_CLASS = "*";

    /**
     * Class to check.
     */
    private final Variable<String> targetClassName;

    /**
     * If the actual must be equal to the target level.
     */
    private final boolean mustBeEqual;

    /**
     * Required level.
     */
    @Nullable
    private final Variable<Number> targetClassLevel;

    /**
     * Create a new class condition.
     * The class needs to be the active.
     *
     * @param className  the name of the required class to check or '*' if to check current class
     * @param classLevel the required level
     * @param equal      whether the actual must be equal to the target level
     */
    public MMOCoreClassCondition(final Variable<String> className, @Nullable final Variable<Number> classLevel, final boolean equal) {
        this.targetClassName = className;
        this.targetClassLevel = classLevel;
        this.mustBeEqual = equal;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());

        final String actualClassName = data.getProfess().getId();
        final int actualClassLevel = data.getLevel();

        final String targetClassName = this.targetClassName.getValue(profile);
        if (actualClassName.equalsIgnoreCase(targetClassName)
                || CURRENT_CLASS.equals(targetClassName) && !"HUMAN".equalsIgnoreCase(actualClassName)) {
            if (targetClassLevel == null) {
                return true;
            }
            final int level = targetClassLevel.getValue(profile).intValue();
            return mustBeEqual ? actualClassLevel == level : actualClassLevel >= level;
        }
        return false;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
