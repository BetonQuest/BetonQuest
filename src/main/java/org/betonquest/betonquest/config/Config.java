package org.betonquest.betonquest.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Handles the configuration of the plugin.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CommentRequired", "NullAway.Init"})
public final class Config {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(Config.class);

    private static final Set<String> LANGUAGES = new LinkedHashSet<>();

    private static QuestManager questManager;

    private static BetonQuest plugin;

    private static String lang;

    private Config() {
    }

    /**
     * Creates new instance of the Config handler.
     *
     * @param plugin the {@link BetonQuest} plugin instance
     * @param config the {@link ConfigurationFile} to load from
     */
    @SuppressFBWarnings("EI_EXPOSE_STATIC_REP2")
    public static void setup(final BetonQuest plugin, final ConfigurationFile config) {
        Config.plugin = plugin;
        LANGUAGES.clear();

        lang = config.getString("language");

        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        questManager = new QuestManager(loggerFactory, loggerFactory.create(QuestManager.class), plugin.getConfigAccessorFactory(), plugin.getDataFolder());
    }

    /**
     * Get all the packages loaded by the plugin.
     *
     * @return the map of packages and their names
     */
    public static Map<String, QuestPackage> getPackages() {
        return questManager.getPackages();
    }

    /**
     * Retrieves the string from the configuration.
     *
     * @param address address of the string without leading {@code config.}
     * @return the requested string
     */
    public static String getConfigString(final String address) {
        return plugin.getPluginConfig().getString(address);
    }

    /**
     * Get the default language.
     *
     * @return the default language
     */
    public static String getLanguage() {
        return lang;
    }

    /**
     * Plays a sound specified in the plugin's config to the player.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param soundName     the name of the sound to play to the player
     */
    public static void playSound(final OnlineProfile onlineProfile, final String soundName) {
        final Player player = onlineProfile.getPlayer();
        final String rawSound = plugin.getPluginConfig().getString("sounds." + soundName);
        if (!"false".equalsIgnoreCase(rawSound)) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf(rawSound), 1F, 1F);
            } catch (final IllegalArgumentException e) {
                player.playSound(player.getLocation(), rawSound.toLowerCase(Locale.ROOT), 1F, 1F);
            }
        }
    }

    /**
     * Get the languages defined for this plugin.
     *
     * @return the languages defined for this plugin
     */
    public static Set<String> getLanguages() {
        return LANGUAGES;
    }
}
