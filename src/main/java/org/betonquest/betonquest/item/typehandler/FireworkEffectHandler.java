package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("PMD.CommentRequired")
public class FireworkEffectHandler {
    private final List<Color> mainColors = new LinkedList<>();

    private final List<Color> fadeColors = new LinkedList<>();

    private Type type = Type.BALL; // default type for giving is small ball

    private Existence typeE = Existence.WHATEVER;

    private Existence mainE = Existence.WHATEVER;

    private Existence fadeE = Existence.WHATEVER;

    private Existence trail = Existence.WHATEVER;

    private Existence flicker = Existence.WHATEVER;

    public FireworkEffectHandler() {
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity"})
    public void set(final String string) throws QuestException {
        final String[] parts = HandlerUtil.getNNSplit(string, "Effect is missing", ":");
        // if "whatever" then all type checking is unnecessary
        if (!"?".equals(parts[0])) {
            if (parts[0].toLowerCase(Locale.ROOT).startsWith("none-")) {
                parts[0] = parts[0].substring(5);
                typeE = Existence.FORBIDDEN;
            }
            try {
                type = Type.valueOf(parts[0].toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Unknown firework effect type: " + parts[0], e);
            }
            if (typeE == Existence.FORBIDDEN) {
                return;
            }
            typeE = Existence.REQUIRED;
        }
        if (parts.length != 5) {
            throw new QuestException("Incorrect effect format: " + string);
        }
        if (Existence.NONE_KEY.equalsIgnoreCase(parts[1])) {
            mainE = Existence.FORBIDDEN;
        } else if ("?".equals(parts[1])) {
            mainE = Existence.WHATEVER;
        } else {
            mainE = Existence.REQUIRED;
            for (final String color : parts[1].split(";")) {
                final Color regularColor = Utils.getColor(color);
                final DyeColor fireworkColor = DyeColor.getByColor(regularColor);
                mainColors.add(fireworkColor == null ? regularColor : fireworkColor.getFireworkColor());
            }
        }
        if (Existence.NONE_KEY.equalsIgnoreCase(parts[2])) {
            fadeE = Existence.FORBIDDEN;
        } else if ("?".equals(parts[2])) {
            fadeE = Existence.WHATEVER;
        } else {
            fadeE = Existence.REQUIRED;
            for (final String color : parts[2].split(";")) {
                final Color regularColor = Utils.getColor(color);
                final DyeColor fireworkColor = DyeColor.getByColor(regularColor);
                fadeColors.add(fireworkColor == null ? regularColor : fireworkColor.getFireworkColor());
            }
        }
        if ("?".equals(parts[3])) {
            trail = Existence.WHATEVER;
        } else {
            trail = Boolean.parseBoolean(parts[3]) ? Existence.REQUIRED : Existence.FORBIDDEN;
        }
        if ("?".equals(parts[4])) {
            flicker = Existence.WHATEVER;
        } else {
            flicker = Boolean.parseBoolean(parts[4]) ? Existence.REQUIRED : Existence.FORBIDDEN;
        }
    }

    public FireworkEffect get() {
        return FireworkEffect.builder()
                .with(type)
                .withColor(mainColors)
                .withFade(fadeColors)
                .trail(trail == Existence.REQUIRED)
                .flicker(flicker == Existence.REQUIRED)
                .build();
    }

    public Type getType() {
        return type;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NcssCount", "PMD.NPathComplexity", "PMD.SwitchDensity", "PMD.CognitiveComplexity"})
    public boolean check(@Nullable final FireworkEffect effect) {
        return switch (typeE) {
            case WHATEVER:
                yield true;
            case REQUIRED:
                if (effect == null || effect.getType() != type) {
                    yield false;
                }
                switch (mainE) {
                    case WHATEVER:
                        break;
                    case REQUIRED:
                        if (effect.getColors().size() != mainColors.size()) {
                            yield false;
                        }
                        for (final Color c : effect.getColors()) {
                            if (!mainColors.contains(c)) {
                                yield false;
                            }
                        }
                        break;
                    case FORBIDDEN:
                        if (!effect.getColors().isEmpty()) {
                            yield false;
                        }
                        break;
                }
                switch (fadeE) {
                    case WHATEVER:
                        break;
                    case REQUIRED:
                        if (effect.getFadeColors().size() != fadeColors.size()) {
                            yield false;
                        }
                        for (final Color c : effect.getFadeColors()) {
                            if (!fadeColors.contains(c)) {
                                yield false;
                            }
                        }
                        break;
                    case FORBIDDEN:
                        if (!effect.getFadeColors().isEmpty()) {
                            yield false;
                        }
                        break;
                }
                switch (trail) {
                    case WHATEVER:
                        break;
                    case REQUIRED:
                        if (!effect.hasTrail()) {
                            yield false;
                        }
                        break;
                    case FORBIDDEN:
                        if (effect.hasTrail()) {
                            yield false;
                        }
                        break;
                }
                switch (flicker) {
                    case WHATEVER:
                        break;
                    case REQUIRED:
                        if (!effect.hasFlicker()) {
                            yield false;
                        }
                        break;
                    case FORBIDDEN:
                        if (effect.hasFlicker()) {
                            yield false;
                        }
                        break;
                }
                yield true;
            case FORBIDDEN:
                yield effect == null || effect.getType() != type;
        };
    }
}
