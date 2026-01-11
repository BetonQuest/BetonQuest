package org.betonquest.betonquest.id.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultReadableIdentifier;
import org.betonquest.betonquest.api.identifier.JournalMainPageIdentifier;

/**
 * The default implementation for {@link JournalMainPageIdentifier}s.
 */
public class DefaultJournalMainPageIdentifier extends DefaultReadableIdentifier implements JournalMainPageIdentifier {

    /**
     * The section name for the journal main page.
     */
    public static final String MAIN_PAGE_SECTION = "journal_main_page";

    /**
     * Creates a new journal main page identifier.
     *
     * @param pack       the package the identifier is related to.
     * @param identifier the identifier of the journal main page.
     * @throws QuestException if the identifier points to a non-existent section.
     */
    protected DefaultJournalMainPageIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, MAIN_PAGE_SECTION);
    }
}
