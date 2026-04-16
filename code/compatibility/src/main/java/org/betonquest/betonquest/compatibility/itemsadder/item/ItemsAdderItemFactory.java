package org.betonquest.betonquest.compatibility.itemsadder.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.compatibility.itemsadder.ItemsAdderParser;

/**
 * Factory for creating {@link QuestItemWrapper} from ItemsAdder items.
 */
public class ItemsAdderItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * The empty default constructor.
     */
    public ItemsAdderItemFactory() {
        // Empty
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        return new ItemsAdderItemWrapper(instruction.parse(ItemsAdderParser.ITEMS_ADDER_PARSER).get());
    }
}
