package org.betonquest.betonquest.quest.objective.equip;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Requires the player to equip a specific item in a specific slot.
 */
public class EquipItemObjective extends Objective implements Listener {
    /**
     * Custom logger for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The item that needs to be equipped.
     */
    private final Item item;

    /**
     * The slot type where the item needs to be equipped.
     */
    private final PlayerArmorChangeEvent.SlotType slotType;

    /**
     * Constructor for the EquipItemObjective.
     *
     * @param instruction the instruction that created this objective
     * @param log         the logger for this objective
     * @param item        the item that needs to be equipped
     * @param slotType    the slot type where the item needs to be equipped
     * @throws QuestException if there is an error in the instruction
     */
    public EquipItemObjective(final Instruction instruction, final BetonQuestLogger log, final Item item, final PlayerArmorChangeEvent.SlotType slotType) throws QuestException {
        super(instruction);
        this.log = log;
        this.item = item;
        this.slotType = slotType;
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Check if the player has equipped the right item in the right slot.
     *
     * @param event the event that triggered this method
     */
    @EventHandler
    public void onEquipmentChange(final PlayerArmorChangeEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        try {
            if (containsPlayer(onlineProfile)
                    && event.getSlotType() == slotType
                    && item.matches(event.getNewItem())
                    && checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Exception while processing EquipItem Objective: " + e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
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
