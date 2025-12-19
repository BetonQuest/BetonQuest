package org.betonquest.betonquest.notify.io;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.notify.NotifyIO;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
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
     * @param variables the variable processor to create and resolve variables
     * @param pack      the related {@link QuestPackage}
     * @param data      map with user instructions.
     * @throws QuestException if the user's input couldn't be parsed.
     */
    public TotemNotifyIO(final Variables variables, @Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(variables, pack, data);
        variableCustomModelData = getNumberData("custommodeldata", 2);
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) throws QuestException {
        final Player player = onlineProfile.getPlayer();
        sendOffhandPacket(player, buildFakeTotem(onlineProfile));
        playSilentTotemEffect(player);
        sendOffhandPacket(player, player.getInventory().getItemInOffHand());
    }

    private ItemStack buildFakeTotem(final Profile profile) throws QuestException {
        final ItemStack fakeTotem = new ItemStack(Material.TOTEM_OF_UNDYING);
        final int customModelData = variableCustomModelData.getValue(profile).intValue();
        fakeTotem.editMeta(meta -> meta.setCustomModelData(customModelData));
        return fakeTotem;
    }

    private void sendOffhandPacket(final Player player, final ItemStack offHandItem) {
        player.sendEquipmentChange(player, EquipmentSlot.OFF_HAND, offHandItem);
    }

    private void playSilentTotemEffect(final Player player) {
        player.playEffect(EntityEffect.TOTEM_RESURRECT);
        player.stopSound(Sound.ITEM_TOTEM_USE, SoundCategory.PLAYERS);
    }
}
