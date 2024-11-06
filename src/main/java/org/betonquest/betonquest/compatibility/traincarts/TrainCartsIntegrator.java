package org.betonquest.betonquest.compatibility.traincarts;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.traincarts.conditions.TrainCartsRideConditionFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsExitObjective;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsLocationObjective;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsRideObjective;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;

/**
 * The TrainCarts integration.
 */
public class TrainCartsIntegrator implements Integrator {
    /**
     * The instance of {@link BetonQuest}.
     */
    private final BetonQuest plugin;

    /**
     * Create the TrainCarts integration.
     */
    public TrainCartsIntegrator() {
        this.plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() throws HookException {
        plugin.registerObjectives("traincartslocation", TrainCartsLocationObjective.class);
        plugin.registerObjectives("traincartsride", TrainCartsRideObjective.class);
        plugin.registerObjectives("traincartsexit", TrainCartsExitObjective.class);

        final PrimaryServerThreadData data = new PrimaryServerThreadData(plugin.getServer(), plugin.getServer().getScheduler(), plugin);
        plugin.getQuestRegistries().getConditionTypes().register("traincartsride",
                new TrainCartsRideConditionFactory(plugin.getLoggerFactory(), data));
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
