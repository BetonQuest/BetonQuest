package org.betonquest.betonquest.menu.command;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class to simplify creation of commands and implementation of tab complete.
 */
@SuppressWarnings("PMD.CommentRequired")
public abstract class SimpleCommand extends Command implements PluginIdentifiableCommand {

    public final int minimalArgs;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    @Nullable
    private final Permission permission;

    @Nullable
    private CommandMap commandMap;

    private String usage = "null";

    public SimpleCommand(final BetonQuestLogger log, final String name, final int minimalArgs) {
        super(name);
        this.log = log;
        this.minimalArgs = minimalArgs;
        this.permission = null;
    }

    public SimpleCommand(final BetonQuestLogger log, final String name, final Permission reqPermission, final int minimalArgs, final String... alises) {
        super(name, "", "", Arrays.asList(alises));
        this.log = log;
        this.minimalArgs = minimalArgs;
        this.permission = reqPermission;
    }

    /**
     * Override this method to handle what is returned on tab complete.
     *
     * @param sender the CommandSender performing the tab complete
     * @param alias  the command alias used
     * @param args   the arguments specified
     * @return must be a list of all possible competitions for the current arg, ignoring already typed chars
     */
    public List<String> simpleTabComplete(final CommandSender sender, final String alias, final String... args) {
        return new ArrayList<>();
    }

    /**
     * Override this method to handle what happens if the command gets executed, all permissions are met and required
     * arguments are given.
     *
     * @param sender the CommandSender performing the command
     * @param alias  the command alias used
     * @param args   the arguments specified
     * @return whether the command could be successfully executed or not
     */
    public abstract boolean simpleCommand(CommandSender sender, String alias, String... args);

    @Override
    public final Command setUsage(final String usage) {
        this.usage = usage;
        return this;
    }

    @Override
    public boolean execute(final CommandSender sender, final String label, final String[] args) {
        if (args.length < minimalArgs) {
            sendMessage(sender, "command_usage", new PluginMessage.Replacement("usage", Component.text(usage)));
            return false;
        }
        if (permission != null && !sender.hasPermission(permission)) {
            sendMessage(sender, "no_permission");
            return false;
        }
        return simpleCommand(sender, label, args);
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) {
        final List<String> completions = this.simpleTabComplete(sender, alias, args);
        final List<String> out = new ArrayList<>();
        final String lastArg = args[args.length - 1];
        for (final String completion : completions) {
            if (lastArg == null || lastArg.matches(" *") || completion.startsWith(lastArg)) {
                out.add(completion);
            }
        }
        return out;
    }

    /**
     * Method to register the command.
     *
     * @return Whether the command was successfully registered
     */
    public final boolean register() {
        this.commandMap = Bukkit.getCommandMap();
        if (!this.commandMap.register("betonquest", this)) {
            log.error("Could not register command " + getName() + " in command map!");
            return false;
        }
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        log.debug("Registered command " + getName() + "!");
        return true;
    }

    /**
     * Method to unregister the command.
     *
     * @return Whether the command was successfully unregistered
     */
    public boolean unregister() {
        if (this.commandMap == null) {
            return false;
        }
        this.unregister(commandMap);
        if (!commandMap.getKnownCommands().values().remove(this)) {
            log.error("Could not unregister command '" + getName() + "' from command map");
            return false;
        }
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        log.debug("Unregistered command " + getName() + "!");
        return true;
    }

    @Override
    public BetonQuest getPlugin() {
        return BetonQuest.getInstance();
    }

    protected void sendMessage(final CommandSender sender, final String message, final PluginMessage.Replacement... replacements) {
        sender.sendMessage(getMessage(sender, message, replacements));
    }

    protected Component getMessage(final CommandSender sender, final String message, final PluginMessage.Replacement... replacements) {
        final PluginMessage pluginMessage = getPlugin().getPluginMessage();
        final OnlineProfile profile = sender instanceof final Player player ? getPlugin().getProfileProvider().getProfile(player) : null;
        try {
            return pluginMessage.getMessage("menu." + message, replacements).asComponent(profile);
        } catch (final QuestException e) {
            log.warn("Failed to get message '" + message + "': " + e.getMessage(), e);
            return Component.text("Failed to get message '" + message + "': " + e.getMessage());
        }
    }
}
