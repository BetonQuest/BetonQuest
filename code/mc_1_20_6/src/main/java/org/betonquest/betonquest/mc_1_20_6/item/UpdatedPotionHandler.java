package org.betonquest.betonquest.mc_1_20_6.item;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.betonquest.betonquest.item.typehandler.PotionHandler;
import org.bukkit.Keyed;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

    @Nullable
    private String getBasePotionEffects(final PotionMeta potionMeta) {
        final Keyed type = potionMeta.getBasePotionType();
        if (type == null) {
            return null;
        }
        final String minimalString = type.getKey().asMinimalString();
        final String effect;
        if (minimalString.startsWith(LONG_PREFIX)) {
            effect = minimalString.substring(LONG_PREFIX.length()) + " extended";
        } else if (minimalString.startsWith(STRONG_PREFIX)) {
            effect = minimalString.substring(STRONG_PREFIX.length()) + " upgraded";
        } else {
            effect = minimalString;
        }
        return "type:" + effect;
    }

    @Override
    @Nullable
    public Attribute<PotionMeta> parse(final Instruction instruction) throws QuestException {
        final Attribute<PotionMeta> parsed = super.parse(instruction);
        if (parsed == null) {
            return null;
        }
        return new UpdatedNonResolved(parsed);
    }

    /**
     * The attribute with placeholders.
     *
     * @param attribute the parent parsed attribute
     */
    private record UpdatedNonResolved(Attribute<PotionMeta> attribute) implements Attribute<PotionMeta> {

        private PotionType typeSet(final String prefix, final String baseType) throws QuestException {
            final String potionType = prefix + baseType;
            try {
                return PotionType.valueOf(potionType.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Invalid potion type: " + potionType, e);
            }
        }

        @Override
        public ResolvedAttribute<PotionMeta> resolve(final @Nullable Profile profile) throws QuestException {
            final ResolvedPotion resolved = (ResolvedPotion) attribute.resolve(profile);

            final PotionType superPotionType = resolved.typePair().getRight();
            final String baseType = superPotionType.getKey().asMinimalString();
            final PotionType potionType;
            if (resolved.extended().orElse(false)) {
                potionType = typeSet(LONG_PREFIX, baseType);
            } else if (resolved.upgraded().orElse(false)) {
                potionType = typeSet(STRONG_PREFIX, baseType);
            } else {
                potionType = superPotionType;
            }
            return new UpdateResolved(resolved, potionType, baseType);
        }
    }

    /**
     * The resolved attribute.
     *
     * @param resolved       the parent parsed attribute
     * @param basePotionType the final potion type to use
     * @param baseType       the base potion type to use for checking
     */
    private record UpdateResolved(ResolvedPotion resolved, PotionType basePotionType, String baseType)
            implements ResolvedAttribute<PotionMeta> {

        @Override
        public Class<PotionMeta> metaClass() {
            return PotionMeta.class;
        }

        @Override
        public void populate(final PotionMeta potionMeta) {
            potionMeta.setBasePotionType(basePotionType);
            for (final PotionEffect effect : resolved.getCustom()) {
                potionMeta.addCustomEffect(effect, true);
            }
        }

        @Override
        public boolean check(final PotionMeta potionMeta) {
            return checkBase(potionMeta.getBasePotionType()) && resolved.checkCustom(potionMeta.getCustomEffects());
        }

        private boolean checkBase(@Nullable final PotionType base) {
            final Pair<Existence, PotionType> pair = resolved.typePair();
            return switch (pair.getLeft()) {
                case WHATEVER -> true;
                case REQUIRED -> {
                    if (base == null || !base.getKey().getNamespace().equals(basePotionType.getKey().getNamespace())) {
                        yield false;
                    }
                    final String key = base.getKey().getKey();
                    final String effect;
                    if (key.startsWith(LONG_PREFIX)) {
                        effect = key.substring(LONG_PREFIX.length());
                    } else if (key.startsWith(STRONG_PREFIX)) {
                        effect = key.substring(STRONG_PREFIX.length());
                    } else {
                        effect = key;
                    }

                    if (!effect.equals(baseType)) {
                        // TODO definitive test that here
                        //   I don't know if we need to have that at all in the old code.
                        yield false;
                    }
                    final Optional<Boolean> extended = resolved.extended();
                    final Optional<Boolean> upgraded = resolved.upgraded();
                    yield (extended.isEmpty() || key.startsWith(LONG_PREFIX) == extended.get())
                            && (upgraded.isEmpty() || key.startsWith(STRONG_PREFIX) == upgraded.get());
                }
                default -> false;
            };
        }
    }
}
