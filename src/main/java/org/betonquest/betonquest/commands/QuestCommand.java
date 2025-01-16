package org.betonquest.betonquest.commands;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.Pointer;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.PlayerLogWatcher;
import org.betonquest.betonquest.modules.logger.format.ChatFormatter;
import org.betonquest.betonquest.modules.logger.handler.history.LogPublishingController;
import org.betonquest.betonquest.modules.web.downloader.DownloadFailedException;
import org.betonquest.betonquest.modules.web.downloader.Downloader;
import org.betonquest.betonquest.modules.web.updater.Updater;
import org.betonquest.betonquest.objectives.VariableObjective;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.give.GiveEvent;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * Main admin command for quest editing.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.NPathComplexity", "PMD.TooManyMethods",
        "PMD.CommentRequired", "PMD.AvoidDuplicateLiterals", "PMD.AvoidLiteralsInIfCondition",
        "PMD.CognitiveComplexity", "PMD.CouplingBetweenObjects"})
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

    private final ConfigAccessorFactory configAccessorFactory;

    private final BukkitAudiences bukkitAudiences;

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
     * @param configAccessorFactory the config accessor factory to use
     * @param bukkitAudiences       the bukkit audiences to use
     * @param logWatcher            the player log watcher to use
     * @param debuggingController   the log publishing controller to use
     * @param log                   the logger that will be used for logging
     * @param plugin                the BetonQuest plugin instance
     * @param dataStorage           the storage providing player data
     */
    public QuestCommand(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log, final ConfigAccessorFactory configAccessorFactory,
                        final BukkitAudiences bukkitAudiences, final PlayerLogWatcher logWatcher, final LogPublishingController debuggingController,
                        final BetonQuest plugin, final PlayerDataStorage dataStorage) {
        this.loggerFactory = loggerFactory;
        this.log = log;
        this.configAccessorFactory = configAccessorFactory;
        this.bukkitAudiences = bukkitAudiences;
        this.logWatcher = logWatcher;
        this.debuggingController = debuggingController;
        this.instance = plugin;
        this.dataStorage = dataStorage;
    }

    @SuppressWarnings("PMD.NcssCount")
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String alias, final String... args) {

        if ("betonquest".equalsIgnoreCase(cmd.getName())) {
            log.debug("Executing /betonquest command for user " + sender.getName()
                    + " with arguments: " + Arrays.toString(args));
            // if the command is empty, display help message
            if (args.length <= 0) {
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
                    Utils.backup(configAccessorFactory);
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
        return false;
    }

    @SuppressWarnings("PMD.NcssCount")
    @Override
    public Optional<List<String>> simpleTabComplete(final CommandSender sender, final Command command, final String alias, final String... args) {
        if (args.length == 1) {
            return Optional.of(Arrays.asList("condition", "event", "item", "give", "objective", "globaltag",
                    "globalpoint", "tag", "point", "journal", "delete", "rename", "version", "purge",
                    "update", "reload", "backup", "debug", "download", "variable"));
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "conditions":
            case "condition":
            case "c":
                return completeConditions(args);
            case "events":
            case "event":
            case "e":
                return completeEvents(args);
            case "items":
            case "item":
            case "i":
            case "give":
            case "g":
                return completeItems(args);
            case "objectives":
            case "objective":
            case "o":
                return completeObjectives(args);
            case "globaltags":
            case "globaltag":
            case "gtag":
            case "gtags":
            case "gt":
                return completeGlobalTags(args);
            case "globalpoints":
            case "globalpoint":
            case "gpoints":
            case "gpoint":
            case "gp":
                return completeGlobalPoints(args);
            case "tags":
            case "tag":
            case "t":
                return completeTags(args);
            case "points":
            case "point":
            case "p":
                return completePoints(args);
            case "journals":
            case "journal":
            case "j":
                return completeJournals(args);
            case "delete":
            case "del":
            case "d":
                return completeDeleting(args);
            case "rename":
            case "r":
                return completeRenaming(args);
            case "purge":
                if (args.length == 2) {
                    return Optional.empty();
                } else {
                    return Optional.of(new ArrayList<>());
                }
            case "debug":
                return completeDebug(args);
            case "download":
                return completeDownload(args);
            case "variable":
            case "var":
                return completeVariables(args);
            case "version":
            case "ver":
            case "v":
            case "update":
            case "reload":
            case "backup":
            case "package":
            default:
                return Optional.of(new ArrayList<>());
        }
    }

    /**
     * Returns a list of all packages for the tab completer.
     *
     * @return
     */
    private Optional<List<String>> completePackage() {
        return Optional.of(new ArrayList<>(Config.getPackages().keySet()));
    }

    /**
     * Returns a list including all possible tab complete options for ids.
     *
     * @param args
     * @param type - the type of the Id (item/event/journal/condition/objective),
     *             null for unspecific
     * @return
     */
    private Optional<List<String>> completeId(final String[] args, @Nullable final AccessorType type) {
        final String last = args[args.length - 1];
        if (last == null || !last.contains(".")) {
            return completePackage();
        } else {
            final String pack = last.substring(0, last.indexOf('.'));
            final QuestPackage configPack = Config.getPackages().get(pack);
            if (configPack == null) {
                return Optional.of(new ArrayList<>());
            }
            if (type == null) {
                final List<String> completions = new ArrayList<>();
                completions.add(pack + '.');
                return Optional.of(completions);
            }
            final ConfigurationSection configuration;
            switch (type) {
                case ITEMS:
                    configuration = configPack.getConfig().getConfigurationSection("items");
                    break;
                case EVENTS:
                    configuration = configPack.getConfig().getConfigurationSection("events");
                    break;
                case JOURNAL:
                    configuration = configPack.getConfig().getConfigurationSection("journal");
                    break;
                case CONDITIONS:
                    configuration = configPack.getConfig().getConfigurationSection("conditions");
                    break;
                case OBJECTIVES:
                    configuration = configPack.getConfig().getConfigurationSection("objectives");
                    break;
                default:
                    return Optional.of(new ArrayList<>());
            }
            final List<String> completions = new ArrayList<>();
            if (configuration != null) {
                for (final String key : configuration.getKeys(false)) {
                    completions.add(pack + '.' + key);
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
                itemID = new ItemID(null, args[1]);
            } catch (final ObjectNotFoundException e) {
                sendMessage(sender, "error", e.getMessage());
                log.warn("Could not find Item: " + e.getMessage(), e);
                return;
            }
            final OnlineEvent give = new GiveEvent(
                    new Item[]{new Item(itemID, new VariableNumber(itemID.getPackage(), "1"))},
                    new NoNotificationSender(),
                    new IngameNotificationSender(log, itemID.getPackage(), itemID.getFullID(), NotificationLevel.ERROR, "inventory_full_backpack", "inventory_full"),
                    new IngameNotificationSender(log, itemID.getPackage(), itemID.getFullID(), NotificationLevel.ERROR, "inventory_full_drop", "inventory_full"),
                    false, dataStorage
            );
            give.execute(PlayerConverter.getID((Player) sender));
        } catch (final QuestException e) {
            sendMessage(sender, "error", e.getMessage());
            log.warn("Error while creating an item: " + e.getMessage(), e);
        }
    }

    /**
     * Purges profile's data.
     */
    private void purgePlayer(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }
        final PlayerData playerData;
        if (profile.getOnlineProfile().isPresent()) {
            playerData = dataStorage.get(profile);
        } else {
            log.debug("Profile is offline, loading his data");
            playerData = new PlayerData(profile);
        }
        log.debug("Purging player " + args[1]);
        playerData.purgePlayer();
        // done
        sendMessage(sender, "purged", args[1]);
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
        return PlayerConverter.getID(Bukkit.getOfflinePlayer(args[1]));
    }

    /**
     * Lists, adds or removes journal entries of certain profile.
     */
    private void handleJournals(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }
        final PlayerData playerData;
        if (profile.getOnlineProfile().isPresent()) {
            playerData = dataStorage.get(profile);
        } else {
            log.debug("Profile is offline, loading his data");
            playerData = new PlayerData(profile);
        }
        final Journal journal = playerData.getJournal();
        // if there are no arguments then list player's pointers
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            log.debug("Listing journal pointers");
            final Predicate<Pointer> shouldDisplay = createListFilter(args, 3, Pointer::getPointer);
            sendMessage(sender, "player_journal");
            journal.getPointers().stream()
                    .filter(shouldDisplay)
                    .forEach(pointer -> {
                        final String date = new SimpleDateFormat(Config.getConfigString("date_format"), Locale.ROOT)
                                .format(new Date(pointer.getTimestamp()));
                        sender.sendMessage("§b- " + pointer.getPointer() + " §c(§2" + date + "§c)");
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
        if (!pointerName.contains(".")) {
            sendMessage(sender, "specify_pointer");
            return;
        }
        // if there are arguments, handle them
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "add":
            case "a":
                final Pointer pointer;
                if (args.length < 5) {
                    final long timestamp = new Date().getTime();
                    log.debug("Adding pointer with current date: " + timestamp);
                    pointer = new Pointer(pointerName, timestamp);
                } else {
                    log.debug("Adding pointer with date " + args[4].replaceAll("_", " "));
                    try {
                        pointer = new Pointer(pointerName, new SimpleDateFormat(Config.getConfigString("date_format"), Locale.ROOT)
                                .parse(args[4].replaceAll("_", " ")).getTime());
                    } catch (final ParseException e) {
                        sendMessage(sender, "specify_date");
                        log.warn("Could not parse date: " + e.getMessage(), e);
                        return;
                    }
                }
                // add the pointer
                journal.addPointer(pointer);
                journal.update();
                sendMessage(sender, "pointer_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the pointer
                log.debug("Removing pointer");
                journal.removePointer(pointerName);
                journal.update();
                sendMessage(sender, "pointer_removed");
                break;
            default:
                // if there was something else, display error message
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest journal command.
     *
     * @param args
     * @return
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
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }
        final PlayerData playerData;
        if (profile.getOnlineProfile().isPresent()) {
            playerData = dataStorage.get(profile);
        } else {
            log.debug("Profile is offline, loading his data");
            playerData = new PlayerData(profile);
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
            case "add":
            case "a":
                if (args.length < 5 || !args[4].matches("-?\\d+")) {
                    log.debug("Missing amount");
                    sendMessage(sender, "specify_amount");
                    return;
                }
                // add the point
                log.debug("Adding points");
                playerData.modifyPoints(category, Integer.parseInt(args[4]));
                sendMessage(sender, "points_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the point (this is unnecessary as adding negative
                // amounts
                // subtracts points, but for the sake of users let's leave it
                // here)
                log.debug("Removing points");
                playerData.removePointsCategory(category);
                sendMessage(sender, "points_removed");
                break;
            default:
                // if there was something else, display error message
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Lists, adds, removes or purges all global points.
     *
     * @param sender
     * @param args
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
            case "add":
            case "a":
                if (args.length < 4 || !args[3].matches("-?\\d+")) {
                    log.debug("Missing amount");
                    sendMessage(sender, "specify_amount");
                    return;
                }
                // add the point
                log.debug("Adding global points");
                data.modifyPoints(category, Integer.parseInt(args[3]));
                sendMessage(sender, "points_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                log.debug("Removing global points");
                data.removePointsCategory(category);
                sendMessage(sender, "points_removed");
                break;
            default:
                // if there was something else, display error message
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest points command.
     *
     * @param args
     * @return
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
     *
     * @param args
     * @return
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

        final String itemID = args[1];
        final String pack;
        final String name;
        if (itemID.contains(".")) {
            final String[] parts = itemID.split("\\.");
            pack = parts[0];
            name = parts[1];
        } else {
            pack = null;
            name = itemID;
        }
        // define parts of the final string
        final QuestPackage configPack = Config.getPackages().get(pack);
        if (configPack == null) {
            log.debug("Cannot continue, package does not exist");
            sendMessage(sender, "specify_package");
            return;
        }
        final ItemStack item = player.getInventory().getItemInMainHand();
        final String instructions = QuestItem.itemToString(item);
        // save it in items.yml
        log.debug("Saving item to configuration as " + args[1]);
        final String path = "items." + name;
        final boolean exists = configPack.getConfig().isSet(path);
        configPack.getConfig().set(path, instructions.trim());
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
        sendMessage(sender, "item_created", args[1]);
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest item command.
     *
     * @param args
     * @return
     */
    private Optional<List<String>> completeItems(final String... args) {
        if (args.length == 2) {
            return completeId(args, AccessorType.ITEMS);
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
            eventID = new EventID(null, args[2]);
        } catch (final ObjectNotFoundException e) {
            sendMessage(sender, "error", e.getMessage());
            log.warn("Could not find event: " + e.getMessage(), e);
            return;
        }
        // fire the event
        final Profile profile = "-".equals(args[1]) ? null : PlayerConverter.getID(Bukkit.getOfflinePlayer(args[1]));
        BetonQuest.event(profile, eventID);
        sendMessage(sender, "player_event", eventID.getInstruction().toString());
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest event command.
     *
     * @param args
     * @return
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
            conditionID = new ConditionID(null, args[2]);
        } catch (final ObjectNotFoundException e) {
            sendMessage(sender, "error", e.getMessage());
            log.warn("Could not find condition: " + e.getMessage(), e);
            return;
        }
        // display message about condition
        final Profile profile = "-".equals(args[1]) ? null : PlayerConverter.getID(Bukkit.getOfflinePlayer(args[1]));
        sendMessage(sender, "player_condition", (conditionID.inverted() ? "! " : "") + conditionID.getInstruction(),
                Boolean.toString(BetonQuest.condition(profile, conditionID)));
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest condition command.
     *
     * @param args
     * @return
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
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }
        final PlayerData playerData;
        if (profile.getOnlineProfile().isPresent()) {
            playerData = dataStorage.get(profile);
        } else {
            log.debug("Profile is offline, loading his data");
            playerData = new PlayerData(profile);
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
            case "add":
            case "a":
                // add the tag
                log.debug(
                        "Adding tag " + tag + " for " + profile);
                playerData.addTag(tag);
                sendMessage(sender, "tag_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the tag
                log.debug(
                        "Removing tag " + tag + " from " + profile);
                playerData.removeTag(tag);
                sendMessage(sender, "tag_removed");
                break;
            default:
                // if there was something else, display error message
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
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
            case "add":
            case "a":
                // add the tag
                log.debug("Adding global tag " + tag);
                data.addTag(tag);
                sendMessage(sender, "tag_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the tag
                log.debug("Removing global tag " + tag);
                data.removeTag(tag);
                sendMessage(sender, "tag_removed");
                break;
            default:
                // if there was something else, display error message
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest tags command
     *
     * @param args
     * @return
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
     * Returns a list including all possible options for tab complete of the
     * /betonquest globaltags command
     *
     * @param args
     * @return
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
    @SuppressWarnings("PMD.NcssCount")
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
            playerData = new PlayerData(profile);
        }
        // if there are no arguments then list player's objectives
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            // display objectives
            log.debug("Listing objectives");
            final Predicate<String> shouldDisplay = createListFilter(args, 3, Function.identity());
            final Stream<String> objectives;
            if (isOnline) {
                // if the player is online then just retrieve tags from his active objectives
                objectives = instance.getPlayerObjectives(profile).stream()
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
        try {
            objectiveID = new ObjectiveID(null, args[3]);
        } catch (final ObjectNotFoundException e) {
            sendMessage(sender, "error", e.getMessage());
            log.warn("Could not find objective: " + e.getMessage(), e);
            return;
        }
        final Objective objective = instance.getObjective(objectiveID);
        if (objective == null) {
            sendMessage(sender, "specify_objective");
            return;
        }
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "start":
            case "s":
            case "add":
            case "a":
                log.debug(
                        "Adding new objective " + objectiveID + " for " + profile);
                // add the objective
                if (isOnline) {
                    BetonQuest.newObjective(profile, objectiveID);
                } else {
                    playerData.addNewRawObjective(objectiveID);
                }
                sendMessage(sender, "objective_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                log.debug(
                        "Deleting objective " + objectiveID + " for " + profile);
                objective.cancelObjectiveForPlayer(profile);
                playerData.removeRawObjective(objectiveID);
                sendMessage(sender, "objective_removed");
                break;
            case "complete":
            case "c":
                log.debug(
                        "Completing objective " + objectiveID + " for " + profile);
                if (isOnline) {
                    objective.completeObjective(profile);
                } else {
                    playerData.removeRawObjective(objectiveID);
                }
                sendMessage(sender, "objective_completed");
                break;
            default:
                // if there was something else, display error message
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest objectives command
     *
     * @param args
     * @return
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
        switch (type) {
            case "tags":
            case "tag":
            case "t":
                updateType = UpdateType.RENAME_ALL_TAGS;
                for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final PlayerData playerData = dataStorage.get(onlineProfile);
                    playerData.removeTag(name);
                    playerData.addTag(rename);
                }
                break;
            case "points":
            case "point":
            case "p":
                updateType = UpdateType.RENAME_ALL_POINTS;
                for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
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
                break;
            case "globalpoints":
            case "globalpoint":
            case "gpoints":
            case "gpoint":
            case "gp":
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
                break;
            case "objectives":
            case "objective":
            case "o":
                updateType = UpdateType.RENAME_ALL_OBJECTIVES;
                // get ID and package
                final ObjectiveID nameID;
                try {
                    nameID = new ObjectiveID(null, name);
                } catch (final ObjectNotFoundException e) {
                    sendMessage(sender, "error", e.getMessage());
                    log.warn("Could not find Objective: " + e.getMessage(), e);
                    return;
                }
                // rename objective in the file
                final MultiConfiguration configuration = nameID.getPackage().getConfig();
                final String newPath = "objectives." + rename.split("\\.")[1];
                configuration.set(newPath, nameID.getInstruction().toString());
                try {
                    final ConfigurationSection sourceConfigurationSection = configuration.getSourceConfigurationSection(nameID.getBaseID());
                    if (sourceConfigurationSection == null) {
                        sendMessage(sender, "error", "There is no SourceConfigurationSection!");
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
                    renameID = new ObjectiveID(null, rename);
                } catch (final ObjectNotFoundException e) {
                    sender.sendMessage("§4There was an unexpected error: " + e.getMessage());
                    log.reportException(e);
                    return;
                }
                instance.renameObjective(nameID, renameID);
                nameID.getPackage().getConfig().set(nameID.getBaseID(), null);
                try {
                    nameID.getPackage().saveAll();
                } catch (final IOException e) {
                    log.warn(nameID.getPackage(), e.getMessage(), e);
                    return;
                }
                break;
            case "journals":
            case "journal":
            case "j":
            case "entries":
            case "entry":
            case "e":
                updateType = UpdateType.RENAME_ALL_ENTRIES;
                for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final Journal journal = dataStorage.get(onlineProfile).getJournal();
                    Pointer journalPointer = null;
                    for (final Pointer pointer : journal.getPointers()) {
                        if (pointer.getPointer().equals(name)) {
                            journalPointer = pointer;
                        }
                    }
                    // skip the player if he does not have this entry
                    if (journalPointer == null) {
                        continue;
                    }
                    journal.removePointer(name);
                    journal.addPointer(new Pointer(rename, journalPointer.getTimestamp()));
                    journal.update();
                }
                break;
            default:
                sendMessage(sender, "unknown_argument");
                return;
        }
        instance.getSaver().add(new Record(updateType, rename, name));
        sendMessage(sender, "everything_renamed");
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest rename command.
     *
     * @param args
     * @return
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
        switch (type) {
            case "tags":
            case "tag":
            case "t":
                updateType = UpdateType.REMOVE_ALL_TAGS;
                for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final PlayerData playerData = dataStorage.get(onlineProfile);
                    playerData.removeTag(name);
                }
                break;
            case "points":
            case "point":
            case "p":
                updateType = UpdateType.REMOVE_ALL_POINTS;
                for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final PlayerData playerData = dataStorage.get(onlineProfile);
                    playerData.removePointsCategory(name);
                }
                break;
            case "objectives":
            case "objective":
            case "o":
                updateType = UpdateType.REMOVE_ALL_OBJECTIVES;
                final ObjectiveID objectiveID;
                try {
                    objectiveID = new ObjectiveID(null, name);
                } catch (final ObjectNotFoundException e) {
                    sendMessage(sender, "error", e.getMessage());
                    log.warn("Could not find objective: " + e.getMessage(), e);
                    return;
                }
                for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final Objective objective = instance.getObjective(objectiveID);
                    if (objective == null) {
                        break;
                    }
                    objective.cancelObjectiveForPlayer(onlineProfile);
                    dataStorage.get(onlineProfile).removeRawObjective(objectiveID);
                }
                break;
            case "journals":
            case "journal":
            case "j":
            case "entries":
            case "entry":
            case "e":
                updateType = UpdateType.REMOVE_ALL_ENTRIES;
                for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final Journal journal = dataStorage.get(onlineProfile).getJournal();
                    journal.removePointer(name);
                    journal.update();
                }
                break;
            default:
                sendMessage(sender, "unknown_argument");
                return;
        }
        instance.getSaver().add(new Record(updateType, name));
        sendMessage(sender, "everything_removed");
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest delete command.
     *
     * @param args
     * @return
     */
    private Optional<List<String>> completeDeleting(final String... args) {
        if (args.length == 2) {
            return Optional.of(Arrays.asList("tag", "point", "objective", "entry"));
        }
        if (args.length == 3) {
            switch (args[1].toLowerCase(Locale.ROOT)) {
                case "tags":
                case "tag":
                case "t":
                case "points":
                case "point":
                case "p":
                    return completeId(args, null);
                case "objectives":
                case "objective":
                case "o":
                    return completeId(args, AccessorType.OBJECTIVES);
                case "journals":
                case "journal":
                case "j":
                case "entries":
                case "entry":
                case "e":
                    return completeId(args, AccessorType.JOURNAL);
                default:
                    break;
            }
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Displays help to the user.
     */
    private void displayHelp(final CommandSender sender, final String alias) {
        log.debug("Just displaying help");
        // specify all commands
        final Map<String, String> cmds = new HashMap<>();
        cmds.put("reload", "reload");
        cmds.put("objectives", "objective <player> [list/add/del] [objective]");
        cmds.put("globaltags", "globaltags [list/add/del/purge]");
        cmds.put("globalpoints", "globalpoints [list/add/del/purge]");
        cmds.put("tags", "tag <player> [list/add/del] [tag]");
        cmds.put("points", "point <player> [list/add/del] [category] [amount]");
        cmds.put("journal", "journal <player> [list/add/del] [entry] [date]");
        cmds.put("condition", "condition <player> <condition>");
        cmds.put("event", "event <player> <event>");
        cmds.put("item", "item <name>");
        cmds.put("give", "give <name>");
        cmds.put("variable", "variable <player> <variable> [list/set/del]");
        cmds.put("rename", "rename <tag/point/globalpoint/objective/journal> <old> <new>");
        cmds.put("delete", "delete <tag/point/objective/journal> <name>");
        cmds.put("version", "version");
        cmds.put("purge", "purge <player>");
        cmds.put("debug", "debug [true/false/ingame]");
        cmds.put("download", "download <gitHubNamespace> <ref> <offsetPath> <sourcePath> [targetPath] [recursive] [overwrite]");
        if (!(sender instanceof Player)) {
            cmds.put("backup", "backup");
        }
        // display them
        sender.sendMessage("§e----- §aBetonQuest §e-----");
        if (sender instanceof Player) {
            final String lang = dataStorage.get(PlayerConverter.getID((Player) sender)).getLanguage();
            for (final Map.Entry<String, String> entry : cmds.entrySet()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "tellraw " + sender.getName() + " {\"text\":\"\",\"extra\":[{\"text\":\"§c/" + alias + ' '
                                + entry.getValue()
                                + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§b"
                                + Config.getMessage(lang, "command_" + entry.getKey()) + "\"}}]}");
            }
        } else {
            for (final Map.Entry<String, String> entry : cmds.entrySet()) {
                sender.sendMessage("§c/" + alias + ' ' + entry.getValue());
                sender.sendMessage("§b- " + Config.getMessage(Config.getLanguage(), "command_" + entry.getKey()));
            }
        }
    }

    private void displayVersionInfo(final CommandSender sender, final String commandAlias) {
        final Updater updater = instance.getUpdater();
        final String updateCommand = "/" + commandAlias + " update";

        final String lang = sender instanceof Player
                ? dataStorage.get(PlayerConverter.getID((Player) sender)).getLanguage()
                : Config.getLanguage();

        final String key = "command_version_context.";
        final String versionInfo = Config.getMessage(lang, key + "version_info");
        final String clickToCopyAll = Config.getMessage(lang, key + "click_to_copy_all");
        final String clickToCopy = Config.getMessage(lang, key + "click_to_copy");
        final String clickToDownloadHint = Config.getMessage(lang, key + "click_to_download_hint");
        final String colorValue = Config.getMessage(lang, key + "color_value");
        final String colorKey = Config.getMessage(lang, key + "color_key");
        final String colorValueVersion = Config.getMessage(lang, key + "color_value_version");
        final String versionBetonQuest = Config.getMessage(lang, key + "version_betonquest");
        final String versionServer = Config.getMessage(lang, key + "version_server");
        final String hookedInto = Config.getMessage(lang, key + "hooked_into");

        final String versionBetonQuestValue = colorValue + instance.getDescription().getVersion();
        final String versionServerValue = colorValue + Bukkit.getServer().getVersion();

        final TextComponent clickToDownload = updater.isUpdateAvailable()
                ? Component.newline().append(Component.text("    "))
                .append(Component.text(Config.getMessage(lang, key + "click_to_download", updater.getUpdateVersion())))
                .hoverEvent(Component.text(clickToDownloadHint)).clickEvent(ClickEvent.runCommand(updateCommand))
                : Component.empty();

        final Map<String, String> hookedTree = new TreeMap<>();
        for (final String plugin : Compatibility.getHooked()) {
            final Plugin plug = Bukkit.getPluginManager().getPlugin(plugin);
            if (plug != null) {
                hookedTree.put(plugin, plug.getDescription().getVersion());
            }
        }
        final StringJoiner hookedJoiner = new StringJoiner(", ");
        for (final Map.Entry<String, String> entry : hookedTree.entrySet()) {
            hookedJoiner.add(colorValue + entry.getKey() + colorValueVersion + " (" + entry.getValue() + ')');
        }
        final String hooked = hookedJoiner.toString();

        final Component compHeader = Component.text(instance.getPluginTag() + versionInfo);
        final Component compVersionBetonQuestKey = Component.text(colorKey + versionBetonQuest);
        final Component compVersionBetonQuestValue = Component.text(versionBetonQuestValue);
        final Component compVersionServerKey = Component.text(colorKey + versionServer);
        final Component compVersionServerValue = Component.text(versionServerValue);
        final Component compHookedKey = Component.text(colorKey + hookedInto);
        final Component compHookedValue = Component.text(hooked);
        final Component compCopyAll = Component.text(clickToCopyAll)
                .hoverEvent(Component.text(clickToCopy))
                .clickEvent(ClickEvent.copyToClipboard(ChatColor.stripColor(versionBetonQuest
                        + versionBetonQuestValue + '\n' + versionServer + versionServerValue + '\n' + '\n'
                        + hookedInto + hooked)));

        final TextComponent version = Component.empty().append(compHeader)
                .append(Component.newline()).append(compVersionBetonQuestKey).append(compVersionBetonQuestValue)
                .append(clickToDownload.clickEvent(ClickEvent.runCommand(updateCommand)))
                .append(Component.newline()).append(compVersionServerKey).append(compVersionServerValue)
                .append(Component.newline())
                .append(Component.newline()).append(compHookedKey).append(compHookedValue);
        if (sender instanceof ConsoleCommandSender) {
            instance.getAdventure().sender(sender).sendMessage(version);
        } else {
            instance.getAdventure().sender(sender)
                    .sendMessage(version.append(Component.newline()).append(compCopyAll));
        }
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
            log.info("Debuging mode was " + (debuggingController.isLogging() ? "enabled" : "disabled") + '!');
            return;
        }
        sendMessage(sender, "unknown_argument");
    }

    @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.SwitchStmtsShouldHaveDefault"})
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
        final List<String> whitelist = instance.getPluginConfig().getStringList("download.repo_whitelist");
        if (whitelist.stream().map(String::trim).noneMatch(githubNamespace::equals)) {
            sendMessage(sender, "download_failed_whitelist");
            log.debug(errSummary, new IllegalArgumentException(githubNamespace));
            return;
        }

        //check if ref is valid
        if (ref.toLowerCase(Locale.ROOT).startsWith("refs/pull/") && !instance.getPluginConfig().getBoolean("download.pull_requests", false)) {
            sendMessage(sender, "download_failed_pr");
            log.debug(errSummary, new IllegalArgumentException(ref));
            return;
        }

        //run download
        final Downloader downloader = new Downloader(loggerFactory.create(Downloader.class, "Downloader"), instance.getDataFolder(), githubNamespace, ref,
                offsetPath, sourcePath, targetPath, recursive, overwrite);
        sendMessage(sender, "download_scheduled");
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            try {
                downloader.call();
                sendMessageSync(sender, "download_success");
            } catch (final DownloadFailedException | SecurityException | FileNotFoundException e) {
                sendMessageSync(sender, "download_failed", e.getMessage());
                log.debug(errSummary, e);
            } catch (final Exception e) {
                sendMessageSync(sender, "download_failed", e.getClass().getSimpleName() + ": " + e.getMessage());
                if (sender instanceof final Player player) {
                    final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.FINE, null, instance);
                    record.setThrown(e);
                    bukkitAudiences.player(player).sendMessage(new ChatFormatter().formatTextComponent(record));
                    log.debug(errSummary, e);
                } else {
                    log.error(errSummary, e);
                }
            }
        });
    }

    private Optional<List<String>> completeDownload(final String... args) {
        return switch (args.length) {
            case 2 -> Optional.of(instance.getPluginConfig().getStringList("download.repo_whitelist"));
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

        final boolean isOnline = profile.getOnlineProfile().isPresent();
        if (!isOnline) {
            log.debug("Can't access variable data on offline player");
            sendMessage(sender, "offline_invalid");
            return;
        }

        if (args.length == 2) {
            log.debug("Missing objective instruction string");
            sendMessage(sender, "specify_objective");
            return;
        }

        // get the objective
        final ObjectiveID objectiveID;
        try {
            objectiveID = new ObjectiveID(null, args[2]);
        } catch (final ObjectNotFoundException e) {
            sendMessage(sender, "error", e.getMessage());
            log.warn("Could not find objective: " + e.getMessage(), e);
            return;
        }
        final Objective tmp = instance.getObjective(objectiveID);
        if (!(tmp instanceof final VariableObjective variableObjective)) {
            if (tmp == null) {
                log.debug("Missing objective instruction string");
            } else {
                log.debug(tmp.getLabel() + " is not a variable objective");
            }
            sendMessage(sender, "specify_objective");
            return;
        }
        log.debug("Using variable objective " + variableObjective.getLabel());

        final String subCommand = args.length == 3 ? "list" : args[3].toLowerCase(Locale.ROOT);
        switch (subCommand) {
            case "list":
            case "l":
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
                sendMessage(sender, "player_variables", variableObjective.getLabel());
                properties.entrySet().stream()
                        .filter(entry -> shouldDisplay.test(entry.getKey()))
                        .sorted((o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()))
                        .forEach(entry -> sender.sendMessage("§b- " + entry.getKey() + "§e: §a" + entry.getValue()));
                break;
            case "set":
            case "s":
                if (args.length < 6) {
                    log.debug("Missing amount");
                    sendMessage(sender, "arguments");
                    return;
                }
                final String value = String.join(" ", Arrays.copyOfRange(args, 5, args.length));
                log.debug("Setting value " + value + " for key " + args[4] + " for " + profile + " in " + variableObjective.getLabel());
                variableObjective.store(profile, args[4], value);
                sendMessage(sender, "value_set", value, args[4]);
                break;
            case "del":
            case "d":
                if (args.length < 5) {
                    log.debug("Missing amount");
                    sendMessage(sender, "arguments");
                    return;
                }
                log.debug("Removing key " + args[4] + " for " + profile + " in " + variableObjective.getLabel());
                variableObjective.store(profile, args[4], null);
                sendMessage(sender, "key_remove", args[4]);
                break;
            default:
                // if there was something else, display error message
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest variables command
     *
     * @param args
     * @return
     */
    private Optional<List<String>> completeVariables(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            final String last = args[args.length - 1];
            if (last == null || !last.contains(".")) {
                return completePackage();
            } else {
                final String pack = last.substring(0, last.indexOf('.'));
                final QuestPackage configPack = Config.getPackages().get(pack);
                if (configPack == null) {
                    return Optional.of(Collections.emptyList());
                }
                final ConfigurationSection configuration = configPack.getConfig().getConfigurationSection("objectives");
                final List<String> completions = new ArrayList<>();
                if (configuration != null) {
                    for (final String key : configuration.getKeys(false)) {
                        final String rawObjectiveInstruction = configuration.getString(key);
                        if (rawObjectiveInstruction != null && rawObjectiveInstruction.stripIndent().startsWith("variable")) {
                            completions.add(pack + '.' + key);
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
            return Optional.of(Arrays.asList("true", "false", "ingame"));
        }
        if (args.length == 3) {
            return completePackage();
        }
        if (args.length == 4) {
            return Optional.of(Arrays.asList("error", "info", "debug"));
        }
        return Optional.of(new ArrayList<>());
    }

    private void sendMessageSync(final CommandSender sender, final String messageName, @Nullable final String... variables) {
        Bukkit.getScheduler().runTask(instance, () -> {
            sendMessage(sender, messageName, variables);
        });
    }

    private void sendMessage(final CommandSender sender, final String messageName, @Nullable final String... variables) {
        if (sender instanceof Player) {
            Config.sendMessage(null, PlayerConverter.getID((Player) sender), messageName, variables);
        } else {
            final String message = Config.getMessage(Config.getLanguage(), messageName, variables);
            if (message == null) {
                log.warn("Missing message: " + messageName);
                return;
            }
            sender.sendMessage(message);
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

    private enum AccessorType {
        EVENTS, CONDITIONS, OBJECTIVES, ITEMS, JOURNAL
    }
}
