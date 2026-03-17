package org.betonquest.betonquest.compatibility.traincarts;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.compatibility.traincarts.conditions.TrainCartsRideConditionFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsExitObjectiveFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsLocationObjectiveFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsRideObjectiveFactory;

/**
 * The TrainCarts integration.
 */
public class TrainCartsIntegrator implements Integration {

    /**
     * Create the TrainCarts integration.
     */
    public TrainCartsIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final ObjectiveRegistry objectiveRegistry = api.objectives().registry();
        objectiveRegistry.register("traincartslocation", new TrainCartsLocationObjectiveFactory());
        objectiveRegistry.register("traincartsride", new TrainCartsRideObjectiveFactory());
        objectiveRegistry.register("traincartsexit", new TrainCartsExitObjectiveFactory());

        api.conditions().registry().register("traincartsride", new TrainCartsRideConditionFactory());
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
