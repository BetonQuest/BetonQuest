package org.betonquest.betonquest.command.quest;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.logger.PlayerLogWatcher;
import org.betonquest.betonquest.logger.handler.history.LogPublishingController;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

/**
 * (Ingame) debug logging options.
 */
@SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.AvoidDuplicateLiterals"})
public class DebugSubCommand extends QuestCommandPart {

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
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public DebugSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams);
        this.logWatcher = constructorParams.playerLogWatcher();
        this.debuggingController = constructorParams.logPublishingController();
    }

    @Override
    public List<String> names() {
        return List.of("debug");
    }

    @Override
    public void handle(final CommandSender sender, final String... args) {
        if (args.length == 1) {
            sender.sendMessage(
                    "§2Debugging mode is currently " + (debuggingController.isLogging() ? "enabled" : "disabled") + '!');
            return;
        }
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "ingame" -> handleIngame(sender, args);
            case "dump" -> handleDump(sender);
            case "true" -> {
                if (args.length == 2) {
                    handleRest(sender, true);
                } else {
                    sendMessage(sender, "unknown_argument");
                }
            }
            case "false" -> {
                if (args.length == 2) {
                    handleRest(sender, false);
                } else {
                    sendMessage(sender, "unknown_argument");
                }
            }
            default -> sendMessage(sender, "unknown_argument");
        }
    }

    private void handleIngame(final CommandSender sender, final String... args) {
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
    }

    private void handleDump(final CommandSender sender) {
        if (debuggingController.isLogging()) {
            sender.sendMessage("§2Can not dump while debugging is enabled!");
            return;
        }
        debuggingController.dumpLog();
        sender.sendMessage("§2Dumped debug log to file!");
        log.info("Dumped debug log to file!");
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void handleRest(final CommandSender sender, final boolean input) {
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
        log.info("Debugging mode was " + (debuggingController.isLogging() ? "enabled" : "disabled") + '!');
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

    @Override
    public Optional<List<String>> complete(final String... args) {
        if (args.length == 2) {
            return Optional.of(Arrays.asList("true", "false", "ingame", "dump"));
        }
        if (args.length == 3) {
            return completePackage();
        }
        if (args.length == 4) {
            return Optional.of(Arrays.asList("error", "info", "debug"));
        }
        return Optional.of(new ArrayList<>());
    }
}
