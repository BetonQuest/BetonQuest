package org.betonquest.betonquest.compatibility.holograms;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensHologram;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class HologramProvider implements Integrator {
    /**
     * Pattern to match an instruction variable in string
     */
    public static final Pattern VARIABLE_VALIDATOR = Pattern.compile("%[^ %\\s]+%");
    protected static List<HologramIntegrator> attemptedIntegrations = new ArrayList<>();
    private static HologramProvider instance;
    private final HologramIntegrator integrator;
    private HologramLoop hologramLoop;

    /**
     * Create a new HologramProvider hooked to a given integrator
     *
     * @param integrator The integrator to use
     */
    public HologramProvider(final HologramIntegrator integrator) {
        this.integrator = integrator;
    }

    /**
     * Add a possible HologramIntegrator to this provider
     *
     * @param integrator The integrator to hook
     */
    public static void addIntegrator(final HologramIntegrator integrator) {
        attemptedIntegrations.add(integrator);
    }

    /**
     * Called after all plugins have been hooked as to allow HologramIntegrators to add themselves to this provider's
     * {@link #attemptedIntegrations} list.
     */
    public static void init() {
        if (instance == null && !attemptedIntegrations.isEmpty()) {
            Collections.sort(attemptedIntegrations);
            instance = new HologramProvider(attemptedIntegrations.get(0));
            try {
                instance.hook();
                LOG.info("Using " + attemptedIntegrations.get(0).getPluginName() + " as dedicated Hologram provider!");
            } catch (final HookException ignored) {
                instance.close(); //Close the hologramLoop if it was partly initialised
                instance = null;
            }
        }
    }

    /**
     * Get an instance of this HologramProvider.
     *
     * @return An instance of Hologram Provider
     * @throws IllegalArgumentException If HologramProvider has been improperly used
     */
    public static HologramProvider getInstance() {
        if (instance == null) {
            throw new IllegalArgumentException("Cannot getInstance() when HologramProvider has not been initialised yet!");
        }
        return instance;
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
        if (instance.hologramLoop != null) {
            instance.hologramLoop.cancel();
            instance.hologramLoop = new HologramLoop();
        }
    }

    @Override
    public void close() {
        if (instance.hologramLoop != null) {
            instance.hologramLoop.cancel();
            instance.hologramLoop = null;
        }
    }

    @SuppressWarnings("PMD.CommentRequired")
    public class HologramListener implements Listener {
        public HologramListener() {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        @EventHandler
        public void onPlayerJoin(final PlayerJoinEvent event) {
            if (instance != null && instance.hologramLoop != null) {
                instance.hologramLoop.refresh(event.getPlayer());
            }
        }
    }

}
