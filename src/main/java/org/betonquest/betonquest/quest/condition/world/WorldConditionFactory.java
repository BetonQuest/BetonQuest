package org.betonquest.betonquest.quest.condition.world;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Factory to create world conditions from {@link Instruction}s.
 */
public class WorldConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the test for block condition factory.
     *
     * @param loggerFactory the logger factory to create a logger for the condition
     * @param data          the data used for checking the condition on the main thread
     */
    public WorldConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final World world = getWorld(instruction.next(), instruction.getPackage());
        final BetonQuestLogger logger = loggerFactory.create(WorldCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new WorldCondition(world), logger, instruction.getPackage()), data);
    }

    private World getWorld(final String name, final QuestPackage questPackage) throws InstructionParseException {
        final World world = Bukkit.getWorld(name);
        if (world != null) {
            return world;
        }
        try {
            return new VariableLocation(BetonQuest.getInstance().getVariableProcessor(), questPackage, name)
                    .getValue(null).getWorld();
        } catch (InstructionParseException | QuestRuntimeException e) {
            throw new InstructionParseException("There is no such world: " + name, e);
        }
    }
}
