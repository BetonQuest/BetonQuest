package org.betonquest.betonquest.quest.objective.equip;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;

/**
 * Factory for creating {@link EquipItemObjective} instances from {@link Instruction}s.
 */
public class EquipItemObjectiveFactory implements ObjectiveFactory {
    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the EquipItemObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public EquipItemObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final PlayerArmorChangeEvent.SlotType slotType = instruction.getEnum(PlayerArmorChangeEvent.SlotType.class);
        final Item item = instruction.getItem();
        final BetonQuestLogger log = loggerFactory.create(EquipItemObjective.class);
        return new EquipItemObjective(instruction, log, item, slotType);
    }
}
