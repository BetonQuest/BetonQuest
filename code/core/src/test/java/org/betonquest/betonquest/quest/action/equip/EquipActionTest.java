package org.betonquest.betonquest.quest.action.equip;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Tests for {@link EquipAction}.
 */
@ExtendWith(MockitoExtension.class)
class EquipActionTest {

    private static final EquipmentSlot SLOT = EquipmentSlot.HEAD;

    @Test
    void equips_item_when_slot_is_empty(
            @Mock final OnlineProfile profile,
            @Mock final Player player,
            @Mock final EntityEquipment equipment,
            @Mock final ItemWrapper item,
            @Mock final ItemStack newItem
    ) throws QuestException {
        final ItemStack emptyItem = mock(ItemStack.class);
        when(emptyItem.getType()).thenReturn(Material.AIR);
        setupProfile(profile, player, equipment, emptyItem);
        when(item.generate(profile)).thenReturn(newItem);

        createAction(item, false, false).execute(profile);

        verify(equipment).setItem(SLOT, newItem);
    }

    @Test
    void keeps_existing_item_without_flags(
            @Mock final OnlineProfile profile,
            @Mock final Player player,
            @Mock final EntityEquipment equipment,
            @Mock final ItemWrapper item,
            @Mock final ItemStack equippedItem
    ) throws QuestException {
        setupProfile(profile, player, equipment, equippedItem);
        when(equippedItem.getType()).thenReturn(Material.DIAMOND_HELMET);

        createAction(item, false, false).execute(profile);

        verify(equipment, never()).setItem(any(), any());
    }

    @Test
    void drops_new_item_when_slot_is_occupied_and_drop_is_set(
            @Mock final OnlineProfile profile,
            @Mock final Player player,
            @Mock final EntityEquipment equipment,
            @Mock final ItemWrapper item,
            @Mock final ItemStack equippedItem,
            @Mock final ItemStack newItem,
            @Mock final Location location,
            @Mock final World world
    ) throws QuestException {
        setupProfile(profile, player, equipment, equippedItem);
        setupDrop(player, location, world);
        when(equippedItem.getType()).thenReturn(Material.DIAMOND_HELMET);
        when(item.generate(profile)).thenReturn(newItem);

        createAction(item, true, false).execute(profile);

        verify(world).dropItem(location, newItem);
        verify(equipment, never()).setItem(any(), any());
    }

    @Test
    void replaces_existing_item_when_force_is_set(
            @Mock final OnlineProfile profile,
            @Mock final Player player,
            @Mock final EntityEquipment equipment,
            @Mock final ItemWrapper item,
            @Mock final ItemStack equippedItem,
            @Mock final ItemStack newItem
    ) throws QuestException {
        setupProfile(profile, player, equipment, equippedItem);
        when(equippedItem.getType()).thenReturn(Material.DIAMOND_HELMET);
        when(item.generate(profile)).thenReturn(newItem);

        createAction(item, false, true).execute(profile);

        verify(equipment).setItem(SLOT, newItem);
    }

    @Test
    void drops_existing_item_when_drop_and_force_are_set(
            @Mock final OnlineProfile profile,
            @Mock final Player player,
            @Mock final EntityEquipment equipment,
            @Mock final ItemWrapper item,
            @Mock final ItemStack equippedItem,
            @Mock final ItemStack newItem,
            @Mock final Location location,
            @Mock final World world
    ) throws QuestException {
        setupProfile(profile, player, equipment, equippedItem);
        setupDrop(player, location, world);
        when(equippedItem.getType()).thenReturn(Material.DIAMOND_HELMET);
        when(item.generate(profile)).thenReturn(newItem);

        createAction(item, true, true).execute(profile);

        verify(world).dropItem(location, equippedItem);
        verify(equipment).setItem(SLOT, newItem);
    }

    private void setupProfile(final OnlineProfile profile, final Player player,
                              final EntityEquipment equipment, final ItemStack equippedItem) {
        when(profile.getPlayer()).thenReturn(player);
        when(player.getEquipment()).thenReturn(equipment);
        when(equipment.getItem(SLOT)).thenReturn(equippedItem);
    }

    private void setupDrop(final Player player, final Location location, final World world) {
        when(player.getLocation()).thenReturn(location);
        when(location.getWorld()).thenReturn(world);
    }

    private EquipAction createAction(final ItemWrapper item, final boolean drop, final boolean force) {
        final FlagArgument<Boolean> dropFlag = profile -> Optional.of(drop);
        final FlagArgument<Boolean> forceFlag = profile -> Optional.of(force);
        return new EquipAction(profile -> item, profile -> SLOT, dropFlag, forceFlag);
    }
}
