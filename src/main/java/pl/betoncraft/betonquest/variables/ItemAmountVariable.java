package pl.betoncraft.betonquest.variables;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.List;

/**
 * Allows you to count items in player's inventory and display number remaining
 * to some amount.
 */
public class ItemAmountVariable extends Variable {

    private QuestItem questItem;
    private Type type;
    private int amount;

    public ItemAmountVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        questItem = instruction.getQuestItem();
        if (instruction.next().toLowerCase().startsWith("left:")) {
            type = Type.LEFT;
            try {
                amount = Integer.parseInt(instruction.current().substring(5));
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse item amount", e);
            }
        } else if (instruction.current().equalsIgnoreCase("amount")) {
            type = Type.AMOUNT;
        } else {
            throw new InstructionParseException(String.format("Unknown variable type: '%s'",
                    instruction.current()));
        }
    }

    @Override
    public String getValue(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        int playersAmount = 0;
        for (final ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                continue;
            }
            if (!questItem.compare(item)) {
                continue;
            }
            playersAmount += item.getAmount();
        }
        final List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerData(playerID).getBackpack();
        for (final ItemStack item : backpackItems) {
            if (item == null) {
                continue;
            }
            if (!questItem.compare(item)) {
                continue;
            }
            playersAmount += item.getAmount();
        }
        switch (type) {
            case AMOUNT:
                return Integer.toString(playersAmount);
            case LEFT:
                return Integer.toString(amount - playersAmount);
            default:
                return "";
        }
    }

    private enum Type {
        AMOUNT, LEFT
    }

}
