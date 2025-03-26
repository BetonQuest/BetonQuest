package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.event;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsGiveEvent implements OnlineEvent {
    /**
     * {@link MMOItems} plugin instance.
     */
    private final MMOItems mmoPlugin;

    private final Type itemType;

    private final String itemID;

    private final boolean scale;

    private final NotificationSender notify;

    private final boolean singleStack;

    private final VariableNumber amountVar;

    public MMOItemsGiveEvent(final MMOItems mmoPlugin, final Type itemType, final String itemID, final boolean scale, final NotificationSender notify, final boolean singleStack, final VariableNumber amountVar) {
        this.mmoPlugin = mmoPlugin;
        this.itemType = itemType;
        this.itemID = itemID;
        this.scale = scale;
        this.notify = notify;
        this.singleStack = singleStack;
        this.amountVar = amountVar;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final ItemStack mmoItem;
        if (scale) {
            mmoItem = mmoPlugin.getItem(itemType, itemID, PlayerData.get(profile.getPlayerUUID()));
        } else {
            mmoItem = mmoPlugin.getItem(itemType, itemID);
        }
        if (mmoItem == null) {
            throw new QuestException("Item with type '" + itemType + "' and ID '" + itemID + "' does not exist!");
        }

        int amount = amountVar.getValue(profile).intValue();

        final Component displayName = mmoItem.getItemMeta().displayName();
        notify.sendNotification(profile,
                new PluginMessage.Replacement("item", displayName == null ? Component.text(itemID) : displayName),
                new PluginMessage.Replacement("amount", Component.text(amount)));

        final Player player = profile.getPlayer();
        while (amount > 0) {
            final int stackSize;
            if (singleStack) {
                stackSize = Math.min(amount, 64);
            } else {
                stackSize = 1;
            }

            mmoItem.setAmount(stackSize);
            final Map<Integer, ItemStack> left = player.getInventory().addItem(mmoItem);
            for (final ItemStack itemStack : left.values()) {
                player.getWorld().dropItem(player.getLocation(), itemStack);
            }
            amount -= stackSize;
        }
    }
}
