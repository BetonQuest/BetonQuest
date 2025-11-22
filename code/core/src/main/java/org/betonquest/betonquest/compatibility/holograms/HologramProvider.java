package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Integrator;
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
public class HologramProvider implements Integrator {
    /**
     * Pattern to match an instruction variable in string.
     */
    public static final Pattern VARIABLE_VALIDATOR = Pattern.compile("%[^ %\\s]+%");

    /**
     * The hooked integrator.
     */
    @Nullable
    private final HologramIntegrator integrator;

    /**
     * The current {@link LocationHologramLoop}.
     */
    @Nullable
    private LocationHologramLoop locationHologramLoop;

    /**
     * The current {@link NpcHologramLoop}.
     */
    @Nullable
    private NpcHologramLoop npcHologramLoop;

    /**
     * Creates a new HologramProvider from hooked {@link HologramIntegrator}.
     *
     * @param integrations The list of integrators to consider.
     */
    public HologramProvider(final List<HologramIntegrator> integrations) {
        this.integrator = init(integrations);
    }

    @Nullable
    private HologramIntegrator init(final List<HologramIntegrator> integrations) {
        Collections.sort(integrations);
        if (integrations.isEmpty()) {
            return null;
        }
        return integrations.get(0);
    }

    /**
     * Creates a wrapped hologram using a hooked hologram plugin.
     *
     * @param location Location of where the hologram should be spawned.
     * @return The hologram.
     */
    public BetonHologram createHologram(final Location location) {
        if (integrator == null) {
            throw new IllegalStateException("Integrator has not been initialized!");
        }
        return integrator.createHologram(location);
    }

    /**
     * Parses a string containing an instruction variable and converts it to the appropriate format for the given
     * plugin implementation.
     *
     * @param pack The quest pack where the variable resides.
     * @param text The raw text.
     * @return The parsed and formatted full string.
     */
    public String parseVariable(final QuestPackage pack, final String text) {
        if (integrator == null) {
            throw new IllegalStateException("Integrator has not been initialized!");
        }
        return integrator.parseVariable(pack, text);
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final BetonQuest plugin = BetonQuest.getInstance();
        final BetonQuestLoggerFactory loggerFactory = api.getLoggerFactory();
        this.locationHologramLoop = new LocationHologramLoop(loggerFactory, loggerFactory.create(LocationHologramLoop.class),
                api.getQuestPackageManager(), plugin.getVariableProcessor(), this, plugin);
        plugin.addProcessor(locationHologramLoop);
        this.npcHologramLoop = new NpcHologramLoop(loggerFactory, loggerFactory.create(NpcHologramLoop.class),
                plugin.getQuestPackageManager(), plugin, plugin.getVariableProcessor(), this,
                api.getFeatureApi(), api.getFeatureRegistries().npc());
        plugin.addProcessor(npcHologramLoop);
        plugin.getServer().getPluginManager().registerEvents(new HologramListener(api.getProfileProvider()), plugin);
    }

    @Override
    public void reload() {
        HologramRunner.cancel();
    }

    @Override
    public void close() {
        HologramRunner.cancel();
        if (locationHologramLoop != null) {
            locationHologramLoop.clear();
            locationHologramLoop = null;
        }
        if (npcHologramLoop != null) {
            npcHologramLoop.close();
            npcHologramLoop = null;
        }
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
