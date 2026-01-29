package org.betonquest.betonquest.id.journal;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.betonquest.betonquest.api.identifier.JournalMainPageIdentifier;

/**
 * The default implementation for {@link JournalMainPageIdentifier}s.
 */
public class DefaultJournalMainPageIdentifier extends DefaultIdentifier implements JournalMainPageIdentifier {

    /**
     * The section name for main pages.
     */
    public static final String MAIN_PAGE_SECTION = "journal_main_page";

    /**
     * Creates a new journal main page identifier.
     *
     * @param pack       the package the identifier is related to.
     * @param identifier the identifier of the journal main page.
     */
    protected DefaultJournalMainPageIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier);
    }
}
