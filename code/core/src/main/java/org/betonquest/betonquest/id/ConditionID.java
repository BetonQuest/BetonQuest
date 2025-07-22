package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * ID of a Condition.
 */
public class ConditionID extends ID {

    /**
     * If the condition is used inverted.
     */
    private final boolean isInverted;

    /**
     * Create a new Condition ID.
     *
     * @param pack       the package of the condition
     * @param identifier the complete identifier of the condition, inclusive exclamation mark for negating
     * @throws QuestException if there is no such condition
     */
    public ConditionID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, removeExclamationMark(identifier), "conditions", "Condition");
        this.isInverted = !identifier.isEmpty() && identifier.charAt(0) == '!';
    }

    private static String removeExclamationMark(final String identifier) {
        if (!identifier.isEmpty() && identifier.charAt(0) == '!') {
            return identifier.substring(1);
        }
        return identifier;
    }

    /**
     * If the Condition is defined as inverted.
     *
     * @return if the condition should be interpreted inverted
     */
    public boolean inverted() {
        return isInverted;
    }
}
