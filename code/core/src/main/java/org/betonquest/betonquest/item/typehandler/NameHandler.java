package org.betonquest.betonquest.item.typehandler;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.NameMetaHandler;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de-/serialization of Display Names.
 */
public class NameHandler implements NameMetaHandler {

    /**
     * The empty default Constructor.
     */
    public NameHandler() {
    }

    @Override
    public Set<String> keys() {
        return Set.of("name");
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        if (meta.hasDisplayName()) {
            return HandlerUtil.toKeyValue("name", meta.displayName());
        }
        return null;
    }

    @Override
    public NameAttribute parse(final Instruction instruction) throws QuestException {
        return new NonResolved(ExistenceArgument.apply("name", instruction.component().map(Component::compact)));
    }

    /**
     * The attribute with placeholders.
     *
     * @param name Item Display Name's required existence and value.
     */
    private record NonResolved(ExistenceArgument<@Nullable Component> name) implements NameAttribute {

        @Override
        public ResolvedNameAttribute resolve(@Nullable final Profile profile) throws QuestException {
            return new Resolved(name.getValue(profile));
        }
    }

    /**
     * The resolved attribute.
     *
     * @param name Item Display Name's required existence and value.
     */
    private record Resolved(Pair<Existence, @Nullable Component> name) implements ResolvedNameAttribute {

        @Override
        public void populate(final ItemMeta meta) {
            meta.displayName(get());
        }

        @Override
        public boolean check(final ItemMeta meta) {
            final Component displayName = meta.hasDisplayName() ? meta.displayName() : null;
            return switch (name.getLeft()) {
                case WHATEVER -> true;
                case REQUIRED -> displayName != null && displayName.compact().equals(name.getRight());
                case FORBIDDEN -> displayName == null;
            };
        }

        @Override
        @Nullable
        public Component get() {
            return name.getRight();
        }
    }
}
