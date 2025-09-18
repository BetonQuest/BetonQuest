package org.betonquest.betonquest.compatibility.skript;

import ch.njol.skript.Skript;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;

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
        Skript.registerEvent("betonquest", SkriptEventBQ.class, BQEventSkript.CustomEventForSkript.class,
                "[betonquest] event %string%");

        final BetonQuest plugin = BetonQuest.getInstance();
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        plugin.getQuestRegistries().event().register("skript", new BQEventSkriptFactory(data));
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
