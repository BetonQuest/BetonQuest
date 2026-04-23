package org.betonquest.betonquest.compatibility.skript;

import ch.njol.skript.Skript;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;

/**
 * Integrator for the Skript plugin.
 */
public class SkriptIntegrator implements Integration {

    /**
     * The minimum required version of Skript.
     */
    public static final String REQUIRED_VERSION = "5.0.0";

    /**
     * The default constructor.
     */
    public SkriptIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) {
        Skript.registerCondition(SkriptConditionBQ.class, "%player% (meet|meets) [betonquest] condition %string%");
        Skript.registerEffect(SkriptEffectBQ.class, "fire [betonquest] action %string% for %player%");
        Skript.registerEvent("betonquest", SkriptEventBQ.class, BQSkriptAction.CustomEventForSkript.class,
                "[betonquest] action %string%");

        api.actions().registry().register("skript", new BQSkriptActionFactory());
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
