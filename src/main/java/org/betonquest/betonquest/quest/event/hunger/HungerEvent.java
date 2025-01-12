package org.betonquest.betonquest.quest.event.hunger;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.entity.Player;

/**
 * The hunger event, changing the hunger of a player.
 */
public class HungerEvent implements OnlineEvent {
    /**
     * The hunger type, how the amount will be applied to the players hunger.
     */
    private final Hunger hunger;

    /**
     * The amount of hunger to apply.
     */
    private final VariableNumber amount;

    /**
     * Create the hunger event to set the given state.
     *
     * @param hunger the hunger type
     * @param amount the amount of hunger to apply
     */
    public HungerEvent(final Hunger hunger, final VariableNumber amount) {
        this.hunger = hunger;
        this.amount = amount;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        player.setFoodLevel(hunger.calculate(player, amount.getValue(profile).intValue()));
    }
}
