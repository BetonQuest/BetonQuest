package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.jetbrains.annotations.Nullable;

/**
 * ID of a Condition.
 */
public class ConditionID extends InstructionIdentifier {

    /**
     * If the condition is used inverted.
     */
    private final boolean isInverted;

    /**
     * Create a new Condition ID.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     * @param pack         the package of the condition
     * @param identifier   the complete identifier of the condition, inclusive exclamation mark for negating
     * @throws QuestException if there is no such condition
     */
    public ConditionID(final Placeholders placeholders, final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(placeholders, packManager, pack, removeExclamationMark(identifier), "conditions", "Condition");
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
