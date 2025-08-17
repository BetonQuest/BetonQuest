package org.betonquest.betonquest.quest.condition.eval;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;

/**
 * A factory for creating Eval conditions.
 */
public class EvalConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The quest package manager to use for the instruction.
     */
    private final QuestPackageManager questPackageManager;

    /**
     * The condition type registry providing factories to parse the evaluated instruction.
     */
    private final ConditionTypeRegistry conditionTypeRegistry;

    /**
     * Creates a new Eval condition factory.
     *
     * @param questPackageManager   the quest package manager to use for the instruction
     * @param conditionTypeRegistry the condition type registry providing factories to parse the evaluated instruction
     */
    public EvalConditionFactory(final QuestPackageManager questPackageManager, final ConditionTypeRegistry conditionTypeRegistry) {
        this.questPackageManager = questPackageManager;
        this.conditionTypeRegistry = conditionTypeRegistry;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return parseEvalCondition(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return parseEvalCondition(instruction);
    }

    private NullableConditionAdapter parseEvalCondition(final Instruction instruction) throws QuestException {
        final String rawInstruction = String.join(" ", instruction.getValueParts());
        return new NullableConditionAdapter(new EvalCondition(questPackageManager, conditionTypeRegistry, instruction.getPackage(),
                instruction.get(rawInstruction, Argument.STRING)));
    }
}
