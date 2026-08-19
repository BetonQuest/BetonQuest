package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.api.data.PointHolder;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.feature.journal.Pointer;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Renames stuff.
 */
@SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.AvoidDuplicateLiterals", "PMD.GodClass", "PMD.CouplingBetweenObjects"})
public class RenameSubCommand extends QuestCommandPart {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * The journal entry processor.
     */
    private final JournalEntryProcessor journalEntryProcessor;

    /**
     * The database saver.
     */
    private final Saver saver;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public RenameSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams, List.of("rename", "r"), "<tag/point/globalpoint/objective/journal> <old> <new>");
        this.saver = constructorParams.saver();
        this.globalData = constructorParams.globalData();
        this.journalEntryProcessor = constructorParams.journalEntryProcessor();
        this.objectiveManager = constructorParams.objectiveManager();
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    public void handle(final CommandSender sender, final String... args) throws QuestException {
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
                    renamePointsForHolder(playerDataStorage.get(onlineProfile).points(), name, rename);
                }
            }
            case "globalpoints", "globalpoint", "gpoints", "gpoint", "gp" -> {
                updateType = UpdateType.RENAME_ALL_GLOBAL_POINTS;
                renamePointsForHolder(globalData.points(), name, rename);
            }
            case "objectives", "objective", "o" -> {
                updateType = UpdateType.RENAME_ALL_OBJECTIVES;
                if (handleObjectives(sender, name, rename)) {
                    return;
                }
            }
            case "journals", "journal", "j", "entries", "entry", "e" -> {
                updateType = UpdateType.RENAME_ALL_ENTRIES;
                if (handleJournalEntries(sender, name, rename, onlineProfiles)) {
                    return;
                }
            }
            default -> {
                sendMessage(sender, "unknown_argument");
                return;
            }
        }
        saver.add(new Saver.Record(updateType, rename, name));
        sendMessage(sender, "everything_renamed");
    }

    private void renamePointsForHolder(final PointHolder pointHolder, final String name, final String rename) {
        final Optional<Integer> point = pointHolder.get(name);
        if (point.isPresent()) {
            pointHolder.remove(name);
            pointHolder.add(rename, point.get());
        }
    }

    private boolean handleObjectives(final CommandSender sender, final String name, final String rename) throws QuestException {
        // get ID and package
        final ObjectiveIdentifier nameID;
        try {
            nameID = getIdentifier(ObjectiveIdentifier.class, name);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Could not find Objective: " + e.getMessage(), e);
            return true;
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
                return false;
            }
            configuration.associateWith(newPath, sourceConfigurationSection);
            nameID.getPackage().saveAll();
        } catch (final IOException | InvalidConfigurationException e) {
            log.warn(nameID.getPackage(), e.getMessage(), e);
            return true;
        }
        // rename objective instance
        final ObjectiveIdentifier renameID;
        try {
            renameID = getIdentifier(ObjectiveIdentifier.class, rename);
        } catch (final QuestException e) {
            sender.sendMessage("§4There was an unexpected error: " + e.getMessage());
            log.reportException(e);
            return true;
        }
        final ObjectiveProcessor objectiveProcessor = (ObjectiveProcessor) objectiveManager;
        objectiveProcessor.renameObjective(nameID, renameID);
        nameID.getPackage().getConfig().set(nameID.get(), null);
        try {
            nameID.getPackage().saveAll();
        } catch (final IOException e) {
            log.warn(nameID.getPackage(), e.getMessage(), e);
            return true;
        }
        return false;
    }

    private boolean handleJournalEntries(final CommandSender sender, final String name, final String rename,
                                         final List<OnlineProfile> onlineProfiles) {
        final QuestPackage newPackage = questPackageManager.getPackages().get(rename.split(Identifier.SEPARATOR)[0]);
        if (newPackage == null) {
            final String message = "You can't rename into non-existent package!";
            sendMessage(sender, "error", new VariableReplacement("error", Component.text(message)));
            log.error(message);
            return true;
        }

        final JournalEntryIdentifier newEntryID;
        try {
            newEntryID = getIdentifier(JournalEntryIdentifier.class, rename);
        } catch (final QuestException e) {
            final String message = "You can't rename into non-existent id!";
            sendMessage(sender, "error", new VariableReplacement("error", Component.text(message)));
            log.error(message);
            return true;
        }

        final JournalEntryIdentifier oldEntryID;
        try {
            oldEntryID = getIdentifier(JournalEntryIdentifier.class, name);
        } catch (final QuestException e) {
            final String message = "Old journal entry " + name + " does not exist, renaming only database entries!";
            log.warn(message, e);
            log.debug("Renaming non existent journal entry only from database: " + e.getMessage(), e);
            sender.sendMessage("§2" + message);
            return false;
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
        return false;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    public Optional<List<String>> complete(final String... args) {
        if (args.length == 2) {
            return Optional.of(Arrays.asList("tag", "point", "globalpoint", "objective", "entry"));
        }
        if (args.length == 3) {
            return switch (args[1].toLowerCase(Locale.ROOT)) {
                case "tags",
                     "tag",
                     "t",
                     "points",
                     "point",
                     "p",
                     "globalpoints",
                     "globalpoint",
                     "gpoints",
                     "gpoint",
                     "gp" -> completeId(args, null);
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
        if (args.length == 4) {
            return completeId(args, null);
        }
        return Optional.of(new ArrayList<>());
    }
}
