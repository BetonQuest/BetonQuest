package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.data.SoulboundData;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.profile.Profile;
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
     * Item type argument.
     */
    private final Argument<Type> itemType;

    /**
     * Item id argument.
     */
    private final Argument<String> itemId;

    /**
     * Whether to add the soul bound stat to the generated item.
     */
    private final FlagArgument<Number> soulBound;

    /**
     * Creates a new MMO Item Wrapper.
     *
     * @param mmoPlugin the mmo items plugin instance to get the item
     * @param itemType  the item type
     * @param itemId    the item id
     * @param soulBound whether to add the soul bound stat to the generated item
     */
    public MMOQuestItemWrapper(final MMOItems mmoPlugin, final Argument<Type> itemType, final Argument<String> itemId,
                               final FlagArgument<Number> soulBound) {
        this.mmoPlugin = mmoPlugin;
        this.itemType = itemType;
        this.itemId = itemId;
        this.soulBound = soulBound;
    }

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        final Type type = itemType.getValue(profile);
        final String identifier = itemId.getValue(profile);
        final MMOItem item;
        if (profile == null) {
            item = mmoPlugin.getMMOItem(type, identifier);
        } else {
            item = mmoPlugin.getMMOItem(type, identifier, PlayerData.get(profile.getPlayerUUID()));
        }
        if (item == null) {
            throw new QuestException("Could not find item for type " + type + " and identifier " + identifier);
        }
        if (profile != null && profile.getOnlineProfile().isPresent()) {
            final int soulBoundLevel = soulBound.getValue(profile).orElse(0).intValue();
            if (soulBoundLevel > 0) {
                item.setData(ItemStats.SOULBOUND, new SoulboundData(profile.getOnlineProfile().get().getPlayer(), soulBoundLevel));
            }
        }
        return new MMOQuestItem(item.newBuilder().buildSilently(), type, identifier);
    }
}
