package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles de-/serialization of Potions.
 * <p>
 * Works only up to MC 1.20.4 with a breaking change for PotionData in the following version.
 */
@SuppressWarnings("PMD.TooManyMethods")
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
     * The potion type, defaulting to water.
     */
    protected ExistenceArgument<PotionType> type = ExistenceArgument.whateverValue(PotionType.WATER);

    /**
     * If the potion is extended.
     */
    protected ExistenceArgument<Boolean> extended = ExistenceArgument.whateverNullValue();

    /**
     * If the potion is upgraded.
     */
    protected ExistenceArgument<Boolean> upgraded = ExistenceArgument.whateverNullValue();


    /**
     * The custom potion effects.
     */
    private ExistenceArgument<List<CustomEffectHandler>> custom = ExistenceArgument.whateverEmptyList();

    /**
     * If the Potions need to be exact the same or just contain all specified effects.
     */
    private Argument<Boolean> exact = new DefaultArgument<>(true);

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
    public void set(final Instruction instruction) throws QuestException {
        this.type = ExistenceArgument.apply("type", instruction.enumeration(PotionType.class)), PotionType.WATER));
        this.extended = HandlerUtil.isKeyOrTrue(EXTENDED, instruction);
        this.upgraded = HandlerUtil.isKeyOrTrue(UPGRADED, instruction);
        this.custom = ExistenceArgument.applyList("effects", instruction.parse(CustomEffectHandler::new));
        this.exact = instruction.bool().map(bool -> !bool).get("effects-containing", true);
    }

    @Override
    public void populate(final PotionMeta potionMeta, @Nullable final Profile profile) throws QuestException {
        potionMeta.setBasePotionData(new PotionData(type.getValue(profile).getValue(),
                extended.getValue(profile).getValue(), upgraded.getValue(profile).getValue()));
        for (final PotionEffect effect : getCustom(profile)) {
            potionMeta.addCustomEffect(effect, true);
        }
    }

    @Override
    public boolean check(final PotionMeta meta, @Nullable Profile profile) throws QuestException {
        return checkBase(meta.getBasePotionData(), profile) && checkCustom(meta.getCustomEffects(), profile);
    }

    /**
     * Gets the stored custom potion effects.
     *@param profile the optional profile for resolving arguments
     * @return the custom potion effects
     * @throws QuestException when there is an exception while resolving profile specific data
     */
    protected List<PotionEffect> getCustom(@Nullable Profile profile) throws QuestException {
        final Pair<Existence, List<CustomEffectHandler>> pair = custom.getValue(profile);
        final List<PotionEffect> effects = new LinkedList<>();
        if (pair.getLeft() == Existence.FORBIDDEN) {
            return effects;
        }
        for (final CustomEffectHandler checker : pair.getRight()) {
            if (checker.customTypeE != Existence.FORBIDDEN) {
                effects.add(new PotionEffect(checker.customType, checker.duration, checker.power));
            }
        }
        return effects;
    }

    private boolean checkBase(@Nullable final PotionData base, @Nullable Profile profile) throws QuestException {
        final Pair<Existence, PotionType> pair = type.getValue(profile);
        return switch (pair.getLeft()) {
            case WHATEVER -> true;
            case REQUIRED -> {
                if (base == null || base.getType() != pair.getRight()) {
                    yield false;
                }
                final Pair<Existence, Boolean> extended = this.extended.getValue(profile);
                final Pair<Existence, Boolean> upgraded = this.upgraded.getValue(profile);
                yield (extended.getLeft() != Existence.REQUIRED || base.isExtended() == extended.getRight())
                        && (upgraded.getLeft() != Existence.REQUIRED || base.isUpgraded() == upgraded.getRight());
            }
            default -> false;
        };
    }

    /**
     * Checks the custom effects.
     * @param custom the effects to check against the stored
     *@param profile the optional profile for resolving arguments
     * @throws QuestException when there is an exception while resolving profile specific data
     * @return if the given effects satisfies the stored
     */
    protected boolean checkCustom(final List<PotionEffect> custom, @Nullable final Profile profile) throws QuestException {
        final Pair<Existence, List<CustomEffectHandler>> pair = this.custom.getValue(profile);
        final Existence customE = pair.getLeft();
        if (customE == Existence.WHATEVER) {
            return true;
        }
        if (custom.isEmpty()) {
            return customE == Existence.FORBIDDEN;
        }
        if (exact.getValue(profile) && custom.size() != pair.getRight().size()) {
            return false;
        }
        for (final CustomEffectHandler checker : pair.getRight()) {
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
