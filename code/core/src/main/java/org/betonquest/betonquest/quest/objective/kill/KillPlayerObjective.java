package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Requires the player to kill a target player.
 */
public class KillPlayerObjective extends CountingObjective {

    /**
     * The name of the victim to kill.
     */
    @Nullable
    private final Argument<String> name;

    /**
     * The conditions of the victim that must be met for the objective to count.
     */
    private final Argument<List<ConditionID>> required;

    /**
     * Constructor for the KillPlayerObjective.
     *
     * @param service      the objective factory service
     * @param targetAmount the amount of players to kill
     * @param name         the name of the player to kill, or null for any player
     * @param required     the conditions of the victim that must be met for the objective to count
     * @throws QuestException if there is an error in the instruction
     */
    public KillPlayerObjective(final ObjectiveFactoryService service, final Argument<Number> targetAmount,
                               @Nullable final Argument<String> name, final Argument<List<ConditionID>> required) throws QuestException {
        super(service, targetAmount, "players_to_kill");
        this.name = name;
        this.required = required;
    }

    /**
     * Check if the player is the killer of the victim.
     *
     * @param event  the PlayerDeathEvent
     * @param killer the profile of the killer
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onKill(final PlayerDeathEvent event, final OnlineProfile killer) throws QuestException {
        final OnlineProfile victim = profileProvider.getProfile(event.getEntity());
        if (containsPlayer(killer)
                && (name == null || event.getEntity().getName().equalsIgnoreCase(name.getValue(killer)))
                && BetonQuest.getInstance().getQuestTypeApi().conditions(victim, required.getValue(victim))
                && checkConditions(killer)) {

            getCountingData(killer).progress();
            completeIfDoneOrNotify(killer);
        }
    }
}
