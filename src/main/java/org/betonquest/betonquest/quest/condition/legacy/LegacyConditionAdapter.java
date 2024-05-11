package org.betonquest.betonquest.quest.condition.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.api.quest.condition.StaticCondition;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Adapter for {@link Condition} and {@link StaticCondition} to fit the old convention of
 * {@link org.betonquest.betonquest.api.Condition Legacy Condition}.
 */
public class LegacyConditionAdapter extends org.betonquest.betonquest.api.Condition {
    /**
     * The normal condition to be adapted.
     */
    private final Condition condition;

    /**
     * The "static" condition to be adapted if present. May be {@code null}!
     */
    @Nullable
    private final StaticCondition staticCondition;

    /**
     * Create a legacy condition from an {@link Event} and a {@link StaticCondition}. If the condition does not support
     * "static" execution ({@code staticness = false}) then no {@link StaticCondition} instance must be provided.
     *
     * @param instruction     instruction used to create the conditions
     * @param condition       condition to use
     * @param staticCondition static condition to use or null if no static execution is supported
     */
    public LegacyConditionAdapter(final Instruction instruction, final Condition condition, @Nullable final StaticCondition staticCondition) {
        super(instruction, false);
        this.condition = condition;
        this.staticCondition = staticCondition;
        staticness = staticCondition != null;
        persistent = true;
    }

    @Override
    protected Boolean execute(@Nullable final Profile profile) throws QuestRuntimeException {
        if (profile == null) {
            Objects.requireNonNull(staticCondition);
            return staticCondition.check();
        } else {
            return condition.check(profile);
        }
    }
}