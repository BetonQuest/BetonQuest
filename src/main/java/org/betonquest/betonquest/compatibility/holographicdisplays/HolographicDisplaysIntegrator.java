package org.betonquest.betonquest.compatibility.holographicdisplays;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensHologram;


@SuppressWarnings("PMD.CommentRequired")
public class HolographicDisplaysIntegrator implements Integrator {

    private static HolographicDisplaysIntegrator instance;
    private HologramLoop hologramLoop;

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
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
