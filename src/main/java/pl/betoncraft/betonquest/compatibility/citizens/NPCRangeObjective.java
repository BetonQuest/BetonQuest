package pl.betoncraft.betonquest.compatibility.citizens;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class NPCRangeObjective extends Objective {

    private final int npcId;
    private final Trigger trigger;
    private final VariableNumber radius;
    private final HashMap<UUID, Boolean> playersInRange;
    private int npcMoveTask;

    public NPCRangeObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        super.template = ObjectiveData.class;
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        trigger = instruction.getEnum(Trigger.class);
        radius = instruction.getVarNum();
        playersInRange = new HashMap<>();
    }

    @Override
    public void start() {
        npcMoveTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(BetonQuest.getInstance(), () -> {
            qreHandler.handle(() -> {
                final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
                if (npc == null) {
                    throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
                }
                final Entity npcEntity = npc.getEntity();
                if (npcEntity == null) {
                    return;
                }
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final String playerID = PlayerConverter.getID(player);
                    if (!containsPlayer(playerID)) {
                        continue;
                    }
                    final double radius = this.radius.getDouble(playerID);
                    final double distanceSqrd = npcEntity.getLocation().distanceSquared(player.getLocation());
                    final double radiusSqrd = radius * radius;

                    if (distanceSqrd <= radiusSqrd) {
                        if (trigger == Trigger.ENTER || trigger == Trigger.LEAVE) {
                            if (playersInRange.containsKey(player.getUniqueId()) && trigger == Trigger.ENTER) {
                                if (playersInRange.get(player.getUniqueId())) {
                                    continue;
                                }
                            } else {
                                playersInRange.put(player.getUniqueId(), true);
                                continue;
                            }
                        } else if (trigger != Trigger.INSIDE) {
                            continue;
                        }
                    } else {
                        if (trigger == Trigger.LEAVE || trigger == Trigger.ENTER) {
                            if (playersInRange.containsKey(player.getUniqueId()) && trigger == Trigger.LEAVE) {
                                if (!playersInRange.get(player.getUniqueId())) {
                                    continue;
                                }
                            } else {
                                playersInRange.put(player.getUniqueId(), false);
                                continue;
                            }
                        } else if (trigger != Trigger.OUTSIDE) {
                            continue;
                        }
                    }

                    if (checkConditions(playerID)) {
                        completeObjective(playerID);
                    }
                }
            });
        }, 0, 20);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(npcMoveTask);
        playersInRange.clear();
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        return "";
    }

    private enum Trigger {
        ENTER,
        LEAVE,
        INSIDE,
        OUTSIDE
    }
}
