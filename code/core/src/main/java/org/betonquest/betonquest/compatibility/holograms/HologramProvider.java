package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.api.text.TextParser;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class which provides Hologram creation.
 */
public class HologramProvider {

    /**
     * Pattern to match a placeholder in a string.
     */
    public static final Pattern PLACEHOLDER_VALIDATOR = Pattern.compile("%[^ %\\s]+%");

    /**
     * The hooked integrator.
     */
    private final HologramIntegrator integrator;

    /**
     * The current {@link LocationHologramLoop}.
     */
    private LocationHologramLoop locationHologramLoop;

    /**
     * The current {@link NpcHologramLoop}.
     */
    private NpcHologramLoop npcHologramLoop;

    private HologramProvider(final BetonQuestApi betonQuestApi, final HologramIntegrator integration) {
        this.integrator = integration;
        load(betonQuestApi);
    }

    /**
     * Creates a new HologramProvider from hooked {@link HologramIntegrator}.
     *
     * @param betonQuestApi the {@link BetonQuestApi} instance providing access to BetonQuest's API
     * @param integrations  The list of integrators to consider.
     * @return the initialized provider or null, if there are no integrations given
     */
    @Nullable
    public static HologramProvider init(final BetonQuestApi betonQuestApi, final List<HologramIntegrator> integrations) {
        Collections.sort(integrations);
        if (integrations.isEmpty()) {
            return null;
        }
        return new HologramProvider(betonQuestApi, integrations.get(0));
    }

    /**
     * Creates a wrapped hologram using a hooked hologram plugin.
     *
     * @param location Location of where the hologram should be spawned.
     * @return The hologram.
     */
    public BetonHologram createHologram(final Location location) {
        return integrator.createHologram(location);
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
        return integrator.parsePlaceholder(pack, text);
    }

    private void load(final BetonQuestApi api) {
        final BetonQuest plugin = BetonQuest.getInstance();
        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        final TextParser textParser = plugin.getComponentLoader().get(TextParser.class);
        final IdentifierFactory<HologramIdentifier> hologramIdentifierFactory = new HologramIdentifierFactory(api.packages());
        api.identifiers().register(HologramIdentifier.class, hologramIdentifierFactory);
        this.locationHologramLoop = new LocationHologramLoop(loggerFactory, loggerFactory.create(LocationHologramLoop.class),
                api.instructions(), hologramIdentifierFactory, plugin.getPluginConfig(),
                this, plugin, textParser, api.conditions().manager(), api.profiles());
        plugin.addProcessor(locationHologramLoop);
        this.npcHologramLoop = new NpcHologramLoop(loggerFactory, loggerFactory.create(NpcHologramLoop.class),
                api.instructions(), plugin, this, plugin.getPluginConfig(),
                hologramIdentifierFactory, api.conditions().manager(), api.npcs().manager(), api.npcs().registry(), textParser, api.profiles());
        plugin.addProcessor(npcHologramLoop);
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
