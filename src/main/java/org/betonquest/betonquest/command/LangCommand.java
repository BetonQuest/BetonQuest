package org.betonquest.betonquest.command;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.notify.Notify;
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
     * Object to get player data and config.
     */
    private final BetonQuest betonQuest;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Creates a new executor for the /questlang command.
     *
     * @param log           the logger that will be used for logging
     * @param betonQuest    the object to get player data and config from
     * @param dataStorage   the storage providing player data
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public LangCommand(final BetonQuestLogger log, final BetonQuest betonQuest, final PlayerDataStorage dataStorage,
                       final PluginMessage pluginMessage) {
        this.log = log;
        this.betonQuest = betonQuest;
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!"questlang".equalsIgnoreCase(cmd.getName())) {
            return false;
        }
        if (!(sender instanceof final Player player)) {
            return true;
        }
        final ProfileProvider profileProvider = betonQuest.getProfileProvider();
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        if (args.length == 0) {
            sender.sendMessage(pluginMessage.getMessage(onlineProfile, "language_missing"));
            return true;
        }
        final Set<String> languages = pluginMessage.getLanguages();
        if (!languages.contains(args[0]) && !"default".equalsIgnoreCase(args[0])) {
            final StringBuilder builder = new StringBuilder();
            builder.append("default (").append(Config.getLanguage()).append("), ").append(String.join(", ", languages));
            if (builder.length() < 3) {
                log.warn("No translations loaded, somethings wrong!");
                return false;
            }
            final String finalMessage = builder.substring(0, builder.length() - 2) + ".";
            sender.sendMessage(pluginMessage.getMessage(onlineProfile, "language_not_exist") + finalMessage);
            return true;
        }
        final String lang = args[0];
        final PlayerData playerData = dataStorage.get(onlineProfile);
        final Journal journal = playerData.getJournal();
        playerData.setLanguage(lang);
        journal.update();
        final String message = pluginMessage.getMessage(onlineProfile, "language_changed");
        try {
            Notify.get(null, "language_changed,info").sendNotify(message, onlineProfile);
        } catch (final QuestException e) {
            log.warn("The notify system was unable to play a sound for the 'language_changed' category. Error was: '" + e.getMessage() + "'", e);
        }
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
