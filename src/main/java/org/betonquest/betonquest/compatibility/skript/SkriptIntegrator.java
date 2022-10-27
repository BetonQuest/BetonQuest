package org.betonquest.betonquest.compatibility.skript;

import ch.njol.skript.Skript;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;


@SuppressWarnings("PMD.CommentRequired")
public class SkriptIntegrator implements Integrator {

    private final BetonQuest plugin;

    public SkriptIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        Skript.registerCondition(SkriptConditionBQ.class, "%player% (meet|meets) [betonquest] condition %string%");
        Skript.registerEffect(SkriptEffectBQ.class, "fire [betonquest] event %string% for %player%");
        Skript.registerEvent("betonquest", SkriptEventBQ.class, BQEventSkript.CustomEventForSkript.class,
                "[betonquest] event %string%");
        plugin.registerEvents("skript", BQEventSkript.class);
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }

}
