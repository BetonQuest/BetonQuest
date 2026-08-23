package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.lib.instruction.section.DefaultSectionInstruction;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.betonquest.betonquest.conversation.ConversationOptionType.PLAYER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DefaultConversationData}.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.UnitTestAssertionsShouldIncludeMessage")
class DefaultConversationDataTest {

    @Mock
    private BetonQuestLogger log;

    @Mock
    private QuestPackageManager packManager;

    @Mock
    private PlaceholderManager placeholders;

    @Mock
    private ConditionManager conditionManager;

    @Mock
    private ConversationProcessor conversationProcessor;

    @Mock
    private ParsedSectionTextCreator textCreator;

    @Mock
    private QuestPackage pack;

    @Mock
    private ConversationPublicData publicData;

    @Mock
    private ConversationIdentifier conversationID;

    @Mock
    private Profile profile;

    @BeforeEach
    void setUp() {
        when(publicData.conversationID()).thenReturn(conversationID);
        when(conversationID.getPackage()).thenReturn(pack);
    }

    @Test
    void testExtendsPropertiesUseFallbackWithPrimaryPrecedence() throws QuestException {
        final MemoryConfiguration config = baseConversation();
        config.set("player_options.root.extends", "fallback");
        config.set("player_options.root.properties.shared", "primary");
        config.set("player_options.fallback.properties.shared", "fallback");
        config.set("player_options.fallback.properties.inherited", "value");
        final DefaultConversationData conversation = createConversation(config);
        when(conditionManager.testAll(eq(profile), anyCollection())).thenReturn(true);

        final ConfigurationSection properties = assertDoesNotThrow(
                () -> conversation.getProperties(profile, new ResolvedOption(conversation, PLAYER, "root")));

        assertAll(
                () -> assertEquals("primary", properties.getString("shared")),
                () -> assertEquals("value", properties.getString("inherited"))
        );
    }

    @Test
    void testExtendsPropertiesRecursively() throws QuestException {
        final MemoryConfiguration config = baseConversation();
        config.set("player_options.root.extends", "middle");
        config.set("player_options.middle.extends", "fallback");
        config.set("player_options.fallback.properties.nested", "value");
        final DefaultConversationData conversation = createConversation(config);
        when(conditionManager.testAll(eq(profile), anyCollection())).thenReturn(true);

        final ConfigurationSection properties = conversation.getProperties(
                profile, new ResolvedOption(conversation, PLAYER, "root"));

        assertEquals("value", properties.getString("nested"));
    }

    @Test
    void testExtendsPropertiesUseFirstPassingCandidate() throws QuestException {
        final MemoryConfiguration config = baseConversation();
        config.set("player_options.root.extends", "failed,passing,unused");
        config.set("player_options.failed.properties.failed", "value");
        config.set("player_options.passing.properties.selected", "value");
        config.set("player_options.unused.properties.unused", "value");
        final DefaultConversationData conversation = createConversation(config);
        when(conditionManager.testAll(eq(profile), anyCollection())).thenReturn(false, true);

        final ConfigurationSection properties = conversation.getProperties(
                profile, new ResolvedOption(conversation, PLAYER, "root"));

        assertAll(
                () -> assertFalse(properties.contains("failed")),
                () -> assertEquals("value", properties.getString("selected")),
                () -> assertFalse(properties.contains("unused"))
        );
        verify(conditionManager, times(2)).testAll(eq(profile), anyCollection());
    }

    private MemoryConfiguration baseConversation() {
        final MemoryConfiguration config = new MemoryConfiguration();
        config.set("first", "start");
        config.createSection("NPC_options.start");
        config.createSection("player_options.root");
        return config;
    }

    private DefaultConversationData createConversation(final ConfigurationSection config) throws QuestException {
        final ArgumentParsers parsers = new DefaultArgumentParsers(mock(ItemManager.class),
                mock(IdentifierFactory.class), mock(TextParser.class), mock(Server.class), mock(Identifiers.class),
                textCreator);
        final SectionInstruction instruction = new DefaultSectionInstruction(parsers, placeholders, packManager, pack,
                config, mock(BetonQuestLoggerFactory.class));
        return new DefaultConversationData(log, packManager, placeholders, conditionManager, instruction,
                conversationProcessor, textCreator, config, publicData);
    }
}
