package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidFieldNameMatchingMethodName"})
public class ConditionID extends ID {

    private final boolean inverted;

    public ConditionID(@Nullable final QuestPackage pack, final String identifier) throws ObjectNotFoundException, QuestException {
        super(pack, removeExclamationMark(identifier), "conditions", "Condition");
        this.inverted = !identifier.isEmpty() && identifier.charAt(0) == '!';
    }

    private static String removeExclamationMark(final String identifier) {
        if (!identifier.isEmpty() && identifier.charAt(0) == '!') {
            return identifier.substring(1);
        }
        return identifier;
    }

    public boolean inverted() {
        return inverted;
    }
}
