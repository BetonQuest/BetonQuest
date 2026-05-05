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
     * The minimum required version of TrainCarts.
     */
    public static final String REQUIRED_VERSION = "1.20.6-v1";

    /**
     * Create the TrainCarts integration.
     */
    public TrainCartsIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        objective("location", new TrainCartsLocationObjectiveFactory());
        objective("ride", new TrainCartsRideObjectiveFactory(api.bukkit().plugin()));
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
