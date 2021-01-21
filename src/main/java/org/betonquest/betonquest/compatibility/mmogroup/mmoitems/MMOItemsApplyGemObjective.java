package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.api.event.item.ApplyGemStoneEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsApplyGemObjective extends Objective implements Listener {

    private final String itemID;
    private final String itemType;
    private final String gemID;

    public MMOItemsApplyGemObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;

        itemType = instruction.next();
        itemID = instruction.next();
        gemID = instruction.next();
    }

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
        final String playerID = event.getPlayer().getUniqueId().toString();
        if (!containsPlayer(playerID) && !checkConditions(playerID)) {
            return;
        }
        completeObjective(playerID);
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
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
