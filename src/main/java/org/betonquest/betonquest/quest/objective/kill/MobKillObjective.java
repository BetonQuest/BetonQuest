package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/**
 * Player has to kill specified amount of specified mobs. It can also require
 * the player to kill specifically named mobs and notify them about the required
 * amount.
 */
public class MobKillObjective extends CountingObjective implements Listener {

    /**
     * The entity types that should be killed.
     */
    private final VariableList<EntityType> entities;

    /**
     * The optional name of the mob.
     */
    @Nullable
    protected String name;

    /**
     * The optional marker for the mobs to identify them.
     */
    @Nullable
    protected VariableIdentifier marked;

    /**
     * Constructor for the MobKillObjective.
     *
     * @param instruction  the instruction that created this objective
     * @param targetAmount the amount of mobs to kill
     * @param entities     the entity types that should be killed
     * @param name         the optional name of the mob
     * @param marked       the optional marker for the mobs to identify them
     * @throws QuestException if there is an error in the instruction
     */
    public MobKillObjective(final Instruction instruction, final Variable<Number> targetAmount,
                            final VariableList<EntityType> entities, @Nullable final String name,
                            @Nullable final VariableIdentifier marked) throws QuestException {
        super(instruction, targetAmount, "mobs_to_kill");
        this.entities = entities;
        this.name = name;
        this.marked = marked;
    }

    /**
     * Check if the player has killed the specified mob.
     *
     * @param event the event containing the mob kill information
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @EventHandler(ignoreCancelled = true)
    public void onMobKill(final MobKilledEvent event) {
        final OnlineProfile onlineProfile = event.getProfile().getOnlineProfile().get();
        try {
            if (!containsPlayer(onlineProfile)
                    || !entities.getValue(onlineProfile).contains(event.getEntity().getType())
                    || name != null && (event.getEntity().getCustomName() == null
                    || !event.getEntity().getCustomName().equals(name))) {
                return;
            }
        } catch (final QuestException e) {
            qeHandler.handle(() -> {
                throw new QuestException("Failed to resolve entities for kill objective: " + e.getMessage(), e);
            });
            return;
        }
        if (marked != null) {
            try {
                final String value = marked.getValue(onlineProfile);
                final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
                final String dataContainerValue = event.getEntity().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (dataContainerValue == null || !dataContainerValue.equals(value)) {
                    return;
                }
            } catch (final QuestException ignored) {
                // Empty
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
