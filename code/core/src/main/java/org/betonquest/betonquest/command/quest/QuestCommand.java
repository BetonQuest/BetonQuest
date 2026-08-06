package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.command.SimpleTabCompleter;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Backup;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.feature.journal.Pointer;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.logger.PlayerLogWatcher;
import org.betonquest.betonquest.web.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
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
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

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
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * The identifier registry.
     */
    private final Identifiers identifiers;

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * The journal entry processor.
     */
    private final JournalEntryProcessor journalEntryProcessor;

    /**
     * The reloader runnable.
     */
    private final Reloader reloader;

    /**
     * The database saver.
     */
    private final Saver saver;

    /**
     * The quest package manager.
     */
    private final QuestPackageManager questPackageManager;

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
        this.playerDataStorage = constructorParams.playerDataStorage();
        this.profileProvider = constructorParams.profileProvider();
        this.localizations = constructorParams.localizations();
        this.updater = constructorParams.updater();
        this.reloader = constructorParams.reloader();
        this.saver = constructorParams.saver();
        this.connector = constructorParams.connector();
        this.globalData = constructorParams.globalData();
        this.questPackageManager = constructorParams.questPackageManager();
        this.journalEntryProcessor = constructorParams.journalEntryProcessor();
        this.objectiveManager = constructorParams.objectiveManager();
        this.identifiers = constructorParams.identifiers();

        this.subCommands = new HashMap<>();
        this.subCommandSuggestions = new ArrayList<>();
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
                new GiveSubCommand(log, constructorParams),
                new ItemSubCommand(log, constructorParams),
                new DebugSubCommand(log, constructorParams),
                new DownloadSubCommand(plugin, log, constructorParams)
        ).forEach(command -> {
            command.names().forEach(name -> subCommands.put(name, command));
            subCommandSuggestions.add(command.names().get(0));
        });
        subCommandSuggestions.addAll(Arrays.asList(
                "delete", "rename", "version", "purge",
                "update", "reload", "backup"));

        this.versionSubCommand = new VersionSubCommand(plugin, constructorParams);
    }

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
                final String lowerCase = args[0].toLowerCase(Locale.ROOT);
                final SubCommand subCommand = subCommands.get(lowerCase);
                if (subCommand != null) {
                    subCommand.handle(sender, args);
                    log.debug("Command executing done");
                    return true;
                }
                switch (lowerCase) {
                    case "delete":
                    case "del":
                    case "d":
                        handleDeleting(sender, args);
                        break;
                    case "rename":
                    case "r":
                        handleRenaming(sender, args);
                        break;
                    case "version":
                    case "ver":
                    case "v":
                        versionSubCommand.displayVersionInfo(sender, alias);
                        break;
                    case "purge":
                        purgePlayer(sender, args);
                        break;
                    case "update":
                        updater.update(sender);
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
                        new Backup(loggerFactory, loggerFactory.create(Backup.class), configAccessorFactory, plugin.getDataFolder(),
                                connector).backup(plugin.getDescription().getVersion());
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
            return Optional.of(subCommandSuggestions);
        }
        final String lowerCase = args[0].toLowerCase(Locale.ROOT);
        final SubCommand subCommand = subCommands.get(lowerCase);
        if (subCommand != null) {
            return subCommand.complete(args);
        }
        return switch (lowerCase) {
            case "delete",
                 "del",
                 "d" -> completeDeleting(args);
            case "rename",
                 "r" -> completeRenaming(args);
            case "purge" -> args.length == 2 ? Optional.empty() : Optional.of(new ArrayList<>());
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
        return Optional.of(new ArrayList<>(questPackageManager.getPackages().keySet()));
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
        }
        final String pack = last.substring(0, last.indexOf(Identifier.SEPARATOR));
        final QuestPackage configPack = questPackageManager.getPackages().get(pack);
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
            for (final String key : configuration.getKeys(type.allowNested)) {
                if (type.allowNested && !type.allowSection && configuration.isConfigurationSection(key)) {
                    continue;
                }
                completions.add(pack + Identifier.SEPARATOR + key);
            }
        }
        return Optional.of(completions);
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
        reloader.reload();
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
        if (profile.getOnlineProfile().isEmpty()) {
            log.debug("Profile is offline, loading his data");
        }
        return playerDataStorage.get(profile);
    }

    /**
     * Renames stuff.
     */
    @SuppressWarnings("PMD.NcssCount")
    private void handleRenaming(final CommandSender sender, final String... args) throws QuestException {
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
                    final PlayerData playerData = playerDataStorage.get(onlineProfile);
                    playerData.tags().remove(name);
                    playerData.tags().add(rename);
                }
            }
            case "points", "point", "p" -> {
                updateType = UpdateType.RENAME_ALL_POINTS;
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    final PlayerData playerData = playerDataStorage.get(onlineProfile);
                    int points = 0;
                    for (final Map.Entry<String, Integer> point : playerData.points().get().entrySet()) {
                        if (point.getKey().equals(name)) {
                            points = point.getValue();
                            break;
                        }
                    }
                    playerData.points().remove(name);
                    playerData.points().add(rename, points);
                }
            }
            case "globalpoints", "globalpoint", "gpoints", "gpoint", "gp" -> {
                updateType = UpdateType.RENAME_ALL_GLOBAL_POINTS;
                int globalpoints = 0;
                for (final Map.Entry<String, Integer> globalpoint : globalData.points().get().entrySet()) {
                    if (globalpoint.getKey().equals(name)) {
                        globalpoints = globalpoint.getValue();
                        break;
                    }
                }
                globalData.points().remove(name);
                globalData.points().add(rename, globalpoints);
            }
            case "objectives", "objective", "o" -> {
                updateType = UpdateType.RENAME_ALL_OBJECTIVES;
                // get ID and package
                final ObjectiveIdentifier nameID;
                try {
                    nameID = getIdentifier(ObjectiveIdentifier.class, name);
                } catch (final QuestException e) {
                    sendMessage(sender, "error",
                            new VariableReplacement("error", Component.text(e.getMessage())));
                    log.warn("Could not find Objective: " + e.getMessage(), e);
                    return;
                }
                // rename objective in the file
                final MultiConfiguration configuration = nameID.getPackage().getConfig();
                final String newPath = "objectives." + rename.split(Identifier.SEPARATOR)[1];
                configuration.set(newPath, nameID.readRawInstruction());
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
                final ObjectiveIdentifier renameID;
                try {
                    renameID = getIdentifier(ObjectiveIdentifier.class, rename);
                } catch (final QuestException e) {
                    sender.sendMessage("§4There was an unexpected error: " + e.getMessage());
                    log.reportException(e);
                    return;
                }
                final ObjectiveProcessor objectiveProcessor = (ObjectiveProcessor) objectiveManager;
                objectiveProcessor.renameObjective(nameID, renameID);
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
                final QuestPackage newPackage = questPackageManager.getPackages().get(rename.split(Identifier.SEPARATOR)[0]);
                if (newPackage == null) {
                    final String message = "You can't rename into non-existent package!";
                    sendMessage(sender, "error", new VariableReplacement("error", Component.text(message)));
                    log.error(message);
                    return;
                }

                final JournalEntryIdentifier newEntryID;
                try {
                    newEntryID = getIdentifier(JournalEntryIdentifier.class, rename);
                } catch (final QuestException e) {
                    final String message = "You can't rename into non-existent id!";
                    sendMessage(sender, "error", new VariableReplacement("error", Component.text(message)));
                    log.error(message);
                    return;
                }

                final JournalEntryIdentifier oldEntryID;
                try {
                    oldEntryID = getIdentifier(JournalEntryIdentifier.class, name);
                } catch (final QuestException e) {
                    final String message = "Old journal entry " + name + " does not exist, renaming only database entries!";
                    log.warn(message, e);
                    log.debug("Renaming non existent journal entry only from database: " + e.getMessage(), e);
                    sender.sendMessage("§2" + message);
                    break;
                }

                journalEntryProcessor.renameJournalEntry(oldEntryID, newEntryID);
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    final Journal journal = playerDataStorage.get(onlineProfile).getJournal();
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
        saver.add(new Record(updateType, rename, name));
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
                    final PlayerData playerData = playerDataStorage.get(onlineProfile);
                    playerData.tags().remove(name);
                }
            }
            case "points", "point", "p" -> {
                updateType = UpdateType.REMOVE_ALL_POINTS;
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    final PlayerData playerData = playerDataStorage.get(onlineProfile);
                    playerData.points().remove(name);
                }
            }
            case "objectives", "objective", "o" -> {
                updateType = UpdateType.REMOVE_ALL_OBJECTIVES;
                final ObjectiveIdentifier objectiveID;
                try {
                    objectiveID = getIdentifier(ObjectiveIdentifier.class, name);
                } catch (final QuestException e) {
                    final String message = "The objective '" + name + "' does not exist, it will still be removed from the database!";
                    sendMessage(sender, "error",
                            new VariableReplacement("error", Component.text(e.getMessage())));
                    log.warn(message, e);
                    log.debug("Removing non existent objective only from database: " + e.getMessage(), e);
                    break;
                }
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    objectiveManager.cancel(onlineProfile, objectiveID);
                    playerDataStorage.get(onlineProfile).removeRawObjective(objectiveID);
                }
            }
            case "journals", "journal", "j", "entries", "entry", "e" -> {
                updateType = UpdateType.REMOVE_ALL_ENTRIES;
                final JournalEntryIdentifier entryID;
                try {
                    entryID = getIdentifier(JournalEntryIdentifier.class, name);
                } catch (final QuestException e) {
                    final String message = "The journal entry '" + name + "' does not exist, it will still be removed from the database!";
                    log.warn(message, e);
                    log.debug("Removing non existent journal entry only from database: " + e.getMessage(), e);
                    sender.sendMessage("§2" + message);
                    break;
                }
                for (final OnlineProfile onlineProfile : onlineProfiles) {
                    final Journal journal = playerDataStorage.get(onlineProfile).getJournal();
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
        saver.add(new Record(updateType, name));
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
        map.put("action", "action <player> <action>");
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

    private <I extends Identifier> I getIdentifier(final Class<I> identifierClass, final String identifier) throws QuestException {
        final IdentifierFactory<I> identifierFactory = identifiers.getFactory(identifierClass);
        return identifierFactory.parseIdentifier(null, identifier);
    }
}
