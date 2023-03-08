package org.betonquest.betonquest.quest.event.effect;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Removes potion effects from the player
 */
public class DeleteEffectEvent implements Event {

    /**
     * The effects to delete.
     */
    private final List<PotionEffectType> effects;

    /**
     * Creates a new DeleteEffect event.
     *
     * @param effects the effects to delete
     */
    public DeleteEffectEvent(final List<PotionEffectType> effects) {
        this.effects = effects;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        if (effects.isEmpty()) {
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        } else {
            effects.forEach(player::removePotionEffect);
        }
    }
}
