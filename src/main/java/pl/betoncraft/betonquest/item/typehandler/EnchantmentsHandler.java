package pl.betoncraft.betonquest.item.typehandler;

import org.bukkit.enchantments.Enchantment;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Existence;
import pl.betoncraft.betonquest.item.QuestItem.Number;

import java.util.*;

public class EnchantmentsHandler {

    private List<SingleEnchantmentHandler> checkers = new ArrayList<>();
    private Existence checkersE = Existence.WHATEVER;
    private boolean exact = true;

    public EnchantmentsHandler() {
    }

    public void set(final String enchants) throws InstructionParseException {
        final String[] parts;
        if (enchants == null || (parts = enchants.split(",")).length == 0) {
            throw new InstructionParseException("Missing value");
        }
        if ("none".equalsIgnoreCase(enchants)) {
            checkersE = Existence.FORBIDDEN;
            return;
        }
        checkers = new ArrayList<>(parts.length);
        for (final String part : parts) {
            final SingleEnchantmentHandler checker = new SingleEnchantmentHandler();
            checker.set(part);
            checkers.add(checker);
        }
        checkersE = Existence.REQUIRED;
    }

    public void setNotExact() {
        exact = false;
    }

    public Map<Enchantment, Integer> get() {
        final Map<Enchantment, Integer> map = new HashMap<>();
        if (checkersE == Existence.FORBIDDEN) {
            return map;
        }
        for (final SingleEnchantmentHandler checker : checkers) {
            if (checker.existence != Existence.FORBIDDEN) {
                map.put(checker.type, checker.level);
            }
        }
        return map;
    }

    public boolean check(final Map<Enchantment, Integer> map) {
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
        for (final SingleEnchantmentHandler checker : checkers) {
            if (!checker.check(map.get(checker.type))) {
                return false;
            }
        }
        return true;
    }

    private class SingleEnchantmentHandler {

        private Enchantment type;
        private Existence existence = Existence.WHATEVER;
        private int level = 1;
        private Number number = Number.WHATEVER;

        public SingleEnchantmentHandler() {
        }

        @SuppressWarnings("deprecation")
        private void set(final String enchant) throws InstructionParseException {
            final String[] parts;
            if (enchant == null || (parts = enchant.split(":")).length == 0) {
                throw new InstructionParseException("Missing value");
            }
            if (parts[0].startsWith("none-")) {
                existence = Existence.FORBIDDEN;
                parts[0] = parts[0].substring(5);
            }
            type = Enchantment.getByName(parts[0].toUpperCase(Locale.ROOT));
            if (type == null) {
                throw new InstructionParseException("Unknown enchantment type: " + parts[0]);
            }
            if (existence == Existence.FORBIDDEN) {
                return;
            }
            existence = Existence.REQUIRED;
            if (parts.length != 2) {
                throw new InstructionParseException("Wrong enchantment format");
            }
            if ("?".equals(parts[1])) {
                number = Number.WHATEVER;
                parts[1] = "1";
            } else if (parts[1].endsWith("-")) {
                number = Number.LESS;
                parts[1] = parts[1].substring(0, parts[1].length() - 1);
            } else if (parts[1].endsWith("+")) {
                number = Number.MORE;
                parts[1] = parts[1].substring(0, parts[1].length() - 1);
            } else {
                number = Number.EQUAL;
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

        private boolean check(final Integer level) {
            if (existence == Existence.WHATEVER) {
                return true;
            }
            if (level == null) {
                return existence == Existence.FORBIDDEN;
            }
            switch (number) {
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
