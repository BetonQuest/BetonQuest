package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a journal entry ID.
 */
public class JournalEntryID extends ID {

    /**
     * Creates new JournalEntryID instance.
     *
     * @param pack       the package where the identifier was used in
     * @param identifier the identifier of the quest compass
     * @throws QuestException if the instruction could not be created or
     *                        when the journal entry could not be resolved with the given identifier
     */
    public JournalEntryID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "journal", "Journal Entry");
    }
}
