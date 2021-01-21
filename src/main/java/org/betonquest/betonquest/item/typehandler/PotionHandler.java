package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem.Existence;
import org.betonquest.betonquest.item.QuestItem.Number;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("PMD.CommentRequired")
public class PotionHandler {

    private PotionType type = PotionType.WATER;
    private Existence typeE = Existence.WHATEVER;
    private boolean extended;
    private Existence extendedE = Existence.WHATEVER;
    private boolean upgraded;
    private Existence upgradedE = Existence.WHATEVER;
    private List<CustomEffectHandler> custom = new ArrayList<>();
    private Existence customE = Existence.WHATEVER;
    private boolean exact = true;

    public PotionHandler() {
    }

    public void setType(final String type) throws InstructionParseException {
        typeE = Existence.REQUIRED;
        try {
            this.type = PotionType.valueOf(type.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InstructionParseException("No such potion type: " + type, e);
        }
    }

    public void setExtended(final String extended) {
        extendedE = Existence.REQUIRED;
        this.extended = Boolean.parseBoolean(extended);
    }

    public void setUpgraded(final String upgraded) {
        upgradedE = Existence.REQUIRED;
        this.upgraded = Boolean.parseBoolean(upgraded);
    }

    public void setNotExact() {
        exact = false;
    }

    public PotionData getBase() {
        return new PotionData(type, extended, upgraded);
    }

    public List<PotionEffect> getCustom() {
        final List<PotionEffect> effects = new LinkedList<>();
        if (customE == Existence.FORBIDDEN) {
            return effects;
        }
        for (final CustomEffectHandler checker : custom) {
            if (checker.customTypeE != Existence.FORBIDDEN) {
                effects.add(checker.get());
            }
        }
        return effects;
    }

    public void setCustom(final String custom) throws InstructionParseException {
        if (custom == null) {
            throw new InstructionParseException("Potion is null!");
        }
        final String[] parts = custom.split(",");
        if (parts.length == 0) {
            throw new InstructionParseException("Missing values!");
        }
        if ("none".equalsIgnoreCase(custom)) {
            customE = Existence.FORBIDDEN;
            return;
        }
        this.custom = new ArrayList<>(parts.length);
        for (final String part : parts) {
            final CustomEffectHandler checker = new CustomEffectHandler();
            checker.set(part);
            this.custom.add(checker);
        }
        customE = Existence.REQUIRED;
    }

    public boolean checkBase(final PotionData base) {
        switch (typeE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                if (base.getType() != type) {
                    return false;
                }
                if (extendedE == Existence.REQUIRED && base.isExtended() != extended) {
                    return false;
                }
                return upgradedE != Existence.REQUIRED || base.isUpgraded() == upgraded;
            default:
                return false;
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public boolean checkCustom(final List<PotionEffect> custom) {
        if (customE == Existence.WHATEVER) {
            return true;
        }
        if (custom == null || custom.isEmpty()) {
            return customE == Existence.FORBIDDEN;
        }
        if (exact && custom.size() != this.custom.size()) {
            return false;
        }
        for (final CustomEffectHandler checker : this.custom) {
            PotionEffect effect = null;
            for (final PotionEffect e : custom) {
                if (e.getType().equals(checker.customType)) {
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

    private static class CustomEffectHandler {

        private PotionEffectType customType;
        private Existence customTypeE = Existence.WHATEVER;
        private int duration = 60 * 20;
        private Number durationE = Number.WHATEVER;
        private int power = 1;
        private Number powerE = Number.WHATEVER;

        public CustomEffectHandler() {
        }

        @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.AvoidLiteralsInIfCondition"})
        private void set(final String custom) throws InstructionParseException {
            if (custom == null) {
                throw new InstructionParseException("Potion is null!");
            }
            final String[] parts = custom.split(":");
            if (parts.length == 0) {
                throw new InstructionParseException("Missing values!");
            }
            if (parts[0].startsWith("none-")) {
                parts[0] = parts[0].substring(5);
                customTypeE = Existence.FORBIDDEN;
            }
            customType = PotionEffectType.getByName(parts[0]);
            if (customType == null) {
                throw new InstructionParseException("Unknown effect type: " + parts[0]);
            }
            if (customTypeE == Existence.FORBIDDEN) {
                return;
            }
            customTypeE = Existence.REQUIRED;
            if (parts.length == 3) {
                // first number is a duration of the potion
                if (duration < 0) {
                    throw new InstructionParseException("Efect duration must be a positive integer");
                }
                // second number is the power of the potion
                if ("?".equals(parts[1])) {
                    powerE = Number.WHATEVER;
                    parts[1] = String.valueOf(power);
                } else if (parts[1].endsWith("-")) {
                    powerE = Number.LESS;
                    parts[1] = parts[1].substring(0, parts[1].length() - 1);
                } else if (parts[1].endsWith("+")) {
                    powerE = Number.MORE;
                    parts[1] = parts[1].substring(0, parts[1].length() - 1);
                } else {
                    powerE = Number.EQUAL;
                }
                try {
                    power = Integer.parseInt(parts[1]) - 1;
                } catch (NumberFormatException e) {
                    throw new InstructionParseException("Could not parse effect power: " + parts[1], e);
                }
                if (power < 0) {
                    throw new InstructionParseException("Effect power must be a positive integer");
                }
                if ("?".equals(parts[2])) {
                    durationE = Number.WHATEVER;
                    parts[2] = String.valueOf(duration);
                } else if (parts[2].endsWith("-")) {
                    durationE = Number.LESS;
                    parts[2] = parts[2].substring(0, parts[2].length() - 1);
                } else if (parts[2].endsWith("+")) {
                    durationE = Number.MORE;
                    parts[2] = parts[2].substring(0, parts[2].length() - 1);
                } else {
                    durationE = Number.EQUAL;
                }
                try {
                    duration = Integer.parseInt(parts[2]) * 20;
                } catch (NumberFormatException e) {
                    throw new InstructionParseException("Could not parse effect duration: " + parts[2], e);
                }
            } else {
                throw new InstructionParseException("Wrong effect format");
            }
        }

        private PotionEffect get() {
            return new PotionEffect(customType, duration, power);
        }

        @SuppressWarnings("PMD.CyclomaticComplexity")
        private boolean check(final PotionEffect effect) {
            switch (customTypeE) {
                case WHATEVER:
                    return true;
                case REQUIRED:
                    if (effect.getType() != customType) {
                        return false;
                    }
                    switch (durationE) {
                        case EQUAL:
                            if (duration != effect.getDuration()) {
                                return false;
                            }
                            break;
                        case MORE:
                            if (duration > effect.getDuration()) {
                                return false;
                            }
                            break;
                        case LESS:
                            if (duration < effect.getDuration()) {
                                return false;
                            }
                            break;
                        case WHATEVER:
                            break;
                    }
                    switch (powerE) {
                        case EQUAL:
                            if (power != effect.getAmplifier()) {
                                return false;
                            }
                            break;
                        case MORE:
                            if (power > effect.getAmplifier()) {
                                return false;
                            }
                            break;
                        case LESS:
                            if (power < effect.getAmplifier()) {
                                return false;
                            }
                            break;
                        case WHATEVER:
                            break;
                    }
                    return true;
                case FORBIDDEN:
                    return effect == null;
                default:
                    return false;
            }
        }
    }

}
