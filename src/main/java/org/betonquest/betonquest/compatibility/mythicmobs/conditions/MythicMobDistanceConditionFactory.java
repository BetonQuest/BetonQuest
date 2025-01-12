package org.betonquest.betonquest.compatibility.mythicmobs.conditions;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link MythicMobDistanceCondition}s from {@link Instruction}s.
 */
public class MythicMobDistanceConditionFactory implements PlayerConditionFactory {
    /**
     * API Helper for getting MythicMobs.
     */
    private final BukkitAPIHelper apiHelper;

    /**
     * Data required for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for {@link MythicMobDistanceCondition}s.
     *
     * @param apiHelper the api helper used get MythicMobs
     * @param data      the primary server thread data required for main thread checking
     */
    public MythicMobDistanceConditionFactory(final BukkitAPIHelper apiHelper, final PrimaryServerThreadData data) {
        this.apiHelper = apiHelper;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String internalName = instruction.next();
        if (apiHelper.getMythicMob(internalName) == null) {
            throw new QuestException("MythicMob with internal name '" + internalName + "' does not exist");
        }

        final VariableNumber distance = instruction.getVarNum();
        return new PrimaryServerThreadPlayerCondition(new MythicMobDistanceCondition(apiHelper, internalName, distance), data);
    }
}
