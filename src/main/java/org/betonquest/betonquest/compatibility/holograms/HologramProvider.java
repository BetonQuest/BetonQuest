package org.betonquest.betonquest.compatibility.holograms;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensHologram;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Singleton class which provides Hologram
 */
@CustomLog
public class HologramProvider implements Integrator {
    /**
     * Pattern to match an instruction variable in string
     */
    public static final Pattern VARIABLE_VALIDATOR = Pattern.compile("%[^ %\\s]+%");

    /**
     * HologramIntegrators when 'hooked' add themselves to this list
     */
    private static final List<HologramIntegrator> ATTEMPTED_INTEGRATIONS = new ArrayList<>();

    /**
     * Singleton instance of this HologramProvider, only ever null if not initialised.
     */
    private static HologramProvider instance;

    /**
     * The currently hooked integrator, this may change during runtime during a reload
     */
    private HologramIntegrator integrator;

    /**
     * The current hologramLoop
     */
    private HologramLoop hologramLoop;


    /**
     * Creates a new HologramProvider object and assigns it to singleton instance if not already
     *
     * @param integrator The initial integrator to hook into
     */
    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    public HologramProvider(final HologramIntegrator integrator) {
        this.integrator = integrator;
        if (instance == null) {
            instance = this;
        }
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
                new HologramProvider(ATTEMPTED_INTEGRATIONS.get(0));
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
     * @param name     Name of the hologram (Not always applicable)
     * @param location Location of where the hologram should be spawned
     * @return The hologram
     */
    public BetonHologram createHologram(final String name, final Location location) {
        return integrator.createHologram(name, location);
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
        this.hologramLoop = new HologramLoop();
        new HologramListener();

        // if Citizens is hooked, start CitizensHologram
        if (Compatibility.getHooked().contains("Citizens")) {
            new CitizensHologram();
        }
    }

    @Override
    public void reload() {
        synchronized (HologramProvider.class) {
            if (instance.hologramLoop != null) {
                instance.hologramLoop.cancel();

                Collections.sort(ATTEMPTED_INTEGRATIONS);

                instance.integrator = ATTEMPTED_INTEGRATIONS.get(0);
                instance.hologramLoop = new HologramLoop();
                if (Compatibility.getHooked().contains("Citizens")) {
                    CitizensHologram.reload();
                }
            }
        }
    }

    @Override
    public void close() {
        synchronized (HologramProvider.class) {
            if (instance.hologramLoop != null) {
                instance.hologramLoop.cancel();
                instance.hologramLoop = null;
                if (Compatibility.getHooked().contains("Citizens")) {
                    CitizensHologram.close();
                }
            }
        }

    }

    @SuppressWarnings("PMD.CommentRequired")
    public static class HologramListener implements Listener {
        public HologramListener() {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        @EventHandler
        public void onPlayerJoin(final PlayerJoinEvent event) {
            if (instance != null && instance.hologramLoop != null) {
                instance.hologramLoop.refresh(PlayerConverter.getID(event.getPlayer()));
            }
        }
    }

}
