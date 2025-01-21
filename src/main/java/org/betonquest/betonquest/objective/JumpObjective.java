package org.betonquest.betonquest.objective;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@SuppressWarnings("PMD.CommentRequired")
public class JumpObjective extends CountingObjective implements Listener {

    public JumpObjective(final Instruction instruction) throws QuestException {
        super(instruction, "times_to_jump");
        targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJump(final PlayerJumpEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
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
}
