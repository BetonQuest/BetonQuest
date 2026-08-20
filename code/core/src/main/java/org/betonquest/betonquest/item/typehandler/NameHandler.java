package org.betonquest.betonquest.item.typehandler;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
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
    private Argument<Pair<Existence, @Nullable Component>> name = Existence.whateverNullValue();

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
    public void set(final Instruction instruction) throws QuestException {
        // TODO is empty check?
        this.name = Existence.apply("name", instruction.component().map(Component::compact));
    }

    @Override
    public void populate(final ItemMeta meta, @Nullable final Profile profile) throws QuestException {
        meta.displayName(get(profile));
    }

    @Override
    public boolean check(final ItemMeta meta, @Nullable final Profile profile) throws QuestException {
        final Pair<Existence, @Nullable Component> pair = name.getValue(profile);
        final Component displayName = meta.hasDisplayName() ? meta.displayName() : null;
        return switch (pair.getLeft()) {
            case WHATEVER -> true;
            case REQUIRED -> displayName != null && displayName.compact().equals(pair.getRight());
            case FORBIDDEN -> displayName == null;
        };
    }

    /**
     * Get the name.
     *
     * @return the name
     */
    @Nullable
    public Component get(@Nullable final Profile profile) throws QuestException {
        return name.getValue(profile).getRight();
    }
}
