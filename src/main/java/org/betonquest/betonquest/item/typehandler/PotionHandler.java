package org.betonquest.betonquest.item.typehandler;

import io.papermc.lib.PaperLib;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.QuestItem.Existence;
import org.betonquest.betonquest.item.QuestItem.Number;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Keyed;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyMethods", "PMD.GodClass"})
public class PotionHandler {
    /**
     * The 1.20.5+ method to check if a Potion Type is in the Potion.
     */
    @Nullable
    private static Method methodHasBasePotionType;

    /**
     * The 1.20.5+ method to get the Potion Type from.
     */
    @Nullable
    private static Method methodGetBasePotionType;

    /**
     * Marker for not re-initializing already failed method reflections.
     */
    private static boolean methodsInit;

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

    /**
     * Converts the Meta into the string equivalent used by Quest Items.
     *
     * @param potionMeta the meta to parse into string
     * @return the representing string or an empty string, when no representative data is present
     */
    public static String metaToString(final PotionMeta potionMeta) {
        // TODO version switch:
        //  Remove this code when only 1.20.5+ is supported
        final String baseEffect = PaperLib.isVersion(20, 5) ? getBasePotionEffects(potionMeta)
                : getBasePotionEffectsPre_1_21(potionMeta);
        return addCustomEffects(potionMeta, baseEffect);
    }

    @SuppressWarnings("PMD.MethodNamingConventions")
    private static String getBasePotionEffectsPre_1_21(final PotionMeta potionMeta) {
        final PotionData pData = potionMeta.getBasePotionData();
        return " type:" + pData.getType() + (pData.isExtended() ? " extended" : "")
                + (pData.isUpgraded() ? " upgraded" : "");
    }

    private static String getBasePotionEffects(final PotionMeta potionMeta) {
        final Keyed type;
        try {
            initReflection();
            if (methodHasBasePotionType == null || methodGetBasePotionType == null) {
                return "";
            }
            if (!(boolean) methodHasBasePotionType.invoke(potionMeta)) {
                return "";
            }
            type = (Keyed) methodGetBasePotionType.invoke(potionMeta);
        } catch (final ReflectiveOperationException e) {
            BetonQuest.getInstance().getLoggerFactory().create(PotionHandler.class)
                    .error("Could not initialize Methods to get Potion Data!", e);
            return "";
        }
        final String minimalString = type.getKey().asMinimalString();
        final String longPrefix = "long_";
        final String strongPrefix = "strong_";
        final String effects;
        if (minimalString.startsWith(longPrefix)) {
            effects = minimalString.substring(longPrefix.length()) + " extended";
        } else if (minimalString.startsWith(strongPrefix)) {
            effects = minimalString.substring(strongPrefix.length()) + " upgraded";
        } else {
            effects = minimalString;
        }
        return " type:" + effects;
    }

    private static void initReflection() throws NoSuchMethodException {
        if (!methodsInit) {
            methodsInit = true;
            methodHasBasePotionType = PotionMeta.class.getDeclaredMethod("hasBasePotionType");
            methodGetBasePotionType = PotionMeta.class.getDeclaredMethod("getBasePotionType");
        }
    }

    private static String addCustomEffects(final PotionMeta potionMeta, final String effects) {
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
        return effects + " effects:" + string.substring(0, string.length() - 1);
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

    public boolean checkMeta(final PotionMeta potionMeta) {
        return checkBase(potionMeta.getBasePotionData()) && checkCustom(potionMeta.getCustomEffects());
    }

    public boolean checkBase(@Nullable final PotionData base) {
        switch (typeE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                if (base == null || base.getType() != type) {
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
