package org.betonquest.betonquest.item.typehandler;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Handles de-/serialization of Item Lore.
 */
public class LoreHandler implements ItemMetaHandler<ItemMeta> {

    /**
     * If the last lore line should be interpreted as 'quest-item' line and ignored in checks.
     */
    private final QuestFunction<Profile, Boolean> ignoreLastLine;

    /**
     * The lore.
     */
    private ExistenceArgument<List<Component>> lore = ExistenceArgument.whateverEmptyList();

    /**
     * If the lore need to be exact the same or just contain all specified lines.
     */
    private Argument<Boolean> exact = new DefaultArgument<>(true);

    /**
     * Creates an empty LoreHandler.
     *
     * @param ignoreLastLine if the last lore line should be interpreted as 'quest-item' line and ignored in checks
     */
    public LoreHandler(final QuestFunction<Profile, Boolean> ignoreLastLine) {
        this.ignoreLastLine = ignoreLastLine;
    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
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
    public void set(final Instruction instruction) throws QuestException {
        this.lore = ExistenceArgument.apply("lore", instruction.component().list()); // problem when there is more than one "lore"
        // TODO fix wrong separator, compact and everything else in this diff
        this.exact = instruction.bool().map(bool -> !bool).get("lore-containing", true);
    }

    @Override
    public void populate(final ItemMeta meta, @Nullable final Profile profile) throws QuestException {
        meta.lore(get(profile));
    }

    @Override
    public boolean check(final ItemMeta meta, @Nullable final Profile profile) throws QuestException {
        final List<Component> original = meta.lore();
        final List<Component> lore = original == null ? null
                : original.subList(0, Math.max(0, original.size() - (ignoreLastLine.apply(profile) ? 1 : 0)));
        final Pair<Existence, List<Component>> pair = this.lore.getValue(profile);
        return switch (pair.getLeft()) {
            case WHATEVER -> true;
            case REQUIRED -> checkRequired(lore, profile, pair.getRight());
            case FORBIDDEN -> lore == null || lore.isEmpty();
        };
    }

    /**
     * Gets the lore.
     *
     * @param profile the optional profile for resolving arguments
     * @return the list of lore lines, can be empty
     * @throws QuestException when there is an exception while resolving profile specific data
     */
    public List<Component> get(@Nullable final Profile profile) throws QuestException {
        return lore.getValue(profile).getRight();
    }

    private boolean checkRequired(@Nullable final List<Component> lore, @Nullable final Profile profile,
                                  final List<Component> storedLore) throws QuestException {
        if (lore == null) {
            return false;
        }
        if (!exact.getValue(profile)) {
            return !checkNonExact(lore, storedLore);
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

    private boolean checkNonExact(final List<Component> lore, final List<Component> storedLore) {
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
