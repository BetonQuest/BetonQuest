package org.betonquest.betonquest.quest.objective.equip;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * Requires the player to equip a specific item in a specific slot.
 */
public class EquipItemObjective extends DefaultObjective {

    /**
     * The item that needs to be equipped.
     */
    private final Argument<ItemWrapper> item;

    /**
     * The slot type where the item needs to be equipped.
     */
    private final Argument<PlayerArmorChangeEvent.SlotType> slotType;

    /**
     * Constructor for the EquipItemObjective.
     *
     * @param service  the objective factory service
     * @param item     the item that needs to be equipped
     * @param slotType the slot type where the item needs to be equipped
     * @throws QuestException if there is an error in the instruction
     */
    public EquipItemObjective(final ObjectiveService service, final Argument<ItemWrapper> item,
                              final Argument<PlayerArmorChangeEvent.SlotType> slotType) throws QuestException {
        super(service);
        this.item = item;
        this.slotType = slotType;
    }

    /**
     * Check if the player has equipped the right item in the right slot.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that equipped the item
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onEquipmentChange(final PlayerArmorChangeEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (event.getSlotType() == slotType.getValue(onlineProfile)
                && item.getValue(onlineProfile).matches(event.getNewItem(), onlineProfile)) {
            getService().complete(onlineProfile);
        }
    }
}
