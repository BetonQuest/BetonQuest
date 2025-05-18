package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.item.QuestItemWrapper;
import org.betonquest.betonquest.kernel.registry.TypeFactory;

/**
 * Factory to create {@link MMOQuestItem}s from {@link Instruction}s.
 */
public class MMOQuestItemFactory implements TypeFactory<QuestItemWrapper> {
    /**
     * {@link MMOItems} plugin instance.
     */
    private final MMOItems mmoPlugin;

    /**
     * Create a new Factory for MMO Items.
     *
     * @param mmoPlugin the plugin instance to get items from
     */
    public MMOQuestItemFactory(final MMOItems mmoPlugin) {
        this.mmoPlugin = mmoPlugin;
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Type> itemType = instruction.get(MMOItemsUtils::getMMOItemType);
        final Variable<String> itemId = instruction.get(Argument.STRING);
        return new MMOQuestItemWrapper(mmoPlugin, itemType, itemId);
    }
}
