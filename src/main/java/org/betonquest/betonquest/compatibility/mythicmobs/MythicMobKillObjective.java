package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Player has to kill MythicMobs monster.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MythicMobKillObjective extends CountingObjective implements Listener {
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
    @Nullable
    protected VariableString marked;

    /**
     * Creates a new MythicMobKillObjective.
     *
     * @param instruction the user-provided instruction
     * @throws QuestException if the instruction is invalid
     */
    public MythicMobKillObjective(final Instruction instruction) throws QuestException {
        super(instruction, "mobs_to_kill");

        Collections.addAll(names, instruction.getArray());
        targetAmount = instruction.getVarNum(instruction.getOptional("amount", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);

        final double deathRadiusAllPlayersTemp = instruction.getDouble(instruction.getOptional("deathRadiusAllPlayers"), 0);
        deathRadiusAllPlayers = Math.pow(deathRadiusAllPlayersTemp, 2);
        final double neutralDeathRadiusAllPlayersTemp = instruction.getDouble(instruction.getOptional("neutralDeathRadiusAllPlayers"), 0);
        neutralDeathRadiusAllPlayers = Math.pow(neutralDeathRadiusAllPlayersTemp, 2);

        final String unsafeMinMobLevel = instruction.getOptional("minLevel");
        final String unsafeMaxMobLevel = instruction.getOptional("maxLevel");

        minMobLevel = instruction.getVarNum(unsafeMinMobLevel == null ? String.valueOf(Double.NEGATIVE_INFINITY) : unsafeMinMobLevel);
        maxMobLevel = instruction.getVarNum(unsafeMaxMobLevel == null ? String.valueOf(Double.POSITIVE_INFINITY) : unsafeMaxMobLevel);
        final String markedString = instruction.getOptional("marked");
        marked = markedString == null ? null : new VariableString(
                instruction.getPackage(),
                Utils.addPackage(instruction.getPackage(), markedString)
        );
    }

    /**
     * Registers a listener for the MythicMobDeathEvent and handles all incoming ones.
     *
     * @param event the MythicMobDeathEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onBossKill(final MythicMobDeathEvent event) {
        final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
        if (!names.contains(event.getMobType().getInternalName())
                || marked != null && !event.getEntity().getPersistentDataContainer().has(key)) {
            return;
        }
        if (deathRadiusAllPlayers > 0) {
            executeForEveryoneInRange(event, deathRadiusAllPlayers, key);
        } else if (event.getKiller() instanceof Player) {
            checkKill(event, PlayerConverter.getID((Player) event.getKiller()), key);
        } else if (neutralDeathRadiusAllPlayers > 0) {
            executeForEveryoneInRange(event, neutralDeathRadiusAllPlayers, key);
        }
    }

    private void executeForEveryoneInRange(final MythicMobDeathEvent event, final double range, final NamespacedKey key) {
        final Location center = BukkitAdapter.adapt(event.getMob().getLocation());
        for (final Player player : center.getWorld().getPlayers()) {
            if (isValidPlayer(player) && player.getLocation().distanceSquared(center) <= range) {
                checkKill(event, PlayerConverter.getID(player), key);
            }
        }
    }

    private void checkKill(final MythicMobDeathEvent event, final OnlineProfile onlineProfile, final NamespacedKey key) {
        if (marked != null) {
            final String value = marked.getString(onlineProfile);
            final String dataContainerValue = event.getEntity().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (dataContainerValue == null || !dataContainerValue.equals(value)) {
                return;
            }
        }
        handlePlayerKill(onlineProfile, event.getMob());
    }

    private boolean isValidPlayer(@Nullable final Player player) {
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
        final double actualMobLevel = mob.getLevel();
        return minMobLevel.getDouble(onlineProfile) <= actualMobLevel && maxMobLevel.getDouble(onlineProfile) >= actualMobLevel;
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
