package org.betonquest.betonquest.compatibility.holograms;

import lombok.CustomLog;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensHologram;
import org.bukkit.Location;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;


@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class HologramIntegrator implements Integrator {

    private static HologramIntegrator instance;
    private final Map<String, Class<? extends BetonHologram>> implementations;
    private HologramLoop hologramLoop;
    private Class<? extends BetonHologram> hologramType;

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    public HologramIntegrator(final Map<String, Class<? extends BetonHologram>> implementations) {
        this.instance = this;
        this.implementations = implementations;
    }


    /**
     * Creates a wrapped hologram using a hooked hologram plugin
     *
     * @param name     Name of the hologram (Not always applicable)
     * @param location Location of where the hologram should be spawned
     * @return The hologram
     */
    public static BetonHologram createHologram(final String name, final Location location) {
        BetonHologram hologram = null;
        try {
            final Constructor<? extends BetonHologram> constructor = instance.hologramType.getConstructor(String.class, Location.class);
            hologram = constructor.newInstance(name + location.toString(), location);
        } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            LOG.warn("Hologram called " + name + " could not be created! This is most likely an implementation error! ", e);
        } catch (final InvocationTargetException e) {
            LOG.warn("Hologram called " + name + " could not be created due to a constructor exception!", e);
        }
        return hologram;
    }

    @Override
    public void hook(final String pluginName) {
        //This method may be called multiple times if multiple Hologram plugins are installed
        if (hologramType == null) {
            //If not initialised
            hologramType = implementations.get(pluginName);
            hologramLoop = new HologramLoop();

            // if Citizens is hooked, start CitizensHologram
            if (Compatibility.getHooked().contains("Citizens")) {
                new CitizensHologram();
            }
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

}
