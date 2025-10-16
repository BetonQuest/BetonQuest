package org.betonquest.betonquest.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.common.component.VariableComponent;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Backup;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.feature.journal.Pointer;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.logger.PlayerLogWatcher;
import org.betonquest.betonquest.logger.format.ChatFormatter;
import org.betonquest.betonquest.logger.handler.history.LogPublishingController;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.give.GiveEvent;
import org.betonquest.betonquest.quest.objective.variable.VariableObjective;
import org.betonquest.betonquest.web.downloader.DownloadFailedException;
import org.betonquest.betonquest.web.downloader.Downloader;
import org.betonquest.betonquest.web.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * Main admin command for quest editing.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals",
        "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity", "PMD.CouplingBetweenObjects"})
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
     * The BetonQuest plugin instance.
     */
    private final BetonQuest instance;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Provider for Player Profiles.
     */
    private final ProfileProvider profileProvider;

    /**
     * Factory to create new Player Data.
     */
    private final PlayerDataFactory playerDataFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * The plugin configuration accessor.
     */
    private final ConfigAccessor config;

    /**
     * The compatibility instance to use for compatibility checks.
     */
    private final Compatibility compatibility;

    /**
     * Accessor to create config to back up.
     */
    private final ConfigAccessorFactory configAccessorFactory;

    /**
     * The PlayerLogWatcher that controls which players receive which log messages.
     */
    private final PlayerLogWatcher logWatcher;

    /**
     * The LogPublishingController to control the debug log.
     */
    private final LogPublishingController debuggingController;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param loggerFactory         logger factory to use
     * @param log                   the logger that will be used for logging
     * @param configAccessorFactory the config accessor factory to use
     * @param logWatcher            the player log watcher to use
     * @param debuggingController   the log publishing controller to use
     * @param plugin                the BetonQuest plugin instance
     * @param dataStorage           the storage providing player data
     * @param profileProvider       the profile provider
     * @param playerDataFactory     the factory to create player data
     * @param pluginMessage         the {@link PluginMessage} instance
     * @param config                the plugin configuration accessor
     * @param compatibility         the compatibility instance to use for compatibility checks
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public QuestCommand(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                        final ConfigAccessorFactory configAccessorFactory, final PlayerLogWatcher logWatcher,
                        final LogPublishingController debuggingController, final BetonQuest plugin,
                        final PlayerDataStorage dataStorage, final ProfileProvider profileProvider, final PlayerDataFactory playerDataFactory,
                        final PluginMessage pluginMessage, final ConfigAccessor config, final Compatibility compatibility) {
        this.loggerFactory = loggerFactory;
        this.log = log;
        this.configAccessorFactory = configAccessorFactory;
        this.logWatcher = logWatcher;
        this.debuggingController = debuggingController;
        this.instance = plugin;
        this.dataStorage = dataStorage;
        this.profileProvider = profileProvider;
        this.playerDataFactory = playerDataFactory;
        this.pluginMessage = pluginMessage;
        this.config = config;
        this.compatibility = compatibility;
    }

    @SuppressWarnings("PMD.NcssCount")
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String alias, final String... args) {
        try {
            if ("betonquest".equalsIgnoreCase(cmd.getName())) {
                log.debug("Executing /betonquest command for user " + sender.getName()
                        + " with arguments: " + Arrays.toString(args));
                // if the command is empty, display help message
                if (args.length == 0) {
                    displayHelp(sender, alias);
                    return true;
                }
                // if there are arguments handle them
                // toLowerCase makes switch case-insensitive
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "conditions":
                    case "condition":
                    case "c":
                        handleConditions(sender, args);
                        break;
                    case "events":
                    case "event":
                    case "e":
                        handleEvents(sender, args);
                        break;
                    case "items":
                    case "item":
                    case "i":
                        // and items, which only use configuration files (they
                        // should be sync)
                        handleItems(sender, args);
                        break;
                    case "give":
                    case "g":
                        giveItem(sender, args);
                        break;
                    case "objectives":
                    case "objective":
                    case "o":
                        handleObjectives(sender, args);
                        break;
                    case "globaltags":
                    case "globaltag":
                    case "gtag":
                    case "gtags":
                    case "gt":
                        handleGlobalTags(sender, args);
                        break;
                    case "globalpoints":
                    case "globalpoint":
                    case "gpoints":
                    case "gpoint":
                    case "gp":
                        handleGlobalPoints(sender, args);
                        break;
                    case "tags":
                    case "tag":
                    case "t":
                        handleTags(sender, args);
                        break;
                    case "points":
                    case "point":
                    case "p":
                        handlePoints(sender, args);
                        break;
                    case "journals":
                    case "journal":
                    case "j":
                        handleJournals(sender, args);
                        break;
                    case "delete":
                    case "del":
                    case "d":
                        handleDeleting(sender, args);
                        break;
                    case "rename":
                    case "r":
                        handleRenaming(sender, args);
                        break;
                    case "variable":
                    case "var":
                        handleVariables(sender, args);
                        break;
                    case "version":
                    case "ver":
                    case "v":
                        displayVersionInfo(sender, alias);
                        break;
                    case "purge":
                        purgePlayer(sender, args);
                        break;
                    case "update":
                        instance.getUpdater().update(sender);
                        break;
                    case "reload":
                        handleReload(sender);
                        break;
                    case "backup":
                        // do a full plugin backup
                        if (sender instanceof Player || !Bukkit.getOnlinePlayers().isEmpty()) {
                            sendMessage(sender, "offline");
                            break;
                        }
                        new Backup(loggerFactory.create(Backup.class), configAccessorFactory, instance.getDataFolder(),
                                new Connector()).backup(instance.getDescription().getVersion());
                        break;
                    case "debug":
                        handleDebug(sender, args);
                        break;
                    case "download":
                        handleDownload(sender, args);
                        break;
                    default:
                        // there was an unknown argument, so handle this
                        sendMessage(sender, "unknown_argument");
                        break;
                }
                log.debug("Command executing done");
                return true;
            }
        } catch (final QuestException e) {
            log.error("Error while executing command: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Optional<List<String>> simpleTabComplete(final CommandSender sender, final Command command, final String alias, final String... args) {
        if (args.length == 1) {
            return Optional.of(Arrays.asList("condition", "event", "item", "give", "objective", "globaltag",
                    "globalpoint", "tag", "point", "journal", "delete", "rename", "version", "purge",
                    "update", "reload", "backup", "debug", "download", "variable"));
        }
        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "conditions",
                 "condition",
                 "c" -> completeConditions(args);
            case "events",
                 "event",
                 "e" -> completeEvents(args);
            case "items",
                 "item",
                 "i",
                 "give",
                 "g" -> completeItems(args);
            case "objectives",
                 "objective",
                 "o" -> completeObjectives(args);
            case "globaltags",
                 "globaltag",
                 "gtag",
                 "gtags",
                 "gt" -> completeGlobalTags(args);
            case "globalpoints",
                 "globalpoint",
                 "gpoints",
                 "gpoint",
                 "gp" -> completeGlobalPoints(args);
            case "tags",
                 "tag",
                 "t" -> completeTags(args);
            case "points",
                 "point",
                 "p" -> completePoints(args);
            case "journals",
                 "journal",
                 "j" -> completeJournals(args);
            case "delete",
                 "del",
                 "d" -> completeDeleting(args);
            case "rename",
                 "r" -> completeRenaming(args);
            case "purge" -> args.length == 2 ? Optional.empty() : Optional.of(new ArrayList<>());
            case "debug" -> completeDebug(args);
            case "download" -> completeDownload(args);
            case "variable",
                 "var" -> completeVariables(args);
            case "version",
                 "ver",
                 "v",
                 "update",
                 "reload",
                 "backup",
                 "package" -> Optional.of(new ArrayList<>());
            default -> Optional.of(new ArrayList<>());
        };
    }

    /**
     * Returns a list of all packages for the tab completer.
     */
    private Optional<List<String>> completePackage() {
        return Optional.of(new ArrayList<>(instance.getQuestPackageManager().getPackages().keySet()));
    }

    /**
     * Returns a list including all possible tab complete options for ids.
     *
     * @param type - the type of the ID, null for unspecific
     */
    private Optional<List<String>> completeId(final String[] args, @Nullable final AccessorType type) {
        final String last = args[args.length - 1];
        if (last == null || !last.contains(Identifier.SEPARATOR)) {
            return completePackage();
        } else {
            final String pack = last.substring(0, last.indexOf(Identifier.SEPARATOR));
            final QuestPackage configPack = instance.getQuestPackageManager().getPackages().get(pack);
            if (configPack == null) {
                return Optional.of(new ArrayList<>());
            }
            if (type == null) {
                final List<String> completions = new ArrayList<>();
                completions.add(pack + Identifier.SEPARATOR);
                return Optional.of(completions);
            }
            final ConfigurationSection configuration = configPack.getConfig()
                    .getConfigurationSection(type.name().toLowerCase(Locale.ROOT));
            final List<String> completions = new ArrayList<>();
            if (configuration != null) {
                for (final String key : configuration.getKeys(false)) {
                    completions.add(pack + Identifier.SEPARATOR + key);
                }
            }
            return Optional.of(completions);
        }
    }

    /**
     * Gives an item to the player.
     */
    private void giveItem(final CommandSender sender, final String... args) {
        // sender must be a player
        if (!(sender instanceof Player)) {
            log.debug("Cannot continue, sender must be player");
            return;
        }
        // and the item name must be specified
        if (args.length < 2) {
            log.debug("Cannot continue, item's name must be supplied");
            sendMessage(sender, "specify_item");
            return;
        }
        try {
            final ItemID itemID;
            try {
                itemID = new ItemID(instance.getQuestPackageManager(), null, args[1]);
            } catch (final QuestException e) {
                sendMessage(sender, "error",
                        new VariableReplacement("error", Component.text(e.getMessage())));
                log.warn("Could not find Item: " + e.getMessage(), e);
                return;
            }
            final OnlineEvent give = new GiveEvent(
                    new VariableList<>(new Item(instance.getFeatureApi(), itemID, new Variable<>(1))),
                    new NoNotificationSender(),
                    new IngameNotificationSender(log, pluginMessage, itemID.getPackage(), itemID.getFull(), NotificationLevel.ERROR,
                            "inventory_full_backpack", "inventory_full"),
                    new IngameNotificationSender(log, pluginMessage, itemID.getPackage(), itemID.getFull(), NotificationLevel.ERROR,
                            "inventory_full_drop", "inventory_full"),
                    false, dataStorage);
            give.execute(profileProvider.getProfile((Player) sender));
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Error while creating an item: " + e.getMessage(), e);
        }
    }

    /**
     * Purges profile's data.
     */
    private void purgePlayer(final CommandSender sender, final String... args) {
        final PlayerData playerData = getTargetPlayerData(sender, args);
        if (playerData == null) {
            return;
        }
        log.debug("Purging player " + args[1]);
        playerData.purgePlayer();
        // done
        sendMessage(sender, "purged",
                new VariableReplacement("player", Component.text(args[1])));
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
        instance.reload();
        sendMessage(sender, "reloaded");
        if (noFilters) {
            logWatcher.removeFilter(uuid, "*");
        }
    }

    @Nullable
    private Profile getTargetProfile(final CommandSender sender, final String... args) {
        if (args.length < 2) {
            log.debug("Player's name is missing");
            sendMessage(sender, "specify_player");
            return null;
        }
        return profileProvider.getProfile(Bukkit.getOfflinePlayer(args[1]));
    }

    @Nullable
    private PlayerData getTargetPlayerData(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return null;
        }
        if (profile.getOnlineProfile().isPresent()) {
            return dataStorage.get(profile);
        } else {
            log.debug("Profile is offline, loading his data");
            return playerDataFactory.createPlayerData(profile);
        }
    }

    /**
     * Lists, adds or removes journal entries of certain profile.
     */
    private void handleJournals(final CommandSender sender, final String... args) {
        final PlayerData playerData = getTargetPlayerData(sender, args);
        if (playerData == null) {
            return;
        }
        final Journal journal = playerData.getJournal();
        // if there are no arguments then list player's pointers
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            log.debug("Listing journal pointers");
            final Predicate<Pointer> shouldDisplay = createListFilter(args, 3, pointer -> pointer.pointer().getFull());
            sendMessage(sender, "player_journal");
            journal.getPointers().stream()
                    .filter(shouldDisplay)
                    .forEach(pointer -> {
                        final String date = new SimpleDateFormat(config.getString("date_format", ""), Locale.ROOT)
                                .format(new Date(pointer.timestamp()));
                        sender.sendMessage("§b- " + pointer.pointer() + " §c(§2" + date + "§c)");
                    });
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            log.debug("Missing pointer");
            sendMessage(sender, "specify_pointer");
            return;
        }
        final String pointerName = args[3];
        if (!pointerName.contains(Identifier.SEPARATOR)) {
            sendMessage(sender, "specify_pointer");
            return;
        }
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "add", "a" -> {
                final JournalEntryID entryID;
                try {
                    entryID = new JournalEntryID(instance.getQuestPackageManager(), null, pointerName);
                } catch (final QuestException e) {
                    sendMessage(sender, "error",
                            new VariableReplacement("error", Component.text(e.getMessage())));
                    log.warn("The journal entry'" + pointerName + "' does not exist!");
                    log.debug("Tried to add non existing journal entry: " + e.getMessage(), e);
                    return;
                }
                final Pointer pointer;
                if (args.length < 5) {
                    final long timestamp = new Date().getTime();
                    log.debug("Adding pointer with current date: " + timestamp);
                    pointer = new Pointer(entryID, timestamp);
                } else {
                    log.debug("Adding pointer with date " + args[4].replaceAll("_", " "));
                    try {
                        pointer = new Pointer(entryID,
                                new SimpleDateFormat(config.getString("date_format", ""), Locale.ROOT)
                                        .parse(args[4].replaceAll("_", " ")).getTime());
                    } catch (final ParseException e) {
                        sendMessage(sender, "specify_date");
                        log.warn("Could not parse date: " + e.getMessage(), e);
                        return;
                    }
                }
                journal.addPointer(pointer);
                journal.update();
                sendMessage(sender, "pointer_added");
            }
            case "remove", "delete", "del", "r", "d" -> {
                log.debug("Removing pointer");
                final JournalEntryID entryID;
                try {
                    entryID = new JournalEntryID(instance.getQuestPackageManager(), null, pointerName);
                } catch (final QuestException e) {
                    sendMessage(sender, "error",
                            new VariableReplacement("error", Component.text(e.getMessage())));
                    log.warn("The journal entry'" + pointerName + "' does not exist!");
                    log.debug("Tried to remove non existing journal entry: " + e.getMessage(), e);
                    return;
                }
                journal.removePointer(entryID);
                journal.update();
                sendMessage(sender, "pointer_removed");
            }
            default -> {
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
            }
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest journal command.
     */
    private Optional<List<String>> completeJournals(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            return Optional.of(Arrays.asList("add", "list", "del"));
        }
        if (args.length == 4) {
            return completeId(args, AccessorType.JOURNAL);
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Lists, adds or removes points of certain profile.
     */
    private void handlePoints(final CommandSender sender, final String... args) {
        final PlayerData playerData = getTargetPlayerData(sender, args);
        if (playerData == null) {
            return;
        }
        // if there are no arguments then list player's points
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            log.debug("Listing points");
            final Predicate<Point> shouldDisplay = createListFilter(args, 3, Point::getCategory);
            sendMessage(sender, "player_points");
            playerData.getPoints().stream()
                    .filter(shouldDisplay)
                    .forEach(point -> sender.sendMessage("§b- " + point.getCategory() + "§e: §a" + point.getCount()));
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            log.debug("Missing category");
            sendMessage(sender, "specify_category");
            return;
        }
        final String category = args[3];
        // if there are arguments, handle them
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "add", "a" -> {
                if (args.length < 5 || !args[4].matches("-?\\d+")) {
                    log.debug("Missing amount");
                    sendMessage(sender, "specify_amount");
                    return;
                }
                log.debug("Adding points");
                playerData.modifyPoints(category, Integer.parseInt(args[4]));
                sendMessage(sender, "points_added");
            }
            case "remove", "delete", "del", "r", "d" -> {
                log.debug("Removing points");
                playerData.removePointsCategory(category);
                sendMessage(sender, "points_removed");
            }
            default -> {
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
            }
        }
    }

    /**
     * Lists, adds, removes or purges all global points.
     */
    private void handleGlobalPoints(final CommandSender sender, final String... args) {
        final GlobalData data = instance.getGlobalData();
        // if there are no arguments then list all global points
        if (args.length < 2 || "list".equalsIgnoreCase(args[1]) || "l".equalsIgnoreCase(args[1])) {
            log.debug("Listing global points");
            final Predicate<Point> shouldDisplay = createListFilter(args, 2, Point::getCategory);
            sendMessage(sender, "global_points");
            data.getPoints().stream()
                    .filter(shouldDisplay)
                    .forEach(point -> sender.sendMessage("§b- " + point.getCategory() + "§e: §a" + point.getCount()));
            return;
        }
        // handle purge
        if ("purge".equalsIgnoreCase(args[1])) {
            log.debug("Purging all global points");
            data.purgePoints();
            sendMessage(sender, "global_points_purged");
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 3) {
            log.debug("Missing category");
            sendMessage(sender, "specify_category");
            return;
        }
        final String category = args[2];
        // if there are arguments, handle them
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "add", "a" -> {
                if (args.length < 4 || !args[3].matches("-?\\d+")) {
                    log.debug("Missing amount");
                    sendMessage(sender, "specify_amount");
                    return;
                }
                log.debug("Adding global points");
                data.modifyPoints(category, Integer.parseInt(args[3]));
                sendMessage(sender, "points_added");
            }
            case "remove", "delete", "del", "r", "d" -> {
                log.debug("Removing global points");
                data.removePointsCategory(category);
                sendMessage(sender, "points_removed");
            }
            default -> {
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
            }
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest points command.
     */
    private Optional<List<String>> completePoints(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            return Optional.of(Arrays.asList("add", "list", "del"));
        }
        if (args.length == 4) {
            return completeId(args, null);
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest globalpoints command.
     */
    private Optional<List<String>> completeGlobalPoints(final String... args) {
        if (args.length == 2) {
            return Optional.of(Arrays.asList("add", "list", "del", "purge"));
        }
        if (args.length == 3) {
            if ("purge".equalsIgnoreCase(args[1])) {
                return Optional.of(new ArrayList<>());
            }
            return completeId(args, null);
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Adds item held in hand to items.yml file.
     */
    private void handleItems(final CommandSender sender, final String... args) {
        // sender must be a player
        if (!(sender instanceof final Player player)) {
            log.debug("Cannot continue, sender must be player");
            return;
        }
        // and the item name must be specified
        if (args.length < 2) {
            log.debug("Cannot continue, item's name must be supplied");
            sendMessage(sender, "specify_item");
            return;
        }
        if (args.length < 3) {
            log.debug("Cannot continue, item's serializer must be supplied");
            sendMessage(sender, "specify_key");
            return;
        }

        final String itemID = args[1];
        final String pack;
        final String name;
        if (itemID.contains(Identifier.SEPARATOR)) {
            final String[] parts = itemID.split(Identifier.SEPARATOR);
            pack = parts[0];
            name = parts[1];
        } else {
            pack = null;
            name = itemID;
        }
        // define parts of the final string
        final QuestPackage configPack = instance.getQuestPackageManager().getPackages().get(pack);
        if (configPack == null) {
            log.debug("Cannot continue, package does not exist");
            sendMessage(sender, "specify_package");
            return;
        }
        final ItemStack item = player.getInventory().getItemInMainHand();
        final String instructions;
        try {
            instructions = instance.getFeatureRegistries().item().getSerializer(args[2]).serialize(item);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Could not serialize item: " + e.getMessage(), e);
            return;
        }
        // save it in items.yml
        log.debug("Saving item to configuration as " + args[1] + " (" + args[2] + ")");
        final String path = "items." + name;
        final boolean exists = configPack.getConfig().isSet(path);
        configPack.getConfig().set(path, args[2] + " " + instructions);
        try {
            if (!exists) {
                final ConfigAccessor itemFile = configPack.getOrCreateConfigAccessor("items.yml");
                configPack.getConfig().associateWith(path, itemFile.getConfig());
            }
            configPack.saveAll();
        } catch (final IOException | InvalidConfigurationException e) {
            log.warn(configPack, e.getMessage(), e);
            return;
        }
        // done
        sendMessage(sender, "item_created",
                new VariableReplacement("item", Component.text(args[1])));
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest item command.
     */
    private Optional<List<String>> completeItems(final String... args) {
        if (args.length == 2) {
            return completeId(args, AccessorType.ITEMS);
        }
        if (args.length == 3) {
            return Optional.of(List.copyOf(instance.getFeatureRegistries().item().serializerKeySet()));
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Fires an event for an online player. It cannot work for offline players!
     */
    private void handleEvents(final CommandSender sender, final String... args) {
        // the player has to be specified every time
        if (args.length < 2 || Bukkit.getPlayer(args[1]) == null && !"-".equals(args[1])) {
            log.debug("Player's name is missing or he's offline");
            sendMessage(sender, "specify_player");
            return;
        }
        if (args.length < 3) {
            log.debug("Event's ID is missing");
            sendMessage(sender, "specify_event");
            return;
        }
        final EventID eventID;
        try {
            eventID = new EventID(instance.getQuestPackageManager(), null, args[2]);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Could not find event: " + e.getMessage(), e);
            return;
        }
        // fire the event
        final Profile profile = "-".equals(args[1]) ? null : profileProvider.getProfile(Bukkit.getOfflinePlayer(args[1]));
        instance.getQuestTypeApi().event(profile, eventID);
        sendMessage(sender, "player_event",
                new VariableReplacement("event", Component.text(eventID.getInstruction().toString())));
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest event command.
     */
    private Optional<List<String>> completeEvents(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            return completeId(args, AccessorType.EVENTS);
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Checks if specified player meets condition described by ID.
     */
    private void handleConditions(final CommandSender sender, final String... args) {
        // the player has to be specified every time
        if (args.length < 2 || Bukkit.getPlayer(args[1]) == null && !"-".equals(args[1])) {
            log.debug("Player's name is missing or he's offline");
            sendMessage(sender, "specify_player");
            return;
        }
        // the condition ID
        if (args.length < 3) {
            log.debug("Condition's ID is missing");
            sendMessage(sender, "specify_condition");
            return;
        }
        final ConditionID conditionID;
        try {
            conditionID = new ConditionID(instance.getQuestPackageManager(), null, args[2]);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Could not find condition: " + e.getMessage(), e);
            return;
        }
        // display message about condition
        final Profile profile = "-".equals(args[1]) ? null : profileProvider.getProfile(Bukkit.getOfflinePlayer(args[1]));
        sendMessage(sender, "player_condition",
                new VariableReplacement("condition", Component.text((conditionID.inverted() ? "! " : "") + conditionID.getInstruction())),
                new VariableReplacement("result", Component.text(instance.getQuestTypeApi().condition(profile, conditionID))));
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest condition command.
     */
    private Optional<List<String>> completeConditions(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            return completeId(args, AccessorType.CONDITIONS);
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Lists, adds or removes tags.
     */
    private void handleTags(final CommandSender sender, final String... args) {
        final PlayerData playerData = getTargetPlayerData(sender, args);
        if (playerData == null) {
            return;
        }
        // if there are no arguments then list player's tags
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            log.debug("Listing tags");
            final Predicate<String> shouldDisplay = createListFilter(args, 3, Function.identity());
            sendMessage(sender, "player_tags");
            playerData.getTags().stream()
                    .filter(shouldDisplay)
                    .sorted()
                    .forEach(tag -> sender.sendMessage("§b- " + tag));
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            log.debug("Missing tag name");
            sendMessage(sender, "specify_tag");
            return;
        }
        final String tag = args[3];
        // if there are arguments, handle them
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "add", "a" -> {
                log.debug("Adding tag");
                playerData.addTag(tag);
                sendMessage(sender, "tag_added");
            }
            case "remove", "delete", "del", "r", "d" -> {
                log.debug("Removing tag");
                playerData.removeTag(tag);
                sendMessage(sender, "tag_removed");
            }
            default -> {
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
            }
        }
    }

    /**
     * Lists, adds or removes global tags.
     */
    private void handleGlobalTags(final CommandSender sender, final String... args) {
        final GlobalData data = instance.getGlobalData();
        // if there are no arguments then list all global tags
        if (args.length < 2 || "list".equalsIgnoreCase(args[1]) || "l".equalsIgnoreCase(args[1])) {
            log.debug("Listing global tags");
            final Predicate<String> shouldDisplay = createListFilter(args, 2, Function.identity());
            sendMessage(sender, "global_tags");
            data.getTags().stream()
                    .filter(shouldDisplay)
                    .sorted()
                    .forEach(tag -> sender.sendMessage("§b- " + tag));
            return;
        }
        // handle purge
        if ("purge".equalsIgnoreCase(args[1])) {
            log.debug("Purging all global tags");
            data.purgeTags();
            sendMessage(sender, "global_tags_purged");
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 3) {
            log.debug("Missing tag name");
            sendMessage(sender, "specify_tag");
            return;
        }
        final String tag = args[2];
        // if there are arguments, handle them
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "add", "a" -> {
                log.debug("Adding global tag " + tag);
                data.addTag(tag);
                sendMessage(sender, "tag_added");
            }
            case "remove", "delete", "del", "r", "d" -> {
                log.debug("Removing global tag " + tag);
                data.removeTag(tag);
                sendMessage(sender, "tag_removed");
            }
            default -> {
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
            }
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the {@code /betonquest tags} command.
     */
    private Optional<List<String>> completeTags(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            return Optional.of(Arrays.asList("list", "add", "del"));
        }
        if (args.length == 4) {
            return completeId(args, null);
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Returns a list including all possible options for tab complete of the {@code /betonquest globaltags} command.
     */
    private Optional<List<String>> completeGlobalTags(final String... args) {
        if (args.length == 2) {
            return Optional.of(Arrays.asList("list", "add", "del", "purge"));
        }
        if (args.length == 3) {
            if ("purge".equalsIgnoreCase(args[1])) {
                return Optional.of(new ArrayList<>());
            }
            return completeId(args, null);
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Lists, adds or removes objectives.
     */
    private void handleObjectives(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }
        final boolean isOnline = profile.getOnlineProfile().isPresent();
        final PlayerData playerData;
        if (isOnline) {
            playerData = dataStorage.get(profile);
        } else {
            log.debug("Profile is offline, loading his data");
            playerData = playerDataFactory.createPlayerData(profile);
        }
        // if there are no arguments then list player's objectives
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            // display objectives
            log.debug("Listing objectives");
            final Predicate<String> shouldDisplay = createListFilter(args, 3, Function.identity());
            final Stream<String> objectives;
            if (isOnline) {
                // if the player is online then just retrieve tags from his active objectives
                objectives = instance.getQuestTypeApi().getPlayerObjectives(profile).stream()
                        .map(Objective::getLabel);
            } else {
                // if player is offline then convert his raw objective strings to tags
                objectives = playerData.getRawObjectives().keySet().stream();
            }
            sendMessage(sender, "player_objectives");
            objectives.filter(shouldDisplay)
                    .sorted()
                    .forEach(objective -> sender.sendMessage("§b- " + objective));
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            log.debug("Missing objective instruction string");
            sendMessage(sender, "specify_objective");
            return;
        }
        // get the objective
        final ObjectiveID objectiveID;
        final Objective objective;
        try {
            objectiveID = new ObjectiveID(instance.getQuestPackageManager(), null, args[3]);
            objective = instance.getQuestTypeApi().getObjective(objectiveID);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Could not find objective: " + e.getMessage(), e);
            return;
        }
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "start", "s", "add", "a" -> {
                log.debug("Adding new objective " + objectiveID + " for " + profile);
                if (isOnline) {
                    instance.getQuestTypeApi().newObjective(profile, objectiveID);
                } else {
                    playerData.addNewRawObjective(objectiveID);
                }
                sendMessage(sender, "objective_added");
            }
            case "remove", "delete", "del", "r", "d" -> {
                log.debug(
                        "Deleting objective " + objectiveID + " for " + profile);
                objective.cancelObjectiveForPlayer(profile);
                playerData.removeRawObjective(objectiveID);
                sendMessage(sender, "objective_removed");
            }
            case "complete", "c" -> {
                log.debug(
                        "Completing objective " + objectiveID + " for " + profile);
                if (isOnline) {
                    objective.completeObjective(profile);
                } else {
                    playerData.removeRawObjective(objectiveID);
                }
                sendMessage(sender, "objective_completed");
            }
            default -> {
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
            }
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the {@code /betonquest objectives} command.
     */
    private Optional<List<String>> completeObjectives(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            return Optional.of(Arrays.asList("list", "add", "del", "complete"));
        }
        if (args.length == 4) {
            return completeId(args, AccessorType.OBJECTIVES);
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Renames stuff.
     */
    @SuppressWarnings("PMD.NcssCount")
    private void handleRenaming(final CommandSender sender, final String... args) {
        if (args.length < 4) {
            sendMessage(sender, "arguments");
            return;
        }
        final String type = args[1].toLowerCase(Locale.ROOT);
        final String name = args[2];
        final String rename = args[3];
        final UpdateType updateType;
        final List<OnlineProfile> onlineProfiles = profileProvider.getOnlineProfiles();
        switch (type) {
            case "tags", "tag", "t" -> {
                updateType = UpdateType.RENAME_ALL_TAGS;
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    final PlayerData playerData = dataStorage.get(onlineProfile);
                    playerData.removeTag(name);
                    playerData.addTag(rename);
                }
            }
            case "points", "point", "p" -> {
                updateType = UpdateType.RENAME_ALL_POINTS;
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    final PlayerData playerData = dataStorage.get(onlineProfile);
                    int points = 0;
                    for (final Point point : playerData.getPoints()) {
                        if (point.getCategory().equals(name)) {
                            points = point.getCount();
                            break;
                        }
                    }
                    playerData.removePointsCategory(name);
                    playerData.modifyPoints(rename, points);
                }
            }
            case "globalpoints", "globalpoint", "gpoints", "gpoint", "gp" -> {
                updateType = UpdateType.RENAME_ALL_GLOBAL_POINTS;
                int globalpoints = 0;
                for (final Point globalpoint : instance.getGlobalData().getPoints()) {
                    if (globalpoint.getCategory().equals(name)) {
                        globalpoints = globalpoint.getCount();
                        break;
                    }
                }
                instance.getGlobalData().removePointsCategory(name);
                instance.getGlobalData().modifyPoints(rename, globalpoints);
            }
            case "objectives", "objective", "o" -> {
                updateType = UpdateType.RENAME_ALL_OBJECTIVES;
                // get ID and package
                final ObjectiveID nameID;
                try {
                    nameID = new ObjectiveID(instance.getQuestPackageManager(), null, name);
                } catch (final QuestException e) {
                    sendMessage(sender, "error",
                            new VariableReplacement("error", Component.text(e.getMessage())));
                    log.warn("Could not find Objective: " + e.getMessage(), e);
                    return;
                }
                // rename objective in the file
                final MultiConfiguration configuration = nameID.getPackage().getConfig();
                final String newPath = "objectives." + rename.split("\\.")[1];
                configuration.set(newPath, nameID.getInstruction().toString());
                try {
                    final ConfigurationSection sourceConfigurationSection = configuration.getSourceConfigurationSection(nameID.get());
                    if (sourceConfigurationSection == null) {
                        sendMessage(sender, "error",
                                new VariableReplacement("error", Component.text("There is no SourceConfigurationSection!")));
                        log.warn(nameID.getPackage(), "There is no SourceConfigurationSection!");
                        break;
                    }
                    configuration.associateWith(newPath, sourceConfigurationSection);
                    nameID.getPackage().saveAll();
                } catch (final IOException | InvalidConfigurationException e) {
                    log.warn(nameID.getPackage(), e.getMessage(), e);
                    return;
                }
                // rename objective instance
                final ObjectiveID renameID;
                try {
                    renameID = new ObjectiveID(instance.getQuestPackageManager(), null, rename);
                } catch (final QuestException e) {
                    sender.sendMessage("§4There was an unexpected error: " + e.getMessage());
                    log.reportException(e);
                    return;
                }
                instance.getQuestTypeApi().renameObjective(nameID, renameID);
                nameID.getPackage().getConfig().set(nameID.get(), null);
                try {
                    nameID.getPackage().saveAll();
                } catch (final IOException e) {
                    log.warn(nameID.getPackage(), e.getMessage(), e);
                    return;
                }
            }
            case "journals", "journal", "j", "entries", "entry", "e" -> {
                updateType = UpdateType.RENAME_ALL_ENTRIES;
                final QuestPackage newPackage = instance.getQuestPackageManager().getPackages().get(rename.split(Identifier.SEPARATOR)[0]);
                if (newPackage == null) {
                    final String message = "You can't rename into non-existent package!";
                    sendMessage(sender, "error", new VariableReplacement("error", Component.text(message)));
                    log.error(message);
                    return;
                }

                final JournalEntryID newEntryID;
                try {
                    newEntryID = new JournalEntryID(instance.getQuestPackageManager(), null, rename);
                } catch (final QuestException e) {
                    final String message = "You can't rename into non-existent id!";
                    sendMessage(sender, "error", new VariableReplacement("error", Component.text(message)));
                    log.error(message);
                    return;
                }

                final JournalEntryID oldEntryID;
                try {
                    oldEntryID = new JournalEntryID(instance.getQuestPackageManager(), null, name);
                } catch (final QuestException e) {
                    final String message = "Old journal entry " + name + " does not exist, renaming only database entries!";
                    log.warn(message, e);
                    log.debug("Renaming non existent journal entry only from database: " + e.getMessage(), e);
                    sender.sendMessage("§2" + message);
                    break;
                }

                instance.getFeatureApi().renameJournalEntry(oldEntryID, newEntryID);
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    final Journal journal = dataStorage.get(onlineProfile).getJournal();
                    final List<Pointer> journalPointers = new ArrayList<>();
                    for (final Pointer pointer : journal.getPointers()) {
                        if (pointer.pointer().equals(oldEntryID)) {
                            journalPointers.add(pointer);
                        }
                    }
                    if (journalPointers.isEmpty()) {
                        continue;
                    }
                    for (final Pointer pointer : journalPointers) {
                        journal.removePointer(oldEntryID);
                        journal.addPointer(new Pointer(newEntryID, pointer.timestamp()));
                    }
                    journal.update();
                }
            }
            default -> {
                sendMessage(sender, "unknown_argument");
                return;
            }
        }
        instance.getSaver().add(new Record(updateType, rename, name));
        sendMessage(sender, "everything_renamed");
    }

    /**
     * Returns a list including all possible options for tab complete of the {@code /betonquest rename} command.
     */
    private Optional<List<String>> completeRenaming(final String... args) {
        if (args.length <= 3) {
            return completeDeleting(args);
        }
        if (args.length == 4) {
            return completeId(args, null);
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Deleted stuff.
     */
    @SuppressWarnings("PMD.NcssCount")
    private void handleDeleting(final CommandSender sender, final String... args) {
        if (args.length < 3) {
            sendMessage(sender, "arguments");
            return;
        }
        final String type = args[1].toLowerCase(Locale.ROOT);
        final String name = args[2];
        final UpdateType updateType;
        final List<OnlineProfile> onlineProfiles = profileProvider.getOnlineProfiles();
        switch (type) {
            case "tags", "tag", "t" -> {
                updateType = UpdateType.REMOVE_ALL_TAGS;
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    final PlayerData playerData = dataStorage.get(onlineProfile);
                    playerData.removeTag(name);
                }
            }
            case "points", "point", "p" -> {
                updateType = UpdateType.REMOVE_ALL_POINTS;
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    final PlayerData playerData = dataStorage.get(onlineProfile);
                    playerData.removePointsCategory(name);
                }
            }
            case "objectives", "objective", "o" -> {
                updateType = UpdateType.REMOVE_ALL_OBJECTIVES;
                final ObjectiveID objectiveID;
                final Objective objective;
                try {
                    objectiveID = new ObjectiveID(instance.getQuestPackageManager(), null, name);
                    objective = instance.getQuestTypeApi().getObjective(objectiveID);
                } catch (final QuestException e) {
                    final String message = "The objective '" + name + "' does not exist, it will still be removed from the database!";
                    sendMessage(sender, "error",
                            new VariableReplacement("error", Component.text(e.getMessage())));
                    log.warn(message, e);
                    log.debug("Removing non existent objective only from database: " + e.getMessage(), e);
                    break;
                }
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    objective.cancelObjectiveForPlayer(onlineProfile);
                    dataStorage.get(onlineProfile).removeRawObjective(objectiveID);
                }
            }
            case "journals", "journal", "j", "entries", "entry", "e" -> {
                updateType = UpdateType.REMOVE_ALL_ENTRIES;
                final JournalEntryID entryID;
                try {
                    entryID = new JournalEntryID(instance.getQuestPackageManager(), null, name);
                } catch (final QuestException e) {
                    final String message = "The journal entry '" + name + "' does not exist, it will still be removed from the database!";
                    log.warn(message, e);
                    log.debug("Removing non existent journal entry only from database: " + e.getMessage(), e);
                    sender.sendMessage("§2" + message);
                    break;
                }
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    final Journal journal = dataStorage.get(onlineProfile).getJournal();
                    int count = 0;
                    for (final Pointer pointer : journal.getPointers()) {
                        if (pointer.pointer().equals(entryID)) {
                            count++;
                        }
                    }
                    if (count == 0) {
                        continue;
                    }
                    for (int i = 0; i < count; i++) {
                        journal.removePointer(entryID);
                    }
                    journal.update();
                }
            }
            default -> {
                sendMessage(sender, "unknown_argument");
                return;
            }
        }
        instance.getSaver().add(new Record(updateType, name));
        sendMessage(sender, "everything_removed");
    }

    /**
     * Returns a list including all possible options for tab complete of the {@code /betonquest delete} command.
     */
    private Optional<List<String>> completeDeleting(final String... args) {
        if (args.length == 2) {
            return Optional.of(Arrays.asList("tag", "point", "objective", "entry"));
        }
        if (args.length == 3) {
            return switch (args[1].toLowerCase(Locale.ROOT)) {
                case "tags",
                     "tag",
                     "t",
                     "points",
                     "point",
                     "p" -> completeId(args, null);
                case "objectives",
                     "objective",
                     "o" -> completeId(args, AccessorType.OBJECTIVES);
                case "journals",
                     "journal",
                     "j",
                     "entries",
                     "entry",
                     "e" -> completeId(args, AccessorType.JOURNAL);
                default -> Optional.of(new ArrayList<>());
            };
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Specify all commands.
     */
    private Map<String, String> getCommandHelpMap(final CommandSender sender) {
        final Map<String, String> map = new HashMap<>();
        map.put("reload", "reload");
        map.put("objectives", "objective <player> [list/add/del] [objective]");
        map.put("globaltags", "globaltags [list/add/del/purge]");
        map.put("globalpoints", "globalpoints [list/add/del/purge]");
        map.put("tags", "tag <player> [list/add/del] [tag]");
        map.put("points", "point <player> [list/add/del] [category] [amount]");
        map.put("journal", "journal <player> [list/add/del] [entry] [date]");
        map.put("condition", "condition <player> <condition>");
        map.put("event", "event <player> <event>");
        map.put("item", "item <name>");
        map.put("give", "give <name>");
        map.put("variable", "variable <player> <variable> [list/set/del]");
        map.put("rename", "rename <tag/point/globalpoint/objective/journal> <old> <new>");
        map.put("delete", "delete <tag/point/objective/journal> <name>");
        map.put("version", "version");
        map.put("purge", "purge <player>");
        map.put("debug", "debug [true/false/ingame/dump]");
        map.put("download", "download <gitHubNamespace> <ref> <offsetPath> <sourcePath> [targetPath] [recursive] [overwrite]");
        if (!(sender instanceof Player)) {
            map.put("backup", "backup");
        }
        return map;
    }

    /**
     * Displays help to the user.
     */
    private void displayHelp(final CommandSender sender, final String alias) throws QuestException {
        final Map<String, String> commandMap = getCommandHelpMap(sender);
        final TextComponent.Builder builder = Component.text();
        builder.append(Component.text("----- ").color(NamedTextColor.YELLOW))
                .append(Component.text("BetonQuest").color(NamedTextColor.GREEN))
                .append(Component.text(" -----").color(NamedTextColor.YELLOW));
        final OnlineProfile profile = sender instanceof final Player player ? profileProvider.getProfile(player) : null;

        for (final Map.Entry<String, String> entry : commandMap.entrySet()) {
            final Component command = Component.text("/" + alias + " " + entry.getValue()).color(NamedTextColor.RED);
            final Component hint = pluginMessage.getMessage(profile, "command_" + entry.getKey()).color(NamedTextColor.AQUA);

            builder.append(Component.newline());
            if (profile == null) {
                builder.append(command.append(Component.text(" - ").color(NamedTextColor.RED)).append(hint));
            } else {
                builder.append(command.hoverEvent(HoverEvent.showText(hint)));
            }
        }
        sender.sendMessage(builder.build());
    }

    private void displayVersionInfo(final CommandSender sender, final String commandAlias) throws QuestException {
        final String updateCommand = "/" + commandAlias + " update";

        final Component hooked = displayVersionInfoHooked();
        final Component update = displayVersionInfoUpdate(instance.getUpdater());
        final Component copy = displayVersionInfoCopy(sender);

        final VariableComponent baseContent = new VariableComponent(pluginMessage.getMessage(null, "command_version_output.info",
                new VariableReplacement("version", Component.text(instance.getDescription().getVersion())),
                new VariableReplacement("server", Component.text(Bukkit.getServer().getVersion())),
                new VariableReplacement("hooked", hooked)));
        final Component copyContent = baseContent.resolve(
                new VariableReplacement("update", Component.empty()),
                new VariableReplacement("copy", Component.empty()));
        final Component info = baseContent.resolve(
                new VariableReplacement("update", update.clickEvent(ClickEvent.suggestCommand(updateCommand))),
                new VariableReplacement("copy", copy.clickEvent(ClickEvent.copyToClipboard(PlainTextComponentSerializer.plainText().serialize(copyContent)))));
        sender.sendMessage(info);
    }

    private Component displayVersionInfoHooked() throws QuestException {
        final TextComponent.Builder hookedBuilder = Component.text();
        for (final String plugin : compatibility.getHooked()) {
            final Plugin plug = Bukkit.getPluginManager().getPlugin(plugin);
            if (plug == null) {
                continue;
            }
            if (!hookedBuilder.children().isEmpty()) {
                hookedBuilder.append(Component.text(", "));
            }
            hookedBuilder.append(pluginMessage.getMessage(null, "command_version_output.hook",
                    new VariableReplacement("plugin", Component.text(plugin)),
                    new VariableReplacement("version", Component.text(plug.getDescription().getVersion()))));
        }
        return hookedBuilder.build();
    }

    private Component displayVersionInfoUpdate(final Updater updater) throws QuestException {
        if (!updater.isUpdateAvailable()) {
            return Component.empty();
        }
        return pluginMessage.getMessage(null, "command_version_output.update",
                new VariableReplacement("version", Component.text(updater.getUpdateVersion())));
    }

    private Component displayVersionInfoCopy(final CommandSender sender) throws QuestException {
        if (sender instanceof ConsoleCommandSender) {
            return Component.empty();
        }
        return pluginMessage.getMessage(null, "command_version_output.copy");
    }

    private void handleDebug(final CommandSender sender, final String... args) {
        if (args.length == 1) {
            sender.sendMessage(
                    "§2Debugging mode is currently " + (debuggingController.isLogging() ? "enabled" : "disabled") + '!');
            return;
        }
        if ("ingame".equalsIgnoreCase(args[1])) {
            if (!(sender instanceof Player)) {
                log.debug("Cannot continue, sender must be player");
                return;
            }
            final UUID uuid = ((Player) sender).getUniqueId();
            if (args.length < 3) {
                sender.sendMessage("§2Active Filters: " + String.join(", ", logWatcher.getActivePatterns(uuid)));
                return;
            }
            final String filter = args[2];
            if (logWatcher.isActivePattern(uuid, filter)) {
                if (args.length == 3) {
                    logWatcher.removeFilter(uuid, filter);
                    sender.sendMessage("§2Filter removed!");
                } else {
                    final Level level = getLogLevel(args[3]);
                    logWatcher.addFilter(uuid, filter, level);
                    sender.sendMessage("§2Filter replaced!");
                }
            } else {
                final Level level = getLogLevel(args.length > 3 ? args[3] : null);
                logWatcher.addFilter(uuid, filter, level);
                sender.sendMessage("§2Filter added!");
            }
            return;
        }
        if ("dump".equalsIgnoreCase(args[1])) {
            if (debuggingController.isLogging()) {
                sender.sendMessage("§2Can not dump while debugging is enabled!");
                return;
            }
            debuggingController.dumpLog();
            sender.sendMessage("§2Dumped debug log to file!");
            log.info("Dumped debug log to file!");
            return;
        }
        final Boolean input = "true".equalsIgnoreCase(args[1]) ? Boolean.TRUE
                : "false".equalsIgnoreCase(args[1]) ? Boolean.FALSE : null;
        if (input != null && args.length == 2) {

            if (debuggingController.isLogging() && input || !debuggingController.isLogging() && !input) {
                sender.sendMessage(
                        "§2Debugging mode is already " + (debuggingController.isLogging() ? "enabled" : "disabled") + '!');
                return;
            }
            try {
                if (input) {
                    debuggingController.startLogging();
                } else {
                    debuggingController.stopLogging();
                }
            } catch (final IOException e) {
                sender.sendMessage("Could not save new debugging state to configuration file!");
                log.warn("Could not save new debugging state to configuration file! " + e.getMessage(), e);
            }
            sender.sendMessage("§2Debugging mode was " + (debuggingController.isLogging() ? "enabled" : "disabled") + '!');
            log.info("Debugging mode was " + (debuggingController.isLogging() ? "enabled" : "disabled") + '!');
            return;
        }
        sendMessage(sender, "unknown_argument");
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void handleDownload(final CommandSender sender, final String... args) {
        if (args.length < 5) {
            sendMessage(sender, "arguments");
            return;
        }
        final String sourcePath = args[4];
        final String targetPath;
        boolean recursive = false;
        boolean overwrite = false;
        if (args.length < 6 || Set.of("recursive", "overwrite").contains(args[5])) {
            targetPath = sourcePath;
        } else {
            targetPath = args[5];
        }
        for (int i = 5; i < args.length; i++) {
            switch (args[i].toLowerCase(Locale.ROOT)) {
                case "recursive" -> recursive = true;
                case "overwrite" -> overwrite = true;
                default -> {
                    if (i > 5) {
                        sendMessage(sender, "unknown_argument");
                        return;
                    }
                }
            }
        }
        final String githubNamespace = args[1];
        final String ref = args[2];
        final String offsetPath = args[3];
        final String errSummary = String.format("Download from %s ref %s of %s at %s to %s failed:",
                githubNamespace, ref, offsetPath, sourcePath, targetPath);

        //Check offset paths
        if (!Downloader.ALLOWED_OFFSET_PATHS.contains(offsetPath)) {
            sendMessage(sender, "download_failed_offset");
            log.debug(errSummary, new IllegalArgumentException(offsetPath));
            return;
        }

        //check if repo is allowed
        final List<String> whitelist = instance.getPluginConfig().getStringList("downloader.repo_whitelist");
        if (whitelist.stream().map(String::trim).noneMatch(githubNamespace::equals)) {
            sendMessage(sender, "download_failed_whitelist");
            log.debug(errSummary, new IllegalArgumentException(githubNamespace));
            return;
        }

        //check if ref is valid
        if (ref.toLowerCase(Locale.ROOT).startsWith("refs/pull/") && !instance.getPluginConfig().getBoolean("downloader.pull_request", false)) {
            sendMessage(sender, "download_failed_pr");
            log.debug(errSummary, new IllegalArgumentException(ref));
            return;
        }

        //run download
        final Downloader downloader = new Downloader(loggerFactory.create(Downloader.class, "Downloader"),
                instance.getDataFolder(), githubNamespace, ref, offsetPath, sourcePath, targetPath, recursive, overwrite);
        sendMessage(sender, "download_scheduled");
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            try {
                downloader.call();
                sendMessageSync(sender, "download_success");
            } catch (final DownloadFailedException | SecurityException | FileNotFoundException e) {
                final String message = e.getMessage();
                sendMessageSync(sender, "download_failed",
                        new VariableReplacement("error", Component.text(message == null ? e.getClass().getSimpleName() : message)));
                log.debug(errSummary, e);
            } catch (final Exception e) {
                sendMessageSync(sender, "download_failed",
                        new VariableReplacement("error", Component.text(e.getClass().getSimpleName() + ": " + e.getMessage())));
                if (sender instanceof final Player player) {
                    final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.FINE, null, instance);
                    record.setThrown(e);
                    player.sendMessage(new ChatFormatter().formatTextComponent(record));
                    log.debug(errSummary, e);
                } else {
                    log.error(errSummary, e);
                }
            }
        });
    }

    private Optional<List<String>> completeDownload(final String... args) {
        return switch (args.length) {
            case 2 -> Optional.of(instance.getPluginConfig().getStringList("downloader.repo_whitelist"));
            case 3 -> Optional.of(List.of("refs/heads/", "refs/tags/"));
            case 4 -> Optional.of(Downloader.ALLOWED_OFFSET_PATHS);
            case 5 -> Optional.of(List.of("/"));
            case 6 -> Optional.of(List.of("/", "overwrite", "recursive"));
            case 7, 8 ->
                    Optional.of(Stream.of("overwrite", "recursive").filter(tag -> !Arrays.asList(args).contains(tag)).toList());
            default -> Optional.of(List.of());
        };
    }

    /**
     * Variables stuff.
     */
    @SuppressWarnings("PMD.NcssCount")
    private void handleVariables(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }

        if (args.length == 2) {
            log.debug("Missing objective instruction string");
            sendMessage(sender, "specify_objective");
            return;
        }

        // get the objective
        final ObjectiveID objectiveID;
        final Objective tmp;
        try {
            objectiveID = new ObjectiveID(instance.getQuestPackageManager(), null, args[2]);
            tmp = instance.getQuestTypeApi().getObjective(objectiveID);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Could not find objective: " + e.getMessage(), e);
            return;
        }
        if (!(tmp instanceof final VariableObjective variableObjective)) {
            log.debug(tmp.getLabel() + " is not a variable objective");
            sendMessage(sender, "specify_objective");
            return;
        }
        log.debug("Using variable objective " + variableObjective.getLabel());

        final boolean isOnline = profile.getOnlineProfile().isPresent();
        final VariableObjective.VariableData data;
        if (isOnline) {
            data = null;
        } else {
            final PlayerData offline = instance.getPlayerDataStorage().getOffline(profile);
            final String instruction = offline.getRawObjectives().get(variableObjective.getLabel());
            if (instruction == null) {
                log.debug("There is no data for that objective for that player!");
                sendMessage(sender, "error",
                        new VariableReplacement("error", Component.text("There is no data for that objective!")));
                return;
            }
            data = new VariableObjective.VariableData(instruction, profile, objectiveID);
        }

        final String subCommand = args.length == 3 ? "list" : args[3].toLowerCase(Locale.ROOT);
        switch (subCommand) {
            case "list", "l" -> {
                if (data != null) {
                    log.debug("Can't list variable data on offline player");
                    sendMessage(sender, "offline_invalid");
                    return;
                }
                // check for actual values
                final Map<String, String> properties = variableObjective.getProperties(profile);
                if (properties == null) {
                    log.debug("No property for profile");
                    sendMessage(sender, "player_no_property");
                    return;
                }
                // display variable objective keys and values
                log.debug("Listing keys and values");
                final Predicate<String> shouldDisplay = createListFilter(args, 4, Function.identity());
                sendMessage(sender, "player_variables",
                        new VariableReplacement("objective", Component.text(variableObjective.getLabel())));
                properties.entrySet().stream()
                        .filter(entry -> shouldDisplay.test(entry.getKey()))
                        .sorted((o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()))
                        .forEach(entry -> sender.sendMessage("§b- " + entry.getKey() + "§e: §a" + entry.getValue()));
            }
            case "set", "s" -> {
                if (args.length < 6) {
                    log.debug("Missing amount");
                    sendMessage(sender, "arguments");
                    return;
                }
                final String value = String.join(" ", Arrays.copyOfRange(args, 5, args.length));
                log.debug("Setting value " + value + " for key " + args[4] + " for " + profile + " in " + variableObjective.getLabel());
                if (data == null) {
                    variableObjective.store(profile, args[4], value);
                } else {
                    data.add(args[4], value);
                }
                sendMessage(sender, "value_set",
                        new VariableReplacement("value", Component.text(value)),
                        new VariableReplacement("key", Component.text(args[4])));
            }
            case "del", "d" -> {
                if (args.length < 5) {
                    log.debug("Missing amount");
                    sendMessage(sender, "arguments");
                    return;
                }
                log.debug("Removing key " + args[4] + " for " + profile + " in " + variableObjective.getLabel());
                if (data == null) {
                    variableObjective.store(profile, args[4], null);
                } else {
                    data.add(args[4], null);
                }
                sendMessage(sender, "key_remove",
                        new VariableReplacement("key", Component.text(args[4])));
            }
            default -> {
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
            }
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the {@code /betonquest variables} command.
     */
    private Optional<List<String>> completeVariables(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            final String last = args[args.length - 1];
            if (last == null || !last.contains(Identifier.SEPARATOR)) {
                return completePackage();
            } else {
                final String pack = last.substring(0, last.indexOf(Identifier.SEPARATOR));
                final QuestPackage configPack = instance.getQuestPackageManager().getPackages().get(pack);
                if (configPack == null) {
                    return Optional.of(Collections.emptyList());
                }
                final ConfigurationSection configuration = configPack.getConfig().getConfigurationSection("objectives");
                final List<String> completions = new ArrayList<>();
                if (configuration != null) {
                    for (final String key : configuration.getKeys(false)) {
                        final String rawObjectiveInstruction = configuration.getString(key);
                        if (rawObjectiveInstruction != null && rawObjectiveInstruction.stripIndent().startsWith("variable")) {
                            completions.add(pack + Identifier.SEPARATOR + key);
                        }
                    }
                }
                return Optional.of(completions);
            }
        }
        if (args.length == 4) {
            return Optional.of(Arrays.asList("list", "set", "del"));
        }
        return Optional.of(Collections.emptyList());
    }

    private Level getLogLevel(@Nullable final String arg) {
        if ("info".equalsIgnoreCase(arg)) {
            return Level.INFO;
        }
        if ("debug".equalsIgnoreCase(arg)) {
            return Level.ALL;
        }
        return Level.WARNING;
    }

    private Optional<List<String>> completeDebug(final String... args) {
        if (args.length == 2) {
            return Optional.of(Arrays.asList("true", "false", "ingame", "dump"));
        }
        if (args.length == 3) {
            return completePackage();
        }
        if (args.length == 4) {
            return Optional.of(Arrays.asList("error", "info", "debug"));
        }
        return Optional.of(new ArrayList<>());
    }

    private void sendMessageSync(final CommandSender sender, final String messageName, final VariableReplacement... variables) {
        Bukkit.getScheduler().runTask(instance, () -> sendMessage(sender, messageName, variables));
    }

    private void sendMessage(final CommandSender sender, final String messageName, final VariableReplacement... variables) {
        final OnlineProfile profile = sender instanceof final Player player ? profileProvider.getProfile(player) : null;
        try {
            sender.sendMessage(pluginMessage.getMessage(profile, messageName, variables));
        } catch (final QuestException e) {
            log.warn("Failed to send message '" + messageName + "': " + e.getMessage(), e);
            sender.sendMessage("Failed to send message '" + messageName + "': " + e.getMessage());
        }
    }

    private <T> Predicate<T> createListFilter(final String[] args, final int filterIndex, final Function<T, String> getId) {
        if (args.length > filterIndex) {
            return createCaseInsensitivePrefixPredicate(args[filterIndex], getId);
        } else {
            return pointer -> true;
        }
    }

    private <T> Predicate<T> createCaseInsensitivePrefixPredicate(final String prefix, final Function<T, String> getId) {
        return element -> getId.apply(element).regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /**
     * Accessor Type for ID completion.
     * The enum in lower case is the used section.
     */
    private enum AccessorType {
        /**
         * EventID.
         */
        EVENTS,
        /**
         * ConditionID.
         */
        CONDITIONS,
        /**
         * ObjectiveID.
         */
        OBJECTIVES,
        /**
         * ItemID.
         */
        ITEMS,
        /**
         * JournalID.
         */
        JOURNAL
    }
}
