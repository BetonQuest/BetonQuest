package org.betonquest.betonquest.compatibility.traincarts;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.objective.ObjectiveRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.traincarts.conditions.TrainCartsRideConditionFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsExitObjectiveFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsLocationObjectiveFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsRideObjectiveFactory;

/**
 * The TrainCarts integration.
 */
public class TrainCartsIntegrator implements Integrator {

    /**
     * Create the TrainCarts integration.
     */
    public TrainCartsIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final ObjectiveRegistry objectiveRegistry = api.getQuestRegistries().objective();
        objectiveRegistry.register("traincartslocation", new TrainCartsLocationObjectiveFactory());
        objectiveRegistry.register("traincartsride", new TrainCartsRideObjectiveFactory());
        objectiveRegistry.register("traincartsexit", new TrainCartsExitObjectiveFactory());

        api.getQuestRegistries().condition().register("traincartsride",
                new TrainCartsRideConditionFactory(api.getLoggerFactory()));
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
