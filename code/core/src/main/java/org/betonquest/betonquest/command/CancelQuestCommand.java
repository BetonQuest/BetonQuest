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
 * The /cancelquest command. It opens the list of quests.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class CancelQuestCommand implements CommandExecutor {

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
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

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
     * The identifier registry.
     */
    private final Identifiers identifiers;

    /**
     * The item manager.
     */
    private final ItemManager itemManager;

    /**
     * Creates a new executor for the /cancelquest command.
     *
     * @param plugin            the plugin instance
     * @param config            the plugin configuration file
     * @param translations      the {@link PluginMessage} instance
     * @param profileProvider   the profile provider instance
     * @param loggerFactory     the logger factory
     * @param playerDataStorage the player data storage
     * @param cancelerProcessor the canceler processor
     * @param compassManager    the compass manager
     * @param identifiers       the identifier registry
     * @param itemManager       the item manager
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CancelQuestCommand(final Plugin plugin, final ConfigAccessor config, final Translations translations,
                              final ProfileProvider profileProvider, final BetonQuestLoggerFactory loggerFactory,
                              final PlayerDataStorage playerDataStorage, final CancelerProcessor cancelerProcessor,
                              final CompassManager compassManager, final Identifiers identifiers, final ItemManager itemManager) {
        this.plugin = plugin;
        this.config = config;
        this.translations = translations;
        this.profileProvider = profileProvider;
        this.loggerFactory = loggerFactory;
        this.playerDataStorage = playerDataStorage;
        this.cancelerProcessor = cancelerProcessor;
        this.compassManager = compassManager;
        this.identifiers = identifiers;
        this.itemManager = itemManager;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("cancelquest".equalsIgnoreCase(cmd.getName())) {
            if (sender instanceof Player) {
                final OnlineProfile onlineProfile = profileProvider.getProfile((Player) sender);
                new Backpack(plugin, loggerFactory.create(Backpack.class), playerDataStorage.get(onlineProfile), cancelerProcessor,
                        compassManager, config, translations, onlineProfile, itemManager, identifiers, DisplayType.CANCEL);
            }
            return true;
        }
        return false;
    }
}
