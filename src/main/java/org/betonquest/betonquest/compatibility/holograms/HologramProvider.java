package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensHologramLoop;
import org.betonquest.betonquest.exception.HookException;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Singleton class which provides Hologram
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public final class HologramProvider implements Integrator {
    /**
     * Pattern to match an instruction variable in string
     */
    public static final Pattern VARIABLE_VALIDATOR = Pattern.compile("%[^ %\\s]+%");

    /**
     * HologramIntegrators when 'hooked' add themselves to this list
     */
    private static final List<HologramIntegrator> ATTEMPTED_INTEGRATIONS = new ArrayList<>();

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(HologramProvider.class);

    /**
     * Singleton instance of this HologramProvider, only ever null if not initialised.
     */
    @Nullable
    private static HologramProvider instance;

    /**
     * The currently hooked integrator, this may change during runtime during a reload
     */
    private HologramIntegrator integrator;

    /**
     * The current {@link LocationHologramLoop}.
     */
    @Nullable
    private LocationHologramLoop locationHologramLoop;

    /**
     * The current {@link CitizensHologramLoop}.
     */
    @Nullable
    private CitizensHologramLoop citizensHologramLoop;

    /**
     * Creates a new HologramProvider object and assigns it to singleton instance if not already
     *
     * @param integrator The initial integrator to hook into
     */
    private HologramProvider(final HologramIntegrator integrator) {
        this.integrator = integrator;
    }

    /**
     * Adds a possible integrator for this provider
     *
     * @param integrator The integrator itself
     */
    public static void addIntegrator(final HologramIntegrator integrator) {
        ATTEMPTED_INTEGRATIONS.add(integrator);
    }

    /**
     * Called only once after all plugins have been hooked as to allow HologramIntegrators to add themselves to this
     * provider's {@link #ATTEMPTED_INTEGRATIONS} list.
     */
    public static void init() {
        synchronized (HologramProvider.class) {
            if (instance == null && !ATTEMPTED_INTEGRATIONS.isEmpty()) {
                Collections.sort(ATTEMPTED_INTEGRATIONS);
                instance = new HologramProvider(ATTEMPTED_INTEGRATIONS.get(0));
                try {
                    instance.hook();
                    LOG.info("Using " + ATTEMPTED_INTEGRATIONS.get(0).getPluginName() + " as dedicated hologram provider!");
                } catch (final HookException ignored) {
                    instance.close(); //Close the hologramLoop if it was partly initialised
                    instance = null;
                }
            }
        }
    }

    /**
     * Get an instance of this HologramProvider.
     *
     * @return An instance of Hologram Provider
     * @throws IllegalStateException Thrown if this method has been used at the incorrect time
     */
    public static HologramProvider getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Cannot getInstance() when HologramProvider has not been initialised yet!");
        }
        return instance;
    }

    /**
     * @param pluginName The name of the plugin to check
     * @return True if plugin is currently hooked to this provider
     * @throws IllegalStateException Thrown if this method has been used at the incorrect time
     */
    public boolean isHooked(final String pluginName) {
        if (this.integrator == null) {
            throw new IllegalStateException("Cannot isHooked() when HologramProvider has not been fully initialised yet!");
        }
        return this.integrator.getPluginName().equalsIgnoreCase(pluginName);
    }

    /**
     * Creates a wrapped hologram using a hooked hologram plugin
     *
     * @param location Location of where the hologram should be spawned
     * @return The hologram
     */
    public BetonHologram createHologram(final Location location) {
        return integrator.createHologram(location);
    }

    /**
     * Parses a string containing an instruction variable and converts it to the appropriate format for the given
     * plugin implementation
     *
     * @param pack The quest pack where the variable resides
     * @param text The raw text
     * @return The parsed and formatted full string
     */
    public String parseVariable(final QuestPackage pack, final String text) {
        return integrator.parseVariable(pack, text);
    }

    @Override
    public void hook() throws HookException {
        final BetonQuestLoggerFactory loggerFactory = BetonQuest.getInstance().getLoggerFactory();
        this.locationHologramLoop = new LocationHologramLoop(loggerFactory, loggerFactory.create(LocationHologramLoop.class));
        if (Compatibility.getHooked().contains("Citizens")) {
            this.citizensHologramLoop = new CitizensHologramLoop(loggerFactory, loggerFactory.create(CitizensHologramLoop.class));
        }
        new HologramListener();
    }

    @Override
    public void reload() {
        synchronized (HologramProvider.class) {
            if (instance != null && instance.locationHologramLoop != null) {
                HologramRunner.cancel();

                Collections.sort(ATTEMPTED_INTEGRATIONS);

                instance.integrator = ATTEMPTED_INTEGRATIONS.get(0);
                final BetonQuestLoggerFactory loggerFactory = BetonQuest.getInstance().getLoggerFactory();
                instance.locationHologramLoop = new LocationHologramLoop(loggerFactory, loggerFactory.create(LocationHologramLoop.class));
                if (instance.citizensHologramLoop != null) {
                    instance.citizensHologramLoop.close();
                    instance.citizensHologramLoop = new CitizensHologramLoop(loggerFactory, loggerFactory.create(CitizensHologramLoop.class));
                }
            }
        }
    }

    @Override
    public void close() {
        synchronized (HologramProvider.class) {
            if (instance != null && instance.locationHologramLoop != null) {
                HologramRunner.cancel();
                instance.locationHologramLoop = null;
                if (instance.citizensHologramLoop != null) {
                    instance.citizensHologramLoop.close();
                    instance.citizensHologramLoop = null;
                }
            }
        }
    }

    /**
     * A listener class for bukkit events that are used by holograms
     */
    public static class HologramListener implements Listener {
        /**
         * Creates and registers a new HologramListener
         */
        public HologramListener() {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        /**
         * Called when a player joins the server
         *
         * @param event The event
         */
        @EventHandler
        public void onPlayerJoin(final PlayerJoinEvent event) {
            HologramRunner.refresh(PlayerConverter.getID(event.getPlayer()));
        }
    }
}
