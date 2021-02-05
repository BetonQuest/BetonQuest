package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

@SuppressWarnings("PMD.CommentRequired")
public class NPCRangeObjective extends Objective {

    private final List<Integer> npcIds;
    private final Trigger trigger;
    private final VariableNumber radius;
    private final Map<UUID, Boolean> playersInRange;
    private int npcMoveTask;

    public NPCRangeObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        super.template = ObjectiveData.class;
        this.npcIds = new ArrayList<>();
        for (final String npcIdString : instruction.getArray()) {
            try {
                final int npcId = Integer.parseInt(npcIdString);
                if (npcId < 0) {
                    throw new InstructionParseException("NPC ID cannot be less than 0");
                }
                npcIds.add(npcId);
            } catch (final NumberFormatException exception) {
                throw new InstructionParseException("NPC ID cannot be parsed to a Number", exception);
            }
        }
        trigger = instruction.getEnum(Trigger.class);
        radius = instruction.getVarNum();
        playersInRange = new HashMap<>();
    }

    @Override
    public void start() {
        npcMoveTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(BetonQuest.getInstance(), () -> qreHandler.handle(this::loopNPCs), 0, 20);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(npcMoveTask);
        playersInRange.clear();
    }

    private void loopNPCs() throws QuestRuntimeException {
        for (final int npcId : npcIds) {
            final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            if (npc == null) {
                throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
            }
            final Entity npcEntity = npc.getEntity();
            if (npcEntity == null) {
                return;
            }
            loopPlayers(npcEntity);
        }
    }

    private void loopPlayers(final Entity npcEntity) throws QuestRuntimeException {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final String playerID = PlayerConverter.getID(player);
            if (!containsPlayer(playerID)) {
                continue;
            }
            if (npcEntity.getWorld() != player.getWorld()) {
                continue;
            }
            final double radius = this.radius.getDouble(playerID);
            final double distanceSqrd = npcEntity.getLocation().distanceSquared(player.getLocation());
            final double radiusSqrd = radius * radius;

            checkPlayer(player, playerID, distanceSqrd <= radiusSqrd);
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void checkPlayer(final Player player, final String playerID, final boolean inside) {
        if (inside) {
            if (trigger == Trigger.ENTER || trigger == Trigger.LEAVE) {
                if (playersInRange.containsKey(player.getUniqueId()) && trigger == Trigger.ENTER) {
                    if (playersInRange.get(player.getUniqueId())) {
                        return;
                    }
                } else {
                    playersInRange.put(player.getUniqueId(), true);
                    return;
                }
            } else if (trigger != Trigger.INSIDE) {
                return;
            }
        } else {
            if (trigger == Trigger.LEAVE || trigger == Trigger.ENTER) {
                if (playersInRange.containsKey(player.getUniqueId()) && trigger == Trigger.LEAVE) {
                    if (!playersInRange.get(player.getUniqueId())) {
                        return;
                    }
                } else {
                    playersInRange.put(player.getUniqueId(), false);
                    return;
                }
            } else if (trigger != Trigger.OUTSIDE) {
                return;
            }
        }

        if (checkConditions(playerID)) {
            completeObjective(playerID);
        }
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
