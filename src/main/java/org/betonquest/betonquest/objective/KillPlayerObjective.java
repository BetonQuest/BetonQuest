package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class KillPlayerObjective extends CountingObjective implements Listener {
    @Nullable
    private final String name;

    private final List<ConditionID> required;

    public KillPlayerObjective(final Instruction instruction) throws QuestException {
        super(instruction, "players_to_kill");
        targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        name = instruction.getOptional("name");
        required = instruction.getIDList(instruction.getOptional("required"), ConditionID::new);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @EventHandler(ignoreCancelled = true)
    public void onKill(final PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            final OnlineProfile victim = PlayerConverter.getID(event.getEntity());
            final OnlineProfile killer = PlayerConverter.getID(event.getEntity().getKiller());

            if (containsPlayer(killer)
                    && (name == null || event.getEntity().getName().equalsIgnoreCase(name))
                    && BetonQuest.conditions(victim, required)
                    && checkConditions(killer)) {

                getCountingData(killer).progress();
                completeIfDoneOrNotify(killer);
            }
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
