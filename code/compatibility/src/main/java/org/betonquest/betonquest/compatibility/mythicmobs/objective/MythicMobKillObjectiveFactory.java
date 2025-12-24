package org.betonquest.betonquest.compatibility.mythicmobs.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

/**
 * Factory for creating {@link MythicMobKillObjective} instances from {@link Instruction}s.
 */
public class MythicMobKillObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MythicMobKillObjectiveFactory.
     */
    public MythicMobKillObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<List<String>> names = instruction.string().getList();
        final Variable<IdentifierMode> mode = instruction.enumeration(IdentifierMode.class).get("mode", IdentifierMode.INTERNAL_NAME);
        final Variable<Number> targetAmount = instruction.number().atLeast(1).get("amount", 1);

        final Variable<Number> deathRadiusAllPlayers = instruction.number().get("deathRadiusAllPlayers", 0);
        final Variable<Number> neutralDeathRadiusAllPlayers = instruction.number().get("neutralDeathRadiusAllPlayers", 0);

        final Variable<Number> minMobLevel = instruction.number().get("minLevel", Double.NEGATIVE_INFINITY);
        final Variable<Number> maxMobLevel = instruction.number().get("maxLevel", Double.POSITIVE_INFINITY);
        final Variable<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        return new MythicMobKillObjective(instruction, targetAmount, names, mode, minMobLevel, maxMobLevel, deathRadiusAllPlayers, neutralDeathRadiusAllPlayers, marked);
    }
}
