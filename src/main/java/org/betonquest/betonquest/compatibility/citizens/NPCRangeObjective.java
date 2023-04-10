package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        final ArrayList<UUID> profilesInside = new ArrayList<>();
        for (final int npcId : npcIds) {
            final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            if (npc == null) {
                throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
            }
            for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                if (!profilesInside.contains(onlineProfile.getProfileUUID()) && isInside(onlineProfile, npc.getStoredLocation())) {
                    profilesInside.add(onlineProfile.getProfileUUID());
                }
            }
        }
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            checkPlayer(onlineProfile.getProfileUUID(), onlineProfile, profilesInside.contains(onlineProfile.getProfileUUID()));
        }
    }

    private boolean isInside(final OnlineProfile onlineProfile, final Location location) throws QuestRuntimeException {
        if (!containsPlayer(onlineProfile) || !location.getWorld().equals(onlineProfile.getPlayer().getWorld())) {
            return false;
        }
        final double radius = this.radius.getDouble(onlineProfile);
        final double distanceSqrd = location.distanceSquared(onlineProfile.getPlayer().getLocation());
        final double radiusSqrd = radius * radius;

        return distanceSqrd <= radiusSqrd;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    private void checkPlayer(final UUID uuid, final Profile profile, final boolean inside) {
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

        if (checkConditions(profile)) {
            if (playersInRange != null) {
                playersInRange.remove(uuid);
            }
            completeObjective(profile);
        }
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }

    private enum Trigger {
        ENTER,
        LEAVE,
        INSIDE,
        OUTSIDE
    }
}
