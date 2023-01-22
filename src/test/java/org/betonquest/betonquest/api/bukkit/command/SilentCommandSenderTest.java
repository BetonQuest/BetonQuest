package org.betonquest.betonquest.api.bukkit.command;

import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.bukkit.command.CommandSender;
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
     * The sender to use.
     */
    private final CommandSender sender;

    /**
     * Create a new SilentCommandSenderTest instance.
     */
    public SilentCommandSenderTest() {
        this(mock(CommandSender.class));
    }

    /**
     * Create a new SilentCommandSenderTest instance.
     *
     * @param sender the sender to use
     */
    public SilentCommandSenderTest(final CommandSender sender) {
        this.sender = sender;
    }

    /**
     * Get the silent command sender to test.
     *
     * @return the silent command sender to test
     */
    public SilentCommandSender getSilentCommandSender() {
        return new SilentCommandSender(sender);
    }

    @Test
    void testSendMessage(final LogValidator validator) {
        getSilentCommandSender().sendMessage("test1");
        verify(sender, never()).sendMessage(anyString());
        validator.assertLogEntry(Level.FINE, "(SilentCommandSender) Silently sending message to console: test1");
        validator.assertEmpty();
    }

    @Test
    void testTestSendMessage(final LogValidator validator) {
        getSilentCommandSender().sendMessage("test2", "test3");
        verify(sender, never()).sendMessage(anyString(), anyString());
        validator.assertLogEntry(Level.FINE, "(SilentCommandSender) Silently sending messages to console: test2, test3");
        validator.assertEmpty();
    }

    @Test
    void testTestSendMessage1(final LogValidator validator) {
        getSilentCommandSender().sendMessage(null, "test4");
        verify(sender, never()).sendMessage(any(UUID.class), anyString());
        validator.assertLogEntry(Level.FINE, "(SilentCommandSender) Silently sending message to console: test4");
        validator.assertEmpty();
    }

    @Test
    void testTestSendMessage2(final LogValidator validator) {
        getSilentCommandSender().sendMessage((UUID) null, "test5", "test6");
        verify(sender, never()).sendMessage(any(UUID.class), anyString(), anyString());
        validator.assertLogEntry(Level.FINE, "(SilentCommandSender) Silently sending messages to console: test5, test6");
        validator.assertEmpty();
    }

    @Test
    void testGetServer() {
        getSilentCommandSender().getServer();
        verify(sender, times(1)).getServer();
    }

    @Test
    void testGetName() {
        getSilentCommandSender().getName();
        verify(sender, times(1)).getName();
    }

    @Test
    void testSpigot() {
        getSilentCommandSender().spigot();
        verify(sender, times(1)).spigot();
    }

    @Test
    void testName() {
        getSilentCommandSender().name();
        verify(sender, times(1)).name();
    }

    @Test
    void testIsPermissionSet() {
        getSilentCommandSender().isPermissionSet("test");
        verify(sender, times(1)).isPermissionSet(anyString());
    }

    @Test
    void testTestIsPermissionSet() {
        getSilentCommandSender().isPermissionSet(mock(org.bukkit.permissions.Permission.class));
        verify(sender, times(1)).isPermissionSet(any(org.bukkit.permissions.Permission.class));
    }

    @Test
    void testHasPermission() {
        getSilentCommandSender().hasPermission("test");
        verify(sender, times(1)).hasPermission(anyString());
    }

    @Test
    void testTestHasPermission() {
        getSilentCommandSender().hasPermission(mock(org.bukkit.permissions.Permission.class));
        verify(sender, times(1)).hasPermission(any(org.bukkit.permissions.Permission.class));
    }

    @Test
    void testAddAttachment() {
        getSilentCommandSender().addAttachment(mock(org.bukkit.plugin.Plugin.class));
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class));
    }

    @Test
    void testTestAddAttachment() {
        getSilentCommandSender().addAttachment(mock(org.bukkit.plugin.Plugin.class), "test", true);
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class), anyString(), anyBoolean());
    }

    @Test
    void testTestAddAttachment1() {
        getSilentCommandSender().addAttachment(mock(org.bukkit.plugin.Plugin.class), "test", true, 1);
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class), anyString(), anyBoolean(), anyInt());
    }

    @Test
    void testTestAddAttachment2() {
        getSilentCommandSender().addAttachment(mock(org.bukkit.plugin.Plugin.class), 1);
        verify(sender, times(1)).addAttachment(any(org.bukkit.plugin.Plugin.class), anyInt());
    }

    @Test
    void testRemoveAttachment() {
        getSilentCommandSender().removeAttachment(mock(org.bukkit.permissions.PermissionAttachment.class));
        verify(sender, times(1)).removeAttachment(any(org.bukkit.permissions.PermissionAttachment.class));
    }

    @Test
    void testRecalculatePermissions() {
        getSilentCommandSender().recalculatePermissions();
        verify(sender, times(1)).recalculatePermissions();
    }

    @Test
    void testGetEffectivePermissions() {
        getSilentCommandSender().getEffectivePermissions();
        verify(sender, times(1)).getEffectivePermissions();
    }

    @Test
    void testIsOp() {
        getSilentCommandSender().isOp();
        verify(sender, times(1)).isOp();
    }

    @Test
    void testSetOp() {
        getSilentCommandSender().setOp(true);
        verify(sender, times(1)).setOp(anyBoolean());
    }
}
