package pl.betoncraft.betonquest.compatibility.holographicdisplays;

import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.compatibility.citizens.CitizensHologram;


@SuppressWarnings("PMD.CommentRequired")
public class HolographicDisplaysIntegrator implements Integrator {

    private static HolographicDisplaysIntegrator instance;
    private HologramLoop hologramLoop;

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
