package org.betonquest.betonquest.mc_1_20_6.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.item.typehandler.Existence;
import org.betonquest.betonquest.item.typehandler.PotionHandler;
import org.bukkit.Keyed;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Handles de-/serialization of Potions.
 */
public class UpdatedPotionHandler extends PotionHandler {

    /**
     * Prefix indicating 'extended' potion types.
     */
    private static final String LONG_PREFIX = "long_";

    /**
     * Prefix indicating 'upgraded' potion types.
     */
    private static final String STRONG_PREFIX = "strong_";

    /**
     * The empty default Constructor.
     */
    public UpdatedPotionHandler() {
        super();
    }

    @Nullable
    private static String addCustomEffects(final PotionMeta potionMeta, @Nullable final String effects) {
        final List<PotionEffect> customEffects = potionMeta.getCustomEffects();
        if (customEffects.isEmpty()) {
            return effects;
        }
        final StringBuilder string = new StringBuilder();
        for (final PotionEffect effect : customEffects) {
            final int power = effect.getAmplifier() + 1;
            final int duration = (effect.getDuration() - (effect.getDuration() % 20)) / 20;
            string.append(effect.getType().getKey().asMinimalString())
                    .append(':').append(power).append(':').append(duration).append(',');
        }
        return (effects == null ? "" : effects) + " effects:" + string.substring(0, string.length() - 1);
    }

    @Override
    @Nullable
    public String serializeToString(final PotionMeta potionMeta) {
        return addCustomEffects(potionMeta, getBasePotionEffects(potionMeta));
    }

    @Override
    public void set(final String key, final String data) throws QuestException {
        super.set(key, data);
        if (EXTENDED.equals(data)) {
            typeSet(LONG_PREFIX);
        } else if (UPGRADED.equals(data)) {
            typeSet(STRONG_PREFIX);
        }
    }

    private void typeSet(final String prefix) throws QuestException {
        final String potionType = prefix + type.getKey().asMinimalString();
        try {
            type = PotionType.valueOf(potionType.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Invalid potion type: " + potionType, e);
        }
    }

    @Override
    public void populate(final PotionMeta potionMeta) {
        potionMeta.setBasePotionType(type);
        for (final PotionEffect effect : getCustom()) {
            potionMeta.addCustomEffect(effect, true);
        }
    }

    @Override
    public boolean check(final PotionMeta meta) {
        return checkBase(meta.getBasePotionType()) && checkCustom(meta.getCustomEffects());
    }

    @Nullable
    private String getBasePotionEffects(final PotionMeta potionMeta) {
        final Keyed type = potionMeta.getBasePotionType();
        if (type == null) {
            return null;
        }
        final String minimalString = type.getKey().asMinimalString();
        final String effects;
        if (minimalString.startsWith(LONG_PREFIX)) {
            effects = minimalString.substring(LONG_PREFIX.length()) + " extended";
        } else if (minimalString.startsWith(STRONG_PREFIX)) {
            effects = minimalString.substring(STRONG_PREFIX.length()) + " upgraded";
        } else {
            effects = minimalString;
        }
        return "type:" + effects;
    }

    private boolean checkBase(@Nullable final PotionType base) {
        switch (typeE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                if (base != type) {
                    return false;
                }
                final String key = base.getKey().getKey();
                if (extendedE == Existence.REQUIRED && key.startsWith(LONG_PREFIX) == extended) {
                    return false;
                }
                return upgradedE != Existence.REQUIRED || key.startsWith(STRONG_PREFIX) == upgraded;
            default:
                return false;
        }
    }
}
