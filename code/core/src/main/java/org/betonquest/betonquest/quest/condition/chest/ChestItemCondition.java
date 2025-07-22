package org.betonquest.betonquest.quest.condition.chest;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Checks if the chest contains specified items.
 */
public class ChestItemCondition implements NullableCondition {

    /**
     * Items that should be in the chest.
     */
    private final Variable<List<Item>> items;

    /**
     * Location of the chest.
     */
    private final Variable<Location> loc;

    /**
     * Constructor of the ChestItemCondition.
     *
     * @param items items that should be in the chest
     * @param loc   location of the chest
     */
    public ChestItemCondition(final Variable<Location> loc, final Variable<List<Item>> items) {
        this.items = items;
        this.loc = loc;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Block block = loc.getValue(profile).getBlock();
        final InventoryHolder chest;
        try {
            chest = (InventoryHolder) block.getState();
        } catch (final ClassCastException e) {
            throw new QuestException("Trying to check items in a chest, but there's no chest! Location: X" + block.getX() + " Y"
                    + block.getY() + " Z" + block.getZ(), e);
        }
        int counter = 0;
        final List<Item> resolvedItems = items.getValue(profile);
        for (final Item item : resolvedItems) {
            int amount = item.getAmount().getValue(profile).intValue();
            final ItemStack[] inventoryItems = chest.getInventory().getContents();
            for (final ItemStack stack : inventoryItems) {
                if (stack == null) {
                    continue;
                }
                if (!item.matches(stack, profile)) {
                    continue;
                }
                amount -= stack.getAmount();
                if (amount <= 0) {
                    counter++;
                    break;
                }
            }
        }
        return counter == resolvedItems.size();
    }
}
