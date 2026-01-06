package org.betonquest.betonquest.quest.action.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Removes potion effects from the player.
 */
public class DeleteEffectAction implements OnlineAction {

    /**
     * The effects to delete.
     */
    private final Argument<List<PotionEffectType>> effects;

    /**
     * Creates a new DeleteEffect action.
     *
     * @param effects the effects to delete or an empty list to delete all effects
     */
    public DeleteEffectAction(final Argument<List<PotionEffectType>> effects) {
        this.effects = effects;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final List<PotionEffectType> resolvedEffects = effects.getValue(profile);
        if (resolvedEffects.isEmpty()) {
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        } else {
            resolvedEffects.forEach(player::removePotionEffect);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
