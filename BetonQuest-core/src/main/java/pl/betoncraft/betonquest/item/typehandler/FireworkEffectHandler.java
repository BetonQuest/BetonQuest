/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.item.typehandler;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Existence;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.LinkedList;
import java.util.List;

public class FireworkEffectHandler {

    private Type type = Type.BALL; // default type for giving is small ball
    private Existence typeE = Existence.WHATEVER;
    private List<Color> mainColors = new LinkedList<>();
    private Existence mainE = Existence.WHATEVER;
    private List<Color> fadeColors = new LinkedList<>();
    private Existence fadeE = Existence.WHATEVER;
    private Existence trail = Existence.WHATEVER;
    private Existence flicker = Existence.WHATEVER;

    public void set(String string) throws InstructionParseException {
        if (string == null || string.isEmpty()) {
            throw new InstructionParseException("Effect is missing");
        }
        String[] parts = string.split(":");
        // if "whatever" then all type checking is unnecessary
        if (!parts[0].equals("?")) {
            if (parts[0].toLowerCase().startsWith("none-")) {
                parts[0] = parts[0].substring(5);
                typeE = Existence.FORBIDDEN;
            }
            try {
                type = Type.valueOf(parts[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InstructionParseException("Unknown firework effect type: " + parts[0], e);
            }
            if (typeE == Existence.FORBIDDEN) {
                return;
            }
            typeE = Existence.REQUIRED;
        }
        if (parts.length != 5) {
            throw new InstructionParseException("Incorrect effect format: " + string);
        }
        if (parts[1].equalsIgnoreCase("none")) {
            mainE = Existence.FORBIDDEN;
        } else if (parts[1].equals("?")) {
            mainE = Existence.WHATEVER;
        } else {
            mainE = Existence.REQUIRED;
            for (String color : parts[1].split(";")) {
                Color regularColor = Utils.getColor(color);
                DyeColor fireworkColor = DyeColor.getByColor(regularColor);
                mainColors.add(fireworkColor != null ? fireworkColor.getFireworkColor() : regularColor);
            }
        }
        if (parts[2].equalsIgnoreCase("none")) {
            fadeE = Existence.FORBIDDEN;
        } else if (parts[2].equals("?")) {
            fadeE = Existence.WHATEVER;
        } else {
            fadeE = Existence.REQUIRED;
            for (String color : parts[2].split(";")) {
                Color regularColor = Utils.getColor(color);
                DyeColor fireworkColor = DyeColor.getByColor(regularColor);
                fadeColors.add(fireworkColor != null ? fireworkColor.getFireworkColor() : regularColor);
            }
        }
        if (parts[3].equals("?")) {
            trail = Existence.WHATEVER;
        } else {
            trail = Boolean.parseBoolean(parts[3]) ? Existence.REQUIRED : Existence.FORBIDDEN;
        }
        if (parts[4].equals("?")) {
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

    public boolean check(FireworkEffect effect) {
        switch (typeE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                if (effect == null || effect.getType() != type) {
                    return false;
                }
                switch (mainE) {
                    case WHATEVER:
                        break;
                    case REQUIRED:
                        if (effect.getColors().size() != mainColors.size()) {
                            return false;
                        }
                        for (Color c : effect.getColors()) {
                            if (!mainColors.contains(c)) {
                                return false;
                            }
                        }
                        break;
                    case FORBIDDEN:
                        if (!effect.getColors().isEmpty()) {
                            return false;
                        }
                        break;
                }
                switch (fadeE) {
                    case WHATEVER:
                        break;
                    case REQUIRED:
                        if (effect.getFadeColors().size() != fadeColors.size()) {
                            return false;
                        }
                        for (Color c : effect.getFadeColors()) {
                            if (!fadeColors.contains(c)) {
                                return false;
                            }
                        }
                        break;
                    case FORBIDDEN:
                        if (!effect.getFadeColors().isEmpty()) {
                            return false;
                        }
                        break;
                }
                switch (trail) {
                    case WHATEVER:
                        break;
                    case REQUIRED:
                        if (!effect.hasTrail()) {
                            return false;
                        }
                        break;
                    case FORBIDDEN:
                        if (effect.hasTrail()) {
                            return false;
                        }
                        break;
                }
                switch (flicker) {
                    case WHATEVER:
                        break;
                    case REQUIRED:
                        if (!effect.hasFlicker()) {
                            return false;
                        }
                        break;
                    case FORBIDDEN:
                        if (effect.hasFlicker()) {
                            return false;
                        }
                        break;
                }
                return true;
            case FORBIDDEN:
                return effect.getType() != type;
            default:
                return false;
        }
    }

}