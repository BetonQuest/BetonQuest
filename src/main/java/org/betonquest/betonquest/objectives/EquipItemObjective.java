package org.betonquest.betonquest.objectives;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.apache.commons.lang3.EnumUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Locale;

@SuppressWarnings({"PMD.CommentRequired", "PMD.CyclomaticComplexity"})
public class EquipItemObjective extends Objective implements Listener {
    private final QuestItem questItems;

    private final PlayerArmorChangeEvent.SlotType slotType;

    public EquipItemObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        final String slot = instruction.next();
        questItems = instruction.getQuestItem();

        if (!EnumUtils.isValidEnum(PlayerArmorChangeEvent.SlotType.class, slot)) {
            throw new QuestException("Slot " + slot + " is Invalid Please Use Valid Slot {HEAD, CHEST, LEGS, FEET}");
        }

        slotType = PlayerArmorChangeEvent.SlotType.valueOf(slot.toUpperCase(Locale.ROOT));
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler
    public void onEquipmentChange(final PlayerArmorChangeEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile)
                && event.getSlotType() == slotType
                && questItems.compare(event.getNewItem())
                && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
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
