package org.betonquest.betonquest.compatibility.mythicmobs;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Player has to kill MythicMobs monster.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MythicMobKillObjective extends CountingObjective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(MythicMobKillObjective.class);

    /**
     * The names of all mobs that this objective should count.
     */
    private final Set<String> names = new HashSet<>();
    /**
     * The minimal level the killed mob must have to count.
     */
    private final VariableNumber minMobLevel;
    /**
     * The maximal level the killed mob must have to count.
     */
    private final VariableNumber maxMobLevel;
    /**
     * The radius in which any of the specified mobs dying will progress the objective for players.
     */
    private final double deathRadiusAllPlayers;
    /**
     * The radius in which any of the specified mobs dying without a killer will progress the objective for players.
     */
    private final double neutralDeathRadiusAllPlayers;
    /**
     * The text with which the mob must have been marked to count.
     */
    protected String marked;

    /**
     * Creates a new MythicMobKillObjective.
     *
     * @param instruction the user-provided instruction
     * @throws InstructionParseException if the instruction is invalid
     */
    public MythicMobKillObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "mobs_to_kill");

        Collections.addAll(names, instruction.getArray());
        targetAmount = instruction.getInt(instruction.getOptional("amount"), 1);

        final double deathRadiusAllPlayersTemp = instruction.getDouble(instruction.getOptional("deathRadiusAllPlayers"), 0);
        deathRadiusAllPlayers = Math.pow(deathRadiusAllPlayersTemp, 2);
        final double neutralDeathRadiusAllPlayersTemp = instruction.getDouble(instruction.getOptional("neutralDeathRadiusAllPlayers"), 0);
        neutralDeathRadiusAllPlayers = Math.pow(neutralDeathRadiusAllPlayersTemp, 2);

        final String unsafeMinMobLevel = instruction.getOptional("minLevel");
        final String unsafeMaxMobLevel = instruction.getOptional("maxLevel");
        final QuestPackage pack = instruction.getPackage();

        minMobLevel = unsafeMinMobLevel == null ? new VariableNumber(Double.NEGATIVE_INFINITY) : new VariableNumber(pack, unsafeMinMobLevel);
        maxMobLevel = unsafeMaxMobLevel == null ? new VariableNumber(Double.POSITIVE_INFINITY) : new VariableNumber(pack, unsafeMaxMobLevel);
        marked = instruction.getOptional("marked");
        if (marked != null) {
            marked = Utils.addPackage(instruction.getPackage(), marked);
        }
    }

    /**
     * Registers a listener for the MythicMobDeathEvent and handles all incoming ones.
     *
     * @param event the MythicMobDeathEvent
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @EventHandler(ignoreCancelled = true)
    public void onBossKill(final MythicMobDeathEvent event) {
        if (!names.contains(event.getMobType().getInternalName())
                || marked != null && !event.getEntity().hasMetadata("betonquest-marked")) {
            return;
        }
        if (deathRadiusAllPlayers > 0) {
            executeForEveryoneInRange(event, deathRadiusAllPlayers);
        } else if (event.getKiller() instanceof Player) {
            checkKill(event, PlayerConverter.getID((Player) event.getKiller()));
        } else if (neutralDeathRadiusAllPlayers > 0) {
            executeForEveryoneInRange(event, neutralDeathRadiusAllPlayers);
        }
    }

    private void executeForEveryoneInRange(final MythicMobDeathEvent event, final double range) {
        final Location center = BukkitAdapter.adapt(event.getMob().getLocation());
        for (final Player player : center.getWorld().getPlayers()) {
            if (isValidPlayer(player) && player.getLocation().distanceSquared(center) <= range) {
                checkKill(event, PlayerConverter.getID(player));
            }
        }
    }

    private void checkKill(final MythicMobDeathEvent event, final OnlineProfile onlineProfile) {
        if (marked != null) {
            final List<MetadataValue> meta = event.getEntity().getMetadata("betonquest-marked");
            for (final MetadataValue m : meta) {
                if (!m.asString().equals(marked.replace("%player%", onlineProfile.getProfileUUID().toString()))) {
                    return;
                }
            }
        }
        handlePlayerKill(onlineProfile, event.getMob());
    }

    private boolean isValidPlayer(final Player player) {
        return player != null
                && player.isOnline()
                && player.isValid();
    }

    private void handlePlayerKill(final OnlineProfile onlineProfile, final ActiveMob mob) {
        if (containsPlayer(onlineProfile) && matchesMobLevel(onlineProfile, mob) && checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
        }

    }

    private boolean matchesMobLevel(final OnlineProfile onlineProfile, final ActiveMob mob) {
        try {
            final double actualMobLevel = mob.getLevel();
            return minMobLevel.getDouble(onlineProfile) <= actualMobLevel && maxMobLevel.getDouble(onlineProfile) >= actualMobLevel;
        } catch (final QuestRuntimeException exception) {
            try {
                LOG.error(instruction.getPackage(), "Unable to resolve minMobLevel / maxMobLevel variable in " + instruction.getObjective().getFullID());
            } catch (final InstructionParseException e) {
                LOG.reportException(instruction.getPackage(), exception);
            }
            return false;
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }
}
