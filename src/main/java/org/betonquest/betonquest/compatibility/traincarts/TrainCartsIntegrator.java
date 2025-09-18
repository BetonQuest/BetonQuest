package org.betonquest.betonquest.compatibility.traincarts;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.kernel.FeatureTypeRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.traincarts.conditions.TrainCartsRideConditionFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsExitObjectiveFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsLocationObjectiveFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsRideObjectiveFactory;
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
    public void hook() {
        final FeatureTypeRegistry<Objective> objectiveRegistry = plugin.getQuestRegistries().objective();
        objectiveRegistry.register("traincartslocation", new TrainCartsLocationObjectiveFactory());
        objectiveRegistry.register("traincartsride", new TrainCartsRideObjectiveFactory());
        objectiveRegistry.register("traincartsexit", new TrainCartsExitObjectiveFactory());

        final PrimaryServerThreadData data = new PrimaryServerThreadData(plugin.getServer(), plugin.getServer().getScheduler(), plugin);
        plugin.getQuestRegistries().condition().register("traincartsride",
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
