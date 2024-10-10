package org.betonquest.betonquest.quest.variable.constant;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.jetbrains.annotations.Nullable;

/**
 * A variable that always evaluates to the same constant value.
 */
public class ConstantVariable implements NullableVariable {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The constant value.
     */
    private final VariableString constant;

    /**
     * Create a variable that always evaluates to the given constant.
     *
     * @param log      The logger.
     * @param constant The constant value.
     */
    public ConstantVariable(final BetonQuestLogger log, final VariableString constant) {
        this.log = log;
        this.constant = constant;
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        try {
            return constant.getValue(profile);
        } catch (final QuestRuntimeException e) {
            log.warn("Could not resolve constant variable: " + e.getMessage(), e);
            return "";
        }
    }
}
