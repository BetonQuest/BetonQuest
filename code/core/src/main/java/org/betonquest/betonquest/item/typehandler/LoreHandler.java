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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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
        final String rawLoreLines = instruction.getValueParts().stream()
                .filter(part -> part.toLowerCase(Locale.ROOT).startsWith("lore:"))
                .map(part -> part.substring("lore:".length()))
                .collect(Collectors.joining(";"));
        final ExistenceArgument<List<Component>> lore;
        if (rawLoreLines.isEmpty()) {
            lore = ExistenceArgument.whateverEmptyList();
        } else {
            lore = instruction.chainForArgument(rawLoreLines).parse(data -> {
                final String[] split = data.split(";");
                final List<Component> lorelei = new ArrayList<>(split.length);
                for (final String line : split) {
                    lorelei.add(instruction.chainForArgument(line).component().map(Component::compact).get().getValue(null));
                }
                return Pair.of(Existence.REQUIRED, lorelei);
            }).get()::getValue;
        }
        final Argument<Boolean> exact = instruction.bool().map(bool -> !bool).get("lore-containing", true);
        final Attribute questAttribute = questHandler.parse(instruction);
        return new NonResolved(lore, exact, questAttribute == null ? profile -> QuestHandler.EMPTY : questAttribute);
    }

    /**
     * The lore with placeholders.
     *
     * @param lore           The Lore with existence.
     * @param exact          If the lore need to be exact the same or just contain all specified lines.
     * @param questAttribute To check if the last lore line should be ignored and to populate/check.
     */
    private record NonResolved(ExistenceArgument<List<Component>> lore, Argument<Boolean> exact,
                               Attribute questAttribute) implements LoreAttribute {

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
     *
     * @param existence     If the lore is required.
     * @param storedLore    The Lore lines.
     * @param exact         If the lore need to be exact the same or just contain all specified lines.
     * @param questResolved To check if the last lore line should be ignored and to populate/check.
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
            if (!questResolved.check(meta)) {
                return false;
            }
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
