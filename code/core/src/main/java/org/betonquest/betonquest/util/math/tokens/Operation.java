package org.betonquest.betonquest.util.math.tokens;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.util.math.Operator;
import org.jetbrains.annotations.Nullable;

/**
 * An operation performed on two tokens.
 *
 * @deprecated This should be replaced with a real expression parsing lib
 */
@Deprecated
public class Operation implements Token {

    /**
     * First value, left of the operator.
     */
    private final Token val1;

    /**
     * The operator.
     */
    private final Operator operator;

    /**
     * Second value, right of the operator.
     */
    private final Token val2;

    /**
     * Creates a new operation.
     *
     * @param val1     left token
     * @param operator operator
     * @param val2     right token
     */
    public Operation(final Token val1, final Operator operator, final Token val2) {
        this.val1 = val1;
        this.operator = operator;
        this.val2 = val2;
    }

    @Override
    public double resolve(@Nullable final Profile profile) throws QuestException {
        return operator.calculate(val1.resolve(profile), val2.resolve(profile));
    }

    @Override
    public String toString() {
        return val1.toString() + operator + val2;
    }
}
