package pl.betoncraft.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.api.event.item.UpgradeItemEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsUpgradeObjective extends Objective implements Listener {

    private final String itemID;
    private final String itemType;

    public MMOItemsUpgradeObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        itemType = instruction.next();
        itemID = instruction.next();
        template = ObjectiveData.class;
    }


    @EventHandler(ignoreCancelled = true)
    public void onUpgradeItem(final UpgradeItemEvent event) {
        final MMOItem upgradedItem = event.getTargetItem();
        if (!upgradedItem.getId().equals(itemID) || !upgradedItem.getType().getId().equals(itemType)) {
            return;
        }
        final String playerID = event.getPlayer().getUniqueId().toString();
        if (!containsPlayer(playerID) || !checkConditions(playerID)) {
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
