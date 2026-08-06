package org.betonquest.betonquest.command.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.database.GlobalData;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Lists, adds, removes or purges all global points.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class GlobalPointSubCommand extends QuestCommandPart {

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
    public GlobalPointSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams);
        this.globalData = constructorParams.globalData();
    }

    @Override
    public List<String> names() {
        return List.of("globalpoint", "globalpoints", "gpoint", "gpoints", "gp");
    }

    @Override
    public void handle(final CommandSender sender, final String... args) throws QuestException {
        // if there are no arguments then list all global points
        if (args.length < 2 || "list".equalsIgnoreCase(args[1]) || "l".equalsIgnoreCase(args[1])) {
            log.debug("Listing global points");
            final Predicate<Map.Entry<String, Integer>> shouldDisplay = createListFilter(args, 2, Map.Entry::getKey);
            sendMessage(sender, "global_points");
            globalData.points().get().entrySet().stream()
                    .filter(shouldDisplay)
                    .forEach(point -> sender.sendMessage("§b- " + point.getKey() + "§e: §a" + point.getValue()));
            return;
        }
        // handle purge
        if ("purge".equalsIgnoreCase(args[1])) {
            log.debug("Purging all global points");
            globalData.purgePoints();
            sendMessage(sender, "global_points_purged");
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 3) {
            log.debug("Missing category");
            sendMessage(sender, "specify_category");
            return;
        }
        handle2(sender, args);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void handle2(final CommandSender sender, final String... args) {
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
                globalData.points().add(category, Integer.parseInt(args[3]));
                sendMessage(sender, "points_added");
            }
            case "remove", "delete", "del", "r", "d" -> {
                log.debug("Removing global points");
                globalData.points().remove(category);
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
}
