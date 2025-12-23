package org.betonquest.betonquest.quest.objective.equip;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.QuestItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Requires the player to equip a specific item in a specific slot.
 */
public class EquipItemObjective extends Objective implements Listener {

    /**
     * The item that needs to be equipped.
     */
    private final Variable<QuestItemWrapper> item;

    /**
     * The slot type where the item needs to be equipped.
     */
    private final Variable<PlayerArmorChangeEvent.SlotType> slotType;

    /**
     * Constructor for the EquipItemObjective.
     *
     * @param instruction the instruction that created this objective
     * @param item        the item that needs to be equipped
     * @param slotType    the slot type where the item needs to be equipped
     * @throws QuestException if there is an error in the instruction
     */
    public EquipItemObjective(final Instruction instruction, final Variable<QuestItemWrapper> item,
                              final Variable<PlayerArmorChangeEvent.SlotType> slotType) throws QuestException {
        super(instruction);
        this.item = item;
        this.slotType = slotType;
    }

    /**
     * Check if the player has equipped the right item in the right slot.
     *
     * @param event the event that triggered this method
     */
    @EventHandler
    public void onEquipmentChange(final PlayerArmorChangeEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        qeHandler.handle(() -> {
            if (containsPlayer(onlineProfile)
                    && event.getSlotType() == slotType.getValue(onlineProfile)
                    && item.getValue(onlineProfile).matches(event.getNewItem(), onlineProfile)
                    && checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
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
