package org.betonquest.betonquest.quest.event.hunger;

import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

/**
 * The hunger event, changing the hunger of a player.
 */
public class HungerEvent implements Event {
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
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        player.setFoodLevel(hunger.calculate(player, amount.getInt(profile)));
    }
}
