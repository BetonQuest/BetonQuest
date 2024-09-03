package org.betonquest.betonquest.quest.event.spawn;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

/**
 * Factory to create spawn mob events from {@link Instruction}s.
 */
public class SpawnMobEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new factory for {@link SpawnMobEvent}s.
     *
     * @param data              the primary server thread data required for main thread checking
     * @param variableProcessor the variable processor to create new variables with
     */
    public SpawnMobEventFactory(final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadEvent(createSpawnMobEvent(instruction), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadStaticEvent(createSpawnMobEvent(instruction), data);
    }

    /**
     * Creates a new spawn mob event from the given instruction.
     *
     * @param instruction the instruction to create the event from
     * @return the created event
     * @throws InstructionParseException if the instruction could not be parsed
     */
    public NullableEventAdapter createSpawnMobEvent(final Instruction instruction) throws InstructionParseException {
        final VariableLocation loc = instruction.getLocation();
        final EntityType type = instruction.getEntity();
        final VariableNumber amount = instruction.getVarNum();
        final String nameString = instruction.getOptional("name");
        final VariableString name = nameString == null ? null : new VariableString(variableProcessor,
                instruction.getPackage(),
                Utils.format(nameString, true, false).replace('_', ' ')
        );
        final String markedString = instruction.getOptional("marked");
        final VariableString marked = markedString == null ? null : new VariableString(variableProcessor,
                instruction.getPackage(),
                Utils.addPackage(instruction.getPackage(), markedString)
        );
        final QuestItem helmet = getQuestItem(instruction, "h");
        final QuestItem chestplate = getQuestItem(instruction, "c");
        final QuestItem leggings = getQuestItem(instruction, "l");
        final QuestItem boots = getQuestItem(instruction, "b");
        final QuestItem mainHand = getQuestItem(instruction, "m");
        final QuestItem offHand = getQuestItem(instruction, "o");
        final Item[] drops = instruction.getItemList(instruction.getOptional("drops"));
        final Equipment equipment = new Equipment(helmet, chestplate, leggings, boots, mainHand, offHand, drops);
        final SpawnMobEvent event = new SpawnMobEvent(loc, type, equipment, amount, name, marked);
        return new NullableEventAdapter(event);
    }

    @Nullable
    private QuestItem getQuestItem(final Instruction instruction, final String key) throws InstructionParseException {
        final ItemID item = instruction.getItem(instruction.getOptional(key));
        return item == null ? null : new QuestItem(item);
    }
}
