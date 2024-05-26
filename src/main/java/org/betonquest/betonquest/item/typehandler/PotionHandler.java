package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.QuestItem.Existence;
import org.betonquest.betonquest.item.QuestItem.Number;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        } catch (final IllegalArgumentException e) {
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
        final String[] parts = HandlerUtil.getNNSplit(custom, "Potion is null!", ",");
        if (QuestItem.NONE_KEY.equalsIgnoreCase(parts[0])) {
            customE = Existence.FORBIDDEN;
            return;
        }
        this.custom = new ArrayList<>(parts.length);
        for (final String part : parts) {
            final CustomEffectHandler checker = new CustomEffectHandler(part);
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
        if (custom.isEmpty()) {
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
        /**
         * The expected argument count of the formatted effect.
         */
        private static final int INSTRUCTION_FORMAT_LENGTH = 3;

        private final PotionEffectType customType;

        private final Existence customTypeE;

        private final Number durationE;

        /**
         * The effect duration, in ticks.
         */
        private final int duration;

        /**
         * The effect amplifier, starting at 0.
         */
        private final int power;

        private final Number powerE;

        @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
        public CustomEffectHandler(final String custom) throws InstructionParseException {
            final String[] parts = HandlerUtil.getNNSplit(custom, "Potion is null!", ":");
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
                throw new InstructionParseException("Wrong effect format");
            }
            final Map.Entry<Number, Integer> effectPower = HandlerUtil.getNumberValue(parts[1], "effect power");
            powerE = effectPower.getKey();
            power = effectPower.getValue() - 1;
            if (power < 0) {
                throw new InstructionParseException("Effect power must be a positive integer");
            }
            final Map.Entry<Number, Integer> effectDuration = HandlerUtil.getNumberValue(parts[2], "effect duration");
            durationE = effectDuration.getKey();
            duration = effectDuration.getValue() * 20;
        }

        private PotionEffectType getType(final String name) throws InstructionParseException {
            return Utils.getNN(PotionEffectType.getByName(name), "Unknown effect type: " + name);
        }

        private PotionEffect get() {
            return new PotionEffect(customType, duration, power);
        }

        @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
        private boolean check(@Nullable final PotionEffect effect) {
            switch (customTypeE) {
                case WHATEVER:
                    return true;
                case REQUIRED:
                    if (effect == null || !effect.getType().equals(customType)) {
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
