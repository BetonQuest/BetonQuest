package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem.Existence;
import org.betonquest.betonquest.item.QuestItem.Number;
import org.bukkit.FireworkEffect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class FireworkHandler {

    private int power;
    private Number powerN = Number.WHATEVER;
    private final List<FireworkEffectHandler> effects = new ArrayList<>();
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
        if (string == null || string.isEmpty()) {
            throw new InstructionParseException("Firework effects missing");
        }
        if ("none".equalsIgnoreCase(string)) {
            effectsE = Existence.FORBIDDEN;
            return;
        }
        effectsE = Existence.REQUIRED;
        final String[] parts = string.split(",");
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
        String power = string;
        if ("?".equals(power)) {
            powerN = Number.WHATEVER;
            power = "1";
        } else if (power.endsWith("-")) {
            powerN = Number.LESS;
            power = power.substring(0, power.length() - 1);
        } else if (power.endsWith("+")) {
            powerN = Number.MORE;
            power = power.substring(0, power.length() - 1);
        } else {
            powerN = Number.EQUAL;
        }
        try {
            this.power = Integer.parseInt(power);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse power: " + power, e);
        }
        if (this.power < 0) {
            throw new InstructionParseException("Firework power must be a positive number");
        }
    }

    public int getSize() {
        return effects.size();
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
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
                return list == null || list.isEmpty();
            default:
                return false;
        }
    }

    public boolean checkSingleEffect(final FireworkEffect single) {
        switch (effectsE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return single != null && !effects.isEmpty() && effects.get(0).check(single);
            case FORBIDDEN:
                return single == null;
            default:
                return false;
        }
    }

    public boolean checkPower(final int powerLevel) {
        switch (powerN) {
            case WHATEVER:
                return true;
            case EQUAL:
                return powerLevel == power;
            case MORE:
                return powerLevel >= power;
            case LESS:
                return powerLevel <= power;
            default:
                return false;
        }
    }

}
