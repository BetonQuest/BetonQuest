package pl.betoncraft.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.events.AbstractTakeEvent;
import pl.betoncraft.betonquest.events.TakeEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Removes items from player's inventory and/or backpack
 *
 * @deprecated Only needed because the 1.12.X item system cannot work with special items like the ones from MMOItems.
 * Will be removed in 2.0 in favor of the new item system. Then the {@link TakeEvent} will be able to handle everything.
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
        itemType = MMOItems.plugin.getTypes().get(instruction.next());
        itemID = instruction.next();

        final String amount = instruction.getOptional("amount");
        if (amount == null) {
            deleteAmountVar = new VariableNumber(1);
        } else {
            deleteAmountVar = instruction.getVarNum(amount);
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final UUID uuid = player.getUniqueId();

        final int deleteAmount = deleteAmountVar.getInt(playerID);
        neededDeletions.put(uuid, deleteAmount);

        checkSelectedTypes(player);

        final ItemStack item = MMOItemsUtils.getMMOItemStack(itemType, itemID);
        final String itemName = item.getItemMeta().getDisplayName();
        notifyPlayer(playerID, itemName, deleteAmount - neededDeletions.get(uuid));
        return null;
    }

    @Override
    protected ItemStack[] takeDesiredAmount(final Player player, final ItemStack... items) {
        int desiredDeletions = neededDeletions.get(player.getUniqueId());

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

        neededDeletions.put(player.getUniqueId(), desiredDeletions);
        return items;
    }
}
