package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jetbrains.annotations.Nullable;

/**
 * Condition to check if the player has a level in a class.
 */
public class MMOCoreClassCondition implements PlayerCondition {
    /**
     * Class to check.
     */
    @Nullable
    private final String targetClassName;

    /**
     * Required level.
     */
    @Nullable
    private final VariableNumber targetClassLevel;

    /**
     * If the actual must be equal to the target level.
     */
    private final boolean mustBeEqual;

    /**
     * Create a new class condition.
     * The class needs to be the active.
     *
     * @param className  the name of the required class to check or null if to check current class
     * @param classLevel the required level
     * @param equal      whether the actual must be equal to the target level
     */
    public MMOCoreClassCondition(@Nullable final String className, @Nullable final VariableNumber classLevel, final boolean equal) {
        this.targetClassName = className;
        this.targetClassLevel = classLevel;
        this.mustBeEqual = equal;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());

        final String actualClassName = data.getProfess().getId();
        final int actualClassLevel = data.getLevel();

        if (actualClassName.equalsIgnoreCase(targetClassName) || targetClassName == null && !"HUMAN".equalsIgnoreCase(actualClassName)) {
            if (targetClassLevel == null) {
                return true;
            }
            final int level = targetClassLevel.getValue(profile).intValue();
            return mustBeEqual ? actualClassLevel == level : actualClassLevel >= level;
        }
        return false;
    }
}
