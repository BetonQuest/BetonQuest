package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem.Existence;
import org.betonquest.betonquest.item.QuestItem.Number;
import org.bukkit.FireworkEffect;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class FireworkHandler {
    private final List<FireworkEffectHandler> effects = new ArrayList<>();

    private int power;

    private Number powerN = Number.WHATEVER;

    private Existence effectsE = Existence.WHATEVER;

    private boolean exact = true;

    public FireworkHandler() {
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
        if ("none".equalsIgnoreCase(parts[0])) {
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
