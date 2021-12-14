package org.betonquest.betonquest.objectives;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Locale;

@SuppressWarnings({"PMD.CommentRequired","PMD.CyclomaticComplexity"})
public class EquipItemObjective extends Objective implements Listener {

    private final QuestItem questItems;
    private final PlayerArmorChangeEvent.SlotType slotType;

    public EquipItemObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        final String slot = instruction.next();
        questItems = instruction.getQuestItem();

        switch (slot.toLowerCase(Locale.ROOT)){
            case "cap":
            case "helm":
            case "helmet":
            case "hat":
            case "head":
                slotType = PlayerArmorChangeEvent.SlotType.HEAD;
                break;
            case "armor":
            case "chest":
            case "chestplate":
                slotType = PlayerArmorChangeEvent.SlotType.CHEST;
                break;
            case "leggings":
            case "pants":
            case "legs":
                slotType = PlayerArmorChangeEvent.SlotType.LEGS;
                break;
            case "boots":
            case "shoes":
            case "feet":
                slotType = PlayerArmorChangeEvent.SlotType.FEET;
                break;
            default:
                throw new InstructionParseException("Slot " + slot + " is Invalid Please Use Valid Slot {HEAD, CHEST, LEGS, FEET}");
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler
    public void onEquipmentChange(final PlayerArmorChangeEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (event.getSlotType() == slotType
                && questItems.compare(event.getNewItem())
                && checkConditions(playerID)
                && containsPlayer(playerID)) {
                completeObjective(playerID);
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
    public String getProperty(final String name, final String playerID) {
        return "";
    }
}
