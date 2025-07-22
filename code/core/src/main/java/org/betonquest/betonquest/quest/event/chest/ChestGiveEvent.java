package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Puts the items in the inventory of a block or drops them if the inventory is full.
 */
public class ChestGiveEvent implements NullableEvent {
    /**
     * The items to put in the blocks inventory.
     */
    private final Variable<List<Item>> questItems;

    /**
     * The location of the block.
     */
    private final Variable<Location> location;

    /**
     * Create the chest give event.
     *
     * @param questItems the items to put in the blocks inventory
     * @param location   the location of the block
     */
    public ChestGiveEvent(final Variable<Location> location, final Variable<List<Item>> questItems) {
        this.questItems = questItems;
        this.location = location;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Block block = location.getValue(profile).getBlock();
        final InventoryHolder chest;
        try {
            chest = (InventoryHolder) block.getState();
        } catch (final ClassCastException e) {
            throw new QuestException("Trying to put items in chest, but there's no chest! Location: X"
                    + block.getX() + " Y" + block.getY() + " Z" + block.getZ(), e);
        }
        final Map<Integer, ItemStack> left = chest.getInventory().addItem(getItemStacks(profile));
        for (final ItemStack itemStack : left.values()) {
            block.getWorld().dropItem(block.getLocation(), itemStack);
        }
    }

    /**
     * Converts the quest items to item stacks.
     *
     * @param profile the profile of the player
     * @return the item stacks
     */
    private ItemStack[] getItemStacks(@Nullable final Profile profile) throws QuestException {
        final List<ItemStack> itemStacks = new ArrayList<>();
        for (final Item item : questItems.getValue(profile)) {
            final QuestItem questItem = item.getItem(profile);
            int amount = item.getAmount().getValue(profile).intValue();
            while (amount > 0) {
                final ItemStack itemStackTemplate = questItem.generate(1, profile);
                final int stackSize = Math.min(amount, itemStackTemplate.getMaxStackSize());
                final ItemStack itemStack = questItem.generate(stackSize, profile);
                itemStacks.add(itemStack);
                amount = amount - stackSize;
            }
        }
        return itemStacks.toArray(ItemStack[]::new);
    }
}
