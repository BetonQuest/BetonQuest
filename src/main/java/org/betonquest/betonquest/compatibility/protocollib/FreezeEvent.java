package org.betonquest.betonquest.compatibility.protocollib;

import com.comphenix.packetwrapper.WrapperPlayServerMount;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("PMD.CommentRequired")
public class FreezeEvent extends QuestEvent {
    private static final Map<UUID, ArmorStand> STANDS = new HashMap<>();

    private final VariableNumber ticksVar;

    public FreezeEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);

        ticksVar = instruction.get(VariableNumber::new);
    }

    public static void cleanup() {
        STANDS.forEach((uuid, armorStand) -> armorStand.remove());
    }

    @Override
    protected Void execute(final Profile profile) throws QuestException {
        final OnlineProfile onlineProfile = profile.getOnlineProfile().get();
        final int ticks = ticksVar.getInt(profile);

        if (STANDS.get(profile.getProfileUUID()) != null) {
            STANDS.get(profile.getProfileUUID()).remove();
        }

        final ArmorStand armorStand = onlineProfile.getPlayer().getWorld().spawn(onlineProfile.getPlayer().getLocation().clone().add(0, -1.1, 0), ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        STANDS.put(profile.getProfileUUID(), armorStand);

        final WrapperPlayServerMount mount = new WrapperPlayServerMount();
        mount.setEntityID(armorStand.getEntityId());
        mount.setPassengerIds(new int[]{onlineProfile.getPlayer().getEntityId()});
        mount.sendPacket(onlineProfile.getPlayer());

        onlineProfile.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(" "));

        Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), () -> {
            STANDS.remove(profile.getProfileUUID());
            armorStand.remove();
        }, ticks);
        return null;
    }
}
