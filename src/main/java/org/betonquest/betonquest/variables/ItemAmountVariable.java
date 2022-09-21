package org.betonquest.betonquest.variables;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

/**
 * Allows you to count items in player's inventory and display number remaining
 * to some amount.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CommentRequired"})
public class ItemAmountVariable extends Variable {

    private final QuestItem questItem;
    private final Type type;
    private int amount;

    public ItemAmountVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        questItem = instruction.getQuestItem();
        if (instruction.next().toLowerCase(Locale.ROOT).startsWith("left:")) {
            type = Type.LEFT;
            try {
                amount = Integer.parseInt(instruction.current().substring(5));
            } catch (final NumberFormatException e) {
                throw new InstructionParseException("Could not parse item amount", e);
            }
        } else if ("amount".equalsIgnoreCase(instruction.current())) {
            type = Type.AMOUNT;
        } else {
            throw new InstructionParseException(String.format("Unknown variable type: '%s'",
                    instruction.current()));
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public String getValue(final Profile profile) {
        final Player player = profile.getOnlineProfile().getOnlinePlayer();
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
        final List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerData(profile).getBackpack();
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
