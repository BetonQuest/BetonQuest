package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Requires the player to kill a target player.
 */
public class KillPlayerObjective extends CountingObjective implements Listener {

    /**
     * The name of the victim to kill.
     */
    @Nullable
    private final Variable<String> name;

    /**
     * The conditions of the victim that must be met for the objective to count.
     */
    private final Variable<List<ConditionID>> required;

    /**
     * Constructor for the KillPlayerObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the amount of players to kill
     * @param name         the name of the player to kill, or null for any player
     * @param required     the conditions of the victim that must be met for the objective to count
     * @throws QuestException if there is an error in the instruction
     */
    public KillPlayerObjective(final Instruction instruction, final Variable<Number> targetAmount,
                               @Nullable final Variable<String> name, final Variable<List<ConditionID>> required) throws QuestException {
        super(instruction, targetAmount, "players_to_kill");
        this.name = name;
        this.required = required;
    }

    /**
     * Check if the player is the killer of the victim.
     *
     * @param event the PlayerDeathEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onKill(final PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            final OnlineProfile victim = profileProvider.getProfile(event.getEntity());
            final OnlineProfile killer = profileProvider.getProfile(event.getEntity().getKiller());
            qeHandler.handle(() -> {
                if (containsPlayer(killer)
                        && (name == null || event.getEntity().getName().equalsIgnoreCase(name.getValue(killer)))
                        && BetonQuest.getInstance().getQuestTypeAPI().conditions(victim, required.getValue(victim))
                        && checkConditions(killer)) {

                    getCountingData(killer).progress();
                    completeIfDoneOrNotify(killer);
                }
            });
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
