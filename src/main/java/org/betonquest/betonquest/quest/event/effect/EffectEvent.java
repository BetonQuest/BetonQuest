package org.betonquest.betonquest.quest.event.effect;

import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Gives the player specified potion effect
 */
public class EffectEvent implements Event {

    /**
     * The effect to apply.
     */
    private final PotionEffectType effect;
    /**
     * The duration of the effect.
     */
    private final VariableNumber duration;
    /**
     * The amplifier of the effect.
     */
    private final VariableNumber amplifier;
    /**
     * Whether the effect is ambient.
     */
    private final boolean ambient;
    /**
     * Whether the effect is hidden.
     */
    private final boolean hidden;
    /**
     * Whether the effect has an icon.
     */
    private final boolean icon;

    /**
     * Create a new effect event.
     *
     * @param effect    the effect to apply
     * @param duration  the duration of the effect
     * @param amplifier the amplifier of the effect
     * @param ambient   whether the effect is ambient
     * @param hidden    whether the effect is hidden
     * @param icon      whether the effect has an icon
     */
    public EffectEvent(final PotionEffectType effect, final VariableNumber duration, final VariableNumber amplifier, final boolean ambient, final boolean hidden, final boolean icon) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.hidden = hidden;
        this.icon = icon;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        profile.getOnlineProfile().get().getPlayer().addPotionEffect(
                new PotionEffect(effect, duration.getInt(profile) * 20, amplifier.getInt(profile) - 1, ambient, !hidden, icon));
    }
}
