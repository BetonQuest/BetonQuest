package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.command.SimpleTabCompleter;
import org.betonquest.betonquest.database.Backup;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.logger.PlayerLogWatcher;
import org.betonquest.betonquest.web.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Main admin command for quest editing.
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.CouplingBetweenObjects"})
public class QuestCommand implements CommandExecutor, SimpleTabCompleter {

    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Provider for Player Profiles.
     */
    private final ProfileProvider profileProvider;

    /**
     * The {@link Localizations} instance.
     */
    private final Localizations localizations;

    /**
     * The betonquest updater.
     */
    private final Updater updater;

    /**
     * The database connector.
     */
    private final Connector connector;

    /**
     * The reloader runnable.
     */
    private final Reloader reloader;

    /**
     * Accessor to create config to back up.
     */
    private final ConfigAccessorFactory configAccessorFactory;

    /**
     * The PlayerLogWatcher that controls which players receive which log messages.
     */
    private final PlayerLogWatcher logWatcher;

    /**
     * Sub commands with their aliases.
     */
    private final Map<String, SubCommand> subCommands;

    /**
     * Primary names for suggestion.
     */
    private final List<String> subCommandSuggestions;

    /**
     * Command names with their usage.
     */
    private final Map<String, String> subCommandSyntax;

    /**
     * Sub command to display version and hook info.
     */
    private final VersionSubCommand versionSubCommand;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param plugin            the plugin instance
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public QuestCommand(final Plugin plugin, final BetonQuestLogger log, final ConstructorParams constructorParams) {
        this.plugin = plugin;
        this.log = log;
        this.loggerFactory = constructorParams.loggerFactory();
        this.configAccessorFactory = constructorParams.configAccessorFactory();
        this.logWatcher = constructorParams.playerLogWatcher();
        this.profileProvider = constructorParams.profileProvider();
        this.localizations = constructorParams.localizations();
        this.updater = constructorParams.updater();
        this.reloader = constructorParams.reloader();
        this.connector = constructorParams.connector();

        this.subCommands = new HashMap<>();
        this.subCommandSyntax = new HashMap<>();
        List.of(
                new ConditionSubCommand(log, constructorParams),
                new ActionSubCommand(log, constructorParams),
                new ObjectiveSubCommand(log, constructorParams),
                new VariableObjectiveSubCommand(log, constructorParams),
                new TagSubCommand(log, constructorParams),
                new GlobalTagSubCommand(log, constructorParams),
                new PointSubCommand(log, constructorParams),
                new GlobalPointSubCommand(log, constructorParams),
                new JournalSubCommand(log, constructorParams),
                new PurgeSubCommand(log, constructorParams),
                new DeleteSubCommand(log, constructorParams),
                new RenameSubCommand(log, constructorParams),
                new GiveSubCommand(log, constructorParams),
                new ItemSubCommand(log, constructorParams),
                new DebugSubCommand(log, constructorParams),
                new DownloadSubCommand(plugin, log, constructorParams)
        ).forEach(command -> {
            command.names().forEach(name -> subCommands.put(name, command));
            final Map.Entry<String, String> syntax = command.syntax();
            subCommandSyntax.put(syntax.getKey(), syntax.getValue());
        });
        subCommandSyntax.put("update", "update");
        subCommandSyntax.put("reload", "reload");
        subCommandSyntax.put("version", "version");
        subCommandSyntax.put("backup", "backup");
        this.subCommandSuggestions = List.copyOf(subCommandSyntax.keySet());
        this.versionSubCommand = new VersionSubCommand(plugin, constructorParams);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String alias, final String... args) {
        if (!"betonquest".equalsIgnoreCase(cmd.getName())) {
            return false;
        }
        try {
            log.debug("Executing /betonquest command for user " + sender.getName()
                    + " with arguments: " + Arrays.toString(args));
            // if the command is empty, display help message
            if (args.length == 0) {
                displayHelp(sender, alias);
                return true;
            }
            // if there are arguments handle them
            // toLowerCase makes switch case-insensitive
            final String lowerCase = args[0].toLowerCase(Locale.ROOT);
            final SubCommand subCommand = subCommands.get(lowerCase);
            if (subCommand != null) {
                subCommand.handle(sender, args);
                log.debug("Command executing done");
                return true;
            }
            switch (lowerCase) {
                case "version", "ver", "v" -> versionSubCommand.displayVersionInfo(sender, alias);
                case "update" -> updater.update(sender);
                case "reload" -> handleReload(sender);
                case "backup" -> handleBackup(sender);
                default -> sendMessage(sender, "unknown_argument");
            }
            log.debug("Command executing done");
            return true;
        } catch (final QuestException e) {
            log.error("Error while executing command: " + e.getMessage(), e);
        }
        return false;
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    @Override
    public Optional<List<String>> simpleTabComplete(final CommandSender sender, final Command command, final String alias, final String... args) {
        if (args.length == 1) {
            return Optional.of(subCommandSuggestions);
        }
        final String lowerCase = args[0].toLowerCase(Locale.ROOT);
        final SubCommand subCommand = subCommands.get(lowerCase);
        if (subCommand != null) {
            return subCommand.complete(args);
        }
        return switch (lowerCase) {
            case "version",
                 "ver",
                 "v",
                 "update",
                 "reload",
                 "backup" -> Optional.of(new ArrayList<>());
            default -> Optional.of(new ArrayList<>());
        };
    }

    private void handleBackup(final CommandSender sender) {
        // do a full plugin backup
        if (sender instanceof Player || !Bukkit.getOnlinePlayers().isEmpty()) {
            sendMessage(sender, "offline");
            return;
        }
        new Backup(loggerFactory, loggerFactory.create(Backup.class), configAccessorFactory, plugin.getDataFolder(),
                connector).backup(plugin.getDescription().getVersion());
    }

    /**
     * Just reloading.
     *
     * @param sender the sender to send the reload confirmation
     */
    @SuppressWarnings("NullAway")
    private void handleReload(final CommandSender sender) {
        final UUID uuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        final boolean noFilters = uuid != null && !logWatcher.hasActiveFilters(uuid);
        if (noFilters) {
            logWatcher.addFilter(uuid, "*", Level.WARNING);
        }
        reloader.reload();
        sendMessage(sender, "reloaded");
        if (noFilters) {
            logWatcher.removeFilter(uuid, "*");
        }
    }

    /**
     * Displays help to the user.
     */
    private void displayHelp(final CommandSender sender, final String alias) throws QuestException {
        final TextComponent.Builder builder = Component.text();
        builder.append(Component.text("----- ").color(NamedTextColor.YELLOW))
                .append(Component.text("BetonQuest").color(NamedTextColor.GREEN))
                .append(Component.text(" -----").color(NamedTextColor.YELLOW));
        final OnlineProfile profile = sender instanceof final Player player ? profileProvider.getProfile(player) : null;

        for (final Map.Entry<String, String> entry : subCommandSyntax.entrySet()) {
            final Component command = Component.text("/" + alias + " " + entry.getValue()).color(NamedTextColor.RED);
            final Component hint = localizations.getMessage(profile, "command_" + entry.getKey()).color(NamedTextColor.AQUA);

            builder.append(Component.newline());
            if (profile == null) {
                builder.append(command.append(Component.text(" - ").color(NamedTextColor.RED)).append(hint));
            } else {
                builder.append(command.hoverEvent(HoverEvent.showText(hint)));
            }
        }
        sender.sendMessage(builder.build());
    }

    private void sendMessage(final CommandSender sender, final String messageName, final VariableReplacement... replacements) {
        final OnlineProfile profile = sender instanceof final Player player ? profileProvider.getProfile(player) : null;
        try {
            sender.sendMessage(localizations.getMessage(profile, messageName, replacements));
        } catch (final QuestException e) {
            log.warn("Failed to send message '" + messageName + "': " + e.getMessage(), e);
            sender.sendMessage("Failed to send message '" + messageName + "': " + e.getMessage());
        }
    }
}
