package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.events.AbstractTakeEvent;
import org.betonquest.betonquest.events.TakeEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Removes items from player's inventory and/or backpack
 *
 * @deprecated Only needed because the BQ 1.12.X item system cannot work with special items like the ones from MMOItems.
 * TODO: Will be replaced by the new item system. Then the {@link TakeEvent} will be able to handle everything.
 */
@SuppressWarnings("PMD.CommentRequired")
@Deprecated
public class MMOItemsTakeEvent extends AbstractTakeEvent {
    private final Type itemType;

    private final String itemID;

    private final VariableNumber deleteAmountVar;

    private final Map<UUID, Integer> neededDeletions = new ConcurrentHashMap<>();

    public MMOItemsTakeEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        itemType = MMOItemsUtils.getMMOItemType(instruction.next());
        itemID = instruction.next();

        final String amount = instruction.getOptional("amount");
        if (amount == null) {
            deleteAmountVar = new VariableNumber(1);
        } else {
            deleteAmountVar = instruction.getVarNum(amount);
        }
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final OnlineProfile onlineProfile = profile.getOnlineProfile().get();
        final int deleteAmount = deleteAmountVar.getInt(profile);
        neededDeletions.put(onlineProfile.getProfileUUID(), deleteAmount);

        checkSelectedTypes(onlineProfile.getPlayer());

        final ItemStack item = MMOItemsUtils.getMMOItemStack(itemType, itemID);
        final String itemName = item.getItemMeta().getDisplayName();
        notifyPlayer(onlineProfile, itemName, deleteAmount - neededDeletions.get(onlineProfile.getProfileUUID()));
        return null;
    }

    @Override
    protected ItemStack[] takeDesiredAmount(final Profile profile, final ItemStack... items) {
        int desiredDeletions = neededDeletions.get(profile.getProfileUUID());

        for (int i = 0; i < items.length && desiredDeletions > 0; i++) {
            final ItemStack item = items[i];
            if (MMOItemsUtils.equalsMMOItem(item, itemType, itemID)) {
                if (item.getAmount() <= desiredDeletions) {
                    items[i] = null;
                    desiredDeletions = desiredDeletions - item.getAmount();
                } else {
                    item.setAmount(item.getAmount() - desiredDeletions);
                    desiredDeletions = 0;
                }
            }
        }

        neededDeletions.put(profile.getProfileUUID(), desiredDeletions);
        return items;
    }
}
