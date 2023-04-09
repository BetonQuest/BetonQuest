package org.betonquest.betonquest.commands;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.Pointer;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.events.GiveEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.PlayerLogWatcher;
import org.betonquest.betonquest.modules.logger.format.ChatFormatter;
import org.betonquest.betonquest.modules.logger.handler.history.LogPublishingController;
import org.betonquest.betonquest.modules.web.downloader.DownloadFailedException;
import org.betonquest.betonquest.modules.web.downloader.Downloader;
import org.betonquest.betonquest.modules.web.updater.Updater;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * Main admin command for quest editing.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.NPathComplexity", "PMD.TooManyMethods",
        "PMD.CommentRequired", "PMD.AvoidDuplicateLiterals", "PMD.AvoidLiteralsInIfCondition",
        "PMD.CognitiveComplexity"})
public class QuestCommand implements CommandExecutor, SimpleTabCompleter {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(QuestCommand.class);

    private final BetonQuest instance = BetonQuest.getInstance();
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
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public QuestCommand(final BukkitAudiences bukkitAudiences, final PlayerLogWatcher logWatcher, final LogPublishingController debuggingController) {
        this.bukkitAudiences = bukkitAudiences;
        this.logWatcher = logWatcher;
        this.debuggingController = debuggingController;
        BetonQuest.getInstance().getCommand("betonquest").setExecutor(this);
        BetonQuest.getInstance().getCommand("betonquest").setTabCompleter(this);
    }

