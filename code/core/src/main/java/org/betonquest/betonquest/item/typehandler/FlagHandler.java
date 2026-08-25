package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles metadata about item flags.
 */
public class FlagHandler implements ItemMetaHandler.Standard {

    /**
     * Construct a new FlagHandler.
     */
    public FlagHandler() {

    }

    @Override
    public Set<String> keys() {
        return Set.of("flags");
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        if (meta.getItemFlags().isEmpty()) {
            return null;
        }
        return "flags:" + String.join(",", meta.getItemFlags().stream().map(ItemFlag::name).sorted().toList());
    }

    @Override
    @Nullable
    public Attribute.Standard parse(final Instruction instruction) throws QuestException {
        final ExistenceArgument<Set<ItemFlag>> flags = (ExistenceArgument<Set<ItemFlag>>) instruction.enumeration(ItemFlag.class)
                .list()
                .map(list -> Pair.of(Existence.REQUIRED, Set.copyOf(list)))
                .prefilter("", Pair.of(Existence.FORBIDDEN, Set.of()))
                .get("flags").orElse(null);
        if (flags == null) {
            return null;
        }
        return new NonResolved(flags);
    }

    /**
     * The attribute with placeholders.
     *
     * @param flags Set of ItemFlags on the ItemStack.
     */
    private record NonResolved(ExistenceArgument<Set<ItemFlag>> flags) implements Attribute.Standard {

        @Override
        public ResolvedAttribute<ItemMeta> resolve(@Nullable final Profile profile) throws QuestException {
            final Pair<Existence, Set<ItemFlag>> pair = flags.getValue(profile);
            return new Resolved(pair.getLeft(), pair.getRight());
        }
    }

    /**
     * The resolved attribute.
     *
     * @param existence Existence of the flags.
     * @param flags     Set of ItemFlags on the ItemStack.
     */
    private record Resolved(Existence existence, Set<ItemFlag> flags) implements ResolvedAttribute.Standard {

        @Override
        public void populate(final ItemMeta meta) {
            flags.forEach(meta::addItemFlags);
        }

        @Override
        public boolean check(final ItemMeta meta) {
            return existence == Existence.WHATEVER
                    || existence == Existence.FORBIDDEN && meta.getItemFlags().isEmpty()
                    || existence == Existence.REQUIRED && !meta.getItemFlags().isEmpty() && flags.equals(meta.getItemFlags());
        }
    }
}
