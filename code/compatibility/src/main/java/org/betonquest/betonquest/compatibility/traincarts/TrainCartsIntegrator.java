package org.betonquest.betonquest.compatibility.traincarts;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.traincarts.conditions.TrainCartsRideConditionFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsExitObjectiveFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsLocationObjectiveFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsRideObjectiveFactory;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * The TrainCarts integration.
 */
public class TrainCartsIntegrator extends IntegrationTemplate {

    /**
     * Create the TrainCarts integration.
     */
    public TrainCartsIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        objective("location", new TrainCartsLocationObjectiveFactory());
        objective("ride", new TrainCartsRideObjectiveFactory());
        objective("exit", new TrainCartsExitObjectiveFactory());

        playerCondition("ride", new TrainCartsRideConditionFactory());

        registerFeatures(api, "traincarts");
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
