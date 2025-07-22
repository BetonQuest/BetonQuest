package org.betonquest.betonquest.compatibility.mythicmobs.objective;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Player has to kill MythicMobs monster.
 */
public class MythicMobKillObjective extends CountingObjective implements Listener {
    /**
     * The marked key.
     */
    private final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");

    /**
     * The names of all mobs that this objective should count.
     */
    private final Variable<List<String>> names;

    /**
     * The minimal level the killed mob must have to count.
     */
    private final Variable<Number> minMobLevel;

    /**
     * The maximal level the killed mob must have to count.
     */
    private final Variable<Number> maxMobLevel;

    /**
     * The radius in which any of the specified mobs dying will progress the objective for players.
     */
    private final Variable<Number> deathRadiusAllPlayers;

    /**
     * The radius in which any of the specified mobs dying without a killer will progress the objective for players.
     */
    private final Variable<Number> neutralDeathRadiusAllPlayers;

    /**
     * The text with which the mob must have been marked to count.
     */
    @Nullable
    private final Variable<String> marked;

    /**
     * Creates a new MythicMobKillObjective.
     *
     * @param instruction                  the user-provided instruction
     * @param targetAmount                 the target amount of kills
     * @param names                        the names of all mobs that this objective should count
     * @param minMobLevel                  the minimal level the mob must have to count
     * @param maxMobLevel                  the maximal level the mob must have to count
     * @param deathRadiusAllPlayers        the radius of a death mob to count for all players
     * @param neutralDeathRadiusAllPlayers the radius of a death mob to count for all players if the killer is not a player
     * @param marked                       the text with which the mob must have been marked to count
     * @throws QuestException if the instruction is invalid
     */
    public MythicMobKillObjective(
            final Instruction instruction, final Variable<Number> targetAmount, final Variable<List<String>> names,
            final Variable<Number> minMobLevel, final Variable<Number> maxMobLevel,
            final Variable<Number> deathRadiusAllPlayers, final Variable<Number> neutralDeathRadiusAllPlayers,
            @Nullable final Variable<String> marked) throws QuestException {
        super(instruction, targetAmount, "mobs_to_kill");
        this.names = names;
        this.minMobLevel = minMobLevel;
        this.maxMobLevel = maxMobLevel;
        this.deathRadiusAllPlayers = deathRadiusAllPlayers;
        this.neutralDeathRadiusAllPlayers = neutralDeathRadiusAllPlayers;
        this.marked = marked;
    }

    /**
     * Registers a listener for the MythicMobDeathEvent and handles all incoming ones.
     *
     * @param event the MythicMobDeathEvent
     * @throws QuestException if a variable could not be resolved
     */
    @EventHandler(ignoreCancelled = true)
    public void onBossKill(final MythicMobDeathEvent event) throws QuestException {
        if (!names.getValue(null).contains(event.getMobType().getInternalName())
                || marked != null && !event.getEntity().getPersistentDataContainer().has(key)) {
            return;
        }
        final double deathRadius = deathRadiusAllPlayers.getValue(null).doubleValue();
        final double neutralDeathRadius = neutralDeathRadiusAllPlayers.getValue(null).doubleValue();
        final double deathRadiusAllPlayers = deathRadius * deathRadius;
        final double neutralDeathRadiusAllPlayers = neutralDeathRadius * neutralDeathRadius;
        if (deathRadiusAllPlayers > 0) {
            executeForEveryoneInRange(event, deathRadiusAllPlayers, key);
        } else if (event.getKiller() instanceof Player) {
            checkKill(event, profileProvider.getProfile((Player) event.getKiller()), key);
        } else if (neutralDeathRadiusAllPlayers > 0) {
            executeForEveryoneInRange(event, neutralDeathRadiusAllPlayers, key);
        }
    }

    private void executeForEveryoneInRange(final MythicMobDeathEvent event, final double range, final NamespacedKey key) throws QuestException {
        final Location center = BukkitAdapter.adapt(event.getMob().getLocation());
        for (final Player player : center.getWorld().getPlayers()) {
            if (isValidPlayer(player) && player.getLocation().distanceSquared(center) <= range) {
                checkKill(event, profileProvider.getProfile(player), key);
            }
        }
    }

    private void checkKill(final MythicMobDeathEvent event, final OnlineProfile onlineProfile, final NamespacedKey key) throws QuestException {
        if (marked != null) {
            final String value = marked.getValue(onlineProfile);
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

    private void handlePlayerKill(final OnlineProfile onlineProfile, final ActiveMob mob) throws QuestException {
        if (containsPlayer(onlineProfile) && matchesMobLevel(onlineProfile, mob) && checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
        }
    }

    private boolean matchesMobLevel(final OnlineProfile onlineProfile, final ActiveMob mob) throws QuestException {
        final double actualMobLevel = mob.getLevel();
        return minMobLevel.getValue(onlineProfile).doubleValue() <= actualMobLevel
                && maxMobLevel.getValue(onlineProfile).doubleValue() >= actualMobLevel;
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
