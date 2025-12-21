package org.betonquest.betonquest.compatibility.mythicmobs.item;

import io.lumine.mythic.api.items.ItemManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.item.QuestItemTagAdapterWrapper;
import org.betonquest.betonquest.item.QuestItemWrapper;

/**
 * Factory to create {@link MythicItemWrapper}s from {@link Instruction}s.
 */
public class MythicItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * Manager instance to get items.
     */
    private final ItemManager itemManager;

    /**
     * Create a new Factory for MMO Items.
     *
     * @param itemManager the manager instance to get items from
     */
    public MythicItemFactory(final ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final MythicItemWrapper mythicItemWrapper = new MythicItemWrapper(itemManager, instruction.get(DefaultArgumentParsers.STRING));
        if (instruction.hasArgument("quest-item")) {
            return new QuestItemTagAdapterWrapper(mythicItemWrapper);
        }
        return mythicItemWrapper;
    }
}
