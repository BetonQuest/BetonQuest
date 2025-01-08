package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("PMD.CommentRequired")
public class DurabilityHandler implements ItemMetaHandler<Damageable> {
    private boolean isSet;

    private int durability;

    private Number number = Number.WHATEVER;

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
    public void set(final String key, final String data) throws InstructionParseException {
        if (!"durability".equals(key)) {
            throw new InstructionParseException("Unknown durability key: " + key);
        }
        set(data);
    }

    @Override
    public void populate(final Damageable damageableMeta) {
        if (isSet) {
            damageableMeta.setDamage(durability);
        }
    }

    @Override
    public boolean check(final Damageable meta) {
        return check(meta.getDamage());
    }

    public void set(final String durability) throws InstructionParseException {
        final Map.Entry<Number, Integer> itemDurability = HandlerUtil.getNumberValue(durability, "item durability");
        this.number = itemDurability.getKey();
        this.durability = itemDurability.getValue();
        isSet = true;
    }

    public int get() {
        return durability;
    }

    public boolean check(final int durability) {
        return switch (number) {
            case WHATEVER -> true;
            case EQUAL -> this.durability == durability;
            case MORE -> this.durability <= durability;
            case LESS -> this.durability >= durability;
        };
    }

    /**
     * @return checks if the state of this type handler should be ignored
     */
    public boolean whatever() {
        return number == Number.WHATEVER;
    }
}
