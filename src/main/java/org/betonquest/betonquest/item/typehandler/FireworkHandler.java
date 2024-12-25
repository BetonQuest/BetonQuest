package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.QuestItem.Existence;
import org.betonquest.betonquest.item.QuestItem.Number;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"PMD.CommentRequired", "PMD.GodClass", "PMD.TooManyMethods"})
public class FireworkHandler implements ItemMetaHandler<FireworkMeta> {
    private final List<FireworkEffectHandler> effects = new ArrayList<>();

    private int power;

    private Number powerN = Number.WHATEVER;

    private Existence effectsE = Existence.WHATEVER;

    private boolean exact = true;

    public FireworkHandler() {
    }

    /**
     * Converts the item meta into QuestItem format.
     *
     * @param fireworkMeta the meta to serialize
     * @return parsed values with leading space or empty string
     */
    public static String serializeToString(final FireworkMeta fireworkMeta) {
        if (!fireworkMeta.hasEffects()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder(17);
        builder.append(" firework:");
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
    public static String serializeToString(final FireworkEffectMeta fireworkMeta) {
        if (!fireworkMeta.hasEffect()) {
            return "";
        }
        final FireworkEffect effect = fireworkMeta.getEffect();
        final StringBuilder builder = new StringBuilder();
        builder.append(" firework:");
        appendFireworkEffect(builder, effect);
        return builder.toString();
    }

    private static void appendFireworkEffect(final StringBuilder builder, final FireworkEffect effect) {
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
    public void set(final String key, final String data) throws InstructionParseException {
        switch (key) {
            case "firework" -> setEffects(data);
            case "power" -> setPower(data);
            case "firework-containing" -> setNotExact();
            default -> throw new InstructionParseException("Unknown firework key: " + key);
        }
    }

    @Override
    public void populate(final FireworkMeta fireworkMeta) {
        fireworkMeta.addEffects(getEffects());
        fireworkMeta.setPower(getPower());
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
        return checkEffects(fireworkMeta.getEffects()) && checkPower(fireworkMeta.getPower());
    }

    /**
     * Check to see if the specified ItemMeta matches the Handler.
     *
     * @param fireworkMeta the ItemMeta to check
     * @return if the meta satisfies the requirement defined via {@link #set(String, String)}
     */
    public boolean check(final FireworkEffectMeta fireworkMeta) {
        return checkSingleEffect(fireworkMeta.getEffect());
    }

    public void setNotExact() {
        exact = false;
    }

    public List<FireworkEffect> getEffects() {
        final List<FireworkEffect> list = new LinkedList<>();
        for (final FireworkEffectHandler effect : effects) {
            list.add(effect.get());
        }
        return list;
    }

    public void setEffects(final String string) throws InstructionParseException {
        final String[] parts = HandlerUtil.getNNSplit(string, "Firework effects missing", ",");
        if (QuestItem.NONE_KEY.equalsIgnoreCase(parts[0])) {
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

    public int getPower() {
        return power;
    }

    public void setPower(final String string) throws InstructionParseException {
        final Map.Entry<Number, Integer> fireworkPower = HandlerUtil.getNumberValue(string, "firework power");
        powerN = fireworkPower.getKey();
        power = fireworkPower.getValue();
    }

    public int getSize() {
        return effects.size();
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    public boolean checkEffects(final List<FireworkEffect> list) {
        switch (effectsE) {
            case WHATEVER:
                return true;
            case REQUIRED:
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
            case FORBIDDEN:
                return list.isEmpty();
            default:
                return false;
        }
    }

    public boolean checkSingleEffect(@Nullable final FireworkEffect single) {
        return switch (effectsE) {
            case WHATEVER -> true;
            case REQUIRED -> single != null && !effects.isEmpty() && effects.get(0).check(single);
            case FORBIDDEN -> single == null;
        };
    }

    public boolean checkPower(final int powerLevel) {
        return switch (powerN) {
            case WHATEVER -> true;
            case EQUAL -> powerLevel == power;
            case MORE -> powerLevel >= power;
            case LESS -> powerLevel <= power;
        };
    }
}
