package org.betonquest.betonquest.compatibility.mythicmobs.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

/**
 * Factory for creating {@link MythicMobKillObjective} instances from {@link DefaultInstruction}s.
 */
public class MythicMobKillObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MythicMobKillObjectiveFactory.
     */
    public MythicMobKillObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<List<String>> names = instruction.getList(Argument.STRING);
        final Variable<Number> targetAmount = instruction.getValue("amount", Argument.NUMBER_NOT_LESS_THAN_ONE, 1);

        final Variable<Number> deathRadiusAllPlayers = instruction.getValue("deathRadiusAllPlayers", Argument.NUMBER, 0);
        final Variable<Number> neutralDeathRadiusAllPlayers = instruction.getValue("neutralDeathRadiusAllPlayers", Argument.NUMBER, 0);

        final Variable<Number> minMobLevel = instruction.getValue("minLevel", Argument.NUMBER, Double.NEGATIVE_INFINITY);
        final Variable<Number> maxMobLevel = instruction.getValue("maxLevel", Argument.NUMBER, Double.POSITIVE_INFINITY);
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        return new MythicMobKillObjective(instruction, targetAmount, names, minMobLevel, maxMobLevel, deathRadiusAllPlayers, neutralDeathRadiusAllPlayers, marked);
    }
}
