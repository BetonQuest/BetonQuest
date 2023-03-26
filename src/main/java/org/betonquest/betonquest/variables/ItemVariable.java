package org.betonquest.betonquest.variables;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.ChatColor;
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
    private boolean raw;

    @SuppressWarnings("PMD.CognitiveComplexity")
    public ItemVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        questItem = parseQuestItem(instruction);
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
            checkForRawString(instruction);
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
            checkForRawString(instruction);
        } else {
            throw new InstructionParseException(String.format("Unknown variable type: '%s'",
                    instruction.current()));
        }
    }

    @SuppressWarnings({"PMD.ExceptionAsFlowControl", "PMD.PreserveStackTrace"})
    private QuestItem parseQuestItem(final Instruction instruction) throws InstructionParseException {
        try {
            return instruction.getQuestItem();
        } catch (final InstructionParseException e) {
            final String path = instruction.current() + "." + instruction.next();
            try {
                return new QuestItem(new ItemID(instruction.getPackage(), path));
            } catch (final ObjectNotFoundException ex) {
                throw new InstructionParseException("Could not load '" + path + "' item: " + ex.getMessage(), ex);
            }
        }
    }

    private void checkForRawString(final Instruction instruction) {
        try {
            if ("raw".equalsIgnoreCase(instruction.next())) {
                raw = true;
            }
        } catch (final InstructionParseException ignored) {
        }
    }

    @Override
    public String getValue(final Profile profile) {
        return switch (type) {
            case AMOUNT -> Integer.toString(itemAmount(profile));
            case LEFT -> Integer.toString(amount - itemAmount(profile));
            case NAME -> conditionalRaw(questItem.getName());
            case LORE -> conditionalRaw(questItem.getLore().get(amount));
        };
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private int itemAmount(final Profile profile) {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        int itemAmount = 0;
        for (final ItemStack item : player.getInventory().getContents()) {
            if (item == null || !questItem.compare(item)) {
                continue;
            }
            itemAmount += item.getAmount();
        }
        final List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerData(profile).getBackpack();
        for (final ItemStack item : backpackItems) {
            if (item == null || !questItem.compare(item)) {
                continue;
            }
            itemAmount += item.getAmount();
        }
        return itemAmount;
    }

    private String conditionalRaw(final String string) {
        if (string == null) {
            return "";
        }
        if (raw) {
            return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', string));
        }
        return string;
    }

    private enum Type {
        AMOUNT, LEFT, NAME, LORE
    }

}
