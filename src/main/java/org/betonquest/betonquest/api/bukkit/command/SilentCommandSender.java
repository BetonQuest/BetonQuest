package org.betonquest.betonquest.api.bukkit.command;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
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
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(SilentCommandSender.class, "SilentCommandSender");
    /**
     * The wrapped command sender.
     */
    private final CommandSender sender;

    /**
     * Create a new silent command sender.
     *
     * @param sender the command sender to wrap
     */
    public SilentCommandSender(final CommandSender sender) {
        this.sender = sender;
    }

    public CommandSender getSender() {
        return sender;
    }

    @Override
    public void sendMessage(@NotNull final String message) {
        LOG.debug("Silently sending message to console: " + message);
    }

    @Override
    public void sendMessage(final @NotNull String... messages) {
        LOG.debug("Silently sending messages to console: " + String.join(", ", messages));
    }

    @Override
    public void sendMessage(@Nullable final UUID sender, @NotNull final String message) {
        LOG.debug("Silently sending message to console: " + message);
    }

    @Override
    public void sendMessage(@Nullable final UUID sender, final @NotNull String... messages) {
        LOG.debug("Silently sending messages to console: " + String.join(", ", messages));
    }

    @Override
    public @NotNull Server getServer() {
        return sender.getServer();
    }

    @Override
    public @NotNull String getName() {
        return sender.getName();
    }

    @Override
    public @NotNull Spigot spigot() {
        return sender.spigot();
    }

    @Override
    public @NotNull Component name() {
        return sender.name();
    }

    @Override
    public boolean isPermissionSet(@NotNull final String name) {
        return sender.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull final Permission perm) {
        return sender.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull final String name) {
        return sender.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull final Permission perm) {
        return sender.hasPermission(perm);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull final Plugin plugin, @NotNull final String name, final boolean value) {
        return sender.addAttachment(plugin, name, value);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull final Plugin plugin) {
        return sender.addAttachment(plugin);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull final Plugin plugin, @NotNull final String name, final boolean value, final int ticks) {
        return sender.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull final Plugin plugin, final int ticks) {
        return sender.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull final PermissionAttachment attachment) {
        sender.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        sender.recalculatePermissions();
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
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
