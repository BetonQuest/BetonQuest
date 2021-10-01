package org.betonquest.betonquest.events;

import com.comphenix.packetwrapper.WrapperPlayServerMount;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FreezeEvent extends QuestEvent {

    private static final Map<UUID, ArmorStand> stands = new HashMap<>();
    VariableNumber ticksVar;

    public FreezeEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        ticksVar = instruction.getVarNum();
    }

    public static void cleanup() {
        stands.forEach((uuid, armorStand) -> armorStand.remove());
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final UUID uuid = player.getUniqueId();
        final int ticks = ticksVar.getInt(playerID);

        if (stands.get(player.getUniqueId()) != null) {
            stands.get(player.getUniqueId()).remove();
        }

        final ArmorStand armorStand = player.getWorld().spawn(player.getLocation().clone().add(0, -1.1, 0), ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCanMove(false);
        armorStand.setCanTick(false);
        stands.put(uuid, armorStand);

        final WrapperPlayServerMount mount = new WrapperPlayServerMount();
        mount.setEntityID(armorStand.getEntityId());
        mount.setPassengerIds(new int[]{player.getEntityId()});
        mount.sendPacket(player);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(" "));

        Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), () -> {
            stands.remove(uuid);
            armorStand.remove();

        }, ticks);
        return null;
    }
}
