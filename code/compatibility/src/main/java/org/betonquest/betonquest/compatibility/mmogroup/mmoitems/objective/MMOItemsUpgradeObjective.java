package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import net.Indyuce.mmoitems.api.event.item.UpgradeItemEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * An objective that listens for the player upgrading their MMOItems item.
 */
public class MMOItemsUpgradeObjective extends DefaultObjective {

    /**
     * The ID of the item to be upgraded.
     */
    private final Argument<String> itemID;

    /**
     * The type of the item to be upgraded.
     */
    private final Argument<String> itemType;

    /**
     * Constructor for the MMOItemsUpgradeObjective.
     *
     * @param instruction the instruction object representing the objective
     * @param itemID      the ID of the item to be upgraded
     * @param itemType    the type of the item to be upgraded
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public MMOItemsUpgradeObjective(final Instruction instruction, final Argument<String> itemID, final Argument<String> itemType) throws QuestException {
        super(instruction);
        this.itemID = itemID;
        this.itemType = itemType;
    }

    /**
     * Checks if the upgrade item event matches the objective's item ID and type.
     *
     * @param event   the upgrade item event
     * @param profile the player
     */
    public void onUpgradeItem(final UpgradeItemEvent event, final OnlineProfile profile) {
        qeHandler.handle(() -> {
            final MMOItem upgradedItem = event.getTargetItem();
            if (!upgradedItem.getId().equals(itemID.getValue(profile))
                    || !upgradedItem.getType().getId().equals(itemType.getValue(profile))) {
                return;
            }
            if (!containsPlayer(profile) || !checkConditions(profile)) {
                return;
            }
            completeObjective(profile);
        });
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
