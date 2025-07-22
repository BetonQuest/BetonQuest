package org.betonquest.betonquest.quest.condition.eval;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.kernel.processor.adapter.ConditionAdapter;
import org.betonquest.betonquest.kernel.registry.TypeFactory;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * A condition which evaluates to another condition.
 */
public class EvalCondition implements NullableCondition {
    /**
     * The condition type registry providing factories to parse the evaluated instruction.
     */
    private final ConditionTypeRegistry conditionTypeRegistry;

    /**
     * The quest package to relate the condition to.
     */
    private final QuestPackage pack;

    /**
     * The evaluation input.
     */
    private final Variable<String> evaluation;

    /**
     * Creates a new Eval condition.
     *
     * @param conditionTypeRegistry the condition type registry providing factories to parse the evaluated instruction
     * @param pack                  the quest package to relate the condition to
     * @param evaluation            the evaluation input
     */
    public EvalCondition(final ConditionTypeRegistry conditionTypeRegistry, final QuestPackage pack, final Variable<String> evaluation) {
        this.conditionTypeRegistry = conditionTypeRegistry;
        this.pack = pack;
        this.evaluation = evaluation;
    }

    /**
     * Constructs a condition with a given instruction and returns it.
     *
     * @param instruction           the instruction string to parse
     * @param conditionTypeRegistry the condition type registry providing factories to parse the evaluated instruction
     * @param pack                  the quest package to relate the condition to
     * @return the condition
     * @throws QuestException if the condition could not be created
     */
    public static ConditionAdapter createCondition(final ConditionTypeRegistry conditionTypeRegistry, final QuestPackage pack, final String instruction) throws QuestException {
        final Instruction conditionInstruction = new Instruction(pack, null, instruction);
        final TypeFactory<ConditionAdapter> conditionFactory = conditionTypeRegistry.getFactory(conditionInstruction.getPart(0));
        return conditionFactory.parseInstruction(conditionInstruction);
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return createCondition(conditionTypeRegistry, pack, evaluation.getValue(profile)).check(profile);
    }
}
