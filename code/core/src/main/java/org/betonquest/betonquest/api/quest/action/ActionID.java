package org.betonquest.betonquest.api.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.jetbrains.annotations.Nullable;

/**
 * ID of an Action.
 */
public class ActionID extends InstructionIdentifier {

    /**
     * Create a new Action ID.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     * @param pack         the package of the action
     * @param identifier   the complete identifier of the action
     * @throws QuestException if there is no such action
     */
    public ActionID(final Placeholders placeholders, final QuestPackageManager packManager, @Nullable final QuestPackage pack,
                    final String identifier) throws QuestException {
        super(placeholders, packManager, pack, identifier, "actions", "Action");
    }
}
