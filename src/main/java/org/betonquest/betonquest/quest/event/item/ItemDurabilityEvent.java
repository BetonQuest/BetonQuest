package org.betonquest.betonquest.quest.event.item;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.event.point.Point;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * The item durability event, to modify the durability of an item.
 */
public class ItemDurabilityEvent implements OnlineEvent {
    /**
     * The slot to target.
     */
    private final EquipmentSlot slot;

    /**
     * The point type, how the durability should be modified.
     */
    private final Point modification;

    /**
     * The amount of the modification.
     */
    private final VariableNumber amount;

    /**
     * To ignore {@link ItemMeta#isUnbreakable()} and {@link Enchantment#DURABILITY}.
     */
    private final boolean ignoreUnbreakable;

    /**
     * To ignore bukkit event logic.
     */
    private final boolean ignoreEvents;

    /**
     * The random instance to use.
     */
    private final Random random;

    /**
     * Creates a new item durability event.
     *
     * @param slot              of the item
     * @param modification      on the durability
     * @param amount            argument of the modification
     * @param ignoreUnbreakable whether if the enchantment and tag should be ignored or respected
     * @param ignoreEvents      whether the bukkit events should be ignored or called
     * @param random            to use for the durability calculation
     */
    public ItemDurabilityEvent(final EquipmentSlot slot, final Point modification, final VariableNumber amount, final boolean ignoreUnbreakable, final boolean ignoreEvents, final Random random) {
        this.slot = slot;
        this.modification = modification;
        this.amount = amount;
        this.ignoreUnbreakable = ignoreUnbreakable;
        this.ignoreEvents = ignoreEvents;
        this.random = random;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final EntityEquipment equipment = player.getEquipment();
        final ItemStack itemStack = equipment.getItem(slot);
        if (itemStack.getType().isAir() || !(itemStack.getItemMeta() instanceof final Damageable damageable)) {
            return;
        }
        if (damageable.isUnbreakable() && !ignoreUnbreakable) {
            return;
        }
        final double value = amount.getValue(profile).doubleValue();
        if (value == 0) {
            if (modification == Point.SET || modification == Point.MULTIPLY) {
                processBreak(player, itemStack, damageable);
            }
            return;
        }
        processDamage(player, itemStack, damageable, value);
    }

    private void processDamage(final Player player, final ItemStack itemStack, final Damageable damageable, final double value) {
        final int maxDurability = itemStack.getType().getMaxDurability();
        final int oldDamage = damageable.getDamage();
        final int actualDurability = maxDurability - oldDamage;

        final int newDurability = modification.modify(actualDurability, value);
        final int newDamage = maxDurability - newDurability;
        final int damageDifference = newDamage - oldDamage;

        final int durabilityModifiedDamage;
        final boolean isArmor = isArmor(itemStack);
        if (damageDifference < 0) {
            durabilityModifiedDamage = -getDurabilityModifiedDamage(damageable, -damageDifference, isArmor);
        } else if (damageDifference > 0) {
            durabilityModifiedDamage = getDurabilityModifiedDamage(damageable, damageDifference, isArmor);
        } else {
            return;
        }

        final int actualNewDamage = oldDamage + durabilityModifiedDamage;
        damageable.setDamage(actualNewDamage);

        if (maxDurability - actualNewDamage <= 0) {
            processBreak(player, itemStack, damageable);
        }

        itemStack.setItemMeta(damageable);
    }

    private int getDurabilityModifiedDamage(final ItemMeta meta, final int damageDifference, final boolean isArmor) {
        if (ignoreUnbreakable) {
            return damageDifference;
        }
        final int level = meta.getEnchantLevel(Enchantment.DURABILITY);
        if (level == 0) {
            return damageDifference;
        }
        final int levelPlusOne = level + 1;
        final int chance;
        if (isArmor) {
            chance = 60 + 40 / levelPlusOne;
        } else {
            chance = 100 / levelPlusOne;
        }
        return (int) random.ints(damageDifference).filter(random -> random >= chance).count();
    }

    private void processBreak(final Player player, final ItemStack itemStack, final Damageable damageable) {
        if (!ignoreEvents) {
            final PlayerItemBreakEvent event = new PlayerItemBreakEvent(player, itemStack);
            Bukkit.getPluginManager().callEvent(event);
        }
        itemStack.setAmount(itemStack.getAmount() - 1);
        damageable.setDamage(0);
        player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1, 1);
    }

    private boolean isArmor(@Nullable final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        final String typeNameString = itemStack.getType().name();
        return typeNameString.endsWith("_HELMET")
                || typeNameString.endsWith("_CHESTPLATE")
                || typeNameString.endsWith("_LEGGINGS")
                || typeNameString.endsWith("_BOOTS");
    }
}
