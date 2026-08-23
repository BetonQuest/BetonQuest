package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de-/serialization of the Unbreakable state.
 */
public class UnbreakableHandler implements ItemMetaHandler<ItemMeta> {

    /**
     * The unbreakable string.
     */
    private static final String UNBREAKABLE = "unbreakable";

    /**
     * The required existence.
     */
    private Argument<Existence> unbreakable = new DefaultArgument<>(Existence.WHATEVER);

    /**
     * The empty default Constructor.
     */
    public UnbreakableHandler() {
    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
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
    public void parse(final Instruction instruction) throws QuestException {
        unbreakable = HandlerUtil.isKeyOrTrue(UNBREAKABLE, instruction);
    }

    @Override
    public ResolvedAttribute<ItemMeta> resolve(@Nullable final Profile profile) throws QuestException {
        final Existence existence = unbreakable.getValue(profile);
        return new ResolvedAttribute.ResolvedItemMeta() {

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
        };
    }
}
