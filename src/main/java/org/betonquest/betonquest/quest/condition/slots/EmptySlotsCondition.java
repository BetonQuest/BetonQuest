package org.betonquest.betonquest.quest.condition.slots;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.inventory.ItemStack;

/**
 * Condition to check if the player has a certain number of empty slots in their inventory.
 */
public class EmptySlotsCondition implements OnlineCondition {

    /**
     * Number of required empty slots.
     */
    private final VariableNumber required;

    /**
     * Whether the number of empty slots should be equal.
     */
    private final boolean equal;

    /**
     * Create the empty slots condition.
     *
     * @param required the number of empty slots required
     * @param equal    whether the number of empty slots should be equal
     */
    public EmptySlotsCondition(final VariableNumber required, final boolean equal) {
        this.required = required;
        this.equal = equal;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final ItemStack[] items = profile.getPlayer().getInventory().getStorageContents();
        int empty = 0;
        for (final ItemStack item : items) {
            if (item == null) {
                empty++;
            }
        }
        return equal ? empty == required.getValue(profile).intValue() : empty >= required.getValue(profile).intValue();
    }
}
