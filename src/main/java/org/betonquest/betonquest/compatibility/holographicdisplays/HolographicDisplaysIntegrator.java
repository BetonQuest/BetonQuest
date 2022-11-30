package org.betonquest.betonquest.compatibility.holographicdisplays;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensHologram;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.UnsupportedVersionException;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for HolographicDisplays API
 */
public class HolographicDisplaysIntegrator implements Integrator {

    /**
     * Instance of the HolographicDisplaysIntegrator
     */
    private static HolographicDisplaysIntegrator instance;

    /**
     * Instance of the {@link HologramLoop}
     */
    private HologramLoop hologramLoop;

    /**
     * Creates a new instance of the HolographicDisplaysIntegrator
     */
    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    public HolographicDisplaysIntegrator() {
        instance = this;
    }

    @Override
    public void hook() throws HookException {
        validateVersion();

        hologramLoop = new HologramLoop();

        HolographicDisplaysAPI.get(BetonQuest.getInstance()).registerIndividualPlaceholder("bq", new HologramPlaceholder());
        HolographicDisplaysAPI.get(BetonQuest.getInstance()).registerGlobalPlaceholder("bqg", new HologramGlobalPlaceholder());

        // if Citizens is hooked, start CitizensHologram
        if (Compatibility.getHooked().contains("Citizens")) {
            new CitizensHologram();
        }
    }

    /**
     * Aborts the hooking process if the installed version of HolographicDisplays is invalid.
     *
     * @throws UnsupportedVersionException if the installed version of HolographicDisplays is < 3.0.0.
     */
    private void validateVersion() throws UnsupportedVersionException {
        final Plugin holographicDisplays = Bukkit.getPluginManager().getPlugin("HolographicDisplays");
        final Version holographicDisplaysVersion = new Version(holographicDisplays.getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR);
        if (comparator.isOtherNewerThanCurrent(holographicDisplaysVersion, new Version("3.0.0+"))) {
            throw new UnsupportedVersionException(holographicDisplays, "5.0.0");
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
