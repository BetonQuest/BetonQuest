package org.betonquest.betonquest.item.typehandler;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de-/serialization of Display Names.
 */
public class NameHandler implements ItemMetaHandler<ItemMeta> {

    /**
     * Item Display Name's required existence and value.
     */
    private ExistenceArgument<@Nullable Component> name = ExistenceArgument.whateverNullValue();

    /**
     * Creates an empty NameHandler.
     */
    public NameHandler() {
    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
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
    public void parse(final Instruction instruction) throws QuestException {
        // TODO is empty check?
        this.name = ExistenceArgument.apply("name", instruction.component().map(Component::compact));
    }

    @Override
    public ResolvedName resolve(@Nullable final Profile profile) throws QuestException {
        return new Resolved(name.getValue(profile));
    }

    /**
     * Specific resolved Name Handler for the {@link QuestItem#getName()} method.
     */
    public interface ResolvedName extends ResolvedAttribute.ResolvedItemMeta {

        /**
         * Get the name.
         *
         * @return the name
         */
        @Nullable
        Component get();
    }

    /**
     * The resolved handler.
     *
     * @param name Item Display Name's required existence and value.
     */
    public record Resolved(Pair<Existence, @Nullable Component> name) implements ResolvedName {

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
