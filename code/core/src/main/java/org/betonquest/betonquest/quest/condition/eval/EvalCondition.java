package org.betonquest.betonquest.quest.condition.eval;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.kernel.processor.adapter.ConditionAdapter;
import org.betonquest.betonquest.kernel.registry.QuestTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * A condition which evaluates to another condition.
 */
public class EvalCondition implements NullableCondition {

    /**
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

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
     * @param variables             the variable processor to create and resolve variables
     * @param packManager           the quest package manager to get quest packages from
     * @param conditionTypeRegistry the condition type registry providing factories to parse the evaluated instruction
     * @param pack                  the quest package to relate the condition to
     * @param evaluation            the evaluation input
     */
    public EvalCondition(final Variables variables, final QuestPackageManager packManager, final ConditionTypeRegistry conditionTypeRegistry,
                         final QuestPackage pack, final Variable<String> evaluation) {
        this.variables = variables;
        this.packManager = packManager;
        this.conditionTypeRegistry = conditionTypeRegistry;
        this.pack = pack;
        this.evaluation = evaluation;
    }

    /**
     * Constructs a condition with a given instruction and returns it.
     *
     * @param variables             the variable processor to create and resolve variables
     * @param packManager           the quest package manager to get quest packages from
     * @param instruction           the instruction string to parse
     * @param conditionTypeRegistry the condition type registry providing factories to parse the evaluated instruction
     * @param pack                  the quest package to relate the condition to
     * @return the condition
     * @throws QuestException if the condition could not be created
     */
    public static ConditionAdapter createCondition(final Variables variables, final QuestPackageManager packManager, final QuestTypeRegistry<PlayerCondition, PlayerlessCondition, ConditionAdapter> conditionTypeRegistry, final QuestPackage pack, final String instruction) throws QuestException {
        final Instruction conditionInstruction = new Instruction(variables, packManager, pack, null, instruction);
        final TypeFactory<ConditionAdapter> conditionFactory = conditionTypeRegistry.getFactory(conditionInstruction.getPart(0));
        return conditionFactory.parseInstruction(conditionInstruction);
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return createCondition(variables, packManager, conditionTypeRegistry, pack, evaluation.getValue(profile)).check(profile);
    }
}
