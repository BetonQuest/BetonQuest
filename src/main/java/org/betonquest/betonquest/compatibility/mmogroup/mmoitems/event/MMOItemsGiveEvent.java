package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.event;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * An event that gives an MMOItems item to a player.
 */
public class MMOItemsGiveEvent implements OnlineEvent {
    /**
     * {@link MMOItems} plugin instance.
     */
    private final MMOItems mmoPlugin;

    /**
     * Type of the item to be given.
     */
    private final Variable<Type> itemType;

    /**
     * ID of the item to be given.
     */
    private final Variable<String> itemID;

    /**
     * Whether to scale the item.
     */
    private final boolean scale;

    /**
     * Notification sender for the event.
     */
    private final NotificationSender notify;

    /**
     * Whether to give the item in a single stack.
     */
    private final boolean singleStack;

    /**
     * Variable number representing the amount of items to be given.
     */
    private final Variable<Number> amountVar;

    /**
     * Constructs a new MMOItemsGiveEvent.
     *
     * @param mmoPlugin   the MMOItems plugin instance
     * @param itemType    the type of the item to be given
     * @param itemID      the ID of the item to be given
     * @param scale       whether to scale the item
     * @param notify      the notification sender
     * @param singleStack whether to give the item in a single stack
     * @param amountVar   the variable number representing the amount of items to be given
     */
    public MMOItemsGiveEvent(final MMOItems mmoPlugin, final Variable<Type> itemType, final Variable<String> itemID, final boolean scale, final NotificationSender notify, final boolean singleStack, final Variable<Number> amountVar) {
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
        final Type itemType = this.itemType.getValue(profile);
        final String itemID = this.itemID.getValue(profile);
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
                new VariableReplacement("item", displayName == null ? Component.text(itemID) : displayName),
                new VariableReplacement("amount", Component.text(amount)));

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
