package org.betonquest.betonquest.command.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.database.PlayerData;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Lists, adds or removes points of certain profile.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class PointSubCommand extends QuestCommandPart {

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public PointSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams, List.of("point", "points", "p"), "<player> [list/add/del] [category] [amount]");
    }

    @Override
    public void handle(final CommandSender sender, final String... args) throws QuestException {
        final PlayerData playerData = getTargetPlayerData(sender, args);
        if (playerData == null) {
            return;
        }
        // if there are no arguments then list player's points
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            log.debug("Listing points");
            final Predicate<Map.Entry<String, Integer>> shouldDisplay = createListFilter(args, 3, Map.Entry::getKey);
            sendMessage(sender, "player_points");
            playerData.points().get().entrySet().stream()
                    .filter(shouldDisplay)
                    .forEach(point -> sender.sendMessage("§b- " + point.getKey() + "§e: §a" + point.getValue()));
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            log.debug("Missing category");
            sendMessage(sender, "specify_category");
            return;
        }
        handle2(sender, args, playerData);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void handle2(final CommandSender sender, final String[] args, final PlayerData playerData) {
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
                playerData.points().add(category, Integer.parseInt(args[4]));
                sendMessage(sender, "points_added");
            }
            case "remove", "delete", "del", "r", "d" -> {
                log.debug("Removing points");
                playerData.points().remove(category);
                sendMessage(sender, "points_removed");
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
            return Optional.of(Arrays.asList("add", "list", "del"));
        }
        if (args.length == 4) {
            return completeId(args, null);
        }
        return Optional.of(new ArrayList<>());
    }
}
