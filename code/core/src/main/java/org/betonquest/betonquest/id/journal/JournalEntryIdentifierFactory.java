package org.betonquest.betonquest.id.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
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
        final MultiConfiguration config = identifier.getPackage().getConfig();
        final String path = DefaultJournalEntryIdentifier.JOURNAL_SECTION + config.options().pathSeparator() + identifier.get();
        if (!config.isString(path) && !config.isConfigurationSection(path)) {
            throw new QuestException("JournalEntry '%s' does not define a instruction in section '%s'!"
                    .formatted(identifier.getFull(), DefaultJournalEntryIdentifier.JOURNAL_SECTION));
        }
        return identifier;
    }
}
