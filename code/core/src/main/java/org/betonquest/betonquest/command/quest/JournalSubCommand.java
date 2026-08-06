package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.feature.journal.Pointer;
import org.bukkit.command.CommandSender;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Lists, adds or removes journal entries of certain profile.
 */
@SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.AvoidDuplicateLiterals"})
public class JournalSubCommand extends QuestCommandPart {

    /**
     * The plugin configuration accessor.
     */
    private final ConfigAccessor config;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public JournalSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams);
        this.config = constructorParams.configAccessor();
    }

    @Override
    public List<String> names() {
        return List.of("journal", "journals", "j");
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    public void handle(final CommandSender sender, final String... args) {
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
                        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(config.getString("date_format", ""), Locale.ROOT);
                        final String date = Instant.ofEpochMilli(pointer.timestamp())
                                .atZone(ZoneId.systemDefault())
                                .format(formatter);
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
            case "add", "a" -> add(sender, args, pointerName, journal);
            case "remove", "delete", "del", "r", "d" -> remove(sender, pointerName, journal);
            default -> {
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
            }
        }
    }

    private void add(final CommandSender sender, final String[] args, final String pointerName, final Journal journal) {
        final JournalEntryIdentifier entryID;
        try {
            entryID = getIdentifier(JournalEntryIdentifier.class, pointerName);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("The journal entry'" + pointerName + "' does not exist!");
            log.debug("Tried to add non existing journal entry: " + e.getMessage(), e);
            return;
        }
        final Pointer pointer;
        if (args.length < 5) {
            final long timestamp = System.currentTimeMillis();
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

    private void remove(final CommandSender sender, final String pointerName, final Journal journal) {
        log.debug("Removing pointer");
        final JournalEntryIdentifier entryID;
        try {
            entryID = getIdentifier(JournalEntryIdentifier.class, pointerName);
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

    @Override
    public Optional<List<String>> complete(final String... args) {
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
}
