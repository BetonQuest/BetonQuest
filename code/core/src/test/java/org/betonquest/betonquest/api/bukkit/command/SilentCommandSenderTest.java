package org.betonquest.betonquest.api.bukkit.command;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * This class tests the {@link SilentCommandSender} class.
 */
@ExtendWith(MockitoExtension.class)
class SilentCommandSenderTest {

    @Mock
    protected BetonQuestLogger logger;

    /**
     * The command sender to use.
     */
    private CommandSender sender;

    /**
     * The silent command sender to use.
     */
    private SilentCommandSender silentSender;

    @BeforeEach
    void setUp() {
        sender = getCommandSender();
        silentSender = getSilentCommandSender();
    }

    /**
     * Get the command sender to use.
     *
     * @return the command sender to use
     */
    protected CommandSender getCommandSender() {
        return mock(CommandSender.class);
    }

    /**
     * Get the silent command sender to test.
     *
     * @return the silent command sender to test
     */
    protected SilentCommandSender getSilentCommandSender() {
        return new SilentCommandSender(logger, sender);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void send_message() {
        silentSender.sendMessage("test1");
        verify(sender, never()).sendMessage(anyString());
        verify(logger, times(1)).debug("Silently sending message to console: 'test1'.");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void send_message_multiple() {
        silentSender.sendMessage("test2", "test3");
        verify(sender, never()).sendMessage(anyString(), anyString());
        verify(logger, times(1)).debug("Silently sending messages to console: 'test2, test3'.");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void send_message_sender_null() {
        silentSender.sendMessage(null, "test4");
        verify(sender, never()).sendMessage(any(UUID.class), anyString());
        verify(logger, times(1)).debug("Silently sending message to console: 'test4'.");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void send_message_uuid_null() {
        silentSender.sendMessage((UUID) null, "test5", "test6");
        verify(sender, never()).sendMessage(any(UUID.class), anyString(), anyString());
        verify(logger, times(1)).debug("Silently sending messages to console: 'test5, test6'.");
        verifyNoMoreInteractions(logger);
    }

    @Test
    void get_server() {
        when(sender.getServer()).thenReturn(mock(org.bukkit.Server.class));
        silentSender.getServer();
        verify(sender, times(1)).getServer();
    }

    @Test
    void get_name() {
        when(sender.getName()).thenReturn("test");
        silentSender.getName();
        verify(sender, times(1)).getName();
    }

    @Test
    void spigot() {
        when(sender.spigot()).thenReturn(mock(CommandSender.Spigot.class));
        silentSender.spigot();
        verify(sender, times(1)).spigot();
    }

    @Test
    void name() {
        when(sender.name()).thenReturn(mock(Component.class));
        silentSender.name();
        verify(sender, times(1)).name();
    }

    @Test
    void is_permission_set() {
        silentSender.isPermissionSet("test");
        verify(sender, times(1)).isPermissionSet(anyString());
    }

    @Test
    void is_permission_set_permission_class() {
        silentSender.isPermissionSet(mock(org.bukkit.permissions.Permission.class));
        verify(sender, times(1)).isPermissionSet(any(org.bukkit.permissions.Permission.class));
    }

    @Test
    void has_permission() {
        silentSender.hasPermission("test");
        verify(sender, times(1)).hasPermission(anyString());
    }

    @Test
    void has_permission_permission_class() {
        silentSender.hasPermission(mock(org.bukkit.permissions.Permission.class));
        verify(sender, times(1)).hasPermission(any(org.bukkit.permissions.Permission.class));
    }

    @Test
    void add_attachment() {
        when(sender.addAttachment(any(org.bukkit.plugin.Plugin.class))).thenReturn(mock(org.bukkit.permissions.PermissionAttachment.class));
        silentSender.addAttachment(mock(org.bukkit.plugin.Plugin.class));
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class));
    }

    @Test
    void add_attachment_name_and_value() {
        when(sender.addAttachment(any(org.bukkit.plugin.Plugin.class), anyString(), anyBoolean())).thenReturn(mock(org.bukkit.permissions.PermissionAttachment.class));
        silentSender.addAttachment(mock(org.bukkit.plugin.Plugin.class), "test", true);
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class), anyString(), anyBoolean());
    }

    @Test
    void add_attachment_name_value_and_ticks() {
        silentSender.addAttachment(mock(org.bukkit.plugin.Plugin.class), "test", true, 1);
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class), anyString(), anyBoolean(), anyInt());
    }

    @Test
    void add_attachment_ticks() {
        silentSender.addAttachment(mock(org.bukkit.plugin.Plugin.class), 1);
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class), anyInt());
    }

    @Test
    void remove_attachment() {
        silentSender.removeAttachment(mock(org.bukkit.permissions.PermissionAttachment.class));
        verify(sender, times(1)).removeAttachment(any(org.bukkit.permissions.PermissionAttachment.class));
    }

    @Test
    void recalculate_permissions() {
        silentSender.recalculatePermissions();
        verify(sender, times(1)).recalculatePermissions();
    }

    @Test
    void get_effective_permissions() {
        silentSender.getEffectivePermissions();
        verify(sender, times(1)).getEffectivePermissions();
    }

    @Test
    void is_op() {
        silentSender.isOp();
        verify(sender, times(1)).isOp();
    }

    @Test
    void set_op() {
        silentSender.setOp(true);
        verify(sender, times(1)).setOp(anyBoolean());
    }
}
