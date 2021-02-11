package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
        playersInRange = trigger == Trigger.ENTER || trigger == Trigger.LEAVE ? new HashMap<>() : null;
    }

    @Override
    public void start() {
        npcMoveTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(BetonQuest.getInstance(), () -> qreHandler.handle(this::loop), 0, 20);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(npcMoveTask);
        if (playersInRange != null) {
            playersInRange.clear();
        }
    }

    private void loop() throws QuestRuntimeException {
        final ArrayList<UUID> playersInside = new ArrayList<>();
        for (final int npcId : npcIds) {
            final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            if (npc == null) {
                throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
            }
            final Entity npcEntity = npc.getEntity();
            if (npcEntity == null) {
                return;
            }
            for (final Player player : Bukkit.getOnlinePlayers()) {
                if (!playersInside.contains(player.getUniqueId()) && isInside(player, npcEntity.getLocation())) {
                    playersInside.add(player.getUniqueId());
                }
            }
        }
        for (final Player player : Bukkit.getOnlinePlayers()) {
            checkPlayer(player.getUniqueId(), PlayerConverter.getID(player), playersInside.contains(player.getUniqueId()));
        }
    }

    private boolean isInside(final Player player, final Location location) throws QuestRuntimeException {
        final String playerID = PlayerConverter.getID(player);
        if (!containsPlayer(playerID) || location.getWorld() != player.getWorld()) {
            return false;
        }
        final double radius = this.radius.getDouble(playerID);
        final double distanceSqrd = location.distanceSquared(player.getLocation());
        final double radiusSqrd = radius * radius;

        return distanceSqrd <= radiusSqrd;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void checkPlayer(final UUID uuid, final String playerID, final boolean inside) {
        if (trigger == Trigger.INSIDE && !inside || trigger == Trigger.OUTSIDE && inside) {
            return;
        } else if (trigger == Trigger.ENTER || trigger == Trigger.LEAVE) {
            if (playersInRange.containsKey(uuid)) {
                if (trigger == Trigger.ENTER && (playersInRange.get(uuid) || !inside)
                        || trigger == Trigger.LEAVE && (!playersInRange.get(uuid) || inside)) {
                    playersInRange.put(uuid, inside);
                    return;
                }
            } else {
                playersInRange.put(uuid, inside);
                return;
            }
        }

        if (checkConditions(playerID)) {
            if (playersInRange != null) {
                playersInRange.remove(uuid);
            }
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
