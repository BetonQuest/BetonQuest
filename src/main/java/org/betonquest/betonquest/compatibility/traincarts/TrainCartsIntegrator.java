package org.betonquest.betonquest.compatibility.traincarts;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.traincarts.conditions.TrainCartsRideConditionFactory;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsExitObjective;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsLocationObjective;
import org.betonquest.betonquest.compatibility.traincarts.objectives.TrainCartsRideObjective;
import org.betonquest.betonquest.exception.HookException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.registry.type.ObjectiveTypeRegistry;

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
        final ObjectiveTypeRegistry objectiveTypes = plugin.getQuestRegistries().objective();
        objectiveTypes.register("traincartslocation", TrainCartsLocationObjective.class);
        objectiveTypes.register("traincartsride", TrainCartsRideObjective.class);
        objectiveTypes.register("traincartsexit", TrainCartsExitObjective.class);

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
