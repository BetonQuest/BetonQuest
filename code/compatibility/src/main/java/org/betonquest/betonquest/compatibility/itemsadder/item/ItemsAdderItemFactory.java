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

    /**
     * Default constructor for ItemsAdderItemFactory.
     */
    public ItemsAdderItemFactory() {
    }

    /**
     * Parses the instruction into an {@link ItemsAdderItemWrapper}.
     * @param instruction the instruction to parse
     * @return the wrapped ItemsAdder item
     * @throws QuestException if parsing fails
     */
    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final ItemsAdderItemWrapper itemsAdderItemWrapper = new ItemsAdderItemWrapper(instruction.parse(ItemsAdderParser.ITEMS_ADDER_PARSER).get());
        final boolean questItem = instruction.bool().getFlag("quest-item", true)
                .getValue(null).orElse(false);
        if (questItem) {
            return new QuestItemTagAdapterWrapper(itemsAdderItemWrapper);
        }
        return itemsAdderItemWrapper;
    }
}
