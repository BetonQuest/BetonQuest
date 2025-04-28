package org.betonquest.betonquest.compatibility.mythicmobs.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;

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
        final List<String> names = instruction.getList();
        final Variable<Number> targetAmount = instruction.getVariable(instruction.getOptional("amount", "1"), Argument.NUMBER_NOT_LESS_THAN_ONE);

        final Variable<Number> deathRadiusAllPlayers = instruction.getVariable(instruction.getOptional("deathRadiusAllPlayers"), Argument.NUMBER, 0);
        final Variable<Number> neutralDeathRadiusAllPlayers = instruction.getVariable(instruction.getOptional("neutralDeathRadiusAllPlayers"), Argument.NUMBER, 0);

        final Variable<Number> minMobLevel = instruction.getVariable(instruction.getOptional("minLevel", String.valueOf(Double.NEGATIVE_INFINITY)), Argument.NUMBER);
        final Variable<Number> maxMobLevel = instruction.getVariable(instruction.getOptional("maxLevel", String.valueOf(Double.POSITIVE_INFINITY)), Argument.NUMBER);
        final Variable<String> marked = instruction.get(instruction.getOptional("marked"), PackageArgument.IDENTIFIER);
        return new MythicMobKillObjective(instruction, targetAmount, names, minMobLevel, maxMobLevel, deathRadiusAllPlayers, neutralDeathRadiusAllPlayers, marked);
    }
}
