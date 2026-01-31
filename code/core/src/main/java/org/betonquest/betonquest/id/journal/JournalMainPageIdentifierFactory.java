package org.betonquest.betonquest.id.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.JournalMainPageIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link JournalMainPageIdentifier}s.
 */
public class JournalMainPageIdentifierFactory extends DefaultIdentifierFactory<JournalMainPageIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public JournalMainPageIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public JournalMainPageIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final Map.Entry<QuestPackage, String> entry = parse(source, input);
        final DefaultJournalMainPageIdentifier identifier = new DefaultJournalMainPageIdentifier(entry.getKey(), entry.getValue());
        return requireSection(identifier, DefaultJournalMainPageIdentifier.MAIN_PAGE_SECTION);
    }
}
