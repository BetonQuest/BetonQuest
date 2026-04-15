package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class which provides Hologram creation.
 */
public final class HologramProvider {

    /**
     * Pattern to match a placeholder in a string.
     */
    public static final Pattern PLACEHOLDER_VALIDATOR = Pattern.compile("%[^ %\\s]+%");

    /**
     * The selected factory.
     */
    private final BetonHologramFactory hologramFactory;

    /**
     * The current {@link LocationHologramLoop}.
     */
    private LocationHologramLoop locationHologramLoop;

    /**
     * The current {@link NpcHologramLoop}.
     */
    private NpcHologramLoop npcHologramLoop;

    private HologramProvider(final BetonQuestApi betonQuestApi, final BetonHologramFactory integration) {
        this.hologramFactory = integration;
        load(betonQuestApi);
    }

    /**
     * Creates a new HologramProvider from hooked {@link HologramIntegration}.
     *
     * @param betonQuestApi the {@link BetonQuestApi} instance providing access to BetonQuest's API
     * @param config        the config to load the priorities
     * @param integrations  The list of integrators to consider.
     * @return the initialized provider
     * @throws QuestException if there are no integrations given or a hologram factory could not be gotten
     */
    public static HologramProvider init(final BetonQuestApi betonQuestApi, final ConfigAccessor config, final List<HologramIntegration> integrations) throws QuestException {
        if (integrations.isEmpty()) {
            throw new QuestException("There are no integrations to load.");
        }
        integrations.sort(Comparator.comparingInt(value -> getPriority(config, value)));
        final HologramIntegration selected = integrations.get(integrations.size() - 1);
        final BetonHologramFactory factory = selected.getHologramFactory(betonQuestApi);
        return new HologramProvider(betonQuestApi, factory);
    }

    /**
     * Get the priority of this integrator based on the plugin name.
     *
     * @return The priority of this integrator ranging from 1 to the amount of HologramIntegrators, or 0 if a config option
     * did not exist or if the plugin was not found.
     */
    private static int getPriority(final ConfigAccessor config, final HologramIntegration integration) {
        final String pluginName = integration.getPluginName();
        final String defaultHolograms = config.getString("hologram.default");
        if (defaultHolograms != null) {
            final String[] split = defaultHolograms.split(",");
            for (int i = 0; i < split.length; i++) {
                if (split[i].equalsIgnoreCase(pluginName)) {
                    return split.length - i;
                }
            }
        }
        return 0;
    }

    /**
     * Creates a wrapped hologram using a hooked hologram plugin.
     *
     * @param location Location of where the hologram should be spawned.
     * @return The hologram.
     */
    public BetonHologram createHologram(final Location location) {
        return hologramFactory.createHologram(location);
    }

    /**
     * Parses a string containing a placeholder and converts it to the appropriate format for the given
     * plugin implementation.
     *
     * @param pack the quest pack where the placeholder resides.
     * @param text the raw text.
     * @return the parsed and formatted full string.
     */
    public String parsePlaceholder(final QuestPackage pack, final String text) {
        return hologramFactory.parsePlaceholder(pack, text);
    }

    private void load(final BetonQuestApi api) {
        final BetonQuest plugin = BetonQuest.getInstance();
        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        final TextParser textParser = plugin.getComponentLoader().get(TextParser.class);
        final Connector connector = plugin.getComponentLoader().get(Connector.class);
        final ProcessorDataLoader processorDataLoader = plugin.getComponentLoader().get(ProcessorDataLoader.class);
        final IdentifierFactory<HologramIdentifier> hologramIdentifierFactory = new HologramIdentifierFactory(api.packages());
        api.identifiers().register(HologramIdentifier.class, hologramIdentifierFactory);
        this.locationHologramLoop = new LocationHologramLoop(loggerFactory, loggerFactory.create(LocationHologramLoop.class),
                connector, api.instructions(), hologramIdentifierFactory, plugin.getPluginConfig(),
                this, plugin, textParser, api.conditions().manager(), api.profiles());
        processorDataLoader.addProcessor(locationHologramLoop);
        this.npcHologramLoop = new NpcHologramLoop(loggerFactory, loggerFactory.create(NpcHologramLoop.class),
                connector, api.instructions(), plugin, this, plugin.getPluginConfig(),
                hologramIdentifierFactory, api.conditions().manager(), api.npcs().manager(), api.npcs().registry(), textParser, api.profiles());
        processorDataLoader.addProcessor(npcHologramLoop);
        api.bukkit().registerEvents(new HologramListener(api.profiles()));
        api.reloader().register(ReloadPhase.INTEGRATION, HologramRunner::cancel);
    }

    /**
     * Disables the hologram loops when BetonQuest is shutting down.
     */
    public void disable() {
        HologramRunner.cancel();
        locationHologramLoop.clear();
        npcHologramLoop.close();
    }

    /**
     * A listener class for bukkit events that holograms use.
     */
    public static class HologramListener implements Listener {

        /**
         * The profile provider instance.
         */
        private final ProfileProvider profileProvider;

        /**
         * Creates a new HologramListener.
         *
         * @param profileProvider the profile provider instance
         */
        public HologramListener(final ProfileProvider profileProvider) {
            this.profileProvider = profileProvider;
        }

        /**
         * Refreshes Holograms when a player joins the server.
         *
         * @param event The event.
         */
        @EventHandler
        public void onPlayerJoin(final PlayerJoinEvent event) {
            HologramRunner.refresh(profileProvider.getProfile(event.getPlayer()));
        }
    }
}
