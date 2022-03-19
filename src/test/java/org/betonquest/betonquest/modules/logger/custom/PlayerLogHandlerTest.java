package org.betonquest.betonquest.modules.logger.custom;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test for the {@link PlayerLogHandler}.
 */
@ExtendWith(BetonQuestLoggerService.class)
class PlayerLogHandlerTest {

    /**
     * Default constructor.
     */
    public PlayerLogHandlerTest() {
    }

    @Test
    void testLogHistory() {
        try (BukkitAudiences audiences = mock(BukkitAudiences.class)) {
            final Logger logger = LogValidator.getSilentLogger();
            final PlayerLogHandler playerHandler = new PlayerLogHandler(audiences);

            playerHandler.setFormatter(new ChatLogFormatter());
            logger.addHandler(playerHandler);

            final Audience audience1 = getAudience(playerHandler, audiences, "Package1", "Package3");
            final Audience audience2 = getAudience(playerHandler, audiences, "Package2", "Package3");

            createLogMessages(logger);
            assertLogMessages(audience1, audience2);
        }
    }

    private Audience getAudience(final PlayerLogHandler playerHandler, final BukkitAudiences audiences, final String... packs) {
        final UUID uuid = UUID.randomUUID();
        for (final String pack : packs) {
            playerHandler.addFilter(uuid, pack, Level.INFO);
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

        final BetonQuestLogRecord record1 = new BetonQuestLogRecord(null, pack1, Level.INFO, "Message 1");
        final BetonQuestLogRecord record2 = new BetonQuestLogRecord(null, pack2, Level.INFO, "Message 2");
        final BetonQuestLogRecord record3 = new BetonQuestLogRecord(null, pack3, Level.INFO, "Message 3");
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

        assertEquals("§7[§8BQ | §7]§r <Package1> §fMessage 1", serializer.serialize(components.get(0)), "");
        assertEquals("§7[§8BQ | §7]§r <Package3> §fMessage 3", serializer.serialize(components.get(1)), "");
        assertEquals("§7[§8BQ | §7]§r <Package2> §fMessage 2", serializer.serialize(components.get(2)), "");
        assertEquals("§7[§8BQ | §7]§r <Package3> §fMessage 3", serializer.serialize(components.get(3)), "");
    }
}
