package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.factory.IdentifierRegistry;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.id.item.ItemIdentifierFactory;
import org.betonquest.betonquest.id.journal.JournalEntryIdentifierFactory;
import org.betonquest.betonquest.kernel.registry.quest.IdentifierTypeRegistry;
import org.bukkit.Server;

import static org.mockito.Mockito.*;

/**
 * Instruction where everything except package and instruction string is mocked.
 */
public class MockedInstruction extends DefaultInstruction {

    /**
     * Creates a new mocked instruction.
     *
     * @param pack        the source package
     * @param instruction the instruction string
     * @throws QuestException when the instruction could not be created
     */
    public MockedInstruction(final QuestPackage pack, final String instruction) throws QuestException {
        super(mock(Placeholders.class), mock(QuestPackageManager.class), pack, null, parsers(), instruction);
    }

    private static ArgumentParsers parsers() throws QuestException {
        final IdentifierRegistry identifierRegistry = new IdentifierTypeRegistry(mock(BetonQuestLogger.class));
        identifierRegistry.register(ItemIdentifier.class, new ItemIdentifierFactory(mock(QuestPackageManager.class)));
        identifierRegistry.register(JournalEntryIdentifier.class, new JournalEntryIdentifierFactory(mock(QuestPackageManager.class)));
        return new DefaultArgumentParsers((i, p) -> null, mock(TextParser.class), mock(Server.class), identifierRegistry);
    }
}
