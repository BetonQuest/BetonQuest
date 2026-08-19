package org.betonquest.betonquest.command.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.database.GlobalData;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Lists, adds or removes global tags.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class GlobalTagSubCommand extends QuestCommandPart {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public GlobalTagSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams, List.of("globaltag", "globaltags", "gtag", "gtags", "gt"), "[list/add/del/purge]");
        this.globalData = constructorParams.globalData();
    }

    @Override
    public void handle(final CommandSender sender, final String... args) throws QuestException {
        // if there are no arguments then list all global tags
        if (args.length < 2 || "list".equalsIgnoreCase(args[1]) || "l".equalsIgnoreCase(args[1])) {
            log.debug("Listing global tags");
            final Predicate<String> shouldDisplay = createListFilter(args, 2, Function.identity());
            sendMessage(sender, "global_tags");
            globalData.tags().get().stream()
                    .filter(shouldDisplay)
                    .sorted()
                    .forEach(tag -> sender.sendMessage("§b- " + tag));
            return;
        }
        // handle purge
        if ("purge".equalsIgnoreCase(args[1])) {
            log.debug("Purging all global tags");
            globalData.purgeTags();
            sendMessage(sender, "global_tags_purged");
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 3) {
            log.debug("Missing tag name");
            sendMessage(sender, "specify_tag");
            return;
        }
        handle2(sender, args);
    }

    private void handle2(final CommandSender sender, final String... args) {
        final String tag = args[2];
        // if there are arguments, handle them
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "add", "a" -> {
                log.debug("Adding global tag " + tag);
                globalData.tags().add(tag);
                sendMessage(sender, "tag_added");
            }
            case "remove", "delete", "del", "r", "d" -> {
                log.debug("Removing global tag " + tag);
                globalData.tags().remove(tag);
                sendMessage(sender, "tag_removed");
            }
            default -> {
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
            }
        }
    }

    @Override
    public Optional<List<String>> complete(final String... args) {
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
}
