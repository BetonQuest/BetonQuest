package org.betonquest.betonquest.command;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Changes the default language for the player.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class LangCommand implements CommandExecutor, SimpleTabCompleter {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The language provider instance.
     */
    private final LanguageProvider languageProvider;

    /**
     * The sender for the language changed notification.
     */
    private final IngameNotificationSender languageChangedSender;

    /**
     * Creates a new executor for the /questlang command.
     *
     * @param log              the logger that will be used for logging
     * @param dataStorage      the storage providing player data
     * @param pluginMessage    the {@link PluginMessage} instance
     * @param profileProvider  the profile provider instance
     * @param languageProvider the language provider instance
     */
    public LangCommand(final BetonQuestLogger log, final PlayerDataStorage dataStorage,
                       final PluginMessage pluginMessage, final ProfileProvider profileProvider,
                       final LanguageProvider languageProvider) {
        this.log = log;
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
        this.profileProvider = profileProvider;
        this.languageProvider = languageProvider;
        this.languageChangedSender = new IngameNotificationSender(log, pluginMessage, null,
                "LanguageCommand", NotificationLevel.INFO, "language_changed");
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!"questlang".equalsIgnoreCase(cmd.getName())) {
            return false;
        }
        if (!(sender instanceof final Player player)) {
            return true;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        if (args.length == 0) {
            try {
                sender.sendMessage(pluginMessage.getMessage(onlineProfile, "language_missing"));
            } catch (final QuestException e) {
                log.warn("Failed to get language missing message: " + e.getMessage(), e);
            }
            return true;
        }
        final Set<String> languages = pluginMessage.getLanguages();
        if (!languages.contains(args[0]) && !"default".equalsIgnoreCase(args[0])) {
            final StringBuilder builder = new StringBuilder();
            builder.append("default (").append(languageProvider.getDefaultLanguage()).append("), ").append(String.join(", ", languages));
            if (builder.length() < 3) {
                log.warn("No translations loaded, somethings wrong!");
                return false;
            }
            final String finalMessage = builder.substring(0, builder.length() - 2) + ".";
            try {
                sender.sendMessage(pluginMessage.getMessage(onlineProfile, "language_not_exist",
                        new VariableReplacement("languages", Component.text(finalMessage))));
            } catch (final QuestException e) {
                log.warn("Failed to get language_not_exist: " + e.getMessage(), e);
            }
            return true;
        }
        final String lang = args[0];
        final PlayerData playerData = dataStorage.get(onlineProfile);
        final Journal journal = playerData.getJournal();
        playerData.setLanguage("default".equalsIgnoreCase(lang) ? null : lang);
        journal.update();
        languageChangedSender.sendNotification(onlineProfile);
        return true;
    }

    @Override
    public Optional<List<String>> simpleTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 1) {
            return Optional.of(pluginMessage.getLanguages().stream().toList());
        }
        return Optional.of(Collections.emptyList());
    }
}
