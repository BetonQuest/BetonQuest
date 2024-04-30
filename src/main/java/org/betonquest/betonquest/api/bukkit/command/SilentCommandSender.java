package org.betonquest.betonquest.api.bukkit.command;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * A wrapper for a {@link CommandSender} that does not send any messages.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class SilentCommandSender implements CommandSender {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The wrapped command sender.
     */
    private final CommandSender sender;

    /**
     * Create a new silent command sender.
     *
     * @param log    the logger that will be used for logging
     * @param sender the command sender to wrap
     */
    public SilentCommandSender(final BetonQuestLogger log, final CommandSender sender) {
        this.log = log;
        this.sender = sender;
    }

    @Override
    public void sendMessage(final String message) {
        log.debug("Silently sending message to console: " + message);
    }

    @Override
    public void sendMessage(final String... messages) {
        log.debug("Silently sending messages to console: " + String.join(", ", messages));
    }

    @Override
    public void sendMessage(@Nullable final UUID sender, final String message) {
        log.debug("Silently sending message to console: " + message);
    }

    @Override
    public void sendMessage(@Nullable final UUID sender, final String... messages) {
        log.debug("Silently sending messages to console: " + String.join(", ", messages));
    }

    @Override
    public Server getServer() {
        return sender.getServer();
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public Spigot spigot() {
        return sender.spigot();
    }

    @Override
    public Component name() {
        return sender.name();
    }

    @Override
    public boolean isPermissionSet(final String name) {
        return sender.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(final Permission perm) {
        return sender.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(final String name) {
        return sender.hasPermission(name);
    }

    @Override
    public boolean hasPermission(final Permission perm) {
        return sender.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin plugin, final String name, final boolean value) {
        return sender.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin plugin) {
        return sender.addAttachment(plugin);
    }

    @Override
    @Nullable
    public PermissionAttachment addAttachment(final Plugin plugin, final String name, final boolean value, final int ticks) {
        return sender.addAttachment(plugin, name, value, ticks);
    }

    @Override
    @Nullable
    public PermissionAttachment addAttachment(final Plugin plugin, final int ticks) {
        return sender.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(final PermissionAttachment attachment) {
        sender.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        sender.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return sender.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return sender.isOp();
    }

    @Override
    public void setOp(final boolean value) {
        sender.setOp(value);
    }
}
