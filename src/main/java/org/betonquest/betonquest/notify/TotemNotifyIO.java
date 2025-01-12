package org.betonquest.betonquest.notify;

import com.comphenix.packetwrapper.WrapperPlayServerEntityStatus;
import com.comphenix.packetwrapper.WrapperPlayServerSetSlot;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**
 * Shows a totem of undying animation with a "customModelData" NBT tag.
 */
public class TotemNotifyIO extends NotifyIO {

    /**
     * The totems customModelData.
     * It instructs the game client to display a different model or texture when the totem is shown.
     */
    private final int customModelData;

    /**
     * Creates a new TotemNotifyIO instance based on the user's instruction string.
     *
     * @param pack the related {@link QuestPackage}
     * @param data map with user instructions.
     * @throws QuestException if the user's input couldn't be parsed.
     */
    public TotemNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);
        customModelData = getIntegerData("custommodeldata", 2);
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        sendOffhandPacket(onlineProfile.getPlayer(), buildFakeTotem());
        playSilentTotemEffect(onlineProfile.getPlayer());
        sendOffhandPacket(onlineProfile.getPlayer(), onlineProfile.getPlayer().getInventory().getItemInOffHand());
    }

    private ItemStack buildFakeTotem() {
        final ItemStack fakeTotem = new ItemStack(Material.TOTEM_OF_UNDYING);
        final ItemMeta totemMeta = fakeTotem.getItemMeta();
        if (totemMeta == null) {
            throw new IllegalStateException("ItemMeta for TotemIO ItemStack is null.");
        }
        totemMeta.setCustomModelData(customModelData);
        fakeTotem.setItemMeta(totemMeta);
        return fakeTotem;
    }

    private void sendOffhandPacket(final Player player, final ItemStack offHandItem) {
        final WrapperPlayServerSetSlot slotPacket = new WrapperPlayServerSetSlot() {
            @Override
            public void setSlot(final int value) {
                handle.getIntegers().write(2, value);
            }
        };

        slotPacket.setSlot(45);
        slotPacket.setSlotData(offHandItem);
        slotPacket.setWindowId(0);
        slotPacket.sendPacket(player);
    }

    private void playSilentTotemEffect(final Player player) {
        final WrapperPlayServerEntityStatus statusPacket = new WrapperPlayServerEntityStatus();
        statusPacket.setEntityStatus((byte) 35);
        statusPacket.setEntityID(player.getEntityId());
        statusPacket.sendPacket(player);

        Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> player.stopSound(Sound.ITEM_TOTEM_USE, SoundCategory.PLAYERS));
    }
}
