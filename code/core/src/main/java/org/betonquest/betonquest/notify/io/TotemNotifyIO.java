package org.betonquest.betonquest.notify.io;

import com.comphenix.packetwrapper.WrapperPlayServerEntityStatus;
import com.comphenix.packetwrapper.WrapperPlayServerSetSlot;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.notify.NotifyIO;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Shows a totem of undying animation with a "customModelData" NBT tag.
 */
public class TotemNotifyIO extends NotifyIO {

    /**
     * The totems customModelData.
     * It instructs the game client to display a different model or texture when the totem is shown.
     */
    private final Variable<Number> variableCustomModelData;

    /**
     * Creates a new TotemNotifyIO instance based on the user's instruction string.
     *
     * @param pack the related {@link QuestPackage}
     * @param data map with user instructions.
     * @throws QuestException if the user's input couldn't be parsed.
     */
    public TotemNotifyIO(@Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);
        variableCustomModelData = getNumberData("custommodeldata", 2);
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) throws QuestException {
        sendOffhandPacket(onlineProfile.getPlayer(), buildFakeTotem(onlineProfile));
        playSilentTotemEffect(onlineProfile.getPlayer());
        sendOffhandPacket(onlineProfile.getPlayer(), onlineProfile.getPlayer().getInventory().getItemInOffHand());
    }

    private ItemStack buildFakeTotem(final Profile profile) throws QuestException {
        final ItemStack fakeTotem = new ItemStack(Material.TOTEM_OF_UNDYING);
        final int customModelData = variableCustomModelData.getValue(profile).intValue();
        fakeTotem.editMeta(meta -> meta.setCustomModelData(customModelData));
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
