package pl.betoncraft.betonquest.compatibility.mmogroup.mmoitems;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.manager.TypeManager;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsItemCondition extends Condition {

    private final Type itemType;
    private final String itemID;
    private int amount = 1;

    public MMOItemsItemCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final TypeManager typeManager = MMOItems.plugin.getTypes();
        itemType = typeManager.get(instruction.next());
        itemID = instruction.next();

        final List<Integer> potentialAmount = instruction.getAllNumbers();
        if (!potentialAmount.isEmpty()) {
            amount = potentialAmount.get(0);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        int counter = 0;

        final ItemStack[] inventoryItems = PlayerConverter.getPlayer(playerID).getInventory().getContents();
        for (final ItemStack item : inventoryItems) {
            if (MMOItemsUtils.equalsMMOItem(item, itemType, itemID)) {
                counter = counter + item.getAmount();
            }
        }

        final List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerData(playerID).getBackpack();
        for (final ItemStack item : backpackItems) {
            if (MMOItemsUtils.equalsMMOItem(item, itemType, itemID)) {
                counter = counter + item.getAmount();
            }
        }

        return counter >= amount;
    }
}
