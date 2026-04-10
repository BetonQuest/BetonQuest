package org.betonquest.betonquest.command;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.compass.CompassManager;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.config.Translations;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.Backpack;
import org.betonquest.betonquest.feature.Backpack.DisplayType;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * The /compass command. It opens the list of quests.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class CompassCommand implements CommandExecutor {

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The plugin configuration file.
     */
    private final ConfigAccessor config;

    /**
     * The {@link PluginMessage} instance.
     */
    private final Translations translations;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The player data storage.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The canceler processor.
     */
    private final CancelerProcessor cancelerProcessor;

    /**
     * The compass manager.
     */
    private final CompassManager compassManager;

    /**
     * The item manager.
     */
    private final ItemManager itemManager;

    /**
     * The identifier registry.
     */
    private final Identifiers identifiers;

    /**
     * Creates a new executor for the /compass command.
     *
     * @param plugin            the plugin instance
     * @param loggerFactory     the logger factory
     * @param config            the plugin configuration accessor
     * @param translations      the {@link PluginMessage} instance
     * @param profileProvider   the profile provider instance
     * @param playerDataStorage the player data storage
     * @param cancelerProcessor the canceler processor
     * @param compassManager    the compass manager
     * @param itemManager       the item manager
     * @param identifiers       the identifier registry
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CompassCommand(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory,
                          final ConfigAccessor config, final Translations translations, final ProfileProvider profileProvider,
                          final PlayerDataStorage playerDataStorage, final CancelerProcessor cancelerProcessor, final CompassManager compassManager,
                          final ItemManager itemManager, final Identifiers identifiers) {
        this.plugin = plugin;
        this.loggerFactory = loggerFactory;
        this.config = config;
        this.translations = translations;
        this.profileProvider = profileProvider;
        this.playerDataStorage = playerDataStorage;
        this.cancelerProcessor = cancelerProcessor;
        this.compassManager = compassManager;
        this.itemManager = itemManager;
        this.identifiers = identifiers;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("compass".equalsIgnoreCase(cmd.getName())) {
            if (sender instanceof Player) {
                final OnlineProfile onlineProfile = profileProvider.getProfile((Player) sender);
                new Backpack(plugin, loggerFactory.create(Backpack.class), playerDataStorage.get(onlineProfile), cancelerProcessor,
                        compassManager, config, translations, onlineProfile, itemManager, identifiers, DisplayType.COMPASS);
            }
            return true;
        }
        return false;
    }
}
