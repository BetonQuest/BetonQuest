package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.protocollib.hider.MythicHider;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.utils.location.CompoundLocation;
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
public class MythicSpawnMobEvent extends QuestEvent {
    private final CompoundLocation loc;

    private final String mob;

    private final VariableNumber amount;

    private final VariableNumber level;

    private final boolean privateMob;

    private final boolean targetPlayer;

    @Nullable
    private final VariableString marked;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public MythicSpawnMobEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        loc = instruction.getLocation();
        final String[] mobParts = instruction.next().split(":");
        if (mobParts.length != 2) {
            throw new InstructionParseException("Wrong mob format");
        }
        mob = mobParts[0];
        level = instruction.getVarNum(mobParts[1]);
        amount = instruction.getVarNum();
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            privateMob = instruction.hasArgument("private");
        } else {
            privateMob = false;
        }
        targetPlayer = instruction.hasArgument("target");
        final String markedString = instruction.getOptional("marked");
        marked = markedString == null ? null : new VariableString(
                instruction.getPackage(),
                Utils.addPackage(instruction.getPackage(), markedString)
        );
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final int pAmount = amount.getInt(profile);
        final int level = this.level.getInt(profile);
        final Location location = loc.getLocation(profile);
        for (int i = 0; i < pAmount; i++) {
            try {
                final Entity entity = new BukkitAPIHelper().spawnMythicMob(mob, location, level);
                final ActiveMob targetMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);

                if (privateMob) {
                    final MythicHider mythicHider = MythicHider.getInstance();
                    if (mythicHider == null) {
                        throw new QuestRuntimeException("Can't hide MythicMob because the Hider is null!");
                    }
                    Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), () -> mythicHider.applyVisibilityPrivate(profile.getOnlineProfile().get(), entity), 20L);
                }
                if (targetPlayer) {
                    Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), () -> targetMob.setTarget(BukkitAdapter.adapt(player)), 20L);
                }
                if (marked != null) {
                    final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
                    entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, marked.getString(profile));
                }
            } catch (final InvalidMobTypeException e) {
                throw new QuestRuntimeException("MythicMob type " + mob + " is invalid.", e);
            }
        }
        return null;
    }
}
