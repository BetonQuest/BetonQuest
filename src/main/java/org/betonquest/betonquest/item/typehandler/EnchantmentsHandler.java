package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem.Existence;
import org.betonquest.betonquest.item.QuestItem.Number;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class EnchantmentsHandler {
    private List<SingleEnchantmentHandler> checkers = new ArrayList<>();

    private Existence checkersE = Existence.WHATEVER;

    private boolean exact = true;

    public EnchantmentsHandler() {
    }

    public void set(final String enchants) throws InstructionParseException {
        final String[] parts = HandlerUtil.getNNSplit(enchants, "Enchantment is null!", ",");
        if ("none".equalsIgnoreCase(parts[0])) {
            checkersE = Existence.FORBIDDEN;
            return;
        }
        checkers = new ArrayList<>(parts.length);
        for (final String part : parts) {
            final SingleEnchantmentHandler checker = new SingleEnchantmentHandler(part);
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
        if (map.isEmpty()) {
            return checkersE == Existence.FORBIDDEN;
        }
        if (exact && map.size() != get().size()) {
            return false;
        }
        for (final SingleEnchantmentHandler checker : checkers) {
            if (!checker.check(map.get(checker.type))) {
                return false;
            }
        }
        return true;
    }

    private static final class SingleEnchantmentHandler {
        /**
         * The expected argument count of the formatted enchantment.
         */
        private static final int INSTRUCTION_FORMAT_LENGTH = 2;

        private final Enchantment type;

        private final Existence existence;

        private final Number number;

        private final int level;

        @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
        private SingleEnchantmentHandler(final String enchant) throws InstructionParseException {
            final String[] parts = HandlerUtil.getNNSplit(enchant, "", ":");
            if (parts[0].startsWith("none-")) {
                existence = Existence.FORBIDDEN;
                type = getType(parts[0].substring("none-".length()));
                number = Number.WHATEVER;
                level = 1;
                return;
            }
            existence = Existence.REQUIRED;
            type = getType(parts[0]);
            if (parts.length != INSTRUCTION_FORMAT_LENGTH) {
                throw new InstructionParseException("Wrong enchantment format");
            }
            final Map.Entry<Number, Integer> enchantmentLevel = HandlerUtil.getNumberValue(parts[1], "enchantment level");
            number = enchantmentLevel.getKey();
            level = enchantmentLevel.getValue();
        }

        @SuppressWarnings("deprecation")
        private Enchantment getType(final String name) throws InstructionParseException {
            return Utils.getNN(Enchantment.getByName(name.toUpperCase(Locale.ROOT)), "Unknown enchantment type: " + name);
        }

        @SuppressWarnings("PMD.UnusedPrivateMethod")
        private boolean check(@Nullable final Integer level) {
            if (existence == Existence.WHATEVER) {
                return true;
            }
            if (level == null) {
                return existence == Existence.FORBIDDEN;
            }
            return switch (number) {
                case EQUAL -> this.level == level;
                case MORE -> this.level <= level;
                case LESS -> this.level >= level;
                case WHATEVER -> true;
            };
        }

    }

}
