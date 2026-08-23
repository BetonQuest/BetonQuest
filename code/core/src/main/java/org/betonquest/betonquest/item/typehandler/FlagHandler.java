package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
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
public class FlagHandler implements ItemMetaHandler<ItemMeta> {

    /**
     * Set of ItemFlags on the ItemStack.
     */
    private ExistenceArgument<Set<ItemFlag>> itemFlags = ExistenceArgument.whateverValue(Set.of());

    /**
     * Construct a new FlagHandler.
     */
    public FlagHandler() {

    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
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
    public void parse(final Instruction instruction) throws QuestException {
        this.itemFlags = (ExistenceArgument<Set<ItemFlag>>) instruction.enumeration(ItemFlag.class)
                .list()
                .map(list -> Pair.of(Existence.REQUIRED, Set.copyOf(list)))
                .prefilter("", Pair.of(Existence.FORBIDDEN, Set.of()))
                .get("flags", Pair.of(Existence.WHATEVER, Set.of()));
    }

    @Override
    public ResolvedAttribute<ItemMeta> resolve(@Nullable final Profile profile) throws QuestException {
        final Pair<Existence, Set<ItemFlag>> pair = this.itemFlags.getValue(profile);
        final Existence existence = pair.getLeft();
        final Set<ItemFlag> itemFlags = pair.getRight();
        return new ResolvedAttribute.ResolvedItemMeta() {

            @Override
            public void populate(final ItemMeta meta) {
                itemFlags.forEach(meta::addItemFlags);
            }

            @Override
            public boolean check(final ItemMeta meta) {
                return existence == Existence.WHATEVER
                        || existence == Existence.FORBIDDEN && meta.getItemFlags().isEmpty()
                        || existence == Existence.REQUIRED && !meta.getItemFlags().isEmpty() && itemFlags.equals(meta.getItemFlags());
            }
        };
    }
}
