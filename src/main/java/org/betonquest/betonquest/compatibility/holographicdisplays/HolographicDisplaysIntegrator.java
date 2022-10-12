package org.betonquest.betonquest.compatibility.holographicdisplays;

import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensHologram;

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
    public void hook() {
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
