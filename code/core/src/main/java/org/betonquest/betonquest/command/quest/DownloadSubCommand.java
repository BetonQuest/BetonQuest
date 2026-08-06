package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.logger.format.ChatFormatter;
import org.betonquest.betonquest.web.downloader.DownloadFailedException;
import org.betonquest.betonquest.web.downloader.Downloader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * Downloads QuestPackages from GitHub.
 */
@SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.AvoidDuplicateLiterals"})
public class DownloadSubCommand extends QuestCommandPart {

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
     * The plugin configuration accessor.
     */
    private final ConfigAccessor config;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param plugin            the source plugin to get the folder from and to schedule tasks
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public DownloadSubCommand(final Plugin plugin, final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams);
        this.plugin = plugin;
        this.log = log;
        this.loggerFactory = constructorParams.loggerFactory();
        this.config = constructorParams.configAccessor();
    }

    @Override
    public List<String> names() {
        return List.of("download");
    }

    @Override
    @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    public void handle(final CommandSender sender, final String... args) {
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
        final String ref = args[2];
        final String offsetPath = args[3];
        final String errSummary = String.format("Download from %s ref %s of %s at %s to %s failed:",
                githubNamespace, ref, offsetPath, sourcePath, targetPath);

        //Check offset paths
        if (!Downloader.ALLOWED_OFFSET_PATHS.contains(offsetPath)) {
            sendMessage(sender, "download_failed_offset");
            log.debug(errSummary, new IllegalArgumentException(offsetPath));
            return;
        }

        //check if repo is allowed
        final List<String> whitelist = config.getStringList("downloader.repo_whitelist");
        if (whitelist.stream().map(String::trim).noneMatch(githubNamespace::equals)) {
            sendMessage(sender, "download_failed_whitelist");
            log.debug(errSummary, new IllegalArgumentException(githubNamespace));
            return;
        }

        //check if ref is valid
        if (ref.toLowerCase(Locale.ROOT).startsWith("refs/pull/") && !config.getBoolean("downloader.pull_request", false)) {
            sendMessage(sender, "download_failed_pr");
            log.debug(errSummary, new IllegalArgumentException(ref));
            return;
        }

        //run download
        final Downloader downloader = new Downloader(loggerFactory.create(Downloader.class, "Downloader"),
                plugin.getDataFolder(), githubNamespace, ref, offsetPath, sourcePath, targetPath, recursive, overwrite);
        sendMessage(sender, "download_scheduled");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                downloader.call();
                sendMessageSync(sender, "download_success");
            } catch (final DownloadFailedException | SecurityException | FileNotFoundException e) {
                final String message = e.getMessage();
                sendMessageSync(sender, "download_failed",
                        new VariableReplacement("error", Component.text(message == null ? e.getClass().getSimpleName() : message)));
                log.debug(errSummary, e);
            } catch (final Exception e) {
                sendMessageSync(sender, "download_failed",
                        new VariableReplacement("error", Component.text(e.getClass().getSimpleName() + ": " + e.getMessage())));
                if (sender instanceof final Player player) {
                    final BetonQuestLogRecord record = new BetonQuestLogRecord(Level.FINE, null, plugin);
                    record.setThrown(e);
                    player.sendMessage(new ChatFormatter().formatTextComponent(record));
                    log.debug(errSummary, e);
                } else {
                    log.error(errSummary, e);
                }
            }
        });
    }

    private void sendMessageSync(final CommandSender sender, final String messageName, final VariableReplacement... replacements) {
        Bukkit.getScheduler().runTask(plugin, () -> sendMessage(sender, messageName, replacements));
    }

    @Override
    public Optional<List<String>> complete(final String... args) {
        return switch (args.length) {
            case 2 -> Optional.of(config.getStringList("downloader.repo_whitelist"));
            case 3 -> Optional.of(List.of("refs/heads/", "refs/tags/"));
            case 4 -> Optional.of(Downloader.ALLOWED_OFFSET_PATHS);
            case 5 -> Optional.of(List.of("/"));
            case 6 -> Optional.of(List.of("/", "overwrite", "recursive"));
            case 7, 8 ->
                    Optional.of(Stream.of("overwrite", "recursive").filter(tag -> !Arrays.asList(args).contains(tag)).toList());
            default -> Optional.of(List.of());
        };
    }
}
