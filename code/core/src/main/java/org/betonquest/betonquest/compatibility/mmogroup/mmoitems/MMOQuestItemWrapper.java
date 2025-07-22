package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.QuestItemWrapper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Creates {@link MMOQuestItem}s from type and id.
 */
public class MMOQuestItemWrapper implements QuestItemWrapper {
    /**
     * Plugin instance to get resolved Items.
     */
    private final MMOItems mmoPlugin;

    /**
     * Item type variable.
     */
    private final Variable<Type> itemType;

    /**
     * Item id variable.
     */
    private final Variable<String> itemId;

    /**
     * Creates a new MMO Item Wrapper.
     *
     * @param mmoPlugin the mmo items plugin instance to get the item
     * @param itemType  the item type
     * @param itemId    the item id
     */
    public MMOQuestItemWrapper(final MMOItems mmoPlugin, final Variable<Type> itemType, final Variable<String> itemId) {
        this.mmoPlugin = mmoPlugin;
        this.itemType = itemType;
        this.itemId = itemId;
    }

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        final Type type = itemType.getValue(profile);
        final String identifier = itemId.getValue(profile);
        final ItemStack stack;
        if (profile == null) {
            stack = mmoPlugin.getItem(type, identifier);
        } else {
            stack = mmoPlugin.getItem(type, identifier, PlayerData.get(profile.getPlayerUUID()));
        }
        if (stack == null) {
            throw new QuestException("Could not find item for type " + type + " and identifier " + identifier);
        }
        return new MMOQuestItem(stack, type, identifier);
    }
}