    @SuppressWarnings("PMD.NcssCount")
    @Override
    public boolean onCommand(@NotNull final CommandSender sender, final Command cmd, @NotNull final String alias, @NotNull final String... args) {

        if ("betonquest".equalsIgnoreCase(cmd.getName())) {
            LOG.debug("Executing /betonquest command for user " + sender.getName()
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
                    // conditions are only possible for online players, so no
                    // MySQL async
                    // access is required
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleConditions(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "events":
                case "event":
                case "e":
                    // the same goes for events
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleEvents(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
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
                    LOG.debug("Loading data asynchronously");
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleObjectives(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "globaltags":
                case "globaltag":
                case "gtag":
                case "gtags":
                case "gt":
                    LOG.debug("Loading data asynchronously");
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleGlobalTags(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "globalpoints":
                case "globalpoint":
                case "gpoints":
                case "gpoint":
                case "gp":
                    LOG.debug("Loading data asynchronously");
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleGlobalPoints(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "tags":
                case "tag":
                case "t":
                    LOG.debug("Loading data asynchronously");
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleTags(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "points":
                case "point":
                case "p":
                    LOG.debug("Loading data asynchronously");
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handlePoints(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "journals":
                case "journal":
                case "j":
                    LOG.debug("Loading data asynchronously");
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleJournals(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "delete":
                case "del":
                case "d":
                    LOG.debug("Loading data asynchronously");
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleDeleting(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "rename":
                case "r":
                    LOG.debug("Loading data asynchronously");
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleRenaming(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "version":
                case "ver":
                case "v":
                    displayVersionInfo(sender, alias);
                    break;
                case "purge":
                    LOG.debug("Loading data asynchronously");
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            purgePlayer(sender, args);
                        }
                    }.runTaskAsynchronously(BetonQuest.getInstance());
                    break;
                case "update":
                    BetonQuest.getInstance().getUpdater().update(sender);
                    break;
                case "reload":
                    // just reloading
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
                    break;
                case "backup":
                    // do a full plugin backup
                    if (sender instanceof Player || !Bukkit.getOnlinePlayers().isEmpty()) {
                        sendMessage(sender, "offline");
                        break;
                    }
                    Utils.backup();
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
            LOG.debug("Command executing done");
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
                    "update", "reload", "backup", "debug", "download"));
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
     * Returns a list of all packages for the tab completer
     *
     * @return
     */
    private Optional<List<String>> completePackage() {
        return Optional.of(new ArrayList<>(Config.getPackages().keySet()));
    }

    /**
     * Returns a list including all possible tab complete options for ids
     *
     * @param args
     * @param type - the type of the Id (item/event/journal/condition/objective),
     *             null for unspecific
     * @return
     */
    private Optional<List<String>> completeId(final String[] args, final AccessorType type) {
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
     * Gives an item to the player
     */
    private void giveItem(final CommandSender sender, final String... args) {
        // sender must be a player
        if (!(sender instanceof Player)) {
            LOG.debug("Cannot continue, sender must be player");
            return;
        }
        // and the item name must be specified
        if (args.length < 2) {
            LOG.debug("Cannot continue, item's name must be supplied");
            sendMessage(sender, "specify_item");
            return;
        }
        try {
            final ItemID itemID;
            try {
                itemID = new ItemID(null, args[1]);
            } catch (final ObjectNotFoundException e) {
                sendMessage(sender, "error", e.getMessage());
                LOG.warn("Could not find Item: " + e.getMessage(), e);
                return;
            }
            final GiveEvent give = new GiveEvent(new Instruction(itemID.getPackage(), null, "give " + itemID.getBaseID()));
            give.fire(PlayerConverter.getID((Player) sender));
        } catch (final InstructionParseException | QuestRuntimeException e) {
            sendMessage(sender, "error", e.getMessage());
            LOG.warn("Error while creating an item: " + e.getMessage(), e);
        }
    }

    /**
     * Purges profile's data
     */
    private void purgePlayer(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }
        PlayerData playerData = instance.getPlayerData(profile);
        // if the player is offline then get his PlayerData outside of the
        // list
        if (playerData == null) {
            LOG.debug("Profile is offline, loading his data");
            playerData = new PlayerData(profile);
        }
        // purge the player
        LOG.debug("Purging player " + args[1]);
        playerData.purgePlayer();
        // done
        sendMessage(sender, "purged", args[1]);
    }

    @Nullable
    private Profile getTargetProfile(final CommandSender sender, final String... args) {
        if (args.length < 2) {
            LOG.debug("Player's name is missing");
            sendMessage(sender, "specify_player");
            return null;
        }
        return PlayerConverter.getID(Bukkit.getOfflinePlayer(args[1]));
    }

    /**
     * Lists, adds or removes journal entries of certain profile
     */
    private void handleJournals(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }
        PlayerData playerData = instance.getPlayerData(profile);
        // if the player is offline then get his PlayerData outside of the
        // list
        if (playerData == null) {
            LOG.debug("Profile is offline, loading his data");
            playerData = new PlayerData(profile);
        }
        final Journal journal = playerData.getJournal();
        // if there are no arguments then list player's pointers
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            LOG.debug("Listing journal pointers");
            sendMessage(sender, "player_journal");
            for (final Pointer pointer : journal.getPointers()) {
                final String date = new SimpleDateFormat(Config.getString("config.date_format"), Locale.ROOT)
                        .format(new Date(pointer.getTimestamp()));
                sender.sendMessage("§b- " + pointer.getPointer() + " §c(§2" + date + "§c)");
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            LOG.debug("Missing pointer");
            sendMessage(sender, "specify_pointer");
            return;
        }
        final String pointerName = args[3];
        // if there are arguments, handle them
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "add":
            case "a":
                final Pointer pointer;
                if (args.length < 5) {
                    final long timestamp = new Date().getTime();
                    LOG.debug("Adding pointer with current date: " + timestamp);
                    pointer = new Pointer(pointerName, timestamp);
                } else {
                    LOG.debug("Adding pointer with date " + args[4].replaceAll("_", " "));
                    try {
                        pointer = new Pointer(pointerName, new SimpleDateFormat(Config.getString("config.date_format"), Locale.ROOT)
                                .parse(args[4].replaceAll("_", " ")).getTime());
                    } catch (final ParseException e) {
                        sendMessage(sender, "specify_date");
                        LOG.warn("Could not parse date: " + e.getMessage(), e);
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
                LOG.debug("Removing pointer");
                journal.removePointer(pointerName);
                journal.update();
                sendMessage(sender, "pointer_removed");
                break;
            default:
                // if there was something else, display error message
                LOG.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest journal command
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
     * Lists, adds or removes points of certain profile
     */
    private void handlePoints(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }
        PlayerData playerData = instance.getPlayerData(profile);
        // if the player is offline then get his PlayerData outside of the
        // list
        if (playerData == null) {
            LOG.debug("Profile is offline, loading his data");
            playerData = new PlayerData(profile);
        }
        // if there are no arguments then list player's points
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            final List<Point> points = playerData.getPoints();
            LOG.debug("Listing points");
            sendMessage(sender, "player_points");
            for (final Point point : points) {
                sender.sendMessage("§b- " + point.getCategory() + "§e: §a" + point.getCount());
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            LOG.debug("Missing category");
            sendMessage(sender, "specify_category");
            return;
        }
        final String category = args[3];
        // if there are arguments, handle them
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "add":
            case "a":
                if (args.length < 5 || !args[4].matches("-?\\d+")) {
                    LOG.debug("Missing amount");
                    sendMessage(sender, "specify_amount");
                    return;
                }
                // add the point
                LOG.debug("Adding points");
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
                LOG.debug("Removing points");
                playerData.removePointsCategory(category);
                sendMessage(sender, "points_removed");
                break;
            default:
                // if there was something else, display error message
                LOG.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Lists, adds, removes or purges all global points
     *
     * @param sender
     * @param args
     */
    private void handleGlobalPoints(final CommandSender sender, final String... args) {
        final GlobalData data = instance.getGlobalData();
        // if there are no arguments then list all global points
        if (args.length < 2 || "list".equalsIgnoreCase(args[1]) || "l".equalsIgnoreCase(args[1])) {
            final List<Point> points = data.getPoints();
            LOG.debug("Listing global points");
            sendMessage(sender, "global_points");
            for (final Point point : points) {
                sender.sendMessage("§b- " + point.getCategory() + "§e: §a" + point.getCount());
            }
            return;
        }
        // handle purge
        if ("purge".equalsIgnoreCase(args[1])) {
            LOG.debug("Purging all global points");
            data.purgePoints();
            sendMessage(sender, "global_points_purged");
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 3) {
            LOG.debug("Missing category");
            sendMessage(sender, "specify_category");
            return;
        }
        final String category = args[2];
        // if there are arguments, handle them
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "add":
            case "a":
                if (args.length < 4 || !args[3].matches("-?\\d+")) {
                    LOG.debug("Missing amount");
                    sendMessage(sender, "specify_amount");
                    return;
                }
                // add the point
                LOG.debug("Adding global points");
                data.modifyPoints(category, Integer.parseInt(args[3]));
                sendMessage(sender, "points_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                LOG.debug("Removing global points");
                data.removePointsCategory(category);
                sendMessage(sender, "points_removed");
                break;
            default:
                // if there was something else, display error message
                LOG.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest points command
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
     * /betonquest globalpoints command
     *
     * @param args
     * @return
     */
    private Optional<List<String>> completeGlobalPoints(final String... args) {
        if (args.length == 2) {
            return Optional.of(Arrays.asList("add", "list", "del"));
        }
        if (args.length == 3) {
            return completeId(args, null);
        }
        return Optional.of(new ArrayList<>());
    }

    /**
     * Adds item held in hand to items.yml file
     */

    private void handleItems(final CommandSender sender, final String... args) {
        // sender must be a player
        if (!(sender instanceof final Player player)) {
            LOG.debug("Cannot continue, sender must be player");
            return;
        }
        // and the item name must be specified
        if (args.length < 2) {
            LOG.debug("Cannot continue, item's name must be supplied");
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
            LOG.debug("Cannot continue, package does not exist");
            sendMessage(sender, "specify_package");
            return;
        }
        final ItemStack item = player.getInventory().getItemInMainHand();
        final String instructions = QuestItem.itemToString(item);
        // save it in items.yml
        LOG.debug("Saving item to configuration as " + args[1]);
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
            LOG.warn(configPack, e.getMessage(), e);
            return;
        }
        // done
        sendMessage(sender, "item_created", args[1]);

    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest item command
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
        if (args.length < 2 || Bukkit.getPlayer(args[1]) == null && !args[1].equals("-")) {
            LOG.debug("Player's name is missing or he's offline");
            sendMessage(sender, "specify_player");
            return;
        }
        if (args.length < 3) {
            LOG.debug("Event's ID is missing");
            sendMessage(sender, "specify_event");
            return;
        }
        final EventID eventID;
        try {
            eventID = new EventID(null, args[2]);
        } catch (final ObjectNotFoundException e) {
            sendMessage(sender, "error", e.getMessage());
            LOG.warn("Could not find event: " + e.getMessage(), e);
            return;
        }
        // fire the event
        final Profile profile = "-".equals(args[1]) ? null : PlayerConverter.getID(Bukkit.getOfflinePlayer(args[1]));
        BetonQuest.event(profile, eventID);
        sendMessage(sender, "player_event", eventID.generateInstruction().getInstruction());
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest event command
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
     * Checks if specified player meets condition described by ID
     */
    private void handleConditions(final CommandSender sender, final String... args) {
        // the player has to be specified every time
        if (args.length < 2 || Bukkit.getPlayer(args[1]) == null && !args[1].equals("-")) {
            LOG.debug("Player's name is missing or he's offline");
            sendMessage(sender, "specify_player");
            return;
        }
        // the condition ID
        if (args.length < 3) {
            LOG.debug("Condition's ID is missing");
            sendMessage(sender, "specify_condition");
            return;
        }
        final ConditionID conditionID;
        try {
            conditionID = new ConditionID(null, args[2]);
        } catch (final ObjectNotFoundException e) {
            sendMessage(sender, "error", e.getMessage());
            LOG.warn("Could not find condition: " + e.getMessage(), e);
            return;
        }
        // display message about condition
        final Profile profile = "-".equals(args[1]) ? null : PlayerConverter.getID(Bukkit.getOfflinePlayer(args[1]));
        sendMessage(sender, "player_condition", (conditionID.inverted() ? "! " : "") + conditionID.generateInstruction().getInstruction(),
                Boolean.toString(BetonQuest.condition(profile, conditionID)));
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest condition command
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
     * Lists, adds or removes tags
     */
    private void handleTags(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }
        PlayerData playerData = instance.getPlayerData(profile);
        // if the player is offline then get his PlayerData outside of the
        // list
        if (playerData == null) {
            LOG.debug("Profile is offline, loading his data");
            playerData = new PlayerData(profile);
        }
        // if there are no arguments then list player's tags
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            final List<String> tags = new ArrayList<>(playerData.getTags());
            LOG.debug("Listing tags");
            sendMessage(sender, "player_tags");
            Collections.sort(tags);
            for (final String tag : tags) {
                sender.sendMessage("§b- " + tag);
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            LOG.debug("Missing tag name");
            sendMessage(sender, "specify_tag");
            return;
        }
        final String tag = args[3];
        // if there are arguments, handle them
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "add":
            case "a":
                // add the tag
                LOG.debug(
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
                LOG.debug(
                        "Removing tag " + tag + " from " + profile);
                playerData.removeTag(tag);
                sendMessage(sender, "tag_removed");
                break;
            default:
                // if there was something else, display error message
                LOG.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
                break;
        }
    }

    /**
     * Lists, adds or removes global tags
     */
    private void handleGlobalTags(final CommandSender sender, final String... args) {
        final GlobalData data = instance.getGlobalData();
        // if there are no arguments then list all global tags
        if (args.length < 2 || "list".equalsIgnoreCase(args[1]) || "l".equalsIgnoreCase(args[1])) {
            final List<String> tags = data.getTags();
            LOG.debug("Listing global tags");
            sendMessage(sender, "global_tags");
            for (final String tag : tags) {
                sender.sendMessage("§b- " + tag);
            }
            return;
        }
        // handle purge
        if ("purge".equalsIgnoreCase(args[1])) {
            LOG.debug("Purging all global tags");
            data.purgeTags();
            sendMessage(sender, "global_tags_purged");
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 3) {
            LOG.debug("Missing tag name");
            sendMessage(sender, "specify_tag");
            return;
        }
        final String tag = args[2];
        // if there are arguments, handle them
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "add":
            case "a":
                // add the tag
                LOG.debug("Adding global tag " + tag);
                data.addTag(tag);
                sendMessage(sender, "tag_added");
                break;
            case "remove":
            case "delete":
            case "del":
            case "r":
            case "d":
                // remove the tag
                LOG.debug("Removing global tag " + tag);
                data.removeTag(tag);
                sendMessage(sender, "tag_removed");
                break;
            default:
                // if there was something else, display error message
                LOG.debug("The argument was unknown");
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
            return Optional.of(Arrays.asList("list", "add", "del"));
        }
        if (args.length == 3) {
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
        PlayerData playerData = instance.getPlayerData(profile);
        // if the player is offline then get his PlayerData outside the list
        if (playerData == null) {
            LOG.debug("Profile is offline, loading his data");
            playerData = new PlayerData(profile);
        }
        // if there are no arguments then list player's objectives
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            final List<String> tags;
            if (isOnline) {
                // if the player is online then just retrieve tags from his
                // active
                // objectives
                tags = new ArrayList<>();
                for (final Objective objective : BetonQuest.getInstance().getPlayerObjectives(profile)) {
                    tags.add(objective.getLabel());
                }
            } else {
                // if player is offline then convert his raw objective strings
                // to tags
                tags = new ArrayList<>(playerData.getRawObjectives().keySet());
            }
            // display objectives
            LOG.debug("Listing objectives");
            sendMessage(sender, "player_objectives");
            Collections.sort(tags);
            for (final String tag : tags) {
                sender.sendMessage("§b- " + tag);
            }
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            LOG.debug("Missing objective instruction string");
            sendMessage(sender, "specify_objective");
            return;
        }
        // get the objective
        final ObjectiveID objectiveID;
        try {
            objectiveID = new ObjectiveID(null, args[3]);
        } catch (final ObjectNotFoundException e) {
            sendMessage(sender, "error", e.getMessage());
            LOG.warn("Could not find objective: " + e.getMessage(), e);
            return;
        }
        final Objective objective = BetonQuest.getInstance().getObjective(objectiveID);
        if (objective == null) {
            sendMessage(sender, "specify_objective");
            return;
        }
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "start":
            case "s":
            case "add":
            case "a":
                LOG.debug(
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
                LOG.debug(
                        "Deleting objective " + objectiveID + " for " + profile);
                objective.cancelObjectiveForPlayer(profile);
                playerData.removeRawObjective(objectiveID);
                sendMessage(sender, "objective_removed");
                break;
            case "complete":
            case "c":
                LOG.debug(
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
                LOG.debug("The argument was unknown");
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
                for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(onlineProfile);
                    playerData.removeTag(name);
                    playerData.addTag(rename);
                }
                break;
            case "points":
            case "point":
            case "p":
                updateType = UpdateType.RENAME_ALL_POINTS;
                for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(onlineProfile);
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
                for (final Point globalpoint : BetonQuest.getInstance().getGlobalData().getPoints()) {
                    if (globalpoint.getCategory().equals(name)) {
                        globalpoints = globalpoint.getCount();
                        break;
                    }
                }
                BetonQuest.getInstance().getGlobalData().removePointsCategory(name);
                BetonQuest.getInstance().getGlobalData().modifyPoints(rename, globalpoints);
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
                    LOG.warn("Could not find Objective: " + e.getMessage(), e);
                    return;
                }
                // rename objective in the file
                final MultiConfiguration configuration = nameID.getPackage().getConfig();
                final String newPath = "objectives." + rename.split("\\.")[1];
                configuration.set(newPath, nameID.generateInstruction().getInstruction());
                try {
                    configuration.associateWith(newPath, configuration.getSourceConfigurationSection(nameID.getBaseID()));
                    nameID.getPackage().saveAll();
                } catch (final IOException | InvalidConfigurationException e) {
                    LOG.warn(nameID.getPackage(), e.getMessage(), e);
                    return;
                }
                // rename objective instance
                final ObjectiveID renameID;
                try {
                    renameID = new ObjectiveID(null, rename);
                } catch (final ObjectNotFoundException e) {
                    sender.sendMessage("§4There was an unexpected error: " + e.getMessage());
                    LOG.reportException(e);
                    return;
                }
                BetonQuest.getInstance().renameObjective(nameID, renameID);
                BetonQuest.getInstance().getObjective(renameID).setLabel(renameID);
                // renaming an active objective probably isn't needed
                for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    boolean found = false;
                    String data = null;
                    for (final Objective obj : BetonQuest.getInstance().getPlayerObjectives(onlineProfile)) {
                        if (obj.getLabel().equals(name)) {
                            found = true;
                            data = obj.getData(onlineProfile);
                            break;
                        }
                    }
                    // skip the player if he does not have this objective
                    if (!found) {
                        continue;
                    }
                    if (data == null) {
                        data = "";
                    }
                    final Objective objective = BetonQuest.getInstance().getObjective(nameID);
                    objective.pauseObjectiveForPlayer(onlineProfile);
                    BetonQuest.getInstance().getPlayerData(onlineProfile).removeRawObjective(nameID);
                    BetonQuest.resumeObjective(onlineProfile, renameID, data);
                }
                nameID.getPackage().getConfig().set(nameID.getBaseID(), null);
                try {
                    nameID.getPackage().saveAll();
                } catch (final IOException e) {
                    LOG.warn(nameID.getPackage(), e.getMessage(), e);
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
                for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final Journal journal = BetonQuest.getInstance().getPlayerData(onlineProfile).getJournal();
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
        BetonQuest.getInstance().getSaver().add(new Record(updateType, rename, name));
        sendMessage(sender, "everything_renamed");
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest rename command
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
                for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(onlineProfile);
                    playerData.removeTag(name);
                }
                break;
            case "points":
            case "point":
            case "p":
                updateType = UpdateType.REMOVE_ALL_POINTS;
                for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(onlineProfile);
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
                    LOG.warn("Could not find objective: " + e.getMessage(), e);
                    return;
                }
                for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final Objective objective = BetonQuest.getInstance().getObjective(objectiveID);
                    objective.cancelObjectiveForPlayer(onlineProfile);
                    BetonQuest.getInstance().getPlayerData(onlineProfile).removeRawObjective(objectiveID);
                }
                break;
            case "journals":
            case "journal":
            case "j":
            case "entries":
            case "entry":
            case "e":
                updateType = UpdateType.REMOVE_ALL_ENTRIES;
                for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final Journal journal = BetonQuest.getInstance().getPlayerData(onlineProfile).getJournal();
                    journal.removePointer(name);
                    journal.update();
                }
                break;
            default:
                sendMessage(sender, "unknown_argument");
                return;
        }
        BetonQuest.getInstance().getSaver().add(new Record(updateType, name));
        sendMessage(sender, "everything_removed");
    }

    /**
     * Returns a list including all possible options for tab complete of the
     * /betonquest delete command
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
        LOG.debug("Just displaying help");
        // specify all commands
        final HashMap<String, String> cmds = new HashMap<>();
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
        cmds.put("rename", "rename <tag/point/globalpoint/objective/journal> <old> <new>");
        cmds.put("delete", "delete <tag/point/objective/journal> <name>");
        cmds.put("config", "config <read/set/add> <path> [string]");
        cmds.put("vector", "vector <pack.varname> <vectorname>");
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
            final String lang = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID((Player) sender)).getLanguage();
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

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private void displayVersionInfo(final CommandSender sender, final String commandAlias) {
        final Updater updater = BetonQuest.getInstance().getUpdater();
        final String updateCommand = "/" + commandAlias + " update";

        final String lang = sender instanceof Player
                ? BetonQuest.getInstance().getPlayerData(PlayerConverter.getID((Player) sender)).getLanguage()
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

        final String versionBetonQuestValue = colorValue + BetonQuest.getInstance().getDescription().getVersion();
        final String versionServerValue = colorValue + Bukkit.getServer().getVersion();

        final TextComponent clickToDownload = updater.isUpdateAvailable() ?
                Component.newline().append(Component.text("    "))
                        .append(Component.text(Config.getMessage(lang, key + "click_to_download", updater.getUpdateVersion())))
                        .hoverEvent(Component.text(clickToDownloadHint)).clickEvent(ClickEvent.runCommand(updateCommand))
                : Component.empty();

        final TreeMap<String, String> hookedTree = new TreeMap<>();
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

        final Component compHeader = Component.text(BetonQuest.getInstance().getPluginTag() + versionInfo);
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
            BetonQuest.getInstance().getAdventure().sender(sender).sendMessage(version);
        } else {
            BetonQuest.getInstance().getAdventure().sender(sender)
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
                LOG.debug("Cannot continue, sender must be player");
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
                LOG.warn("Could not save new debugging state to configuration file! " + e.getMessage(), e);
            }
            sender.sendMessage("§2Debugging mode was " + (debuggingController.isLogging() ? "enabled" : "disabled") + '!');
            LOG.info("Debuging mode was " + (debuggingController.isLogging() ? "enabled" : "disabled") + '!');
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
        final String ref = args[2].startsWith("refs/") ? args[2] : "refs/heads/" + args[2];
        final String offsetPath = args[3];
        final String errSummary = String.format("Download from %s ref %s of %s at %s to %s failed:",
                githubNamespace, ref, offsetPath, sourcePath, targetPath);

        //Check offset paths
        if (!Downloader.ALLOWED_OFFSET_PATHS.contains(offsetPath)) {
            sendMessage(sender, "download_failed_offset");
            LOG.debug(errSummary, new IllegalArgumentException(offsetPath));
            return;
        }

        //check if repo is allowed
        final List<String> whitelist = instance.getPluginConfig().getStringList("download.repo_whitelist");
        if (whitelist.stream().map(String::trim).noneMatch(githubNamespace::equals)) {
            sendMessage(sender, "download_failed_whitelist");
            LOG.debug(errSummary, new IllegalArgumentException(githubNamespace));
            return;
        }

        //check if ref is valid
        if (ref.toLowerCase(Locale.ROOT).startsWith("refs/pull/") && !instance.getPluginConfig().getBoolean("download.pull_requests", false)) {
            sendMessage(sender, "download_failed_pr");
            LOG.debug(errSummary, new IllegalArgumentException(ref));
            return;
        }

        //run download
        final Downloader downloader = new Downloader(instance.getDataFolder(), githubNamespace, ref,
                offsetPath, sourcePath, targetPath, recursive, overwrite);
        sendMessage(sender, "download_scheduled");
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            try {
                downloader.call();
                sendMessage(sender, "download_success");
            } catch (final DownloadFailedException | SecurityException e) {
                sendMessage(sender, "download_failed", e.getMessage());
                LOG.debug(errSummary, e);
            } catch (final Exception e) {
                sendMessage(sender, "download_failed", e.getClass().getSimpleName() + ": " + e.getMessage());
                if (sender instanceof final Player player) {
                    final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.FINE, "", instance);
                    record.setThrown(e);
                    final String msgJson = new ChatFormatter().format(record);
                    bukkitAudiences.player(player).sendMessage(GsonComponentSerializer.gson().deserialize(msgJson));
                    LOG.debug(errSummary, e);
                } else {
                    LOG.error(errSummary, e);
                }
            }
        });
    }

    private Optional<List<String>> completeDownload(final String... args) {
        return switch (args.length) {
            case 2 -> Optional.of(instance.getPluginConfig().getStringList("download.repo_whitelist"));
            case 3 -> Optional.of(List.of("main", "refs/heads/", "refs/tags/"));
            case 4 -> Optional.of(Downloader.ALLOWED_OFFSET_PATHS);
            case 5 -> Optional.of(List.of("/"));
            case 6 -> Optional.of(List.of("/", "overwrite", "recursive"));
            case 7, 8 ->
                    Optional.of(Stream.of("overwrite", "recursive").filter(tag -> !Arrays.asList(args).contains(tag)).toList());
            default -> Optional.of(List.of());
        };
    }

    private Level getLogLevel(final String arg) {
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

    private void sendMessage(final CommandSender sender, final String messageName) {
        sendMessage(sender, messageName, new String[0]);
    }

    private void sendMessage(final CommandSender sender, final String messageName, final String... variables) {
        if (sender instanceof Player) {
            Config.sendMessage(null, PlayerConverter.getID((Player) sender), messageName, variables);
        } else {
            final String message = Config.getMessage(Config.getLanguage(), messageName, variables);
            sender.sendMessage(message);
        }
    }

    private enum AccessorType {
        EVENTS, CONDITIONS, OBJECTIVES, ITEMS, JOURNAL
    }
}
