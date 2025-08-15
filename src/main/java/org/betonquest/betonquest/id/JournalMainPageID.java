package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Journal Main Page ID.
 */
public class JournalMainPageID extends InstructionIdentifier {

    /**
     * Creates new JournalMainPageID instance.
     *
     * @param pack       the package where the identifier was used in
     * @param identifier the identifier of the quest compass
     * @throws QuestException if the instruction could not be created or
     *                        when the main page could not be resolved with the given identifier
     */
    public JournalMainPageID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "journal_main_page", "Journal Main Page");
    }
}
