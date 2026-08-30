package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.Number;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Handles de-/serialization of Potions.
 * <p>
 * Works only up to MC 1.20.4 with a breaking change for PotionData in the following version.
 */
public class PotionHandler implements ItemMetaHandler<PotionMeta> {

    /**
     * The 'extended' string.
     */
    public static final String EXTENDED = "extended";

    /**
     * The 'upgraded' string.
     */
    public static final String UPGRADED = "upgraded";

    /**
     * The empty default Constructor.
     */
    public PotionHandler() {
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
            string.append(effect.getType().getName()).append(':').append(power).append(':').append(duration).append(',');
        }
        return (effects == null ? "" : effects) + " effects:" + string.substring(0, string.length() - 1);
    }

    @Override
    public Class<PotionMeta> metaClass() {
        return PotionMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("type", EXTENDED, UPGRADED, "effects", "effects-containing");
    }

    @Override
    @Nullable
    public String serializeToString(final PotionMeta potionMeta) {
        final PotionData pData = potionMeta.getBasePotionData();
        final String baseEffect = "type:" + pData.getType() + (pData.isExtended() ? " extended" : "")
                + (pData.isUpgraded() ? " upgraded" : "");
        return addCustomEffects(potionMeta, baseEffect);
    }

    @Override
    @Nullable
    public Attribute parse(final Instruction instruction) throws QuestException {
        final ExistenceArgument<PotionType> type = ExistenceArgument.applyOrNull("type", instruction.enumeration(PotionType.class));
        final FlagArgument<Boolean> extended = instruction.bool().getFlag(EXTENDED, true);
        final FlagArgument<Boolean> upgraded = instruction.bool().getFlag(UPGRADED, true);
        final ExistenceArgument<List<CustomEffectHandler>> custom = ExistenceArgument.applyListOrNull("effects", instruction.parse(CustomEffectHandler::new));
        final Optional<Argument<Boolean>> exact = instruction.bool().map(bool -> !bool).get("effects-containing");
        if (type == null && custom == null && exact.isEmpty()
                && extended.getState() == FlagState.ABSENT && upgraded.getState() == FlagState.ABSENT) {
            return null;
        }
        return new NonResolved(type == null ? ExistenceArgument.whateverValue(PotionType.WATER) : type,
                extended, upgraded, ExistenceArgument.fallbackEmptyList(custom), exact.orElse(profile -> true));
    }

    /**
     * The attribute with placeholders.
     *
     * @param type     The potion type, defaulting to water.
     * @param extended If the potion is extended.
     * @param upgraded If the potion is upgraded.
     * @param custom   The custom potion effects.
     * @param exact    If the Potions need to be exact the same or just contain all specified effects.
     */
    private record NonResolved(ExistenceArgument<PotionType> type, FlagArgument<Boolean> extended,
                               FlagArgument<Boolean> upgraded, ExistenceArgument<List<CustomEffectHandler>> custom,
                               Argument<Boolean> exact) implements Attribute {

        @Override
        public ResolvedAttribute<PotionMeta> resolve(@Nullable final Profile profile) throws QuestException {
            final Pair<Existence, PotionType> typePair = type.getValue(profile);
            final Optional<Boolean> extended = this.extended.getValue(profile);
            final Optional<Boolean> upgraded = this.upgraded.getValue(profile);
            final Pair<Existence, List<CustomEffectHandler>> customPair = this.custom.getValue(profile);
            final boolean exact = this.exact.getValue(profile);
            return new ResolvedPotion(typePair, extended, upgraded, customPair, exact);
        }
    }

    /**
     * Resolved Potion Handler.
     *
     * @param typePair   The potion type, defaulting to water.
     * @param extended   If the potion is extended.
     * @param upgraded   If the potion is upgraded.
     * @param customPair The custom potion effects.
     * @param exact      If the Potions need to be exact the same or just contain all specified effects.
     */
    public record ResolvedPotion(Pair<Existence, PotionType> typePair, Optional<Boolean> extended,
                                 Optional<Boolean> upgraded, Pair<Existence, List<CustomEffectHandler>> customPair,
                                 boolean exact) implements ResolvedAttribute<PotionMeta> {

        @Override
        public Class<PotionMeta> metaClass() {
            return PotionMeta.class;
        }

        @Override
        public void populate(final PotionMeta potionMeta) {
            potionMeta.setBasePotionData(new PotionData(typePair.getValue(),
                    extended.orElse(false), upgraded.orElse(false)));
            for (final PotionEffect effect : getCustom()) {
                potionMeta.addCustomEffect(effect, true);
            }
        }

        @Override
        public boolean check(final PotionMeta meta) {
            return checkBase(meta.getBasePotionData()) && checkCustom(meta.getCustomEffects());
        }

        /**
         * Gets the stored custom potion effects.
         *
         * @return the custom potion effects
         */
        public List<PotionEffect> getCustom() {
            final List<PotionEffect> effects = new LinkedList<>();
            if (customPair.getLeft() == Existence.FORBIDDEN) {
                return effects;
            }
            for (final CustomEffectHandler checker : customPair.getRight()) {
                if (checker.customTypeE != Existence.FORBIDDEN) {
                    effects.add(new PotionEffect(checker.customType, checker.duration, checker.power));
                }
            }
            return effects;
        }

        private boolean checkBase(@Nullable final PotionData base) {
            return switch (typePair.getLeft()) {
                case WHATEVER -> true;
                case REQUIRED -> {
                    if (base == null || base.getType() != typePair.getRight()) {
                        yield false;
                    }
                    yield (extended.isEmpty() || base.isExtended() == extended.get())
                            && (upgraded.isEmpty() || base.isUpgraded() == upgraded.get());
                }
                default -> false;
            };
        }

        /**
         * Checks the custom effects.
         *
         * @param custom the effects to check against the stored
         * @return if the given effects satisfies the stored
         */
        public boolean checkCustom(final List<PotionEffect> custom) {

            final Existence customE = customPair.getLeft();
            if (customE == Existence.WHATEVER) {
                return true;
            }
            if (custom.isEmpty()) {
                return customE == Existence.FORBIDDEN;
            }
            if (exact && custom.size() != customPair.getRight().size()) {
                return false;
            }
            for (final CustomEffectHandler checker : customPair.getRight()) {
                PotionEffect effect = null;
                for (final PotionEffect potionEffect : custom) {
                    if (potionEffect.getType().equals(checker.customType)) {
                        effect = potionEffect;
                        break;
                    }
                }
                if (!checker.check(effect)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Handles additional potion effects.
     */
    private static final class CustomEffectHandler {

        /**
         * The expected argument count of the formatted effect.
         */
        private static final int INSTRUCTION_FORMAT_LENGTH = 3;

        /**
         * The custom potion type.
         */
        private final PotionEffectType customType;

        /**
         * The required existence.
         */
        private final Existence customTypeE;

        /**
         * The number compare state for the duration.
         */
        private final Number durationE;

        /**
         * The effect duration, in ticks.
         */
        private final int duration;

        /**
         * The effect amplifier, starting at 0.
         */
        private final int power;

        /**
         * The number compare state for the power.
         */
        private final Number powerE;

        /**
         * Create a new Custom Potion data from serialized string.
         *
         * @param custom the serialized potion data
         * @throws QuestException if the data is malformed
         */
        private CustomEffectHandler(final String custom) throws QuestException {
            final String[] parts = HandlerUtil.getSplit(custom, "Potion is null!", ":");
            if (parts[0].startsWith("none-")) {
                customTypeE = Existence.FORBIDDEN;
                customType = getType(parts[0].substring("none-".length()));
                powerE = Number.WHATEVER;
                power = 1;
                durationE = Number.WHATEVER;
                duration = 60 * 20;
                return;
            }
            customType = getType(parts[0]);
            customTypeE = Existence.REQUIRED;
            if (parts.length != INSTRUCTION_FORMAT_LENGTH) {
                throw new QuestException("Wrong effect format");
            }
            final Map.Entry<Number, Integer> effectPower = HandlerUtil.getNumberValue(parts[1], "effect power");
            powerE = effectPower.getKey();
            power = effectPower.getValue() - 1;
            if (power < 0) {
                throw new QuestException("Effect power must be a positive integer");
            }
            final Map.Entry<Number, Integer> effectDuration = HandlerUtil.getNumberValue(parts[2], "effect duration");
            durationE = effectDuration.getKey();
            duration = effectDuration.getValue() * 20;
        }

        private PotionEffectType getType(final String name) throws QuestException {
            final PotionEffectType effectType = PotionEffectType.getByName(name);
            if (effectType == null) {
                throw new QuestException("Unknown effect type '%s'!".formatted(name));
            }
            return effectType;
        }

        private boolean check(@Nullable final PotionEffect effect) {
            return switch (customTypeE) {
                case WHATEVER -> true;
                case REQUIRED -> effect != null && effect.getType().equals(customType)
                        && durationE.isValid(effect.getDuration(), duration)
                        && powerE.isValid(effect.getAmplifier(), power);
                case FORBIDDEN -> effect == null;
            };
        }
    }
}
