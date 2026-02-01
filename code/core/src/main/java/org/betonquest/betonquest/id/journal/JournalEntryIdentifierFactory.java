package org.betonquest.betonquest.id.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link JournalEntryIdentifier}s.
 */
public class JournalEntryIdentifierFactory extends DefaultIdentifierFactory<JournalEntryIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public JournalEntryIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager, "JournalEntry");
    }

    @Override
    public JournalEntryIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        final DefaultJournalEntryIdentifier identifier = new DefaultJournalEntryIdentifier(entry.getKey(), entry.getValue());
        return requireInstruction(identifier, DefaultJournalEntryIdentifier.JOURNAL_SECTION);
    }
}
