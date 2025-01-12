package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
public class KillPlayerObjective extends CountingObjective implements Listener {
    @Nullable
    private final String name;

    private final ConditionID[] required;

    public KillPlayerObjective(final Instruction instruction) throws QuestException {
        super(instruction, "players_to_kill");
        targetAmount = instruction.getVarNum(VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        name = instruction.getOptional("name");
        required = instruction.getList(instruction.getOptional("required"), instruction::getCondition)
                .toArray(new ConditionID[0]);
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
