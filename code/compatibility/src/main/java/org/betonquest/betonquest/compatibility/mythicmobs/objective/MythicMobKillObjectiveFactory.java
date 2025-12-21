package org.betonquest.betonquest.compatibility.mythicmobs.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
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
        final Variable<List<String>> names = instruction.getList(DefaultArgumentParsers.STRING);
        final Variable<IdentifierMode> mode = instruction.getValue("mode", DefaultArgumentParsers.forEnum(IdentifierMode.class), IdentifierMode.INTERNAL_NAME);
        final Variable<Number> targetAmount = instruction.getValue("amount", DefaultArgumentParsers.NUMBER_NOT_LESS_THAN_ONE, 1);

        final Variable<Number> deathRadiusAllPlayers = instruction.getValue("deathRadiusAllPlayers", DefaultArgumentParsers.NUMBER, 0);
        final Variable<Number> neutralDeathRadiusAllPlayers = instruction.getValue("neutralDeathRadiusAllPlayers", DefaultArgumentParsers.NUMBER, 0);

        final Variable<Number> minMobLevel = instruction.getValue("minLevel", DefaultArgumentParsers.NUMBER, Double.NEGATIVE_INFINITY);
        final Variable<Number> maxMobLevel = instruction.getValue("maxLevel", DefaultArgumentParsers.NUMBER, Double.POSITIVE_INFINITY);
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        return new MythicMobKillObjective(instruction, targetAmount, names, mode, minMobLevel, maxMobLevel, deathRadiusAllPlayers, neutralDeathRadiusAllPlayers, marked);
    }
}
