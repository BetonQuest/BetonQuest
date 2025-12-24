package org.betonquest.betonquest.quest.variable.sync;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.quest.variable.eval.EvalVariable;
import org.betonquest.betonquest.quest.variable.eval.EvalVariableFactory;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for creating {@link EvalVariable}s.
 */
public class SyncVariableFactory extends EvalVariableFactory {

    /**
     * Create a new SyncVariableFactory.
     */
    public SyncVariableFactory() {
        super();
    }

    @Override
    protected NullableVariable parseNullableVariable(final Instruction instruction) throws QuestException {
        final NullableVariable nullableVariable = super.parseNullableVariable(instruction);
        return new NullableVariable() {
            @Override
            public String getValue(@Nullable final Profile profile) throws QuestException {
                return nullableVariable.getValue(profile);
            }

            @Override
            public boolean isPrimaryThreadEnforced() {
                return true;
            }
        };
    }
}
