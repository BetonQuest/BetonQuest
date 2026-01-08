package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective;

import net.Indyuce.mmoitems.api.event.item.ApplyGemStoneEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * An objective that listens for the player applying a gem to their MMOItems item.
 */
public class MMOItemsApplyGemObjective extends DefaultObjective {

    /**
     * The ID of the item to be upgraded.
     */
    private final Argument<String> itemID;

    /**
     * The type of the item to be upgraded.
     */
    private final Argument<String> itemType;

    /**
     * The ID of the gem to be applied.
     */
    private final Argument<String> gemID;

    /**
     * Constructor for the MMOItemsApplyGemObjective.
     *
     * @param service  the objective factory service
     * @param itemID   the ID of the item to be upgraded
     * @param itemType the type of the item to be upgraded
     * @param gemID    the ID of the gem to be applied
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public MMOItemsApplyGemObjective(final ObjectiveFactoryService service, final Argument<String> itemID, final Argument<String> itemType, final Argument<String> gemID) throws QuestException {
        super(service);
        this.itemID = itemID;
        this.itemType = itemType;
        this.gemID = gemID;
    }

    /**
     * Checks if the apply gem event matches the objective's item ID and type.
     *
     * @param event   the apply gem event
     * @param profile the player
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onApplyGem(final ApplyGemStoneEvent event, final OnlineProfile profile) throws QuestException {
        final MMOItem upgradedItem = event.getTargetItem();
        if (!upgradedItem.getId().equals(itemID.getValue(profile))
                || !upgradedItem.getType().getId().equals(itemType.getValue(profile))) {
            return;
        }
        final MMOItem gemStone = event.getGemStone();
        if (!gemStone.getId().equals(gemID.getValue(profile))) {
            return;
        }
        completeObjective(profile);
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
