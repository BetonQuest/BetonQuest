package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Requires the player to kill a target player.
 */
public class KillPlayerObjective extends CountingObjective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The name of the victim to kill.
     */
    @Nullable
    private final String name;

    /**
     * The conditions of the victim that must be met for the objective to count.
     */
    private final VariableList<ConditionID> required;

    /**
     * Constructor for the KillPlayerObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param log          the logger for this objective
     * @param targetAmount the amount of players to kill
     * @param name         the name of the player to kill, or null for any player
     * @param required     the conditions of the victim that must be met for the objective to count
     * @throws QuestException if there is an error in the instruction
     */
    public KillPlayerObjective(final Instruction instruction, final BetonQuestLogger log, final Variable<Number> targetAmount,
                               @Nullable final String name, final VariableList<ConditionID> required) throws QuestException {
        super(instruction, targetAmount, "players_to_kill");
        this.log = log;
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

            try {
                if (containsPlayer(killer)
                        && (name == null || event.getEntity().getName().equalsIgnoreCase(name))
                        && BetonQuest.getInstance().getQuestTypeAPI().conditions(victim, required.getValue(victim))
                        && checkConditions(killer)) {

                    getCountingData(killer).progress();
                    completeIfDoneOrNotify(killer);
                }
            } catch (final QuestException e) {
                log.warn(instruction.getPackage(), "Failed to resolve victim conditions for kill objective: " + e.getMessage(), e);
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
