package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
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
    public void set(final Instruction instruction) throws QuestException {
        this.durability = NumberValue.create("durability", "item durability", instruction);
    }

    @Override
    public void populate(final Damageable damageableMeta, @Nullable final Profile profile) throws QuestException {
        if (durability != null) {
            damageableMeta.setDamage(durability.getValue(profile).value());
        }
    }

    @Override
    public boolean check(final Damageable meta, @Nullable final Profile profile) throws QuestException {
        return this.durability != null && this.durability.getValue(profile).isValid(meta.getDamage());
    }
}
