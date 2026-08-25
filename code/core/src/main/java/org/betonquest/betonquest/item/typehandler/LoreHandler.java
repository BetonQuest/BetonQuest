package org.betonquest.betonquest.item.typehandler;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.LoreMetaHandler;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Handles de-/serialization of Item Lore.
 */
public class LoreHandler implements LoreMetaHandler {

    /**
     * Handler to use and to check
     * if the last lore line should be interpreted as 'quest-item' line and ignored in checks.
     */
    private final QuestHandler questHandler;

    /**
     * Creates an empty LoreHandler.
     *
     * @param questHandler the handler to parse from and use to check
     *                     if the last lore line should be interpreted as 'quest-item' line and ignored in checks
     */
    public LoreHandler(final QuestHandler questHandler) {
        this.questHandler = questHandler;
    }

    @Override
    public Set<String> keys() {
        return Set.of("lore", "lore-containing");
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        if (meta.hasLore()) {
            final StringBuilder string = new StringBuilder(22);
            for (final Component line : meta.lore()) {
                string.append(' ').append(HandlerUtil.toKeyValue("lore", line));
            }
            return string.substring(1);
        }
        return null;
    }

    @Override
    public LoreAttribute parse(final Instruction instruction) throws QuestException {
        final ExistenceArgument<List<Component>> lore = ExistenceArgument.apply("lore", instruction.component().list()); // problem when there is more than one "lore"
        // TODO fix wrong separator, compact and everything else in this diff
        final Argument<Boolean> exact = instruction.bool().map(bool -> !bool).get("lore-containing", true);
        final Attribute.Standard questAttribute = questHandler.parse(instruction);
        return new NonResolved(lore, exact, questAttribute == null ? profile -> QuestHandler.EMPTY : questAttribute);
    }

    /**
     * The lore with placeholders.
     */
    private record NonResolved(ExistenceArgument<List<Component>> lore, Argument<Boolean> exact,
                               Attribute.Standard questAttribute) implements LoreAttribute {

        @Override
        public ResolvedLoreAttribute resolve(@Nullable final Profile profile) throws QuestException {
            final Pair<Existence, List<Component>> pair = this.lore.getValue(profile);
            final boolean exact = this.exact.getValue(profile);
            final QuestHandler.QuestResolved questResolved = (QuestHandler.QuestResolved) questAttribute.resolve(profile);
            return new Resolved(pair.getLeft(), pair.getRight(), exact, questResolved);
        }
    }

    /**
     * The resolved lore.
     */
    private record Resolved(Existence existence, List<Component> storedLore, boolean exact,
                            QuestHandler.QuestResolved questResolved) implements ResolvedLoreAttribute {

        @Override
        public void populate(final ItemMeta meta) {
            meta.lore(get());
            questResolved.populate(meta);
        }

        @Override
        public boolean check(final ItemMeta meta) {
            final List<Component> original = meta.lore();
            final List<Component> lore = original == null ? null
                    : original.subList(0, Math.max(0, original.size() - (questResolved.isLoreSet() ? 1 : 0)));

            return switch (existence) {
                case WHATEVER -> true;
                case REQUIRED -> checkRequired(lore);
                case FORBIDDEN -> lore == null || lore.isEmpty();
            };
        }

        @Override
        public List<Component> get() {
            return storedLore;
        }

        private boolean checkRequired(@Nullable final List<Component> lore) {
            if (lore == null) {
                return false;
            }
            if (!exact) {
                return !checkNonExact(lore);
            }
            if (storedLore.size() != lore.size()) {
                return false;
            }
            for (int i = 0; i < lore.size(); i++) {
                if (!storedLore.get(i).equals(lore.get(i).compact())) {
                    return false;
                }
            }
            return true;
        }

        private boolean checkNonExact(final List<Component> lore) {
            for (final Component line : storedLore) {
                boolean has = false;
                for (final Component itemLine : lore) {
                    if (itemLine.compact().equals(line)) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    return true;
                }
            }
            return false;
        }
    }
}
