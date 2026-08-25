package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.Number;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Handles de-/serialization of Enchantments.
 */
public class EnchantmentsHandler implements ItemMetaHandler.Standard {

    /**
     * The empty default Constructor.
     */
    public EnchantmentsHandler() {
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
    @Nullable
    public Attribute.Standard parse(final Instruction instruction) throws QuestException {
        final ExistenceArgument<List<SingleEnchantmentHandler>> checkers = ExistenceArgument.applyListOrNull("enchants", instruction.parse(SingleEnchantmentHandler::new));
        final Optional<Argument<Boolean>> exact = instruction.bool().map(bool -> !bool).get("enchants-containing");
        if (checkers == null && exact.isEmpty()) {
            return null;
        }
        return new NonParsed(checkers == null ? ExistenceArgument.whateverEmptyList() : checkers, exact.orElse(profile -> true));
    }

    /**
     * The attribute with placeholders.
     *
     * @param checkers The individual Enchantment Handlers.
     * @param exact    If the Enchantment need to be exact the same or just contain all specified enchantments.
     */
    private record NonParsed(ExistenceArgument<List<SingleEnchantmentHandler>> checkers, Argument<Boolean> exact)
            implements Attribute.Standard {

        @Override
        public ResolvedAttribute.Standard resolve(@Nullable final Profile profile) throws QuestException {
            final Pair<Existence, List<SingleEnchantmentHandler>> pair = checkers.getValue(profile);
            final boolean exact = this.exact.getValue(profile);
            return new Resolved(pair, exact);
        }
    }

    /**
     * The resolved attribute.
     */
    private record Resolved(Pair<Existence, List<SingleEnchantmentHandler>> pair, boolean exact)
            implements ResolvedAttribute.Standard {

        @Override
        public void populate(final ItemMeta meta) {
            if (meta instanceof final EnchantmentStorageMeta enchantMeta) {
                final Map<Enchantment, Integer> map = get();
                for (final Map.Entry<Enchantment, Integer> enchantmentEntry : map.entrySet()) {
                    enchantMeta.addStoredEnchant(enchantmentEntry.getKey(), enchantmentEntry.getValue(), true);
                }
            } else {
                final Map<Enchantment, Integer> map = get();
                for (final Map.Entry<Enchantment, Integer> enchantmentEntry : map.entrySet()) {
                    meta.addEnchant(enchantmentEntry.getKey(), enchantmentEntry.getValue(), true);
                }
            }
        }

        @Override
        public boolean check(final ItemMeta meta) {
            if (meta instanceof final EnchantmentStorageMeta enchantMeta) {
                return check(enchantMeta.getStoredEnchants());
            }
            return check(meta.getEnchants());
        }

        private Map<Enchantment, Integer> get() {
            final Existence checkersE = pair.getLeft();
            final Map<Enchantment, Integer> map = new HashMap<>();
            if (checkersE == Existence.FORBIDDEN) {
                return map;
            }
            for (final SingleEnchantmentHandler checker : pair.getRight()) {
                if (checker.existence != Existence.FORBIDDEN) {
                    map.put(checker.type, checker.level);
                }
            }
            return map;
        }

        private boolean check(final Map<Enchantment, Integer> map) {
            final Existence checkersE = pair.getLeft();
            if (checkersE == Existence.WHATEVER) {
                return true;
            }
            if (map.isEmpty()) { // TODO remove that? - any value should be set if not whatever? but they could all be '?' too
                return checkersE == Existence.FORBIDDEN;
            }
            if (exact && map.size() != get().size()) {
                return false;
            }
            for (final SingleEnchantmentHandler checker : pair.getRight()) {
                if (!checker.check(map.get(checker.type))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Checks a single Enchantments validity.
     */
    private static final class SingleEnchantmentHandler {

        /**
         * The expected argument count of the formatted enchantment.
         */
        private static final int INSTRUCTION_FORMAT_LENGTH = 2;

        /**
         * The enchantment to check.
         */
        private final Enchantment type;

        /**
         * The required existence.
         */
        private final Existence existence;

        /**
         * The number compare state.
         */
        private final Number number;

        /**
         * The set enchantment level.
         */
        private final int level;

        private SingleEnchantmentHandler(final String enchant) throws QuestException {
            final String[] parts = HandlerUtil.getSplit(enchant, "Enchantment is null!", ":");
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
            final Enchantment enchantment = Enchantment.getByName(name.toUpperCase(Locale.ROOT));
            if (enchantment == null) {
                throw new QuestException("Unknown enchantment type '%s'!".formatted(name));
            }
            return enchantment;
        }

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
