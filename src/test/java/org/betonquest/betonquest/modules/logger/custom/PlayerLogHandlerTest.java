package org.betonquest.betonquest.modules.logger.custom;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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

            final Audience audience1 = getAudience(playerHandler, "Package1", audiences);
            final Audience audience2 = getAudience(playerHandler, "Package2", audiences);

            createLogMessages(logger);
            assertLogMessages(audience1, audience2);
        }
    }

    private Audience getAudience(final PlayerLogHandler playerHandler, final String pack, final BukkitAudiences audiences) {
        final UUID uuid = UUID.randomUUID();
        playerHandler.addFilter(uuid, pack, Level.INFO);

        final Audience audience = mock(Audience.class);
        when(audiences.player(uuid)).thenReturn(audience);
        return audience;
    }

    private void createLogMessages(final Logger logger) {
        final QuestPackage pack1 = mock(QuestPackage.class);
        final QuestPackage pack2 = mock(QuestPackage.class);
        when(pack1.getPackagePath()).thenReturn("Package1");
        when(pack2.getPackagePath()).thenReturn("Package2");

        final BetonQuestLogRecord record = new BetonQuestLogRecord(null, null, Level.INFO, "Message");
        final BetonQuestLogRecord record1 = new BetonQuestLogRecord(null, pack1, Level.INFO, "Message 1");
        final BetonQuestLogRecord record2 = new BetonQuestLogRecord(null, pack2, Level.INFO, "Message 2");
        logger.log(record);
        logger.log(record1);
        logger.log(record2);
    }

    private void assertLogMessages(final Audience audience1, final Audience audience2) {
        verify(audience1, times(1)).sendMessage(any(Component.class));
        verify(audience1, times(1)).sendMessage(any(Component.class));

        verify(audience2, times(1)).sendMessage(any(Component.class));
        verify(audience2, times(1)).sendMessage(any(Component.class));
    }
}
