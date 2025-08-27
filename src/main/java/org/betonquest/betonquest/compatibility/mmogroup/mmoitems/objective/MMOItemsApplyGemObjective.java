package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import net.Indyuce.mmoitems.api.event.item.ApplyGemStoneEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * An objective that listens for the player applying a gem to their MMOItems item.
 */
public class MMOItemsApplyGemObjective extends Objective implements Listener {
    /**
     * The ID of the item to be upgraded.
     */
    private final String itemID;

    /**
     * The type of the item to be upgraded.
     */
    private final String itemType;

    /**
     * The ID of the gem to be applied.
     */
    private final String gemID;

    /**
     * Constructor for the MMOItemsApplyGemObjective.
     *
     * @param instruction the instruction object representing the objective
     * @param itemID      the ID of the item to be upgraded
     * @param itemType    the type of the item to be upgraded
     * @param gemID       the ID of the gem to be applied
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public MMOItemsApplyGemObjective(final Instruction instruction, final String itemID, final String itemType, final String gemID) throws QuestException {
        super(instruction);
        this.itemID = itemID;
        this.itemType = itemType;
        this.gemID = gemID;
    }

    /**
     * Checks if the apply gem event matches the objective's item ID and type.
     *
     * @param event the apply gem event
     */
    @EventHandler(ignoreCancelled = true)
    public void onApplyGem(final ApplyGemStoneEvent event) {
        final MMOItem upgradedItem = event.getTargetItem();
        if (!upgradedItem.getId().equals(itemID) || !upgradedItem.getType().getId().equals(itemType)) {
            return;
        }
        final MMOItem gemStone = event.getGemStone();
        if (!gemStone.getId().equals(gemID)) {
            return;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }
        completeObjective(onlineProfile);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
