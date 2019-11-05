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

import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Existence;
import pl.betoncraft.betonquest.item.QuestItem.Number;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PotionHandler {

    private PotionType type = PotionType.WATER;
    private Existence typeE = Existence.WHATEVER;
    private boolean extended = false;
    private Existence extendedE = Existence.WHATEVER;
    private boolean upgraded = false;
    private Existence upgradedE = Existence.WHATEVER;
    private List<CustomEffectHandler> custom = new ArrayList<>();
    private Existence customE = Existence.WHATEVER;
    private boolean exact = true;

    public void setType(String type) throws InstructionParseException {
        typeE = Existence.REQUIRED;
        try {
            this.type = PotionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InstructionParseException("No such potion type: " + type, e);
        }
    }

    public void setExtended(String extended) {
        extendedE = Existence.REQUIRED;
        this.extended = Boolean.parseBoolean(extended);
    }

    public void setUpgraded(String upgraded) {
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
        List<PotionEffect> effects = new LinkedList<>();
        if (customE == Existence.FORBIDDEN) {
            return effects;
        }
        for (CustomEffectHandler checker : custom) {
            if (checker.customTypeE != Existence.FORBIDDEN) {
                effects.add(checker.get());
            }
        }
        return effects;
    }

    public void setCustom(String custom) throws InstructionParseException {
        String[] parts;
        if (custom == null || (parts = custom.split(",")).length == 0) {
            throw new InstructionParseException("Missing value");
        }
        if (custom.equalsIgnoreCase("none")) {
            customE = Existence.FORBIDDEN;
            return;
        }
        this.custom = new ArrayList<>(parts.length);
        for (String part : parts) {
            CustomEffectHandler checker = new CustomEffectHandler();
            checker.set(part);
            this.custom.add(checker);
        }
        customE = Existence.REQUIRED;
    }

    public boolean checkBase(PotionData base) {
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

    public boolean checkCustom(List<PotionEffect> custom) {
        if (customE == Existence.WHATEVER) {
            return true;
        }
        if (custom == null || custom.isEmpty()) {
            return customE == Existence.FORBIDDEN;
        }
        if (exact) {
            if (custom.size() != this.custom.size()) {
                return false;
            }
        }
        for (CustomEffectHandler checker : this.custom) {
            PotionEffect effect = null;
            for (PotionEffect e : custom) {
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

    private class CustomEffectHandler {

        PotionEffectType customType;
        Existence customTypeE = Existence.WHATEVER;
        int duration = 60 * 20;
        Number durationE = Number.WHATEVER;
        int power = 1;
        Number powerE = Number.WHATEVER;

        void set(String custom) throws InstructionParseException {
            String[] parts;
            if (custom == null || (parts = custom.split(":")).length == 0) {
                throw new InstructionParseException("Missing value");
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
                if (parts[1].equals("?")) {
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
                if (parts[2].equals("?")) {
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

        PotionEffect get() {
            return new PotionEffect(customType, duration, power);
        }

        boolean check(PotionEffect effect) {
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
