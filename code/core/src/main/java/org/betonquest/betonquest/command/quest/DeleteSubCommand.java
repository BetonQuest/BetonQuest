package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.feature.journal.Pointer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Deletes stuff.
 */
@SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.GodClass"})
public class DeleteSubCommand extends QuestCommandPart {

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

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
    public DeleteSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams, List.of("delete", "del", "d"), "<tag/point/objective/journal> <name>");
        this.saver = constructorParams.saver();
        this.objectiveManager = constructorParams.objectiveManager();
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    public void handle(final CommandSender sender, final String... args) throws QuestException {
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
                handleObjectives(sender, name, onlineProfiles);
            }
            case "journals", "journal", "j", "entries", "entry", "e" -> {
                updateType = UpdateType.REMOVE_ALL_ENTRIES;
                handleJournalEntries(sender, name, onlineProfiles);
            }
            default -> {
                sendMessage(sender, "unknown_argument");
                return;
            }
        }
        saver.add(new Saver.Record(updateType, name));
        sendMessage(sender, "everything_removed");
    }

    private void handleObjectives(final CommandSender sender, final String name, final List<OnlineProfile> onlineProfiles) {
        final ObjectiveIdentifier objectiveID;
        try {
            objectiveID = getIdentifier(ObjectiveIdentifier.class, name);
        } catch (final QuestException e) {
            final String message = "The objective '" + name + "' does not exist, it will still be removed from the database!";
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn(message, e);
            log.debug("Removing non existent objective only from database: " + e.getMessage(), e);
            return;
        }
        for (final OnlineProfile onlineProfile : onlineProfiles) {
            objectiveManager.cancel(onlineProfile, objectiveID);
            playerDataStorage.get(onlineProfile).removeRawObjective(objectiveID);
        }
    }

    private void handleJournalEntries(final CommandSender sender, final String name, final List<OnlineProfile> onlineProfiles) {
        final JournalEntryIdentifier entryID;
        try {
            entryID = getIdentifier(JournalEntryIdentifier.class, name);
        } catch (final QuestException e) {
            final String message = "The journal entry '" + name + "' does not exist, it will still be removed from the database!";
            log.warn(message, e);
            log.debug("Removing non existent journal entry only from database: " + e.getMessage(), e);
            sender.sendMessage("§2" + message);
            return;
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

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    public Optional<List<String>> complete(final String... args) {
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
}
