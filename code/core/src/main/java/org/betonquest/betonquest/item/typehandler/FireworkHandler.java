package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Handles de-/serialization of Fireworks.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods"})
public class FireworkHandler implements ItemMetaHandler<FireworkMeta> {

    /**
     * The individual Firework Effect Handlers.
     */
    private ExistenceArgument<List<FireworkEffectHandler>> effects = ExistenceArgument.whateverEmptyList();

    /**
     * The firework power.
     */
    @Nullable
    private Argument<NumberValue> power;

    /**
     * If the Firework need to be exact the same or just contain all specified effects.
     */
    private Argument<Boolean> exact = new DefaultArgument<>(true);

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
    public void set(final Instruction instruction) throws QuestException {
        this.effects = ExistenceArgument.applyList("firework", instruction.parse(resolved -> {
            final FireworkEffectHandler effect = new FireworkEffectHandler();
            effect.set(resolved);
            return effect;
        }));
        this.power = NumberValue.create("power", "firework power", instruction);
        this.exact = instruction.bool().map(bool -> !bool).get("firework-containing", true);
    }

    @Override
    public void populate(final FireworkMeta fireworkMeta, @Nullable final Profile profile) throws QuestException {
        fireworkMeta.addEffects(getEffects(profile));
        if (power != null) {
            fireworkMeta.setPower(power.getValue(profile).value());
        }
    }

    /**
     * Sets the Handler's values into the Meta.
     *
     * @param fireworkMeta the meta to populate
     * @param profile      the optional profile for resolving arguments
     * @throws QuestException when there is an exception while resolving profile specific data
     */
    public void populate(final FireworkEffectMeta fireworkMeta, @Nullable final Profile profile) throws QuestException {
        final List<FireworkEffect> list = getEffects(profile);
        fireworkMeta.setEffect(list.isEmpty() ? null : list.get(0));
    }

    @Override
    public boolean check(final FireworkMeta fireworkMeta, @Nullable final Profile profile) throws QuestException {
        return checkEffects(profile, fireworkMeta.getEffects()) && (power == null || power.getValue(profile).isValid(fireworkMeta.getPower()));
    }

    @Override
    public boolean rawCheck(final ItemMeta meta, @Nullable final Profile profile) throws QuestException {
        if (meta instanceof final FireworkMeta fireworkMeta && !check(fireworkMeta, profile)) {
            return false;
        }
        if (meta instanceof final FireworkEffectMeta fireworkMeta) {
            return check(fireworkMeta, profile);
        }
        return true;
    }

    @Override
    public void rawPopulate(final ItemMeta meta, @Nullable final Profile profile) throws QuestException {
        if (meta instanceof final FireworkMeta fireworkMeta) {
            populate(fireworkMeta, profile);
        }
        if (meta instanceof final FireworkEffectMeta fireworkMeta) {
            populate(fireworkMeta, profile);
        }
    }

    /**
     * Check to see if the specified ItemMeta matches the Handler.
     *
     * @param fireworkMeta the ItemMeta to check
     * @param profile      the optional profile for resolving arguments
     * @return if the meta satisfies the requirement defined via {@link #set(Instruction)}
     * @throws QuestException when there is an exception while resolving profile specific data
     */
    public boolean check(final FireworkEffectMeta fireworkMeta, @Nullable final Profile profile) throws QuestException {
        final FireworkEffect single = fireworkMeta.getEffect();
        final Pair<Existence, List<FireworkEffectHandler>> pair = this.effects.getValue(profile);
        final List<FireworkEffectHandler> effects = pair.getRight();
        return switch (pair.getLeft()) {
            case WHATEVER -> true;
            case REQUIRED -> single != null && !effects.isEmpty() && effects.get(0).check(single);
            case FORBIDDEN -> single == null;
        };
    }

    private List<FireworkEffect> getEffects(@Nullable final Profile profile) throws QuestException {
        final List<FireworkEffect> list = new LinkedList<>();
        for (final FireworkEffectHandler effect : effects.getValue(profile).getRight()) {
            list.add(effect.get());
        }
        return list;
    }

    private boolean checkEffects(@Nullable final Profile profile, final List<FireworkEffect> list) throws QuestException {
        final Pair<Existence, List<FireworkEffectHandler>> pair = effects.getValue(profile);
        return switch (pair.getLeft()) {
            case WHATEVER -> true;
            case REQUIRED -> checkRequired(profile, pair.getRight(), list);
            case FORBIDDEN -> list.isEmpty();
        };
    }

    private boolean checkRequired(@Nullable final Profile profile, final List<FireworkEffectHandler> effects,
                                  final List<FireworkEffect> list) throws QuestException {
        if (exact.getValue(profile) && list.size() != effects.size()) {
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
