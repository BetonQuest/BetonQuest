package org.betonquest.betonquest.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;

/**
 * Handles the configuration of the plugin.
 */
@SuppressWarnings("NullAway.Init")
public final class Config {
    /**
     * The quest manager.
     */
    private static QuestManager questManager;

    /**
     * The plugin instance.
     */
    private static BetonQuest plugin;

    /**
     * The default language.
     */
    private static String lang;

    private Config() {
    }

    /**
     * Creates new instance of the Config handler.
     *
     * @param plugin the {@link BetonQuest} plugin instance
     * @param config the {@link ConfigAccessor} to load from
     */
    @SuppressFBWarnings("EI_EXPOSE_STATIC_REP2")
    public static void setup(final BetonQuest plugin, final ConfigAccessor config) {
        Config.plugin = plugin;
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
}
