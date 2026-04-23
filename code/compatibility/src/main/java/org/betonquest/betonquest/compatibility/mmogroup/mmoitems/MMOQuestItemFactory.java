package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.quest.TypeFactory;

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
        final Argument<Type> itemType = instruction.parse(MMOItemsUtils::getMMOItemType).get();
        final Argument<String> itemId = instruction.string().get();
        final FlagArgument<Boolean> scale = instruction.bool().getFlag("scale", true);
        final FlagArgument<Number> soulBound = instruction.number().getFlag("soulBound", 1);
        return new MMOQuestItemWrapper(mmoPlugin, itemType, itemId, scale, soulBound);
    }
}
