package org.betonquest.betonquest.menu.command;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.config.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class to simplify creation of commands and implementation of tab complete.
 */
public abstract class SimpleCommand extends Command implements PluginIdentifiableCommand {

    /**
     * Amount of minimal arguments.
     */
    public final int minimalArgs;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * Required permission to use the command.
     */
    @Nullable
    private final Permission permission;

    /**
     * Command map to un-/register command in.
     */
    @Nullable
    private CommandMap commandMap;

    /**
     * Potential help display string.
     */
    private String usage = "null";

    /**
     * Create a new simple command.
     *
     * @param log         the custom logger instance for this class
     * @param name        the command name
     * @param minimalArgs the minimum amount of required arguments
     */
    public SimpleCommand(final BetonQuestLogger log, final String name, final int minimalArgs) {
        super(name);
        this.log = log;
        this.minimalArgs = minimalArgs;
        this.permission = null;
    }

    /**
     * Create a new simple command.
     *
     * @param log           the custom logger instance for this class
     * @param name          the command name
     * @param reqPermission the required permission to use the command
     * @param minimalArgs   the minimum amount of required arguments
     * @param aliases       the aliases for the command
     */
    public SimpleCommand(final BetonQuestLogger log, final String name, final Permission reqPermission,
                         final int minimalArgs, final String... aliases) {
        super(name, "", "", Arrays.asList(aliases));
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
            sendMessage(sender, "command_usage", new VariableReplacement("usage", Component.text(usage)));
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
     */
    public final void register() {
        this.commandMap = Bukkit.getCommandMap();
        if (!this.commandMap.register("betonquest", this)) {
            log.info("Command " + getName() + " is already in command map, using fallback!");
        }
        log.debug("Registered command " + getName() + "!");
    }

    /**
     * Method to unregister the command.
     */
    public void unregister() {
        if (this.commandMap == null) {
            return;
        }
        int count = 0;
        while (commandMap.getKnownCommands().values().remove(this)) {
            count++;
        }
        if (count == 0) {
            log.error("Could not unregister command '" + getName() + "' from command map");
            return;
        }
        log.debug("Unregistered command " + getName() + " " + count + " times!");
    }

    /**
     * Syncs changes in the command map to all players.
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void syncCraftBukkitCommands() {
        try {
            final Class<?> craftServer = Bukkit.getServer().getClass();
            final Method method = craftServer.getDeclaredMethod("syncCommands");
            method.invoke(Bukkit.getServer());
        } catch (final Exception e) {
            log.error("Could not sync commands: " + e.getMessage(), e);
        }
    }

    @Override
    public BetonQuest getPlugin() {
        return BetonQuest.getInstance();
    }

    /**
     * Sends the resolved message from plugin message string.
     *
     * @param sender       the sender to get resolve the message and send it to
     * @param message      the message string to resolve
     * @param replacements the message replacements
     */
    protected void sendMessage(final CommandSender sender, final String message, final VariableReplacement... replacements) {
        sender.sendMessage(getMessage(sender, message, replacements));
    }

    /**
     * Get the resolved message from plugin message string.
     *
     * @param sender       the sender to get resolve the message
     * @param message      the message string to resolve
     * @param replacements the message replacements
     * @return the resolved message component
     */
    protected Component getMessage(final CommandSender sender, final String message, final VariableReplacement... replacements) {
        final PluginMessage pluginMessage = getPlugin().getPluginMessage();
        final OnlineProfile profile = sender instanceof final Player player ? getPlugin().getProfileProvider().getProfile(player) : null;
        try {
            return pluginMessage.getMessage(profile, "menu." + message, replacements);
        } catch (final QuestException e) {
            log.warn("Failed to get message '" + message + "': " + e.getMessage(), e);
            return Component.text("Failed to get message '" + message + "': " + e.getMessage());
        }
    }
}
