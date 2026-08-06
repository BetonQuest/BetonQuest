package org.betonquest.betonquest.command.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.database.PlayerData;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Lists, adds or removes tags.
 */
public class TagSubCommand extends QuestCommandPart {

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public TagSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams);
    }

    @Override
    public List<String> names() {
        return List.of("tag", "tags", "t");
    }

    @Override
    public void handle(final CommandSender sender, final String... args) throws QuestException {
        final PlayerData playerData = getTargetPlayerData(sender, args);
        if (playerData == null) {
            return;
        }
        // if there are no arguments then list player's tags
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            log.debug("Listing tags");
            final Predicate<String> shouldDisplay = createListFilter(args, 3, Function.identity());
            sendMessage(sender, "player_tags");
            playerData.tags().get().stream()
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
                playerData.tags().add(tag);
                sendMessage(sender, "tag_added");
            }
            case "remove", "delete", "del", "r", "d" -> {
                log.debug("Removing tag");
                playerData.tags().remove(tag);
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
}
