package org.betonquest.betonquest.compatibility.mythicmobs.event;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Spawns MythicMobs mobs.
 */
public class MythicSpawnMobEvent implements OnlineEvent, PlayerlessEvent {

    /**
     * Plugin to start tasks.
     */
    private final Plugin plugin;

    /**
     * The location where the mob should be spawned.
     */
    private final Variable<Location> loc;

    /**
     * The name and level of the MythicMob to spawn.
     */
    private final Variable<Map.Entry<MythicMob, Double>> mobLevel;

    /**
     * The amount of mobs to spawn.
     */
    private final Variable<Number> amount;

    /**
     * Whether the mob should be private (invisible to other players).
     */
    @Nullable
    private final MythicHider mythicHider;

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
     * Key to mark mobs.
     */
    private final NamespacedKey markedKey;

    /**
     * Constructs a new MythicSpawnMobEvent.
     *
     * @param plugin       the plugin to start tasks
     * @param loc          the location where the mob should be spawned
     * @param mobLevel     the name and level of the MythicMob to spawn
     * @param amount       the amount of mobs to spawn
     * @param mythicHider  the mythic hider, if the mob should be private (invisible to other players), or null
     * @param targetPlayer whether the mob should target the player who triggered the event
     * @param marked       an optional variable containing a string to mark the mob with
     */
    public MythicSpawnMobEvent(final Plugin plugin, final Variable<Location> loc, final Variable<Map.Entry<MythicMob, Double>> mobLevel,
                               final Variable<Number> amount, @Nullable final MythicHider mythicHider, final boolean targetPlayer, @Nullable final Variable<String> marked) {
        this.plugin = plugin;
        this.loc = loc;
        this.mobLevel = mobLevel;
        this.amount = amount;
        this.mythicHider = mythicHider;
        this.targetPlayer = targetPlayer;
        this.marked = marked;
        markedKey = new NamespacedKey("betonquest", "betonquest-marked");
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final int pAmount = amount.getValue(profile).intValue();
        final Map.Entry<MythicMob, Double> mobLevelValue = mobLevel.getValue(profile);
        final MythicMob mob = mobLevelValue.getKey();
        final double level = mobLevelValue.getValue();
        final AbstractLocation abstractLocation = BukkitAdapter.adapt(loc.getValue(profile));
        final String mark = marked == null ? null : marked.getValue(null);
        for (int i = 0; i < pAmount; i++) {
            final ActiveMob targetMob = mob.spawn(abstractLocation, level);
            if (targetMob == null) {
                throw new QuestException("MythicMob '" + mob + "' could not spawn at '" + abstractLocation + "' !");
            }
            final Entity entity = targetMob.getEntity().getBukkitEntity();

            if (mythicHider != null) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> mythicHider.applyVisibilityPrivate(profile, entity), 20L);
            }
            if (targetPlayer) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> targetMob.setTarget(BukkitAdapter.adapt(player)), 20L);
            }
            if (mark != null) {
                entity.getPersistentDataContainer().set(markedKey, PersistentDataType.STRING, mark);
            }
        }
    }

    @Override
    public void execute() throws QuestException {
        final int pAmount = amount.getValue(null).intValue();
        final Map.Entry<MythicMob, Double> mobLevelValue = mobLevel.getValue(null);
        final MythicMob mob = mobLevelValue.getKey();
        final double level = mobLevelValue.getValue();
        final AbstractLocation abstractLocation = BukkitAdapter.adapt(loc.getValue(null));
        final String mark = marked == null ? null : marked.getValue(null);
        for (int i = 0; i < pAmount; i++) {
            final ActiveMob targetMob = mob.spawn(abstractLocation, level);
            final Entity entity = targetMob.getEntity().getBukkitEntity();
            if (mark != null) {
                entity.getPersistentDataContainer().set(markedKey, PersistentDataType.STRING, mark);
            }
        }
    }
}
