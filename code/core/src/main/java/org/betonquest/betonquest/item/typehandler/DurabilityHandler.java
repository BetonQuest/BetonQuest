package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
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
     * The durability with their compare state.
     */
    @Nullable
    private Argument<NumberValue> durability;

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
    public void parse(final Instruction instruction) throws QuestException {
        this.durability = NumberValue.create("durability", "item durability", instruction);
    }

    @Override
    public ResolvedAttribute<Damageable> resolve(@Nullable final Profile profile) throws QuestException {
        final NumberValue durability = this.durability == null ? null : this.durability.getValue(profile);
        return new ResolvedAttribute<>() {

            @Override
            public Class<Damageable> metaClass() {
                return Damageable.class;
            }

            @Override
            public void populate(final Damageable damageableMeta) {
                if (durability != null) {
                    damageableMeta.setDamage(durability.value());
                }
            }

            @Override
            public boolean check(final Damageable meta) {
                return durability != null && durability.isValid(meta.getDamage());
            }
        };
    }
}
