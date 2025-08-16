package org.betonquest.betonquest.quest.objective.npc;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.common.function.QuestBiPredicate;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestListException;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The player has to reach certain radius around a specified Npc.
 */
public class NpcRangeObjective extends Objective {

    /**
     * Stores the relevant Npc Ids to get their locations.
     */
    private final Variable<List<NpcID>> npcIds;

    /**
     * Maximal distance between player and NPC.
     */
    private final Variable<Number> radius;

    /**
     * Checks if the condition based on the {@link Trigger} is not met.
     */
    private final QuestBiPredicate<Profile, Boolean> checkStuff;

    /**
     * Stores the state of player to ensure correct completion based on the {@link Trigger}.
     */
    private final Map<UUID, Boolean> playersInRange;

    /**
     * BukkitTask ID to stop range check loop.
     */
    private int npcMoveTask;

    /**
     * Creates a new NPCRangeObjective from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @param npcIds      the list of Npc IDs to check
     * @param radius      the radius around the Npc
     * @param trigger     the trigger type for the objective
     * @throws QuestException if the instruction is invalid
     */
    public NpcRangeObjective(final Instruction instruction, final Variable<List<NpcID>> npcIds, final Variable<Number> radius,
                             final Variable<Trigger> trigger) throws QuestException {
        super(instruction);
        this.npcIds = npcIds;
        this.radius = radius;
        this.checkStuff = getStuff(trigger);
        this.playersInRange = new HashMap<>();
    }

    private QuestBiPredicate<Profile, Boolean> getStuff(final Variable<Trigger> trigger) {
        return (profile, inside) -> {
            final UUID uuid = profile.getPlayerUUID();
            return switch (trigger.getValue(profile)) {
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
        };
    }

    @Override
    public void start() {
        npcMoveTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(BetonQuest.getInstance(), () -> qeHandler.handle(this::loop), 0, 20);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(npcMoveTask);
        playersInRange.clear();
    }

    private void loop() throws QuestException {
        final List<UUID> profilesInside = new ArrayList<>();
        final List<OnlineProfile> allOnlineProfiles = profileProvider.getOnlineProfiles();
        final QuestListException questListException = new QuestListException("Could not loop all online profiles:");
        for (final OnlineProfile onlineProfile : allOnlineProfiles) {
            try {
                for (final NpcID npcId : npcIds.getValue(onlineProfile)) {
                    final Location npcLocation = BetonQuest.getInstance().getFeatureApi().getNpc(npcId, onlineProfile).getLocation();
                    if (!profilesInside.contains(onlineProfile.getProfileUUID()) && isInside(onlineProfile, npcLocation)) {
                        profilesInside.add(onlineProfile.getProfileUUID());
                    }
                }
            } catch (final QuestException e) {
                questListException.addException(onlineProfile.toString(), e);
            }
        }
        for (final OnlineProfile onlineProfile : allOnlineProfiles) {
            checkPlayer(onlineProfile, profilesInside.contains(onlineProfile.getProfileUUID()));
        }
        questListException.throwIfNotEmpty();
    }

    private boolean isInside(final OnlineProfile onlineProfile, final Location location) throws QuestException {
        if (!containsPlayer(onlineProfile) || !location.getWorld().equals(onlineProfile.getPlayer().getWorld())) {
            return false;
        }
        final double radius = this.radius.getValue(onlineProfile).doubleValue();
        final double distanceSquared = location.distanceSquared(onlineProfile.getPlayer().getLocation());
        final double radiusSquared = radius * radius;

        return distanceSquared <= radiusSquared;
    }

    private void checkPlayer(final Profile profile, final boolean inside) throws QuestException {
        if (checkStuff.test(profile, inside)) {
            return;
        }

        if (checkConditions(profile)) {
            playersInRange.remove(profile.getPlayerUUID());
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
}
