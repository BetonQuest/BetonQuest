package org.betonquest.betonquest.quest.action.chest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Removes items from a chest.
 */
public class ChestTakeAction extends AbstractChestAction {

    /**
     * The action manager.
     */
    @Nullable
    private final ActionManager actionManager;

    /**
     * The items to take from the chest.
     */
    private final Argument<List<ItemWrapper>> items;

    /**
     * If not all items being present should result in a fail.
     */
    private final FlagArgument<Boolean> abort;

    /**
     * Actions to execute if not all items could be taken.
     */
    @Nullable
    private final Argument<List<ActionIdentifier>> failActions;

    /**
     * Creates a new ChestTakeAction.
     *
     * @param location The location of the chest.
     * @param items    The items to take from the chest.
     */
    public ChestTakeAction(final Argument<Location> location, final Argument<List<ItemWrapper>> items) {
        super(location);
        this.actionManager = null;
        this.items = items;
        this.abort = profile -> Optional.of(false);
        this.failActions = null;
    }

    /**
     * Creates a new ChestTakeAction.
     *
     * @param location      The location of the chest.
     * @param actionManager the action manager
     * @param items         The items to take from the chest.
     * @param abort         if not all items being present should result in a fail
     * @param failActions   the actions to execute if not all items could be taken
     */
    public ChestTakeAction(final Argument<Location> location, final ActionManager actionManager, final Argument<List<ItemWrapper>> items,
                           final FlagArgument<Boolean> abort, @Nullable final Argument<List<ActionIdentifier>> failActions) {
        super(location);
        this.actionManager = actionManager;
        this.items = items;
        this.abort = abort;
        this.failActions = failActions;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        try {
            final boolean abort = this.abort.getValue(profile).orElse(false);
            final Inventory inventory = getChest(profile).getInventory();
            final ItemStack[] inventoryContents = inventory.getContents();
            boolean anyNotTaken = false;
            for (final ItemWrapper item : items.getValue(profile)) {
                final QuestItem questItem = item.getItem(profile);
                final int amount = item.getAmount().getValue(profile).intValue();
                anyNotTaken |= removeItems(inventoryContents, questItem, amount);
            }
            if (anyNotTaken) {
                if (actionManager != null && failActions != null) {
                    actionManager.run(profile, failActions.getValue(profile));
                }
                if (abort) {
                    return;
                }
            }
            inventory.setContents(inventoryContents);
        } catch (final QuestException e) {
            throw new QuestException("Trying to take items from chest. " + e.getMessage(), e);
        }
    }

    private boolean removeItems(final ItemStack[] items, final QuestItem questItem, final int amount) {
        int desiredDeletions = amount;
        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            if (questItem.matches(item)) {
                if (item.getAmount() - desiredDeletions <= 0) {
                    desiredDeletions -= item.getAmount();
                    items[i] = null;
                } else {
                    item.subtract(desiredDeletions);
                    desiredDeletions = 0;
                }
                if (desiredDeletions <= 0) {
                    break;
                }
            }
        }
        return desiredDeletions > 0;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
