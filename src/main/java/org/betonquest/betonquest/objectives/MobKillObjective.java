package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Player has to kill specified amount of specified mobs. It can also require
 * the player to kill specifically named mobs and notify them about the required
 * amount.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MobKillObjective extends CountingObjective implements Listener {
    private final List<EntityType> entities;

    @Nullable
    protected String name;

    @Nullable
    protected VariableString marked;

    public MobKillObjective(final Instruction instruction) throws QuestException {
        super(instruction, "mobs_to_kill");
        entities = instruction.getList(mob -> instruction.getEnum(mob, EntityType.class));
        targetAmount = instruction.getVarNum(VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        name = instruction.getOptional("name");
        if (name != null) {
            name = Utils.format(name, true, false).replace('_', ' ');
        }
        final String markedString = instruction.getOptional("marked");
        marked = markedString == null ? null : new VariableString(
                instruction.getPackage(),
                Utils.addPackage(instruction.getPackage(), markedString)
        );
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
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
            final String value = marked.getString(onlineProfile);
            final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
            final String dataContainerValue = event.getEntity().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (dataContainerValue == null || !dataContainerValue.equals(value)) {
                return;
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
