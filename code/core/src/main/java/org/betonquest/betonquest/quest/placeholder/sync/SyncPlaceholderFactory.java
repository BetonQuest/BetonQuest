package org.betonquest.betonquest.quest.placeholder.sync;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholder;
import org.betonquest.betonquest.quest.placeholder.eval.EvalPlaceholder;
import org.betonquest.betonquest.quest.placeholder.eval.EvalPlaceholderFactory;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for creating {@link EvalPlaceholder}s.
 */
public class SyncPlaceholderFactory extends EvalPlaceholderFactory {

    /**
     * Create a new SyncPlaceholderFactory.
     */
    public SyncPlaceholderFactory() {
        super();
    }

    @Override
    protected NullablePlaceholder parseNullablePlaceholder(final Instruction instruction) throws QuestException {
        final NullablePlaceholder nullablePlaceholder = super.parseNullablePlaceholder(instruction);
        return new NullablePlaceholder() {
            @Override
            public String getValue(@Nullable final Profile profile) throws QuestException {
                return nullablePlaceholder.getValue(profile);
            }

            @Override
            public boolean isPrimaryThreadEnforced() {
                return true;
            }
        };
    }
}
