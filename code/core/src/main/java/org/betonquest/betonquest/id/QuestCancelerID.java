package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a quest canceler ID.
 */
public class QuestCancelerID extends InstructionIdentifier {

    /**
     * Creates new QuestCancelerID instance.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     * @param pack         the package where the identifier was used in
     * @param identifier   the identifier of the quest canceler
     * @throws QuestException if the instruction could not be created or
     *                        when the quest canceler could not be resolved with the given identifier
     */
    public QuestCancelerID(final Placeholders placeholders, final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(placeholders, packManager, pack, identifier, "cancel", "Quest Canceler");
    }
}
