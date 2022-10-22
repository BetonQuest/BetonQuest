package org.betonquest.betonquest.compatibility.holograms;

import lombok.CustomLog;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensHologram;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Location;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class HologramIntegrator implements Integrator {
    /**
     * Pattern to match a instruction variable in string
     */
    public static final Pattern VARIABLE_VALIDATOR = Pattern.compile("(%|\\$)[^ %\\s]+(%|\\$)");
    /**
     * Pattern to match a global variable in string
     */
    public static final Pattern STATIC_VARIABLE_VALIDATOR = Pattern.compile("\\$[^ %\\s]+\\$");
    private static HologramIntegrator instance;
    private final Map<String, HologramSubIntegrator> integrators;
    private HologramLoop hologramLoop;
    private HologramSubIntegrator subIntegrator;

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    public HologramIntegrator(final HologramSubIntegrator... integrators) {
        this.instance = this;
        this.integrators = new HashMap<>();
        for (final HologramSubIntegrator integrator : integrators) {
            this.integrators.put(integrator.getPluginName(), integrator);
        }
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
            final Constructor<? extends BetonHologram> constructor = instance.subIntegrator.getHologramType().getConstructor(String.class, Location.class);
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
    public static String parseVariable(final QuestPackage pack, final String text) {
        return instance.subIntegrator.parseVariable(pack, text);
    }

    public Set<String> getSubIntegratorNames() {
        return this.integrators.keySet();
    }

    @Override
    public void hook(final String pluginName) throws HookException {
        //This method may be called multiple times if multiple Hologram plugins are installed
        if (subIntegrator != null) {
            return;
        }
        //If not initialised
        this.subIntegrator = integrators.get(pluginName); //assert that the passed in pluginName is in the integrator
        this.subIntegrator.init();
        hologramLoop = new HologramLoop();

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

}
