package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"PMD.CommentRequired", "PMD.GodClass", "PMD.TooManyMethods"})
public class FireworkHandler implements ItemMetaHandler<FireworkMeta> {
    private final List<FireworkEffectHandler> effects = new ArrayList<>();

    private int power;

    private Number powerN = Number.WHATEVER;

    private Existence effectsE = Existence.WHATEVER;

    /**
     * If the Firework need to be exact the same or just contain all specified effects.
     */
    private boolean exact = true;

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
     * @return @return parsed values with leading space or empty string
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
        for (final Color c : effect.getColors()) {
            final DyeColor dye = DyeColor.getByFireworkColor(c);
            builder.append(dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye).append(';');
        }
        // remove last semicolon
        builder.setLength(Math.max(builder.length() - 1, 0));
        builder.append(':');
        for (final Color c : effect.getFadeColors()) {
            final DyeColor dye = DyeColor.getByFireworkColor(c);
            builder.append(dye == null ? '#' + Integer.toHexString(c.asRGB()) : dye).append(';');
        }
        builder.setLength(Math.max(builder.length() - 1, 0));
        builder.append(':').append(effect.hasTrail()).append(':').append(effect.hasFlicker());
    }

    @Override
    public void set(final String key, final String data) throws QuestException {
        switch (key) {
            case "firework" -> setEffects(data);
            case "power" -> {
                final Map.Entry<Number, Integer> fireworkPower = HandlerUtil.getNumberValue(data, "firework power");
                powerN = fireworkPower.getKey();
                power = fireworkPower.getValue();
            }
            case "firework-containing" -> exact = false;
            default -> throw new QuestException("Unknown firework key: " + key);
        }
    }

    @Override
    public void populate(final FireworkMeta fireworkMeta) {
        fireworkMeta.addEffects(getEffects());
        fireworkMeta.setPower(power);
    }

    /**
     * Sets the Handler's values into the Meta.
     *
     * @param fireworkMeta the meta to populate
     */
    public void populate(final FireworkEffectMeta fireworkMeta) {
        final List<FireworkEffect> list = getEffects();
        fireworkMeta.setEffect(list.isEmpty() ? null : list.get(0));
    }

    @Override
    public boolean check(final FireworkMeta fireworkMeta) {
        return checkEffects(fireworkMeta.getEffects()) && powerN.isValid(fireworkMeta.getPower(), power);
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
    public void rawPopulate(final ItemMeta meta, @Nullable final Profile profile) {
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
     * @return if the meta satisfies the requirement defined via {@link #set(String, String)}
     */
    public boolean check(final FireworkEffectMeta fireworkMeta) {
        final FireworkEffect single = fireworkMeta.getEffect();
        return switch (effectsE) {
            case WHATEVER -> true;
            case REQUIRED -> single != null && !effects.isEmpty() && effects.get(0).check(single);
            case FORBIDDEN -> single == null;
        };
    }

    public List<FireworkEffect> getEffects() {
        final List<FireworkEffect> list = new LinkedList<>();
        for (final FireworkEffectHandler effect : effects) {
            list.add(effect.get());
        }
        return list;
    }

    public void setEffects(final String string) throws QuestException {
        final String[] parts = HandlerUtil.getNNSplit(string, "Firework effects missing", ",");
        if (Existence.NONE_KEY.equalsIgnoreCase(parts[0])) {
            effectsE = Existence.FORBIDDEN;
            return;
        }
        effectsE = Existence.REQUIRED;
        for (final String part : parts) {
            final FireworkEffectHandler effect = new FireworkEffectHandler();
            effect.set(part);
            effects.add(effect);
        }
    }

    public boolean checkEffects(final List<FireworkEffect> list) {
        return switch (effectsE) {
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
            for (final FireworkEffect e : list) {
                if (e.getType() == checker.getType()) {
                    effect = e;
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
