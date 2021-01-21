package org.betonquest.betonquest.objectives;

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
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * Requires the player to consume an item (eat food or drink s potion).
 */
@SuppressWarnings("PMD.CommentRequired")
public class ConsumeObjective extends Objective implements Listener {

    private final QuestItem item;

    public ConsumeObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        item = instruction.getQuestItem();
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(final PlayerItemConsumeEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID) && item.compare(event.getItem()) && checkConditions(playerID)) {
            completeObjective(playerID);
        }
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
