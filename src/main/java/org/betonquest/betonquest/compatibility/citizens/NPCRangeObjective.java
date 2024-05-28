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
import java.util.function.BiPredicate;

@SuppressWarnings("PMD.CommentRequired")
public class NPCRangeObjective extends Objective {
    private final List<Integer> npcIds;

    private final VariableNumber radius;

    private final Map<UUID, Boolean> playersInRange;

    private final BiPredicate<UUID, Boolean> checkStuff;

    private int npcMoveTask;

    public NPCRangeObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
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
        final Trigger trigger = instruction.getEnum(Trigger.class);
        playersInRange = new HashMap<>();
        checkStuff = getStuff(trigger);
        radius = instruction.getVarNum();
    }

    private BiPredicate<UUID, Boolean> getStuff(final Trigger trigger) {
        return (uuid, inside) -> switch (trigger) {
            case INSIDE -> !inside;
            case OUTSIDE -> inside;
            case ENTER -> {
                if (playersInRange.containsKey(uuid)) {
                    if (playersInRange.get(uuid) || !inside) {
                        playersInRange.put(uuid, inside);
                        yield true;
                    }
                } else {
                    playersInRange.put(uuid, inside);
                    yield true;
                }
                yield false;
            }
            case LEAVE -> {
                if (playersInRange.containsKey(uuid)) {
                    if (!playersInRange.get(uuid) || inside) {
                        playersInRange.put(uuid, inside);
                        yield true;
                    }
                } else {
                    playersInRange.put(uuid, inside);
                    yield true;
                }
                yield false;
            }
        };
    }

    @Override
    public void start() {
        npcMoveTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(BetonQuest.getInstance(), () -> qreHandler.handle(this::loop), 0, 20);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(npcMoveTask);
        playersInRange.clear();
    }

    private void loop() throws QuestRuntimeException {
        final List<UUID> profilesInside = new ArrayList<>();
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
        if (checkStuff.test(uuid, inside)) {
            return;
        }

        if (checkConditions(profile)) {
            playersInRange.remove(uuid);
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
