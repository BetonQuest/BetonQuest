package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsItemCondition extends Condition {
    private final Type itemType;

    private final String itemID;

    private final VariableNumber amount;

    public MMOItemsItemCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);

        itemType = MMOItemsUtils.getMMOItemType(instruction.next());
        itemID = instruction.next();

        amount = instruction.hasNext() ? instruction.get(VariableNumber::new) : instruction.get("1", VariableNumber::new);
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        int counter = 0;

        final OnlineProfile onlineProfile = profile.getOnlineProfile().get();
        final ItemStack[] inventoryItems = onlineProfile.getPlayer().getInventory().getContents();
        for (final ItemStack item : inventoryItems) {
            if (MMOItemsUtils.equalsMMOItem(item, itemType, itemID)) {
                counter = counter + item.getAmount();
            }
        }

        final List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerDataStorage()
                .get(onlineProfile).getBackpack();
        for (final ItemStack item : backpackItems) {
            if (MMOItemsUtils.equalsMMOItem(item, itemType, itemID)) {
                counter = counter + item.getAmount();
            }
        }

        return counter >= amount.getInt(profile);
    }
}
