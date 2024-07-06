package org.betonquest.betonquest.compatibility.npcs.abstractnpc.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCSupplierStandard;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCUtil;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * The player has to reach certain radius around a specified NPC.
 */
public abstract class NPCRangeObjective extends Objective {
    /**
     * Stores the relevant NPC ID and their supplier get their location.
     */
    private final Map<String, Supplier<BQNPCAdapter<?>>> npcIds;

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
     * @param instruction      the user-provided instruction
     * @param supplierStandard the supplier providing the npc adapter supplier
     * @throws InstructionParseException if the instruction is invalid
     */
    public NPCRangeObjective(final Instruction instruction, final NPCSupplierStandard supplierStandard) throws InstructionParseException {
        super(instruction);
        final String[] rawIds = instruction.getArray();
        this.npcIds = new HashMap<>(rawIds.length);
        for (final String rawId : rawIds) {
            npcIds.put(rawId, supplierStandard.getSupplierByID(rawId));
        }
        final Trigger trigger = instruction.getEnum(NPCRangeObjective.Trigger.class);
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
        final List<OnlineProfile> allOnlineProfiles = PlayerConverter.getOnlineProfiles();
        for (final Map.Entry<String, Supplier<BQNPCAdapter<?>>> npcId : npcIds.entrySet()) {
            final Location npcLocation = NPCUtil.getNPC(npcId.getValue(), npcId.getKey()).getLocation();
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

    private boolean isInside(final OnlineProfile onlineProfile, final Location location) throws QuestRuntimeException {
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
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
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
         * The player ha to be outside the range.
         */
        OUTSIDE
    }
}
