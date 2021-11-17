package org.betonquest.betonquest.menu.commands;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.menu.config.RPGMenuConfig;
import org.betonquest.betonquest.menu.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Abstract class to simplify creation of commands and implementation of tab complete
 */
@CustomLog
@SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidUncheckedExceptionsInSignatures", "PMD.CommentRequired"})
public abstract class SimpleCommand extends Command implements PluginIdentifiableCommand {

    public final int minimalArgs;
    private final Permission perimssion;
    private CommandMap commandMap;
    private String usage = "null";

    public SimpleCommand(final String name, final int minimalArgs) {
        super(name);
        this.minimalArgs = minimalArgs;
        this.perimssion = null;
    }

    public SimpleCommand(final String name, final Permission reqPermission, final int minimalArgs, final String... alises) {
        super(name, "", "", Arrays.asList(alises));
        this.minimalArgs = minimalArgs;
        this.perimssion = reqPermission;
    }

    /**
     * Override this method to handle what is returned on tab complete
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
     * arguments are given
     *
     * @param sender the CommandSender performing the command
     * @param alias  the command alias used
     * @param args   the arguments specified
     * @return whether the command could be successfully executed or not
     */
    public abstract boolean simpleCommand(CommandSender sender, String alias, String... args);

    /**
     * Override this method to specify the message which is send when the command sender doesn't has the required Permission
     *
     * @param sender the CommandSender performing the command
     * @return the message to send
     */
    protected String noPermissionMessage(final CommandSender sender) {
        return RPGMenuConfig.getMessage(sender, "command_no_permission");
    }

    @Override
    public Command setUsage(final String usage) {
        this.usage = usage;
        return this;
    }

    @Override
    public boolean execute(final CommandSender sender, final String label, final String[] args) {
        if (args.length < minimalArgs) {
            RPGMenuConfig.sendMessage(sender, "command_usage", usage);
            return false;
        }
        if (perimssion != null && !sender.hasPermission(perimssion)) {
            sender.sendMessage(noPermissionMessage(sender));
            return false;
        }
        return simpleCommand(sender, label, args);
    }

    @Override
    @SuppressFBWarnings("NP_NONNULL_RETURN_VIOLATION")
    public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
        if (sender == null || alias == null || args == null) {
            return super.tabComplete(sender, alias, args);
        }
        final List<String> completations = this.simpleTabComplete(sender, alias, args);
        if (completations == null) {
            return null;
        }
        final List<String> out = new ArrayList<>();
        final String lastArg = args[args.length - 1];
        for (final String completation : completations) {
            if (lastArg == null || lastArg.matches(" *") || completation.startsWith(lastArg)) {
                out.add(completation);
            }
        }
        return out;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender,
                                    final String alias,
                                    final String[] args,
                                    final Location location) throws IllegalArgumentException {
        return this.tabComplete(sender, alias, args);
    }

    /**
     * Method to register the command
     *
     * @return Whether the command was successfully registered
     */
    public boolean register() {
        try {
            final PluginManager manager = Bukkit.getPluginManager();
            final Class<? extends PluginManager> managerClass = manager.getClass();
            this.commandMap = (CommandMap) Utils.getField(managerClass, "commandMap").get(manager);
            this.commandMap.register("betonquest", this);
            syncCraftBukkitCommands();
            LOG.debug("Registered command " + getName() + "!");
            return true;
        } catch (final Exception e) {
            LOG.error("Could not register command " + getName() + ":", e);
            return false;
        }
    }

    /**
     * Method to unregister the command
     *
     * @return Whether the command was successfully unregistered
     */
    @SuppressWarnings({"unchecked", "PMD.AvoidLiteralsInIfCondition"})
    public boolean unregister() {
        if (this.commandMap == null) {
            return false;
        }
        try {
            this.unregister(commandMap);
            final Collection<Command> commands = (Collection<Command>) Utils
                    .getMethod(commandMap.getClass(), "getCommands", 0)
                    .invoke(commandMap);
            if ("UnmodifiableCollection".equals(commands.getClass().getSimpleName())) {
                final Field originalField = commands.getClass().getDeclaredField("c");
                originalField.setAccessible(true);
                final Collection<Command> original = (Collection<Command>) originalField.get(commands);
                original.remove(this);
            } else {
                commands.remove(this);
            }
            syncCraftBukkitCommands();
            LOG.debug("Unregistered command " + getName() + "!");
            return true;
        } catch (final RuntimeException e) {
            if (!"java.lang.reflect.InaccessibleObjectException".equals(e.getClass().getName())) {
                throw e;
            }
            return false;
        } catch (final Exception e) {
            LOG.error("Could not unregister command §7" + getName() + "§4:", e);
            return false;
        }
    }

    private void syncCraftBukkitCommands() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> craftServer = Class.forName("org.bukkit.craftbukkit." + getServerVersion() + ".CraftServer");
        final Method method = craftServer.getDeclaredMethod("syncCommands");
        method.invoke(Bukkit.getServer());
    }

    private String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring("org.bukkit.craftbukkit.".length());
    }

    @Override
    public Plugin getPlugin() {
        return BetonQuest.getInstance();
    }
}
