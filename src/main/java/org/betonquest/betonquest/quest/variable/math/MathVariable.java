package org.betonquest.betonquest.quest.variable.math;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.util.math.tokens.Token;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * This variable evaluates the given calculation and returns the result.
 */
public class MathVariable implements NullableVariable {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Pack used for log identification.
     */
    private final QuestPackage pack;

    /**
     * The full calculation token.
     */
    @SuppressWarnings("deprecation")
    private final Token calculation;

    /**
     * Create a math variable from the given calculation.
     *
     * @param log         the custom {@link BetonQuestLogger} instance for this class
     * @param pack        the pack used for log identification.
     * @param calculation calculation to parse
     */
    @SuppressWarnings("deprecation")
    public MathVariable(final BetonQuestLogger log, final QuestPackage pack, final Token calculation) {
        this.log = log;
        this.pack = pack;
        this.calculation = calculation;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        final double value;
        try {
            value = this.calculation.resolve(profile);
        } catch (final QuestException e) {
            log.warn(pack, "Could not calculate '" + calculation + "' (" + e.getMessage() + "). Returning 0 instead.", e);
            return "0";
        }
        if (value % 1 == 0) {
            return String.format(Locale.US, "%.0f", value);
        }
        return String.valueOf(value);
    }
}
