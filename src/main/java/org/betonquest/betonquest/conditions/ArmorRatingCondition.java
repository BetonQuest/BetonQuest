package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Requires the player to have specific armor rating
 */
@SuppressWarnings("PMD.CommentRequired")
public class ArmorRatingCondition extends Condition {

    private final VariableNumber required;

    public ArmorRatingCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        required = instruction.getVarNum();
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NcssCount", "PMD.NPathComplexity"})
    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final PlayerInventory inv = PlayerConverter.getPlayer(playerID).getInventory();
        int rating = 0;
        final ItemStack boots = inv.getBoots();
        final ItemStack helmet = inv.getHelmet();
        final ItemStack chest = inv.getChestplate();
        final ItemStack leggings = inv.getLeggings();
        if (helmet != null) {
            if (helmet.getType() == Material.LEATHER_HELMET) {
                rating += 1;
            } else if (helmet.getType() == Material.GOLDEN_HELMET) {
                rating += 2;
            } else if (helmet.getType() == Material.CHAINMAIL_HELMET) {
                rating += 2;
            } else if (helmet.getType() == Material.IRON_HELMET) {
                rating += 2;
            } else if (helmet.getType() == Material.DIAMOND_HELMET) {
                rating += 3;
            } else if ("NETHERITE_HELMET".equals(helmet.getType().toString())) {
                rating += 3;
            }
        }
        if (boots != null) {
            if (boots.getType() == Material.LEATHER_BOOTS) {
                rating += 1;
            } else if (boots.getType() == Material.GOLDEN_BOOTS) {
                rating += 1;
            } else if (boots.getType() == Material.CHAINMAIL_BOOTS) {
                rating += 1;
            } else if (boots.getType() == Material.IRON_BOOTS) {
                rating += 2;
            } else if (boots.getType() == Material.DIAMOND_BOOTS) {
                rating += 3;
            } else if ("NETHERITE_BOOTS".equals(boots.getType().toString())) {
                rating += 3;
            }

        }
        if (leggings != null) {
            if (leggings.getType() == Material.LEATHER_LEGGINGS) {
                rating += 2;
            } else if (leggings.getType() == Material.GOLDEN_LEGGINGS) {
                rating += 3;
            } else if (leggings.getType() == Material.CHAINMAIL_LEGGINGS) {
                rating += 4;
            } else if (leggings.getType() == Material.IRON_LEGGINGS) {
                rating += 5;
            } else if (leggings.getType() == Material.DIAMOND_LEGGINGS) {
                rating += 6;
            } else if ("NETHERITE_LEGGINGS".equals(leggings.getType().toString())) {
                rating += 6;
            }
        }
        if (chest != null) {
            if (chest.getType() == Material.LEATHER_CHESTPLATE) {
                rating += 3;
            } else if (chest.getType() == Material.GOLDEN_CHESTPLATE) {
                rating += 5;
            } else if (chest.getType() == Material.CHAINMAIL_CHESTPLATE) {
                rating += 5;
            } else if (chest.getType() == Material.IRON_CHESTPLATE) {
                rating += 6;
            } else if (chest.getType() == Material.DIAMOND_CHESTPLATE) {
                rating += 8;
            } else if ("NETHERITE_CHESTPLATE".equals(chest.getType().toString())) {
                rating += 8;
            }
        }
        return rating >= required.getInt(playerID);
    }

}
