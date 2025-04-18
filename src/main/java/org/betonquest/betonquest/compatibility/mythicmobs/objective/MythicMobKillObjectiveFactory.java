package org.betonquest.betonquest.compatibility.mythicmobs.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.Set;

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
        final Set<String> names = Set.of(instruction.getArray());
        final VariableNumber targetAmount = instruction.get(instruction.getOptional("amount", "1"), VariableArgument.NUMBER_NOT_LESS_THAN_ONE);

        final double deathRadiusAllPlayers = instruction.getDouble(instruction.getOptional("deathRadiusAllPlayers"), 0);
        final double neutralDeathRadiusAllPlayers = instruction.getDouble(instruction.getOptional("neutralDeathRadiusAllPlayers"), 0);

        final VariableNumber minMobLevel = instruction.get(instruction.getOptional("minLevel", String.valueOf(Double.NEGATIVE_INFINITY)), VariableNumber::new);
        final VariableNumber maxMobLevel = instruction.get(instruction.getOptional("maxLevel", String.valueOf(Double.POSITIVE_INFINITY)), VariableNumber::new);
        final VariableIdentifier marked = instruction.get(instruction.getOptional("marked"), VariableIdentifier::new);
        return new MythicMobKillObjective(instruction, targetAmount, names, minMobLevel, maxMobLevel, deathRadiusAllPlayers, neutralDeathRadiusAllPlayers, marked);
    }
}
