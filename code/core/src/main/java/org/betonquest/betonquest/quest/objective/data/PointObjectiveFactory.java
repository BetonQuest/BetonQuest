package org.betonquest.betonquest.quest.objective.data;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.condition.number.Operation;

/**
 * Factory to create {@link PointObjective}s from {@link Instruction}s.
 */
public class PointObjectiveFactory implements ObjectiveFactory {
    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Create a new Point Objective Factory.
     *
     * @param playerDataStorage the storage for player data
     */
    public PointObjectiveFactory(final PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<String> category = instruction.get(PackageArgument.IDENTIFIER);
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER);
        final Variable<CountingMode> mode = instruction.getValue("mode", Argument.ENUM(CountingMode.class), CountingMode.TOTAL);
        final Variable<Operation> operation = instruction.getValue("operation", Operation::fromSymbol, Operation.GREATER_EQUAL);
        return new PointObjective(instruction, playerDataStorage, category, targetAmount, mode, operation);
    }
}
