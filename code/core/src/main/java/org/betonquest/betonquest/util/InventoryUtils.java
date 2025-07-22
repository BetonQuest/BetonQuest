package org.betonquest.betonquest.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * This is a utility class for working with inventories and crafting.
 */
public final class InventoryUtils {

    private InventoryUtils() {
    }

    /**
     * Check whether an item "is nothing". Sometimes they are {@link ItemStack}s with material {@link Material#AIR} but
     * they also might be null. This method provides an easy check.
     *
     * @param slotItem item to check
     * @return true if the slot is empty
     */
    public static boolean isEmptySlot(@Nullable final ItemStack slotItem) {
        return slotItem == null || slotItem.getType().equals(Material.AIR);
    }

    /**
     * Calculate the maximum amount an item can fit into an inventory. The amount of the item stack itself will be
     * ignored, to see how often the given stack fits use {@code calculateSpaceForItem(inventory, item) / item.getAmount()}.
     *
     * @param inventory the inventory to check
     * @param item      the item to fit.
     * @return the maximum amount the item fits into the inventory
     */
    public static int calculateSpaceForItem(final Inventory inventory, final ItemStack item) {
        int remainingSpace = 0;
        for (final ItemStack i : inventory.getStorageContents()) {
            if (isEmptySlot(i)) {
                remainingSpace += item.getMaxStackSize();
            } else if (i.isSimilar(item)) {
                remainingSpace += item.getMaxStackSize() - i.getAmount();
            }
        }
        return remainingSpace;
    }

    /**
     * Calculate the maximum amount a "crafting" can be done, This works by checking every non-air element of a given
     * input array of ingredients and calculating the minimum stack size. A "crafting" might be any action that
     * transforms one or more items to one other item in a one-of-each-to-one ratio.
     *
     * @param ingredients the ingredients to check
     * @return the amount of the smallest ingredient stack
     * @see #calculateMaximumCraftAmount(ItemStack, ItemStack...)
     */
    public static int calculateMaximumCraftActions(final ItemStack... ingredients) {
        return Arrays.stream(ingredients)
                .filter(((Predicate<ItemStack>) InventoryUtils::isEmptySlot).negate())
                .mapToInt(ItemStack::getAmount)
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    /**
     * Calculate how many items will be shift crafted. This method takes into consideration how many items are crafted
     * per craft, how many items can be crafted at most and how many items fit into the inventory.
     *
     * @param result      the result of one craft action
     * @param inventory   the inventory to deposit the result into
     * @param ingredients the ingredients used for crafting (e.g. the crafting matrix)
     * @return the exact amount of items that will be created by shift-crafting
     */
    public static int calculateShiftCraftAmount(final ItemStack result, final Inventory inventory, final ItemStack... ingredients) {
        final int remainingSpace = calculateSpaceForItem(inventory, result);
        final int itemsPerCraft = result.getAmount();
        final int spaceForCrafts = remainingSpace / itemsPerCraft;
        return Math.min(calculateMaximumCraftActions(ingredients), spaceForCrafts) * itemsPerCraft;
    }

    /**
     * Calculate how many items can be crafted at max. This is done by multiplying the maximum amount of craft actions
     * by the amount of items created with each craft action.
     *
     * @param result      the result of one craft action
     * @param ingredients the ingredients to check
     * @return the amount of items that can be crafted
     * @see #calculateMaximumCraftActions(ItemStack...)
     */
    public static int calculateMaximumCraftAmount(final ItemStack result, final ItemStack... ingredients) {
        return calculateMaximumCraftActions(ingredients) * result.getAmount();
    }

    /**
     * Calculate how many items will be crafted by pressing a fast-swap button (either offhand or hotbar keys). As the
     * item in the hotbar cannot be put into the result slot this will either do exactly one craft action if the used
     * slot is empty or non if there is already something inside the slot. Minecraft will not add crafted items to an
     * already existing item stack of the same type with this method, even if there is theoretically enough space.
     *
     * @param result  the result of one craft action
     * @param swapped the item in the target fast-swap slot
     * @return either 0 or the amount of the result
     */
    public static int calculateSwapCraftAmount(final ItemStack result, @Nullable final ItemStack swapped) {
        return isEmptySlot(swapped) ? result.getAmount() : 0;
    }

    /**
     * Calculate how many items will be crafted by simply clicking onto a crafting slot. It will take into consideration
     * if and how much items of the result will still fit into the cursor.
     *
     * @param result the result of one craft action
     * @param cursor the item currently held in the cursor
     * @return either 0 or the amount of the result
     */
    @SuppressWarnings("NullAway")
    public static int calculateSimpleCraftAmount(final ItemStack result, @Nullable final ItemStack cursor) {
        if (isEmptySlot(cursor)
                || cursor.isSimilar(result) && cursor.getAmount() + result.getAmount() <= cursor.getMaxStackSize()) {
            return result.getAmount();
        }
        return 0;
    }

    /**
     * Calculates how many items can be made by dropping the item with q. No items can be dropped with q from a
     * workbench if the player has an item in hand.
     *
     * @param result the result of one craft action
     * @param cursor the item currently held in the cursor
     * @return either 0 or the amount of the result
     */
    public static int calculateDropCraftAmount(final ItemStack result, @Nullable final ItemStack cursor) {
        if (cursor != null && cursor.getType().equals(Material.AIR)) {
            return result.getAmount();
        }
        return 0;
    }
}
