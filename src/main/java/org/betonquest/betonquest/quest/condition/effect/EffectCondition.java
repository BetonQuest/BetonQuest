package org.betonquest.betonquest.quest.condition.effect;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.potion.PotionEffectType;

/**
 * A condition that checks if a player has a potion effect.
 */
public class EffectCondition implements OnlineCondition {

    /**
     * The type of the potion effect.
     */
    private final PotionEffectType type;

    /**
     * Create a new effect condition.
     *
     * @param type the type of the potion effect
     */
    public EffectCondition(final PotionEffectType type) {
        this.type = type;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().hasPotionEffect(type);
    }
}
