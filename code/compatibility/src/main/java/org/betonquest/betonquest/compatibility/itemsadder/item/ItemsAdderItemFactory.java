package org.betonquest.betonquest.compatibility.itemsadder.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.compatibility.itemsadder.ItemsAdderParser;
import org.betonquest.betonquest.item.QuestItemTagAdapterWrapper;
import org.betonquest.betonquest.item.QuestItemWrapper;

/**
 * Factory for creating {@link QuestItemWrapper} from ItemsAdder items.
 */
public class ItemsAdderItemFactory implements TypeFactory<QuestItemWrapper> {

    /** The empty default constructor. */
    public ItemsAdderItemFactory() { }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final ItemsAdderItemWrapper wrapper = new ItemsAdderItemWrapper(instruction.parse(ItemsAdderParser.ITEMS_ADDER_PARSER).get());
        final boolean isQuestItem = instruction.bool().getFlag("quest-item", true)
                .getValue(null).orElse(false);
        return isQuestItem ? new QuestItemTagAdapterWrapper(wrapper) : wrapper;
    }
}
