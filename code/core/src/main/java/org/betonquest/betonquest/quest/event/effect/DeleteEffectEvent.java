package org.betonquest.betonquest.quest.event.effect;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Removes potion effects from the player.
 */
public class DeleteEffectEvent implements OnlineEvent {

    /**
     * The effects to delete.
     */
    private final Variable<List<PotionEffectType>> effects;

    /**
     * Creates a new DeleteEffect event.
     *
     * @param effects the effects to delete or an empty list to delete all effects
     */
    public DeleteEffectEvent(final Variable<List<PotionEffectType>> effects) {
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
}
