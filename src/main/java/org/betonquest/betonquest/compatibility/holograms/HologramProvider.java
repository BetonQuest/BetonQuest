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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;


@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class HologramProvider implements Integrator {
    /**
     * Pattern to match an instruction variable in string
     */
    public static final Pattern VARIABLE_VALIDATOR = Pattern.compile("(%|\\$)[^ %\\s]+(%|\\$)");

    private static HologramProvider instance;
    private final HologramIntegrator integrator;
    private HologramLoop hologramLoop;

    public HologramProvider(final HologramIntegrator integrator) {
        this.integrator = integrator;
    }

    /**
     * Initialise the static instance if it hasn't already been initialised
     *
     * @param integrator The integrator to hook
     * @return True if this was the first initialisation of the HologramProvider, false if it wasn't
     */
    @SuppressWarnings("PMD.NonThreadSafeSingleton")
    public static boolean initialise(final HologramIntegrator integrator) {
        if (instance == null) {
            instance = new HologramProvider(integrator);
            return true;
        }
        return false;
    }

    public static HologramProvider getInstance() {
        if (instance == null) {
            throw new IllegalArgumentException("Cannot getInstance() from HologramProvider that has not been initialised!");
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
        BetonHologram hologram = null;
        try {
            final Constructor<? extends BetonHologram> constructor = integrator.getHologramType().getConstructor(String.class, Location.class);
            hologram = constructor.newInstance(name + location.toString(), location);
        } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            LOG.warn("Hologram called " + name + " could not be created! This is most likely an implementation error! ", e);
        } catch (final InvocationTargetException e) {
            LOG.warn("Hologram called " + name + " could not be created due to an exception thrown by it's constructor!", e);
        }
        return hologram;
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
    public void hook(final String pluginName) throws HookException {
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
            hologramLoop.cancel();
        }
    }

    @SuppressWarnings("PMD.CommentRequired")
    public class HologramListener implements Listener {
        public HologramListener() {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        @EventHandler
        public void onPlayerJoin(final PlayerJoinEvent event) {
            if (hologramLoop != null) {
                hologramLoop.refresh(event.getPlayer());
            }
        }
    }

}
