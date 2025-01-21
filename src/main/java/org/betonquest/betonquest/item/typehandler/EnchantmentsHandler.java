package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("PMD.CommentRequired")
public class EnchantmentsHandler implements ItemMetaHandler<ItemMeta> {
    private List<SingleEnchantmentHandler> checkers = new ArrayList<>();

    private Existence checkersE = Existence.WHATEVER;

    /**
     * If the Enchantment need to be exact the same or just contain all specified enchantments.
     */
    private boolean exact = true;

    public EnchantmentsHandler() {
    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("enchants", "enchants-containing");
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        if (meta instanceof final EnchantmentStorageMeta storageMeta) {
            if (!storageMeta.hasStoredEnchants()) {
                return null;
            }
            final StringBuilder string = new StringBuilder();
            for (final Enchantment enchant : storageMeta.getStoredEnchants().keySet()) {
                string.append(enchant.getName()).append(':').append(storageMeta.getStoredEnchants().get(enchant)).append(',');
            }
            return "enchants:" + string.substring(0, string.length() - 1);
        }
        if (!meta.hasEnchants()) {
            return null;
        }
        final StringBuilder string = new StringBuilder();
        for (final Enchantment enchant : meta.getEnchants().keySet()) {
            string.append(enchant.getName()).append(':').append(meta.getEnchants().get(enchant)).append(',');
        }
        return "enchants:" + string.substring(0, string.length() - 1);
    }

    @Override
    public void set(final String key, final String data) throws QuestException {
        switch (key) {
            case "enchants" -> set(data);
            case "enchants-containing" -> exact = false;
            default -> throw new QuestException("Unknown enchantment key: " + key);
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        if (meta instanceof final EnchantmentStorageMeta enchantMeta) {
            // why no bulk adding method?!
            final Map<Enchantment, Integer> map = get();
            for (final Map.Entry<Enchantment, Integer> e : map.entrySet()) {
                enchantMeta.addStoredEnchant(e.getKey(), e.getValue(), true);
            }
        } else {
            final Map<Enchantment, Integer> map = get();
            for (final Map.Entry<Enchantment, Integer> e : map.entrySet()) {
                meta.addEnchant(e.getKey(), e.getValue(), true);
            }
        }
    }

    @Override
    public boolean check(final ItemMeta meta) {
        if (meta instanceof final EnchantmentStorageMeta enchantMeta) {
            return check(enchantMeta.getStoredEnchants());
        } else {
            return check(meta.getEnchants());
        }
    }

    public void set(final String enchants) throws QuestException {
        final String[] parts = HandlerUtil.getNNSplit(enchants, "Enchantment is null!", ",");
        if (Existence.NONE_KEY.equalsIgnoreCase(parts[0])) {
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

        private SingleEnchantmentHandler(final String enchant) throws QuestException {
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
                throw new QuestException("Wrong enchantment format");
            }
            final Map.Entry<Number, Integer> enchantmentLevel = HandlerUtil.getNumberValue(parts[1], "enchantment level");
            number = enchantmentLevel.getKey();
            level = enchantmentLevel.getValue();
        }

        @SuppressWarnings("deprecation")
        private Enchantment getType(final String name) throws QuestException {
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
