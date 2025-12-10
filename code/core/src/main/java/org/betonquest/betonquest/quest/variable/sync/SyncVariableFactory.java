package org.betonquest.betonquest.quest.variable.sync;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.api.quest.variable.thread.PrimaryServerThreadNullableVariable;
import org.betonquest.betonquest.quest.variable.eval.EvalVariable;
import org.betonquest.betonquest.quest.variable.eval.EvalVariableFactory;

/**
 * Factory for creating {@link EvalVariable}s.
 */
public class SyncVariableFactory extends EvalVariableFactory {

    /**
     * {@link PrimaryServerThreadData} used for checking the condition on the main thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new SyncVariableFactory.
     *
     * @param data the data used for checking the condition on the main thread
     */
    public SyncVariableFactory(final PrimaryServerThreadData data) {
        super();
        this.data = data;
    }

    @Override
    protected NullableVariable parseNullableVariable(final Instruction instruction) throws QuestException {
        final NullableVariable nullableVariable = super.parseNullableVariable(instruction);
        return new PrimaryServerThreadNullableVariable(nullableVariable, data);
    }
}
