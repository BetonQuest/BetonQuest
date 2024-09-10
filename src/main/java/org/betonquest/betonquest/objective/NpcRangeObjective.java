package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;

/**
 * The player has to reach certain radius around a specified Npc.
 */
public class NpcRangeObjective extends Objective {
    /**
     * Processor to get npc.
     */
    private final NpcProcessor npcProcessor;

    /**
     * Stores the relevant Npc Ids to get their locations.
     */
    private final List<NpcID> npcIds;

    /**
     * Maximal distance between player and NPC.
     */
    private final VariableNumber radius;

    /**
     * Stores the state of player to ensure correct completion based on the {@link Trigger}.
     */
    private final Map<UUID, Boolean> playersInRange;

    /**
     * Checks if the condition based on the {@link Trigger} is not met.
     */
    private final BiPredicate<UUID, Boolean> checkStuff;

    /**
     * BukkitTask ID to stop range check loop.
     */
    private int npcMoveTask;

    /**
     * Creates a new NPCRangeObjective from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @throws QuestException if the instruction is invalid
     */
    public NpcRangeObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        this.npcProcessor = BetonQuest.getInstance().getNpcProcessor();
        this.npcIds = instruction.getIDList(NpcID::new);
        this.playersInRange = new HashMap<>();
        this.checkStuff = getStuff(instruction.getEnum(Trigger.class));
        this.radius = instruction.get(VariableNumber::new);
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
        npcMoveTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(BetonQuest.getInstance(), () -> qeHandler.handle(this::loop), 0, 20);
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(npcMoveTask);
        playersInRange.clear();
    }

    private void loop() throws QuestException {
        final List<UUID> profilesInside = new ArrayList<>();
        final List<OnlineProfile> allOnlineProfiles = PlayerConverter.getOnlineProfiles();
        for (final NpcID npcId : npcIds) {
            final Location npcLocation = npcProcessor.getNpc(npcId).getLocation();
            for (final OnlineProfile onlineProfile : allOnlineProfiles) {
                if (!profilesInside.contains(onlineProfile.getProfileUUID()) && isInside(onlineProfile, npcLocation)) {
                    profilesInside.add(onlineProfile.getProfileUUID());
                }
            }
        }
        for (final OnlineProfile onlineProfile : allOnlineProfiles) {
            checkPlayer(onlineProfile.getProfileUUID(), onlineProfile, profilesInside.contains(onlineProfile.getProfileUUID()));
        }
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

    /**
     * The action that completes the objective.
     */
    private enum Trigger {
        /**
         * The player has to enter the range.
         * <p>
         * When the player is already inside the range he has to leave first.
         */
        ENTER,
        /**
         * The player has to leave the range.
         * <p>
         * If the player is already outside the range he has to enter first.
         */
        LEAVE,
        /**
         * The player has to be inside the range.
         */
        INSIDE,
        /**
         * The player has to be outside the range.
         */
        OUTSIDE
    }
}
