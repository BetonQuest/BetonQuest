package org.betonquest.betonquest.compatibility.fancynpcs.objective;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;

/**
 * The player has to reach certain radius around a specified NPC.
 */
public class NPCRangeObjective extends Objective {
    /**
     * Stores the relevant NPC IDs.
     */
    private final List<String> npcIds;

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
     * @throws InstructionParseException if the instruction is invalid
     */
    public NPCRangeObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.npcIds = new ArrayList<>();
        npcIds.addAll(Arrays.asList(instruction.getArray()));
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
        for (final String npcId : npcIds) {
            final Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpcById(npcId);
            if (npc == null) {
                throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
            }
            for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                if (!profilesInside.contains(onlineProfile.getProfileUUID()) && isInside(onlineProfile, npc.getData().getLocation())) {
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
        final double radius = this.radius.getValue(onlineProfile).doubleValue();
        final double distanceSquared = location.distanceSquared(onlineProfile.getPlayer().getLocation());
        final double radiusSquared = radius * radius;

        return distanceSquared <= radiusSquared;
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
