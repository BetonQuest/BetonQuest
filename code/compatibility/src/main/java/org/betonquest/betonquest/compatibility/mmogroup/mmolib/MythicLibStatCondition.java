package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;

/**
 * A condition that checks the value of a MythicLib stat.
 */
public class MythicLibStatCondition implements PlayerCondition {

    /**
     * The name of the stat to check.
     */
    private final Variable<String> statName;

    /**
     * The required minimum target level of the stat.
     */
    private final Variable<Number> targetLevel;

    /**
     * Whether the actual level must be equal to the target level.
     */
    private final boolean mustBeEqual;

    /**
     * Create a new Stat Condition.
     *
     * @param stat        the stat to check
     * @param targetLevel the required level
     * @param equal       whether the level should be equal
     */
    public MythicLibStatCondition(final Variable<String> stat, final Variable<Number> targetLevel, final boolean equal) {
        this.statName = stat;
        this.targetLevel = targetLevel;
        this.mustBeEqual = equal;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final MMOPlayerData data = MMOPlayerData.get(profile.getPlayerUUID());
        final double requiredLevel = targetLevel.getValue(profile).doubleValue();
        final double actualLevel = data.getStatMap().getStat(statName.getValue(profile));
        return mustBeEqual ? actualLevel == requiredLevel : actualLevel >= requiredLevel;
    }
}
