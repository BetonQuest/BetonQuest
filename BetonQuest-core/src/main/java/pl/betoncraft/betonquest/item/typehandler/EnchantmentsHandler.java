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

import org.bukkit.enchantments.Enchantment;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Existence;
import pl.betoncraft.betonquest.item.QuestItem.Number;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentsHandler {

    private List<SingleEnchantmentHandler> checkers = new ArrayList<>();
    private Existence checkersE = Existence.WHATEVER;
    private boolean exact = true;

    public void set(String enchants) throws InstructionParseException {
        String[] parts;
        if (enchants == null || (parts = enchants.split(",")).length == 0) {
            throw new InstructionParseException("Missing value");
        }
        if (enchants.equalsIgnoreCase("none")) {
            checkersE = Existence.FORBIDDEN;
            return;
        }
        checkers = new ArrayList<>(parts.length);
        for (String part : parts) {
            SingleEnchantmentHandler checker = new SingleEnchantmentHandler();
            checker.set(part);
            checkers.add(checker);
        }
        checkersE = Existence.REQUIRED;
    }

    public void setNotExact() {
        exact = false;
    }

    public Map<Enchantment, Integer> get() {
        Map<Enchantment, Integer> map = new HashMap<>();
        if (checkersE == Existence.FORBIDDEN) {
            return map;
        }
        for (SingleEnchantmentHandler checker : checkers) {
            if (checker.ex != Existence.FORBIDDEN) {
                map.put(checker.type, checker.level);
            }
        }
        return map;
    }

    public boolean check(Map<Enchantment, Integer> map) {
        if (checkersE == Existence.WHATEVER) {
            return true;
        }
        if (map == null || map.isEmpty()) {
            return checkersE == Existence.FORBIDDEN;
        }
        if (exact) {
            if (map.size() != get().size()) {
                return false;
            }
        }
        for (SingleEnchantmentHandler checker : checkers) {
            if (!checker.check(map.get(checker.type))) {
                return false;
            }
        }
        return true;
    }

    private class SingleEnchantmentHandler {

        Enchantment type;
        Existence ex = Existence.WHATEVER;
        int level = 1;
        Number nr = Number.WHATEVER;

        @SuppressWarnings("deprecation")
        void set(String enchant) throws InstructionParseException {
            String[] parts;
            if (enchant == null || (parts = enchant.split(":")).length == 0) {
                throw new InstructionParseException("Missing value");
            }
            if (parts[0].startsWith("none-")) {
                ex = Existence.FORBIDDEN;
                parts[0] = parts[0].substring(5);
            }
            type = Enchantment.getByName(parts[0].toUpperCase());
            if (type == null) {
                throw new InstructionParseException("Unknown enchantment type: " + parts[0]);
            }
            if (ex == Existence.FORBIDDEN) {
                return;
            }
            ex = Existence.REQUIRED;
            if (parts.length != 2) {
                throw new InstructionParseException("Wrong enchantment format");
            }
            if (parts[1].equals("?")) {
                nr = Number.WHATEVER;
                parts[1] = "1";
            } else if (parts[1].endsWith("-")) {
                nr = Number.LESS;
                parts[1] = parts[1].substring(0, parts[1].length() - 1);
            } else if (parts[1].endsWith("+")) {
                nr = Number.MORE;
                parts[1] = parts[1].substring(0, parts[1].length() - 1);
            } else {
                nr = Number.EQUAL;
            }
            try {
                level = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse enchantment level: " + parts[1], e);
            }
            if (level < 1) {
                throw new InstructionParseException("Enchantment level must be a positive integer");
            }
        }

        boolean check(Integer level) {
            if (ex == Existence.WHATEVER) {
                return true;
            }
            if (level == null) {
                return ex == Existence.FORBIDDEN;
            }
            switch (nr) {
                case EQUAL:
                    return this.level == level;
                case MORE:
                    return this.level <= level;
                case LESS:
                    return this.level >= level;
                case WHATEVER:
                    return true;
                default:
                    return false;
            }
        }

    }

}
