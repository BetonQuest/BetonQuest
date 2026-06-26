package org.betonquest.betonquest.quest.action.heal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * The heal action. Heals the player for a given amount.
 */
public class HealAction implements OnlineAction {

    /**
     * The amount of health to give the player.
     */
    private final Argument<Number> amount;

    /**
     * The operation to perform on the health.
     */
    private final Argument<HealOperation> operation;

    /**
     * Creates a new heal action.
     *
     * @param amount    the amount of health to give the player
     * @param operation the operation to perform on the health
     */
    public HealAction(final Argument<Number> amount, final Argument<HealOperation> operation) {
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        double health = player.getHealth();
        final double healAmount = amount.getValue(profile).doubleValue();
        switch (operation.getValue(profile)) {
            case ADD -> health += healAmount;
            case SET -> health = healAmount;
        }
        health = clampWithinAllowedRange(health, player.getAttribute(Attribute.GENERIC_MAX_HEALTH));
        player.setHealth(health);
    }

    private double clampWithinAllowedRange(final double value, @Nullable final AttributeInstance maxHealth) {
        final double health = Math.max(0, value);
        if (maxHealth == null) {
            return health;
        }
        return Math.min(maxHealth.getValue(), health);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
