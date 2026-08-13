package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.dependency.CoreComponentLoader;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;
import org.betonquest.betonquest.quest.action.hologram.UpdateHologramActionFactory;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

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

    /**
     * The current listener.
     */
    private HologramListener listener;

    private HologramProvider(final BetonQuestApi betonQuestApi, final ConfigAccessor config, final BetonHologramFactory integration) {
        this.hologramFactory = integration;
        load(betonQuestApi, config);
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
        return new HologramProvider(betonQuestApi, config, factory);
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
     * Registers dummy processors that warn when trying to load holograms without a provider.
     *
     * @param betonQuestApi the {@link BetonQuestApi} instance providing access to BetonQuest's API
     */
    public static void createDummyProcessors(final BetonQuestApi betonQuestApi) {
        final ProcessorDataLoader processorDataLoader = BetonQuest.getInstance().getComponentLoader().get(ProcessorDataLoader.class);
        final BetonQuestLogger logger = betonQuestApi.loggerFactory().create(HologramProvider.class);
        processorDataLoader.addProcessor(new ThrowingQuestProcessor(logger, "Hologram", "holograms"));
        processorDataLoader.addProcessor(new ThrowingQuestProcessor(logger, "NPC Hologram", "npc_holograms"));
        betonQuestApi.actions().registry().register("updateHologram", (PlayerQuestFactory<PlayerAction>) instruction -> profile -> {
            throw new QuestException("Tried to update hologram, but there is no hologram integration loaded!");
        });
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

    private void load(final BetonQuestApi api, final ConfigAccessor config) {
        final CoreComponentLoader componentLoader = BetonQuest.getInstance().getComponentLoader();
        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        final TextParser textParser = componentLoader.get(TextParser.class);
        final Connector connector = componentLoader.get(Connector.class);
        final ProcessorDataLoader processorDataLoader = componentLoader.get(ProcessorDataLoader.class);
        final IdentifierFactory<HologramIdentifier> hologramIdentifierFactory = new HologramIdentifierFactory(api.packages());
        api.identifiers().register(HologramIdentifier.class, hologramIdentifierFactory);
        this.locationHologramLoop = new LocationHologramLoop(loggerFactory, loggerFactory.create(LocationHologramLoop.class),
                connector, api.instructions(), hologramIdentifierFactory, config,
                this, api.bukkit().plugin(), textParser, api.conditions().manager(), api.profiles());
        processorDataLoader.addProcessor(locationHologramLoop);
        this.npcHologramLoop = new NpcHologramLoop(loggerFactory, loggerFactory.create(NpcHologramLoop.class),
                connector, api.instructions(), api.bukkit().plugin(), this, config,
                hologramIdentifierFactory, api.conditions().manager(), api.npcs().manager(), api.npcs().registry(), textParser, api.profiles());
        processorDataLoader.addProcessor(npcHologramLoop);
        this.listener = new HologramListener(api.profiles(), npcHologramLoop);
        api.bukkit().registerEvents(listener);
        api.reloader().register(ReloadPhase.INTEGRATION, HologramRunner::cancel);

        api.actions().registry().register("updateHologram", new UpdateHologramActionFactory(locationHologramLoop, npcHologramLoop));
    }

    /**
     * Disables the hologram loops when BetonQuest is shutting down.
     */
    public void disable() {
        HologramRunner.cancel();
        locationHologramLoop.clear();
        npcHologramLoop.close();
        HandlerList.unregisterAll(listener);
    }

    /**
     * QuestProcessor to warn when a hologram section is found but no integration is loaded.
     */
    private static final class ThrowingQuestProcessor extends QuestProcessor<Identifier, Object> {

        private ThrowingQuestProcessor(final BetonQuestLogger log, final String readable, final String internal) {
            super(log, (source, input) -> {
                throw new QuestException("Dummy factory");
            }, readable, internal);
        }

        @Override
        public void load(final QuestPackage pack) {
            if (pack.getConfig().isConfigurationSection(internal)) {
                log.warn("Tried to load section '%s' in pack '%s', but there is no hologram integration loaded!"
                        .formatted(internal, pack.getQuestPath()));
            }
        }
    }
}
