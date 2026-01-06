package org.betonquest.betonquest.quest.action.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Gives the player specified potion effect.
 */
public class EffectAction implements OnlineAction {

    /**
     * The effect to apply.
     */
    private final PotionEffectType effect;

    /**
     * The duration of the effect.
     */
    private final Argument<Number> duration;

    /**
     * The amplifier of the effect.
     */
    private final Argument<Number> level;

    /**
     * Whether the effect is ambient.
     */
    private final FlagArgument<Boolean> ambient;

    /**
     * Whether the effect is hidden.
     */
    private final FlagArgument<Boolean> hidden;

    /**
     * Whether the effect has not an icon.
     */
    private final FlagArgument<Boolean> noicon;

    /**
     * Create a new effect action.
     *
     * @param effect   the effect to apply
     * @param duration the duration of the effect
     * @param level    the level of the effect
     * @param ambient  whether the effect is ambient
     * @param hidden   whether the effect is hidden
     * @param noicon   whether the effect has an icon
     */
    public EffectAction(final PotionEffectType effect, final Argument<Number> duration, final Argument<Number> level,
                        final FlagArgument<Boolean> ambient, final FlagArgument<Boolean> hidden, final FlagArgument<Boolean> noicon) {
        this.effect = effect;
        this.duration = duration;
        this.level = level;
        this.ambient = ambient;
        this.hidden = hidden;
        this.noicon = noicon;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final int durationInt = duration.getValue(profile).intValue();
        profile.getPlayer().addPotionEffect(new PotionEffect(
                effect,
                durationInt == -1 ? -1 : durationInt * 20,
                level.getValue(profile).intValue() - 1,
                ambient.getValue(profile).orElse(false),
                !hidden.getValue(profile).orElse(false),
                !noicon.getValue(profile).orElse(false)
        ));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
