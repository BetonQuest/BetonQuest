package org.betonquest.betonquest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.dependency.CoreComponentLoader;
import org.betonquest.betonquest.api.integration.IntegrationService;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.conversation.AnswerFilter;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.integration.DefaultIntegrationService;
import org.betonquest.betonquest.integration.IntegrationManager;
import org.betonquest.betonquest.kernel.BetonQuestComponents;
import org.betonquest.betonquest.kernel.component.DatabaseComponent;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.lib.dependency.component.DefaultCoreComponentLoader;
import org.betonquest.betonquest.lib.logger.CachingBetonQuestLoggerFactory;
import org.betonquest.betonquest.logger.DefaultBetonQuestLoggerFactory;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.playerhider.PlayerHider;
import org.betonquest.betonquest.schedule.ActionScheduling;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Map;

/**
 * Represents BetonQuest plugin.
 */
@SuppressWarnings("NullAway.Init")
public class BetonQuest extends JavaPlugin {

    /**
     * The BetonQuest Plugin instance.
     */
    private static BetonQuest instance;

    /**
     * The custom logger for the plugin.
     */
    protected BetonQuestLogger log;

    /**
     * The core component loader instance.
     */
    private CoreComponentLoader coreComponentLoader;

    /**
     * The required default constructor without arguments for plugin creation.
     */
    public BetonQuest() {
        super();
    }

    /**
     * Get the plugin's instance.
     *
     * @return The plugin's instance.
     */
    public static BetonQuest getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        final BetonQuestLoggerFactory loggerFactory = new CachingBetonQuestLoggerFactory(new DefaultBetonQuestLoggerFactory());
        this.log = loggerFactory.create(this);

        final IntegrationManager integrationManager = new IntegrationManager(loggerFactory.create(IntegrationManager.class), loggerFactory);
        final DefaultIntegrationService integrationService = new DefaultIntegrationService(integrationManager);
        this.getServer().getServicesManager().register(IntegrationService.class, integrationService, this, ServicePriority.Highest);

        this.coreComponentLoader = new DefaultCoreComponentLoader(loggerFactory.create(DefaultCoreComponentLoader.class));
        this.coreComponentLoader.init(BetonQuestLoggerFactory.class, loggerFactory);
        this.coreComponentLoader.init(IntegrationManager.class, integrationManager);

        initPluginDependencies(coreComponentLoader);
        BetonQuestComponents.createDefaults(this.getFile()).forEach(coreComponentLoader::register);
    }

    @Override
    public void onEnable() {
        instance = this;

        this.coreComponentLoader.load();

        // block betonquestanswer logging (it's just a spam)
        try {
            Class.forName("org.apache.logging.log4j.core.Filter");
            final Logger coreLogger = (Logger) LogManager.getRootLogger();
            coreLogger.addFilter(new AnswerFilter());
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
            log.warn("Could not disable /betonquestanswer logging", e);
        }

        log.info("BetonQuest successfully enabled!");
    }

    private void initPluginDependencies(final CoreComponentLoader coreComponentLoader) {
        coreComponentLoader.init(JavaPlugin.class, this);
        coreComponentLoader.init(Server.class, getServer());
        coreComponentLoader.init(PluginManager.class, getServer().getPluginManager());
        coreComponentLoader.init(BukkitScheduler.class, getServer().getScheduler());
        coreComponentLoader.init(PluginDescriptionFile.class, getDescription());
        coreComponentLoader.init(ServicesManager.class, getServer().getServicesManager());
    }

    @Override
    public void onDisable() {
        coreComponentLoader.getOptional(ActionScheduling.class).ifPresent(ActionScheduling::clear);

        coreComponentLoader.getOptional(ConversationProcessor.class).map(ConversationProcessor::getActiveConversations).map(Map::values)
                .ifPresent(conversations -> conversations.forEach(Conversation::suspend));
        coreComponentLoader.getOptional(ProfileProvider.class).map(ProfileProvider::getOnlineProfiles)
                .ifPresent(onlineProfiles -> onlineProfiles.forEach(onlineProfile -> onlineProfile.getPlayer().closeInventory()));

        coreComponentLoader.getOptional(Saver.class).ifPresent(Saver::end);
        coreComponentLoader.getOptional(IntegrationManager.class).ifPresent(IntegrationManager::disable);
        coreComponentLoader.getOptional(Compatibility.class).ifPresent(Compatibility::disable);
        coreComponentLoader.getOptional(Connector.class).ifPresent(connector -> connector.getDatabase().closeConnection());
        coreComponentLoader.getOptional(PlayerHider.class).ifPresent(PlayerHider::stop);
        coreComponentLoader.getOptional(RPGMenu.class).ifPresent(RPGMenu::onDisable);

        log.info("BetonQuest successfully disabled!");
    }

    /**
     * Returns the {@link CoreComponentLoader} instance.
     *
     * @return the {@link CoreComponentLoader} instance
     */
    public CoreComponentLoader getComponentLoader() {
        return coreComponentLoader;
    }

    /**
     * Get the plugin configuration file.
     *
     * @return config file
     */
    public ConfigAccessor getPluginConfig() {
        return coreComponentLoader.get(FileConfigAccessor.class);
    }

    /**
     * Returns the {@link BetonQuestApi} instance.
     *
     * @return the {@link BetonQuestApi} instance
     */
    public BetonQuestApi getBetonQuestApi() {
        return coreComponentLoader.get(BetonQuestApi.class);
    }

    /**
     * Checks if MySQL is used or not.
     *
     * @return if MySQL is used (false means that SQLite is being used)
     */
    public boolean isMySQLUsed() {
        return coreComponentLoader.get(DatabaseComponent.class).usesMySQL();
    }

    /**
     * Returns the {@link Saver} instance used by BetonQuest.
     *
     * @return the database saver
     */
    public Saver getSaver() {
        return coreComponentLoader.get(Saver.class);
    }

    /**
     * Gets the stored player data.
     *
     * @return storage for currently loaded player data
     */
    public PlayerDataStorage getPlayerDataStorage() {
        return coreComponentLoader.get(PlayerDataStorage.class);
    }
}
