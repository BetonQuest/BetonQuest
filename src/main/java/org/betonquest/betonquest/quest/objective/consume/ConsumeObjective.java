package org.betonquest.betonquest.quest.objective.consume;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * Requires the player to consume an item (eat food or drink a potion).
 */
public class ConsumeObjective extends CountingObjective implements Listener {

    /**
     * Custom logger for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The item to consume.
     */
    private final Item item;

    /**
     * Constructs a new {@code ConsumeObjective} for the given {@code Instruction}.
     *
     * @param instruction  the instruction out of a quest package
     * @param targetAmount the amount of items to consume
     * @param log          the logger for this objective
     * @param item         the item to consume
     * @throws QuestException if the instruction is invalid
     */
    public ConsumeObjective(final Instruction instruction, final Variable<Number> targetAmount, final BetonQuestLogger log,
                            final Item item) throws QuestException {
        super(instruction, targetAmount, null);
        this.log = log;
        this.item = item;
    }

    /**
     * The listener that handles a consumed item.
     *
     * @param event the Bukkit event for consuming an item
     */
    @EventHandler(ignoreCancelled = true)
    public void onConsume(final PlayerItemConsumeEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        try {
            if (containsPlayer(onlineProfile) && item.matches(event.getItem()) && checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress();
                completeIfDoneOrNotify(onlineProfile);
            }
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Exception while processing Consume Objective: " + e.getMessage(), e);
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
