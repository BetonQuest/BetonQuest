package org.betonquest.betonquest.mc_1_20_6.item;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.typehandler.HandlerUtil;
import org.betonquest.betonquest.item.typehandler.NameHandler;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de-/serialization of (Display) Names.
 */
public class UpdatedNameHandler extends NameHandler {

    /**
     * The 'item-name' string.
     */
    private static final String ITEM_NAME = "item-name";

    /**
     * Creates an empty NameHandler with also an 'itemName'.
     */
    public UpdatedNameHandler() {
        super();
    }

    @Override
    public Set<String> keys() {
        return Set.of("name", ITEM_NAME);
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        final String name = super.serializeToString(meta);
        if (meta.hasItemName()) {
            return (name == null ? "" : name + " ") + HandlerUtil.toKeyValue("item-name", meta.itemName());
        }
        return name;
    }

    @Override
    public NameAttribute parse(final Instruction instruction) throws QuestException {
        final NameAttribute parsed = super.parse(instruction);
        // TODO is empty check?
        return new UpdatedNonResolved(parsed, ExistenceArgument.apply(ITEM_NAME, instruction.component().map(Component::compact)));
    }

    /**
     * The attribute with placeholders.
     */
    private record UpdatedNonResolved(NameAttribute displayName,
                                      Argument<Pair<Existence, @Nullable Component>> itemName) implements NameAttribute {

        @Override
        public ResolvedNameAttribute resolve(final @Nullable Profile profile) throws QuestException {
            final ResolvedNameAttribute displayName = displayName().resolve(profile);
            return new UpdatedResolved(displayName, itemName.getValue(profile));
        }
    }

    /**
     * The resolved attribute.
     *
     * @param itemName Item Display Name's required existence and value.
     */
    public record UpdatedResolved(ResolvedNameAttribute displayName,
                                  Pair<Existence, @Nullable Component> itemName) implements ResolvedNameAttribute {

        @Override
        public void populate(final ItemMeta meta) {
            displayName.populate(meta);
            meta.itemName(itemName.getRight());
        }

        @Override
        public boolean check(final ItemMeta meta) {
            if (!displayName.check(meta)) {
                return false;
            }
            final Component itemName = meta.hasItemName() ? meta.itemName() : null;
            return switch (this.itemName.getLeft()) {
                case WHATEVER -> true;
                case REQUIRED -> itemName != null && itemName.compact().equals(this.itemName.getRight());
                case FORBIDDEN -> itemName == null;
            };
        }

        @Override
        @Nullable
        public Component get() {
            final Component display = displayName.get();
            return display == null ? itemName.getRight() : display;
        }
    }
}
