package org.betonquest.betonquest.compatibility.itemsadder.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.compatibility.itemsadder.ItemsAdderParser;
import org.betonquest.betonquest.item.QuestItemTagAdapterWrapper;
import org.betonquest.betonquest.item.QuestItemWrapper;

public class ItemsAdderItemFactory implements TypeFactory<QuestItemWrapper> {

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final ItemsAdderItemWapper itemsAdderItemWapper = new ItemsAdderItemWapper(instruction.parse(ItemsAdderParser.ITEMS_ADDER_PARSER).get());
        final boolean questItem = instruction.bool().getFlag("quest-item", true)
                .getValue(null).orElse(false);
        if (questItem) {
            return new QuestItemTagAdapterWrapper(itemsAdderItemWapper);
        }
        return itemsAdderItemWapper;
    }
}
