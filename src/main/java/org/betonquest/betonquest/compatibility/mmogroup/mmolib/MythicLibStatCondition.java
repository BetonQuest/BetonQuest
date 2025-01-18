package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * A condition that checks the value of a MythicLib stat.
 */
public class MythicLibStatCondition extends Condition {

    /**
     * The name of the stat to check.
     */
    private final String statName;

    /**
     * The required minimum target level of the stat.
     */
    private final double targetLevel;

    /**
     * Whether the actual level must be equal to the target level.
     */
    private final boolean mustBeEqual;

    /**
     * Parses the instruction and creates a new condition.
     *
     * @param instruction the user-provided instruction
     * @throws QuestException if the instruction is invalid
     */
    public MythicLibStatCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);

        statName = instruction.next();
        targetLevel = instruction.getDouble();
        mustBeEqual = instruction.hasArgument("equal");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        final MMOPlayerData data = MMOPlayerData.get(profile.getPlayerUUID());
        final double actualLevel = data.getStatMap().getStat(statName);
        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
