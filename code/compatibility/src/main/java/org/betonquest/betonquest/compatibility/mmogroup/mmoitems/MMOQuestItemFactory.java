package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.item.QuestItemTagAdapterWrapper;
import org.betonquest.betonquest.item.QuestItemWrapper;

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
        final MMOQuestItemWrapper mmoQuestItemWrapper = new MMOQuestItemWrapper(mmoPlugin, itemType, itemId);
        if (instruction.hasArgument("quest-item")) {
            return new QuestItemTagAdapterWrapper(mmoQuestItemWrapper);
        }
        return mmoQuestItemWrapper;
    }
}
