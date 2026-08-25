package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.NumberValue;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de-/serialization of Durability/Damage.
 */
public class DurabilityHandler implements ItemMetaHandler<Damageable> {

    /**
     * The empty default Constructor.
     */
    public DurabilityHandler() {
    }

    @Override
    public Class<Damageable> metaClass() {
        return Damageable.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("durability");
    }

    @Override
    @Nullable
    public String serializeToString(final Damageable damageable) {
        if (damageable.hasDamage()) {
            return "durability:" + damageable.getDamage();
        }
        return null;
    }

    @Override
    @Nullable
    public Attribute<Damageable> parse(final Instruction instruction) throws QuestException {
        final Argument<NumberValue> durability = NumberValue.create("durability", "item durability", instruction);
        if (durability == null) {
            return null;
        }
        return new NonResolved(durability);
    }

    /**
     * The attribute with placeholders.
     *
     * @param durability The durability with their compare state.
     */
    private record NonResolved(Argument<NumberValue> durability) implements Attribute<Damageable> {

        @Override
        public ResolvedAttribute<Damageable> resolve(@Nullable final Profile profile) throws QuestException {
            return new Resolved(durability.getValue(profile));
        }
    }

    /**
     * The resolved attribute.
     *
     * @param durability The durability with their compare state.
     */
    private record Resolved(NumberValue durability) implements ResolvedAttribute<Damageable> {

        @Override
        public Class<Damageable> metaClass() {
            return Damageable.class;
        }

        @Override
        public void populate(final Damageable damageableMeta) {
            damageableMeta.setDamage(durability.value());
        }

        @Override
        public boolean check(final Damageable meta) {
            return durability.isValid(meta.getDamage());
        }
    }
}
