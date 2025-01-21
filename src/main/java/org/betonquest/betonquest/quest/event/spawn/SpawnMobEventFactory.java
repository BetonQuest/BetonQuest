package org.betonquest.betonquest.quest.event.spawn;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;
import org.betonquest.betonquest.util.Utils;
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
     * Create a new factory for {@link SpawnMobEvent}s.
     *
     * @param data the primary server thread data required for main thread checking
     */
    public SpawnMobEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createSpawnMobEvent(instruction), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadStaticEvent(createSpawnMobEvent(instruction), data);
    }

    /**
     * Creates a new spawn mob event from the given instruction.
     *
     * @param instruction the instruction to create the event from
     * @return the created event
     * @throws QuestException if the instruction could not be parsed
     */
    public NullableEventAdapter createSpawnMobEvent(final Instruction instruction) throws QuestException {
        final VariableLocation loc = instruction.get(VariableLocation::new);
        final EntityType type = instruction.getEnum(EntityType.class);
        final VariableNumber amount = instruction.get(VariableNumber::new);
        final String nameString = instruction.getOptional("name");
        final VariableString name = nameString == null ? null : instruction.get(Utils.format(
                nameString, true, false).replace('_', ' '), VariableString::new);
        final String markedString = instruction.getOptional("marked");
        final VariableString marked = markedString == null ? null : instruction.get(
                Utils.addPackage(instruction.getPackage(), markedString), VariableString::new);
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
    private QuestItem getQuestItem(final Instruction instruction, final String key) throws QuestException {
        final ItemID item = instruction.getID(instruction.getOptional(key), ItemID::new);
        return item == null ? null : new QuestItem(item);
    }
}
