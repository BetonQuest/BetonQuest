package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Handles de-/serialization of Durability/Damage.
 */
public class DurabilityHandler implements ItemMetaHandler<Damageable> {

    /**
     * If a durability is set.
     */
    private boolean isSet;

    /**
     * The durability.
     */
    private int durability;

    /**
     * The number compare state.
     */
    private Number number = Number.WHATEVER;

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
    public void set(final String key, final String data) throws QuestException {
        if (!"durability".equals(key)) {
            throw new QuestException("Unknown durability key: " + key);
        }
        final Map.Entry<Number, Integer> itemDurability = HandlerUtil.getNumberValue(data, "item durability");
        this.number = itemDurability.getKey();
        this.durability = itemDurability.getValue();
        isSet = true;
    }

    @Override
    public void populate(final Damageable damageableMeta) {
        if (isSet) {
            damageableMeta.setDamage(durability);
        }
    }

    @Override
    public boolean check(final Damageable meta) {
        return number.isValid(meta.getDamage(), this.durability);
    }
}
