package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

/**
 * Player has to kill specified amount of specified mobs. It can also require
 * the player to kill specifically named mobs and notify them about the required
 * amount.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class MobKillObjective extends CountingObjective implements Listener {

    private final List<EntityType> entities;
    protected String name;
    protected String marked;

    public MobKillObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "mobs_to_kill");
        entities = instruction.getList(mob -> instruction.getEnum(mob, EntityType.class));
        targetAmount = instruction.getPositive();
        name = instruction.getOptional("name");
        if (name != null) {
            name = Utils.format(name, true, false).replace('_', ' ');
        }
        marked = instruction.getOptional("marked");
        if (marked != null) {
            marked = Utils.addPackage(instruction.getPackage(), marked);
        }
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onMobKill(final MobKilledEvent event) {
        final OnlineProfile onlineProfile = event.getProfile().getOnlineProfile().get();
        if (!containsPlayer(onlineProfile)
                || !entities.contains(event.getEntity().getType())
                || name != null && (event.getEntity().getCustomName() == null
                || !event.getEntity().getCustomName().equals(name))) {
            return;
        }
        if (marked != null) {
            if (!event.getEntity().hasMetadata("betonquest-marked")) {
                return;
            }
            final List<MetadataValue> meta = event.getEntity().getMetadata("betonquest-marked");
            for (final MetadataValue m : meta) {
                if (!m.asString().equals(marked.replace("%player%", event.getProfile().getProfileUUID().toString()))) {
                    return;
                }
            }
        }

        if (checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
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
