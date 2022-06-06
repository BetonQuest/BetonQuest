package org.betonquest.betonquest.modules.logger.custom.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.format.ChatFormatter;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test for the {@link ChatHandler}.
 */
@ExtendWith(BetonQuestLoggerService.class)
class ChatHandlerTest {
    /**
     * Default constructor.
     */
    public ChatHandlerTest() {
        // Empty
    }

    @Test
    void testLoggingToPlayersChat() {
        try (BukkitAudiences audiences = mock(BukkitAudiences.class)) {
            final PlayerFilter playerFilter = mock(PlayerFilter.class);
            final UUID uuid1 = UUID.randomUUID();
            final UUID uuid2 = UUID.randomUUID();
            when(playerFilter.getUUIDs()).thenReturn(Set.of(uuid1, uuid2));
            final Audience audience1 = getAudience(playerFilter, audiences, uuid1, "Package1", "Package3");
            final Audience audience2 = getAudience(playerFilter, audiences, uuid2, "Package2", "Package3");

            final ChatHandler playerHandler = new ChatHandler(playerFilter, audiences);
            playerHandler.setFormatter(new ChatFormatter());

            final Logger logger = LogValidator.getSilentLogger();
            logger.addHandler(playerHandler);

            createLogMessages(logger);
            assertLogMessages(audience1, audience2);
        }
    }

    private Audience getAudience(final PlayerFilter playerFilter, final BukkitAudiences audiences, final UUID uuid, final String... packs) {
        for (final String pack : packs) {
            when(playerFilter.match(uuid, pack, Level.INFO)).thenReturn(true);
        }

        final Audience audience = mock(Audience.class);
        when(audiences.player(uuid)).thenReturn(audience);
        return audience;
    }

    private void createLogMessages(final Logger logger) {
        final QuestPackage pack1 = mock(QuestPackage.class);
        final QuestPackage pack2 = mock(QuestPackage.class);
        final QuestPackage pack3 = mock(QuestPackage.class);
        when(pack1.getPackagePath()).thenReturn("Package1");
        when(pack2.getPackagePath()).thenReturn("Package2");
        when(pack3.getPackagePath()).thenReturn("Package3");

        final Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("BetonQuest");

        final BetonQuestLogRecord record1 = new BetonQuestLogRecord(plugin, pack1, Level.INFO, "Message 1");
        final BetonQuestLogRecord record2 = new BetonQuestLogRecord(plugin, pack2, Level.INFO, "Message 2");
        final BetonQuestLogRecord record3 = new BetonQuestLogRecord(plugin, pack3, Level.INFO, "Message 3");
        logger.log(record1);
        logger.log(record2);
        logger.log(record3);
    }

    private void assertLogMessages(final Audience audience1, final Audience audience2) {
        final ArgumentCaptor<Component> argumentCaptor = ArgumentCaptor.forClass(Component.class);

        verify(audience1, times(2)).sendMessage(argumentCaptor.capture());
        verify(audience2, times(2)).sendMessage(argumentCaptor.capture());

        final List<Component> components = argumentCaptor.getAllValues();

        final PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();

        assertEquals("<Package1> §fMessage 1", serializer.serialize(components.get(0)), "Audience 1 should first receive 'Message 1'");
        assertEquals("<Package3> §fMessage 3", serializer.serialize(components.get(1)), "Audience 1 should then receive 'Message 3'");
        assertEquals("<Package2> §fMessage 2", serializer.serialize(components.get(2)), "Audience 2 should first receive 'Message 2'");
        assertEquals("<Package3> §fMessage 3", serializer.serialize(components.get(3)), "Audience 2 should then receive 'Message 3'");
    }
}
