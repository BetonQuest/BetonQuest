package org.betonquest.betonquest.quest.objective.drop;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Objective that requires the player to drop a specific item.
 */
public class DropObjective extends DefaultObjective {

    /**
     * A list of allowed drops to complete the objective.
     */
    private final Argument<List<ItemWrapper>> dropItems;

    /**
     * Constructor for the DropObjective.
     *
     * @param service   the objective service
     * @param dropItems the list of allowed drops
     */
    public DropObjective(final ObjectiveService service, final Argument<List<ItemWrapper>> dropItems) {
        super(service);
        this.dropItems = dropItems;
    }

    /**
     * Handles the PlayerDropItemEvent event.
     *
     * @param event   the bukkit event
     * @param profile the profile of the player that dropped the item
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onDrop(final PlayerDropItemEvent event, final OnlineProfile profile) throws QuestException {
        if (isValidItem(profile, event.getItemDrop().getItemStack())) {
            getService().complete(profile);
        }
    }

    private boolean isValidItem(final OnlineProfile onlineProfile, final ItemStack itemStack) throws QuestException {
        final List<ItemWrapper> allowedDropItems = dropItems.getValue(onlineProfile);
        for (final ItemWrapper item : allowedDropItems) {
            final int requiredAmount = item.getAmount().getValue(onlineProfile).intValue();
            final boolean sufficiently = requiredAmount <= itemStack.getAmount();
            if (sufficiently && item.matches(itemStack, onlineProfile)) {
                return true;
            }
        }
        return false;
    }
}
