package org.betonquest.betonquest.compatibility.mythicmobs.event;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
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
public class MythicSpawnMobEvent implements OnlineAction, PlayerlessAction {

    /**
     * Plugin to start tasks.
     */
    private final Plugin plugin;

    /**
     * The location where the mob should be spawned.
     */
    private final Argument<Location> loc;

    /**
     * The name and level of the MythicMob to spawn.
     */
    private final Argument<Map.Entry<MythicMob, Double>> mobLevel;

    /**
     * The amount of mobs to spawn.
     */
    private final Argument<Number> amount;

    /**
     * Whether the mob should be private (invisible to other players).
     */
    private final FlagArgument<MythicHider> mythicHider;

    /**
     * Whether the mob should target the player who triggered the event.
     */
    private final FlagArgument<Boolean> targetPlayer;

    /**
     * The optional identifier for the marked mob.
     */
    @Nullable
    private final Argument<String> marked;

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
     * @param marked       the optional identifier for the marked mob
     */
    public MythicSpawnMobEvent(final Plugin plugin, final Argument<Location> loc, final Argument<Map.Entry<MythicMob, Double>> mobLevel,
                               final Argument<Number> amount, final FlagArgument<MythicHider> mythicHider,
                               final FlagArgument<Boolean> targetPlayer, @Nullable final Argument<String> marked) {
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
        final String mark = marked == null ? null : marked.getValue(profile);
        for (int i = 0; i < pAmount; i++) {
            final ActiveMob targetMob = mob.spawn(abstractLocation, level);
            if (targetMob == null) {
                throw new QuestException("MythicMob '" + mob + "' could not spawn at '" + abstractLocation + "' !");
            }
            final Entity entity = targetMob.getEntity().getBukkitEntity();

            this.mythicHider.getValue(profile).ifPresent(mythicHider ->
                    Bukkit.getScheduler().runTaskLater(plugin, () -> mythicHider.applyVisibilityPrivate(profile, entity), 20L));
            if (targetPlayer.getValue(profile).orElse(false)) {
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
            if (targetMob == null) {
                throw new QuestException("MythicMob '" + mob + "' could not spawn at '" + abstractLocation + "' !");
            }
            final Entity entity = targetMob.getEntity().getBukkitEntity();
            if (mark != null) {
                entity.getPersistentDataContainer().set(markedKey, PersistentDataType.STRING, mark);
            }
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
