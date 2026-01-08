package org.betonquest.betonquest.quest.objective.consume;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * Requires the player to consume an item (eat food or drink a potion).
 */
public class ConsumeObjective extends CountingObjective {

    /**
     * The item to consume.
     */
    private final Argument<ItemWrapper> item;

    /**
     * Constructs a new {@code ConsumeObjective} for the given {@code Instruction}.
     *
     * @param service      the objective factory service
     * @param targetAmount the amount of items to consume
     * @param item         the item to consume
     * @throws QuestException if the instruction is invalid
     */
    public ConsumeObjective(final ObjectiveFactoryService service, final Argument<Number> targetAmount,
                            final Argument<ItemWrapper> item) throws QuestException {
        super(service, targetAmount, null);
        this.item = item;
    }

    /**
     * The listener that handles a consumed item.
     *
     * @param event         the Bukkit event for consuming an item
     * @param onlineProfile the profile of the player that consumed the item
     */
    public void onConsume(final PlayerItemConsumeEvent event, final OnlineProfile onlineProfile) {
        qeHandler.handle(() -> {
            if (containsPlayer(onlineProfile)
                    && item.getValue(onlineProfile).matches(event.getItem(), onlineProfile)
                    && checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress();
                completeIfDoneOrNotify(onlineProfile);
            }
        });
    }
}
