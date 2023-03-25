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
 * Allows you to display properties of QuestItems like the name
 * or the amount in player's inventory.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CommentRequired"})
public class ItemVariable extends Variable {

    private final QuestItem questItem;
    private final Type type;
    private int amount;

    @SuppressWarnings("PMD.CognitiveComplexity")
    public ItemVariable(final Instruction instruction) throws InstructionParseException {
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
        } else if ("name".equalsIgnoreCase(instruction.current())) {
            type = Type.NAME;
        } else if (instruction.current().toLowerCase(Locale.ROOT).startsWith("lore:")) {
            type = Type.LORE;
            try {
                amount = Integer.parseInt(instruction.current().substring(5));
                if (amount >= questItem.getLore().size()) {
                    throw new InstructionParseException(String.format("Lore does not have this line: '%d'", amount));
                }
            } catch (final NumberFormatException e) {
                throw new InstructionParseException("Could not parse line", e);
            }
        } else {
            throw new InstructionParseException(String.format("Unknown variable type: '%s'",
                    instruction.current()));
        }
    }

    @Override
    public String getValue(final Profile profile) {
        switch (type) {
            case AMOUNT:
                return Integer.toString(playersAmount(profile));
            case LEFT:
                return Integer.toString(amount - playersAmount(profile));
            case NAME:
                final String name = questItem.getName();
                return name == null ? "" : name;
            case LORE:
                return questItem.getLore().get(amount);
            default:
                return "";
        }
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private int playersAmount(final Profile profile) {
        final Player player = profile.getOnlineProfile().get().getPlayer();
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
        return playersAmount;
    }

    private enum Type {
        AMOUNT, LEFT, NAME, LORE
    }

}
