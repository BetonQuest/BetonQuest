package org.betonquest.betonquest.commands;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
     * Creates a new executor for the /questlang command.
     *
     * @param log         the logger that will be used for logging
     * @param betonQuest  the object to get player data and config from
     * @param dataStorage the storage providing player data
     */
    public LangCommand(final BetonQuestLogger log, final BetonQuest betonQuest, final PlayerDataStorage dataStorage) {
        this.log = log;
        this.betonQuest = betonQuest;
        this.dataStorage = dataStorage;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!"questlang".equalsIgnoreCase(cmd.getName())) {
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage(Config.getMessage(Config.getLanguage(), "language_missing"));
            return true;
        }
        if (!Config.getLanguages().contains(args[0]) && !"default".equalsIgnoreCase(args[0])) {
            final StringBuilder builder = new StringBuilder();
            builder.append("default (").append(Config.getLanguage()).append("), ");
            for (final String lang : Config.getLanguages()) {
                builder.append(lang).append(", ");
            }
            if (builder.length() < 3) {
                log.warn("No translations loaded, somethings wrong!");
                return false;
            }
            final String finalMessage = builder.substring(0, builder.length() - 2) + ".";
            sender.sendMessage(Config.getMessage(Config.getLanguage(), "language_not_exist") + finalMessage);
            return true;
        }
        if (sender instanceof Player) {
            final String lang = args[0];
            final OnlineProfile onlineProfile = PlayerConverter.getID((Player) sender);
            final PlayerData playerData = dataStorage.get(onlineProfile);
            final Journal journal = playerData.getJournal();
            playerData.setLanguage(lang);
            journal.update();
            try {
                Config.sendNotify(null, onlineProfile, "language_changed", new String[]{lang}, "language_changed,info");
            } catch (final QuestException e) {
                log.warn("The notify system was unable to play a sound for the 'language_changed' category. Error was: '" + e.getMessage() + "'", e);
            }
        } else {
            betonQuest.getPluginConfig().set("language", args[0]);
            sender.sendMessage(Config.getMessage(args[0], "default_language_changed"));
        }
        return true;
    }

    @Override
    public Optional<List<String>> simpleTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 1) {
            return Optional.of(Config.getLanguages().stream().toList());
        }
        return Optional.of(Collections.emptyList());
    }
}
