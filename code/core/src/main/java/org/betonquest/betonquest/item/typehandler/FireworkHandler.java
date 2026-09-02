package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.NumberValue;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Handles de-/serialization of Fireworks.
 */
public class FireworkHandler implements ItemMetaHandler<FireworkMeta> {

    /**
     * The empty default Constructor.
     */
    public FireworkHandler() {
    }

    @Override
    public Class<FireworkMeta> metaClass() {
        return FireworkMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("firework", "power", "firework-containing");
    }

    @Override
    @Nullable
    public String rawSerializeToString(final ItemMeta meta) {
        if (meta instanceof FireworkEffectMeta) {
            return serializeToString((FireworkEffectMeta) meta);
        }
        if (meta instanceof FireworkMeta) {
            return serializeToString((FireworkMeta) meta);
        }
        return null;
    }

    @Override
    @Nullable
    public String serializeToString(final FireworkMeta fireworkMeta) {
        if (!fireworkMeta.hasEffects()) {
            return null;
        }
        final StringBuilder builder = new StringBuilder(17);
        builder.append("firework:");
        for (final FireworkEffect effect : fireworkMeta.getEffects()) {
            appendFireworkEffect(builder, effect);
            builder.append(',');
        }
        builder.setLength(Math.max(builder.length() - 1, 0));
        builder.append(" power:").append(fireworkMeta.getPower());
        return builder.toString();
    }

    /**
     * Converts the item meta into QuestItem format.
     *
     * @param fireworkMeta the meta to serialize
     * @return parsed values with leading space or empty string
     */
    @Nullable
    public String serializeToString(final FireworkEffectMeta fireworkMeta) {
        if (!fireworkMeta.hasEffect()) {
            return null;
        }
        final FireworkEffect effect = fireworkMeta.getEffect();
        final StringBuilder builder = new StringBuilder();
        builder.append("firework:");
        appendFireworkEffect(builder, effect);
        return builder.toString();
    }

