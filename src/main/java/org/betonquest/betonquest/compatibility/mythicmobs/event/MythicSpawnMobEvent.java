package org.betonquest.betonquest.compatibility.mythicmobs.event;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.compatibility.protocollib.hider.MythicHider;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/**
 * Spawns MythicMobs mobs.
 */
public class MythicSpawnMobEvent implements PlayerEvent, PlayerlessEvent {
    /**
     * The BukkitAPIHelper used to interact with MythicMobs.
     */
    private final BukkitAPIHelper apiHelper;

    /**
     * The location where the mob should be spawned.
     */
    private final Variable<Location> loc;

    /**
     * The name of the MythicMob to spawn.
     */
    private final String mob;

    /**
     * The level of the mob to spawn.
     */
    private final Variable<Number> level;

    /**
     * The amount of mobs to spawn.
     */
    private final Variable<Number> amount;

    /**
     * Whether the mob should be private (invisible to other players).
     */
    private final boolean privateMob;

    /**
     * Whether the mob should target the player who triggered the event.
     */
    private final boolean targetPlayer;

    /**
     * An optional variable containing a string to mark the mob with.
     */
    @Nullable
    private final Variable<String> marked;

    /**
     * Constructs a new MythicSpawnMobEvent.
     *
     * @param apiHelper    the BukkitAPIHelper to use for spawning mobs
     * @param loc          the location where the mob should be spawned
     * @param mob          the name of the MythicMob to spawn
     * @param level        the level of the mob to spawn
     * @param amount       the amount of mobs to spawn
     * @param privateMob   whether the mob should be private (invisible to other players)
     * @param targetPlayer whether the mob should target the player who triggered the event
     * @param marked       an optional variable containing a string to mark the mob with
     */
    public MythicSpawnMobEvent(final BukkitAPIHelper apiHelper, final Variable<Location> loc, final String mob, final Variable<Number> level,
                               final Variable<Number> amount, final boolean privateMob, final boolean targetPlayer, @Nullable final Variable<String> marked) {
        this.apiHelper = apiHelper;
        this.loc = loc;
        this.mob = mob;
        this.level = level;
        this.amount = amount;
        this.privateMob = privateMob;
        this.targetPlayer = targetPlayer;
        this.marked = marked;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final int pAmount = amount.getValue(profile).intValue();
        final int level = this.level.getValue(profile).intValue();
        final Location location = loc.getValue(profile);
        for (int i = 0; i < pAmount; i++) {
            try {
                final Entity entity = apiHelper.spawnMythicMob(mob, location, level);
                final ActiveMob targetMob = apiHelper.getMythicMobInstance(entity);

                if (privateMob) {
                    final MythicHider mythicHider = MythicHider.getInstance();
                    if (mythicHider == null) {
                        throw new QuestException("Can't hide MythicMob because the Hider is null!");
                    }
                    Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), () -> mythicHider.applyVisibilityPrivate(profile.getOnlineProfile().get(), entity), 20L);
                }
                if (targetPlayer) {
                    Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), () -> targetMob.setTarget(BukkitAdapter.adapt(player)), 20L);
                }
                if (marked != null) {
                    final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
                    entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, marked.getValue(profile));
                }
            } catch (final InvalidMobTypeException e) {
                throw new QuestException("MythicMob type " + mob + " is invalid.", e);
            }
        }
    }

    @Override
    public void execute() throws QuestException {
        final int pAmount = amount.getValue(null).intValue();
        final int level = this.level.getValue(null).intValue();
        final Location location = loc.getValue(null);
        for (int i = 0; i < pAmount; i++) {
            try {
                final Entity entity = apiHelper.spawnMythicMob(mob, location, level);
                if (marked != null) {
                    final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
                    entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, marked.getValue(null));
                }
            } catch (final InvalidMobTypeException e) {
                throw new QuestException("MythicMob type " + mob + " is invalid.", e);
            }
        }
    }
}
