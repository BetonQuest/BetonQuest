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
    private final boolean raw;
    private int amount;

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.AvoidLiteralsInIfCondition", "PMD.PreserveStackTrace"})
    public ItemVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        int pos = instruction.size() - 1;
        if ("raw".equalsIgnoreCase(instruction.getPart(pos))) {
            raw = true;
            pos--;
        } else {
            raw = false;
        }
        final String argument = instruction.getPart(pos).toLowerCase(Locale.ROOT);
        if (argument.startsWith("left:")) {
            type = Type.LEFT;
            try {
                amount = Integer.parseInt(argument.substring(5));
            } catch (final NumberFormatException e) {
                throw new InstructionParseException("Could not parse item amount", e);
            }
        } else if ("amount".equals(argument)) {
            type = Type.AMOUNT;
        } else if ("name".equals(argument)) {
            type = Type.NAME;
        } else if (argument.startsWith("lore:")) {
            type = Type.LORE;
            try {
                amount = Integer.parseInt(argument.substring(5));
            } catch (final NumberFormatException e) {
                throw new InstructionParseException("Could not parse line", e);
            }
        } else {
            throw new InstructionParseException(String.format("Unknown argument type: '%s'",
                    argument));
        }
        if (pos == 3) {
            final String path = instruction.getPart(1) + "." + instruction.getPart(2);
            try {
                questItem = new QuestItem(new ItemID(instruction.getPackage(), path));
            } catch (final ObjectNotFoundException e) {
                throw new InstructionParseException("Could not load '" + path + "' item: " + e.getMessage());
            }
        } else {
            questItem = instruction.getQuestItem();
        }
    }

    @Override
    public String getValue(final Profile profile) {
        return switch (type) {
            case AMOUNT -> Integer.toString(itemAmount(profile));
            case LEFT -> Integer.toString(amount - itemAmount(profile));
            case NAME -> conditionalRaw(questItem.getName());
            case LORE -> {
                try {
                    yield conditionalRaw(questItem.getLore().get(amount));
                } catch (final IndexOutOfBoundsException e) {
                    yield "";
                }
            }
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
