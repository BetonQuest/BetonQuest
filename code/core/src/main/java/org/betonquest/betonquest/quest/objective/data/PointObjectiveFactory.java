package org.betonquest.betonquest.quest.objective.data;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerUpdatePointEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.condition.number.Operation;

/**
 * Factory to create {@link PointObjective}s from {@link Instruction}s.
 */
public class PointObjectiveFactory implements ObjectiveFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Create a new Point Objective Factory.
     *
     * @param playerDataStorage the storage for player data
     */
    public PointObjectiveFactory(final PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<String> category = instruction.packageIdentifier().get();
        final Argument<Number> targetAmount = instruction.number().get();
        final Argument<CountingMode> mode = instruction.enumeration(CountingMode.class).get("mode", CountingMode.TOTAL);
        final Argument<Operation> operation = instruction.parse(Operation::fromSymbol).get("operation", Operation.GREATER_EQUAL);
        final PointObjective objective = new PointObjective(service, playerDataStorage, category, targetAmount, mode, operation);
        service.request(PlayerUpdatePointEvent.class).handler(objective::onPointUpdate)
                .profile(PlayerUpdatePointEvent::getProfile).subscribe(false);
        service.request(PlayerObjectiveChangeEvent.class).handler(objective::onStart)
                .profile(PlayerObjectiveChangeEvent::getProfile).subscribe(false);
        return objective;
    }
}
