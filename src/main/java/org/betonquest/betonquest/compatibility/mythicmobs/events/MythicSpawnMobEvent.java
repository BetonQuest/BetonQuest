package org.betonquest.betonquest.compatibility.mythicmobs.events;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.compatibility.protocollib.hider.MythicHider;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/**
 * Spawns MythicMobs mobs
 */
@SuppressWarnings("PMD.CommentRequired")
public class MythicSpawnMobEvent implements Event, StaticEvent {
    private final BukkitAPIHelper apiHelper;

    private final VariableLocation loc;

    private final String mob;

    private final VariableNumber amount;

    private final VariableNumber level;

    private final boolean privateMob;

    private final boolean targetPlayer;

    @Nullable
    private final VariableString marked;

    public MythicSpawnMobEvent(final BukkitAPIHelper apiHelper, final VariableLocation loc, final String mob, final VariableNumber level,
                               final VariableNumber amount, final boolean privateMob, final boolean targetPlayer, @Nullable final VariableString marked) {
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
