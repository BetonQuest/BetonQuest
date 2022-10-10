package org.betonquest.betonquest.compatibility.holograms;

import lombok.CustomLog;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensHologram;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.UnsupportedVersionException;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

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
            LOG.warn("Hologram called " + name + " could not be created due to an exception thrown by it's constructor!", e);
        }
        return hologram;
    }

    @Override
    public void hook(final String pluginName) throws HookException {
        //This method may be called multiple times if multiple Hologram plugins are installed
        if (hologramType == null) {
            //If not initialised
            if ("DecentHolograms".equalsIgnoreCase(pluginName)) {
                validateVersion(pluginName, "2.7.3");
            }
            hologramType = implementations.get(pluginName);
            hologramLoop = new HologramLoop();

            // if Citizens is hooked, start CitizensHologram
            if (Compatibility.getHooked().contains("Citizens")) {
                new CitizensHologram();
            }
        }
    }

    private void validateVersion(final String pluginName, final String requiredVersion) throws UnsupportedVersionException {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin != null) {
            final Version version = new Version(plugin.getDescription().getVersion());
            final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR);
            if (comparator.isOtherNewerThanCurrent(version, new Version(requiredVersion))) {
                throw new UnsupportedVersionException(plugin, requiredVersion);
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
