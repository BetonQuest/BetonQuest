package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsItemCondition extends Condition {
    private final Type itemType;

    private final String itemID;

    private final VariableNumber amount;

    public MMOItemsItemCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        itemType = MMOItemsUtils.getMMOItemType(instruction.next());
        itemID = instruction.next();

        amount = instruction.hasNext() ? instruction.getVarNum() : new VariableNumber(1);
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        int counter = 0;

        final OnlineProfile onlineProfile = profile.getOnlineProfile().get();
        final ItemStack[] inventoryItems = onlineProfile.getPlayer().getInventory().getContents();
        for (final ItemStack item : inventoryItems) {
            if (MMOItemsUtils.equalsMMOItem(item, itemType, itemID)) {
                counter = counter + item.getAmount();
            }
        }

        final List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerData(onlineProfile).getBackpack();
        for (final ItemStack item : backpackItems) {
            if (MMOItemsUtils.equalsMMOItem(item, itemType, itemID)) {
                counter = counter + item.getAmount();
            }
        }

        return counter >= amount.getInt(profile);
    }
}