    private void appendFireworkEffect(final StringBuilder builder, final FireworkEffect effect) {
        builder.append(effect.getType()).append(':');
        for (final Color color : effect.getColors()) {
            final DyeColor dye = DyeColor.getByFireworkColor(color);
            builder.append(dye == null ? '#' + Integer.toHexString(color.asRGB()) : dye).append(';');
        }
        if (!effect.getColors().isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append(':');
        for (final Color color : effect.getFadeColors()) {
            final DyeColor dye = DyeColor.getByFireworkColor(color);
            builder.append(dye == null ? '#' + Integer.toHexString(color.asRGB()) : dye).append(';');
        }
        if (!effect.getFadeColors().isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append(':').append(effect.hasTrail()).append(':').append(effect.hasFlicker());
    }

    @Override
    @Nullable
    public Attribute parse(final Instruction instruction) throws QuestException {
        final ExistenceArgument<List<FireworkEffectHandler>> effects = ExistenceArgument.applyListOrNull("firework", instruction.parse(resolved -> {
            final FireworkEffectHandler effect = new FireworkEffectHandler();
            effect.set(resolved);
            return effect;
        }));
        final Argument<NumberValue> power = NumberValue.create("power", "firework power", instruction);
        final Optional<Argument<Boolean>> exact = instruction.bool().map(bool -> !bool).get("firework-containing");
        if (effects == null && power == null && exact.isEmpty()) {
            return null;
        }
        return new NonResolved(ExistenceArgument.fallbackEmptyList(effects), power, exact.orElse(profile -> true));
    }

    /**
     * The attribute with placeholders.
     *
     * @param effects The individual Firework Effect Handlers.
     * @param power   The firework power.
     * @param exact   If the Firework need to be exact the same or just contain all specified effects.
     */
    private record NonResolved(ExistenceArgument<List<FireworkEffectHandler>> effects,
                               @Nullable Argument<NumberValue> power, Argument<Boolean> exact)
            implements Attribute {

        @Override
        public ResolvedAttribute<FireworkMeta> resolve(@Nullable final Profile profile) throws QuestException {
            final Pair<Existence, List<FireworkEffectHandler>> pair = this.effects.getValue(profile);
            final List<FireworkEffect> effects = new LinkedList<>();
            for (final FireworkEffectHandler effect : pair.getRight()) {
                effects.add(effect.get());
            }
            final NumberValue power = this.power == null ? null : this.power.getValue(profile);
            final boolean exact = this.exact.getValue(profile);
            return new ResolvedFirework(pair.getLeft(), pair.getRight(), effects, power, exact);
        }
    }

    /**
     * Resolved Firework Handler.
     *
     * @param existence       If the single effect is required.
     * @param effects         The effect Handlers to check.
     * @param resolvedEffects The individual Firework Effect Handlers.
     * @param power           The firework power.
     * @param exact           If the Firework need to be exact the same or just contain all specified effects.
     */
    private record ResolvedFirework(Existence existence, List<FireworkEffectHandler> effects,
                                    List<FireworkEffect> resolvedEffects, @Nullable NumberValue power, boolean exact)
            implements ResolvedAttribute<FireworkMeta> {

        @Override
        public Class<FireworkMeta> metaClass() {
            return FireworkMeta.class;
        }

        @Override
        public void populate(final FireworkMeta fireworkMeta) {
            fireworkMeta.addEffects(resolvedEffects);
            if (power != null) {
                fireworkMeta.setPower(power.value());
            }
        }

        /**
         * Sets the Handler's values into the Meta.
         *
         * @param fireworkMeta the meta to populate
         */
        private void populate(final FireworkEffectMeta fireworkMeta) {
            fireworkMeta.setEffect(resolvedEffects.isEmpty() ? null : resolvedEffects.get(0));
        }

        @Override
        public boolean check(final FireworkMeta fireworkMeta) {
            return checkEffects(fireworkMeta.getEffects()) && (power == null || power.isValid(fireworkMeta.getPower()));
        }

        @Override
        public boolean rawCheck(final ItemMeta meta) {
            if (meta instanceof final FireworkMeta fireworkMeta && !check(fireworkMeta)) {
                return false;
            }
            if (meta instanceof final FireworkEffectMeta fireworkMeta) {
                return check(fireworkMeta);
            }
            return true;
        }

        @Override
        public void rawPopulate(final ItemMeta meta) {
            if (meta instanceof final FireworkMeta fireworkMeta) {
                populate(fireworkMeta);
            }
            if (meta instanceof final FireworkEffectMeta fireworkMeta) {
                populate(fireworkMeta);
            }
        }

        /**
         * Check to see if the specified ItemMeta matches the Handler.
         *
         * @param fireworkMeta the ItemMeta to check
         * @return if the meta satisfies the requirement defined via {@link #parse(Instruction)}
         */
        private boolean check(final FireworkEffectMeta fireworkMeta) {
            final FireworkEffect single = fireworkMeta.getEffect();
            return switch (existence) {
                case WHATEVER -> true;
                case REQUIRED -> single != null && !effects.isEmpty() && effects.get(0).check(single);
                case FORBIDDEN -> single == null;
            };
        }

        private boolean checkEffects(final List<FireworkEffect> list) {
            return switch (existence) {
                case WHATEVER -> true;
                case REQUIRED -> checkRequired(list);
                case FORBIDDEN -> list.isEmpty();
            };
        }

        private boolean checkRequired(final List<FireworkEffect> list) {
            if (exact && list.size() != effects.size()) {
                return false;
            }
            for (final FireworkEffectHandler checker : effects) {
                FireworkEffect effect = null;
                for (final FireworkEffect fireworkEffect : list) {
                    if (fireworkEffect.getType() == checker.getType()) {
                        effect = fireworkEffect;
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
}
