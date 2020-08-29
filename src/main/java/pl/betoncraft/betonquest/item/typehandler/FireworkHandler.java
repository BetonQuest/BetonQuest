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

import org.bukkit.FireworkEffect;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Existence;
import pl.betoncraft.betonquest.item.QuestItem.Number;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FireworkHandler {

    private int power = 0;
    private Number powerN = Number.WHATEVER;
    private List<FireworkEffectHandler> effects = new ArrayList<>();
    private Existence effectsE = Existence.WHATEVER;
    private boolean exact = true;

    public FireworkHandler() {}

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
        if (string.equalsIgnoreCase("none")) {
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

    public void setPower(String string) throws InstructionParseException {
        if (string.equals("?")) {
            powerN = Number.WHATEVER;
            string = "1";
        } else if (string.endsWith("-")) {
            powerN = Number.LESS;
            string = string.substring(0, string.length() - 1);
        } else if (string.endsWith("+")) {
            powerN = Number.MORE;
            string = string.substring(0, string.length() - 1);
        } else {
            powerN = Number.EQUAL;
        }
        try {
            power = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse power: " + string, e);
        }
        if (power < 0) {
            throw new InstructionParseException("Firework power must be a positive number");
        }
    }

    public int getSize() {
        return effects.size();
    }

    public boolean checkEffects(final List<FireworkEffect> list) {
        switch (effectsE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                if (exact) {
                    if (list.size() != effects.size()) {
                        return false;
                    }
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
