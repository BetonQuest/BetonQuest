package org.betonquest.betonquest.compatibility.skript;

import ch.njol.skript.Skript;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.Integrator;

/**
 * Integrator for the Skript plugin.
 */
public class SkriptIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public SkriptIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        Skript.registerCondition(SkriptConditionBQ.class, "%player% (meet|meets) [betonquest] condition %string%");
        Skript.registerEffect(SkriptEffectBQ.class, "fire [betonquest] event %string% for %player%");
        Skript.registerEvent("betonquest", SkriptEventBQ.class, BQSkriptAction.CustomEventForSkript.class,
                "[betonquest] event %string%");

        api.getQuestRegistries().action().register("skript", new BQSkriptActionFactory());
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
