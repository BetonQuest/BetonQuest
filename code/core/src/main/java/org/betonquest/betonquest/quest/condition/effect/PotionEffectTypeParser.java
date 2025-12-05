package org.betonquest.betonquest.quest.condition.effect;

import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.potion.PotionEffectType;

/**
 * Parses a string to a {@link PotionEffectType}.
 */
public class PotionEffectTypeParser implements Argument<PotionEffectType> {

    /**
     * Parser for {@link PotionEffectType}s.
     */
    public static final PotionEffectTypeParser POTION_EFFECT_TYPE = new PotionEffectTypeParser();

    /**
     * Creates a new parser.
     */
    public PotionEffectTypeParser() {
    }

    @Override
    public PotionEffectType apply(final String string) throws QuestException {
        final PotionEffectType type = PotionEffectType.getByName(string);
        if (type == null) {
            throw new QuestException("PotionEffectType '" + string + "' does not exist");
        }
        return type;
    }
}
