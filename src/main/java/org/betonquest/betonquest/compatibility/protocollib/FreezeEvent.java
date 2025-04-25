package org.betonquest.betonquest.compatibility.protocollib;

import com.comphenix.packetwrapper.WrapperPlayServerMount;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event to block all player entity movement.
 */
public class FreezeEvent implements OnlineEvent {
    /**
     * Armor stands to set as mount.
     */
    private static final Map<UUID, ArmorStand> STANDS = new HashMap<>();

    /**
     * Freeze duration.
     */
    private final Variable<Number> ticksVar;

    /**
     * Create a new event that freezes a player.
     *
     * @param ticks the freeze duration
     */
    public FreezeEvent(final Variable<Number> ticks) {
        ticksVar = ticks;
    }

    /**
     * Removes all armor stands.
     */
    public static void cleanup() {
        STANDS.forEach((uuid, armorStand) -> armorStand.remove());
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final int ticks = ticksVar.getValue(profile).intValue();

        if (STANDS.get(profile.getProfileUUID()) != null) {
            STANDS.get(profile.getProfileUUID()).remove();
        }

        final Player player = profile.getPlayer();
        final ArmorStand armorStand = player.getWorld().spawn(player.getLocation().clone().add(0, -1.1, 0), ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        STANDS.put(profile.getProfileUUID(), armorStand);

        final WrapperPlayServerMount mount = new WrapperPlayServerMount();
        mount.setEntityID(armorStand.getEntityId());
        mount.setPassengerIds(new int[]{player.getEntityId()});
        mount.sendPacket(player);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(" "));

        Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), () -> {
            STANDS.remove(profile.getProfileUUID());
            armorStand.remove();
        }, ticks);
    }
}
