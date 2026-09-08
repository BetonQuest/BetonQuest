package org.betonquest.betonquest.item.typehandler;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.common.component.font.DefaultFontRegistry;
import org.betonquest.betonquest.api.common.component.font.Font;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.lib.font.FontRetriever;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD") // will be removed anyway
class BookHandlerTest {

    private static final String ITEM = """
            simple WRITTEN_BOOK
            "title:Malleus Maleficarum"
            "author:&eGallus Anonymus"
            "text:@[minimessage]Lorem ipsum dolor sit amet, <newline>consectetur adipiscing elit. |Pellentesque ligula urna(...)"
            """;

    @SuppressWarnings({"DataFlowIssue", "NullAway"})
    private Instruction instruction() throws QuestException {
        final TextParser textParser = (message) -> LegacyComponentSerializer.legacySection().deserialize(
                ChatColor.translateAlternateColorCodes('&', message.replace("_", " ")));

        final ArgumentParsers argumentParsers = new DefaultArgumentParsers(
                (profile, itemIdentifier) -> null,
                (source, input) -> null,
                textParser, null, null, null
        );

        return new DefaultInstruction(PlaceholderProcessor.EMPTY_PLACEHOLDER, Map::of, null,
                null, argumentParsers, ITEM);
    }

    private FontRegistry fontRegistry() {
        final Key defaultkey = Key.key("default");
        final File fontFolder = new File("src/main/resources/fonts");
        final FontRetriever fontRetriever = new FontRetriever();
        final DefaultFontRegistry fontRegistry = new DefaultFontRegistry(defaultkey);
        final List<Pair<Key, Font>> fonts = fontRetriever.loadFonts(fontFolder.toPath());
        fonts.forEach(pair -> fontRegistry.registerFont(pair.getKey(), pair.getValue()));
        return fontRegistry;
    }

    @Test
    void test() throws QuestException {
        final BookPageWrapper bookPageWrapper = new BookPageWrapper(fontRegistry(), 114, 14);

        final BookHandler bookHandler = new BookHandler(bookPageWrapper);
        final Instruction instruction = instruction();

        final long start = System.currentTimeMillis();

        final Attribute attribute = bookHandler.parse(instruction);
        if (attribute != null) {
            for (int i = 0; i < 1_000_000; i++) {
                final ResolvedAttribute<?> resolved = attribute.resolve(null);
            }
        }
        final long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
