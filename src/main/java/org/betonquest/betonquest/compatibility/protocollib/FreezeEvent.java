package org.betonquest.betonquest.compatibility.protocollib;

import com.comphenix.packetwrapper.WrapperPlayServerMount;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("PMD.CommentRequired")
public class FreezeEvent extends QuestEvent {

    private static final Map<UUID, ArmorStand> STANDS = new HashMap<>();
    private final VariableNumber ticksVar;

    public FreezeEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        ticksVar = instruction.getVarNum();
    }

    public static void cleanup() {
        STANDS.forEach((uuid, armorStand) -> armorStand.remove());
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().getOnlinePlayer();
        final UUID uuid = player.getUniqueId();
        final int ticks = ticksVar.getInt(profile);

        if (STANDS.get(player.getUniqueId()) != null) {
            STANDS.get(player.getUniqueId()).remove();
        }

        final ArmorStand armorStand = player.getWorld().spawn(player.getLocation().clone().add(0, -1.1, 0), ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        STANDS.put(uuid, armorStand);

        final WrapperPlayServerMount mount = new WrapperPlayServerMount();
        mount.setEntityID(armorStand.getEntityId());
        mount.setPassengerIds(new int[]{player.getEntityId()});
        mount.sendPacket(player);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(" "));

        Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), () -> {
            STANDS.remove(uuid);
            armorStand.remove();

        }, ticks);
        return null;
    }
}
