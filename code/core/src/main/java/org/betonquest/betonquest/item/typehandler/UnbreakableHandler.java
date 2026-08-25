package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de-/serialization of the Unbreakable state.
 */
public class UnbreakableHandler implements ItemMetaHandler.Standard {

    /**
     * The unbreakable string.
     */
    private static final String UNBREAKABLE = "unbreakable";

    /**
     * The empty default Constructor.
     */
    public UnbreakableHandler() {
    }

    @Override
    public Set<String> keys() {
        return Set.of(UNBREAKABLE);
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        if (meta.isUnbreakable()) {
            return UNBREAKABLE;
        }
        return null;
    }

    @Override
    @Nullable
    public Attribute.Standard parse(final Instruction instruction) throws QuestException {
        final Argument<Existence> unbreakable = HandlerUtil.getIsKeyOrTrue(UNBREAKABLE, instruction);
        if (unbreakable == null) {
            return null;
        }
        return new NonResolved(unbreakable);
    }

    /**
     * The attribute with placeholders.
     *
     * @param unbreakable if the item should be unbreakable
     */
    private record NonResolved(Argument<Existence> unbreakable) implements Attribute.Standard {

        @Override
        public ResolvedAttribute<ItemMeta> resolve(@Nullable final Profile profile) throws QuestException {
            final Existence existence = unbreakable.getValue(profile);
            return new Resolved(existence);
        }
    }

    /**
     * The resolved attribute.
     *
     * @param existence if the item should be unbreakable
     */
    private record Resolved(Existence existence) implements ResolvedAttribute.Standard {

        @Override
        public void populate(final ItemMeta meta) {
            meta.setUnbreakable(existence == Existence.REQUIRED);
        }

        @Override
        public boolean check(final ItemMeta meta) {
            return switch (existence) {
                case WHATEVER -> true;
                case REQUIRED -> meta.isUnbreakable();
                case FORBIDDEN -> !meta.isUnbreakable();
            };
        }
    }
}
