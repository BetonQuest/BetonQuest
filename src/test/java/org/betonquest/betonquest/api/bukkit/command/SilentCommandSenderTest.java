package org.betonquest.betonquest.api.bukkit.command;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;
import java.util.logging.Level;

import static org.mockito.Mockito.*;

/**
 * This class tests the {@link SilentCommandSender} class.
 */
@ExtendWith(BetonQuestLoggerService.class)
class SilentCommandSenderTest {
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
    public CommandSender getCommandSender() {
        return mock(CommandSender.class);
    }

    /**
     * Get the silent command sender to test.
     *
     * @return the silent command sender to test
     */
    public SilentCommandSender getSilentCommandSender() {
        return new SilentCommandSender(sender);
    }

    @Disabled
    @Test
    void testSendMessage(final LogValidator validator) {
        silentSender.sendMessage("test1");
        verify(sender, never()).sendMessage(anyString());
        validator.assertLogEntry(Level.FINE, "(SilentCommandSender) Silently sending message to console: test1");
        validator.assertEmpty();
    }

    @Disabled
    @Test
    void testTestSendMessage(final LogValidator validator) {
        silentSender.sendMessage("test2", "test3");
        verify(sender, never()).sendMessage(anyString(), anyString());
        validator.assertLogEntry(Level.FINE, "(SilentCommandSender) Silently sending messages to console: test2, test3");
        validator.assertEmpty();
    }

    @Disabled
    @Test
    void testTestSendMessage1(final LogValidator validator) {
        silentSender.sendMessage(null, "test4");
        verify(sender, never()).sendMessage(any(UUID.class), anyString());
        validator.assertLogEntry(Level.FINE, "(SilentCommandSender) Silently sending message to console: test4");
        validator.assertEmpty();
    }

    @Disabled
    @Test
    void testTestSendMessage2(final LogValidator validator) {
        silentSender.sendMessage((UUID) null, "test5", "test6");
        verify(sender, never()).sendMessage(any(UUID.class), anyString(), anyString());
        validator.assertLogEntry(Level.FINE, "(SilentCommandSender) Silently sending messages to console: test5, test6");
        validator.assertEmpty();
    }

    @Test
    void testGetServer() {
        when(sender.getServer()).thenReturn(mock(org.bukkit.Server.class));
        silentSender.getServer();
        verify(sender, times(1)).getServer();
    }

    @Test
    void testGetName() {
        when(sender.getName()).thenReturn("test");
        silentSender.getName();
        verify(sender, times(1)).getName();
    }

    @Test
    void testSpigot() {
        when(sender.spigot()).thenReturn(mock(CommandSender.Spigot.class));
        silentSender.spigot();
        verify(sender, times(1)).spigot();
    }

    @Test
    void testName() {
        when(sender.name()).thenReturn(mock(Component.class));
        silentSender.name();
        verify(sender, times(1)).name();
    }

    @Test
    void testIsPermissionSet() {
        silentSender.isPermissionSet("test");
        verify(sender, times(1)).isPermissionSet(anyString());
    }

    @Test
    void testTestIsPermissionSet() {
        silentSender.isPermissionSet(mock(org.bukkit.permissions.Permission.class));
        verify(sender, times(1)).isPermissionSet(any(org.bukkit.permissions.Permission.class));
    }

    @Test
    void testHasPermission() {
        silentSender.hasPermission("test");
        verify(sender, times(1)).hasPermission(anyString());
    }

    @Test
    void testTestHasPermission() {
        silentSender.hasPermission(mock(org.bukkit.permissions.Permission.class));
        verify(sender, times(1)).hasPermission(any(org.bukkit.permissions.Permission.class));
    }

    @Test
    void testAddAttachment() {
        when(sender.addAttachment(any(org.bukkit.plugin.Plugin.class))).thenReturn(mock(org.bukkit.permissions.PermissionAttachment.class));
        silentSender.addAttachment(mock(org.bukkit.plugin.Plugin.class));
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class));
    }

    @Test
    void testTestAddAttachment() {
        when(sender.addAttachment(any(org.bukkit.plugin.Plugin.class), anyString(), anyBoolean())).thenReturn(mock(org.bukkit.permissions.PermissionAttachment.class));
        silentSender.addAttachment(mock(org.bukkit.plugin.Plugin.class), "test", true);
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class), anyString(), anyBoolean());
    }

    @Test
    void testTestAddAttachment1() {
        silentSender.addAttachment(mock(org.bukkit.plugin.Plugin.class), "test", true, 1);
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class), anyString(), anyBoolean(), anyInt());
    }

    @Test
    void testTestAddAttachment2() {
        silentSender.addAttachment(mock(org.bukkit.plugin.Plugin.class), 1);
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class), anyInt());
    }

    @Test
    void testRemoveAttachment() {
        silentSender.removeAttachment(mock(org.bukkit.permissions.PermissionAttachment.class));
        verify(sender, times(1)).removeAttachment(any(org.bukkit.permissions.PermissionAttachment.class));
    }

    @Test
    void testRecalculatePermissions() {
        silentSender.recalculatePermissions();
        verify(sender, times(1)).recalculatePermissions();
    }

    @Test
    void testGetEffectivePermissions() {
        silentSender.getEffectivePermissions();
        verify(sender, times(1)).getEffectivePermissions();
    }

    @Test
    void testIsOp() {
        silentSender.isOp();
        verify(sender, times(1)).isOp();
    }

    @Test
    void testSetOp() {
        silentSender.setOp(true);
        verify(sender, times(1)).setOp(anyBoolean());
    }
}
