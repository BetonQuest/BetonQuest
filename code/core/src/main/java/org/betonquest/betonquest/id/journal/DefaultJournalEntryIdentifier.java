package org.betonquest.betonquest.id.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultReadableIdentifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;

/**
 * The default implementation for {@link JournalEntryIdentifier}s.
 */
public class DefaultJournalEntryIdentifier extends DefaultReadableIdentifier implements JournalEntryIdentifier {

    /**
     * The section name for journal entries.
     */
    public static final String JOURNAL_SECTION = "journal";

    /**
     * Creates a new journal entry identifier.
     *
     * @param pack       the package the identifier is related to.
     * @param identifier the identifier of the journal entry.
     * @throws QuestException if the identifier points to a non-existent section.
     */
    protected DefaultJournalEntryIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, JOURNAL_SECTION);
    }
}
