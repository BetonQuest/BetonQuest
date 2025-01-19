package org.betonquest.betonquest.compatibility.protocollib.hider;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyMethods"})
public final class NPCHider extends BukkitRunnable implements Listener {
    @Nullable
    private static NPCHider instance;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final EntityHider hider;

    private final Map<Integer, Set<ConditionID>> npcs;

    private NPCHider(final BetonQuestLogger log) {
        super();
        this.log = log;
        npcs = new HashMap<>();
        final int updateInterval = BetonQuest.getInstance().getPluginConfig().getInt("npc_hider_check_interval", 5 * 20);
        hider = new EntityHider(BetonQuest.getInstance(), EntityHider.Policy.BLACKLIST);
        loadFromConfig();
        runTaskTimer(BetonQuest.getInstance(), 0, updateInterval);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Starts (or restarts) the NPCHider. It loads the current configuration for hidden NPCs
     *
     * @param log the logger that will be used for logging
     */
    public static void start(final BetonQuestLogger log) {
        if (instance != null) {
            instance.stop();
        }
        instance = new NPCHider(log);
    }

    /**
     * @return the currently used NPCHider instance
     */
    @Nullable
    public static NPCHider getInstance() {
        return instance;
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private void loadFromConfig() {

        for (final QuestPackage cfgPackage : Config.getPackages().values()) {
            final ConfigurationSection custom = cfgPackage.getConfig();
            final ConfigurationSection section = custom.getConfigurationSection("hide_npcs");
            if (section == null) {
                continue;
            }
            npcs:
            for (final String npcIds : section.getKeys(false)) {
                final int npcId;
                try {
                    npcId = Integer.parseInt(npcIds);
                } catch (final NumberFormatException e) {
                    log.warn(cfgPackage, "NPC ID '" + npcIds + "' is not a valid number, in hide_npcs", e);
                    continue;
                }
                final Set<ConditionID> conditions = new HashSet<>();
                final String conditionsString = section.getString(npcIds);

                for (final String condition : conditionsString.split(",")) {
                    try {
                        conditions.add(new ConditionID(cfgPackage, condition));
                    } catch (final ObjectNotFoundException e) {
                        log.warn(cfgPackage, "Condition '" + condition + "' does not exist, in hide_npcs with ID " + npcIds, e);
                        continue npcs;
                    }
                }

                if (npcs.containsKey(npcId)) {
                    npcs.get(npcId).addAll(conditions);
                } else {
                    npcs.put(npcId, conditions);
                }
            }
        }
    }

    @Override
    public void run() {
        applyVisibility();
    }

    /**
     * Stops the NPCHider, cleaning up all listeners, runnables etc.
     */
    public void stop() {
        hider.close();
        cancel();
        HandlerList.unregisterAll(this);
    }

    /**
     * Updates the visibility of the specified NPC for this player.
     *
     * @param onlineProfile the online profile of the player
     * @param npcID         ID of the NPC
     */
    public void applyVisibility(final OnlineProfile onlineProfile, final Integer npcID) {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);
        if (npc == null) {
            log.warn("NPCHider could not update visibility for npc " + npcID + ": No npc with this id found!");
            return;
        }
        if (npc.isSpawned()) {
            final Set<ConditionID> conditions = npcs.get(npcID);
            if (conditions == null || conditions.isEmpty() || !BetonQuest.conditions(onlineProfile, conditions)) {
                getEntityList(npc).forEach(entity -> hider.showEntity(onlineProfile, entity));
            } else {
                getEntityList(npc).forEach(entity -> hider.hideEntity(onlineProfile, entity));
            }
        }
    }

    private List<Entity> getEntityList(final NPC npc) {
        final List<Entity> entityList = new ArrayList<>();
        entityList.add(npc.getEntity());

        final HologramTrait hologramTrait = npc.getTraitNullable(HologramTrait.class);
        if (hologramTrait != null) {
            final Entity nameEntity = hologramTrait.getNameEntity();
            if (nameEntity != null) {
                entityList.add(nameEntity);
            }
            entityList.addAll(hologramTrait.getHologramEntities());
        }

        return entityList;
    }

    /**
     * Updates the visibility of all NPCs for this player.
     *
     * @param onlineProfile the online profile of the player
     */
    public void applyVisibility(final OnlineProfile onlineProfile) {
        for (final Integer npcID : npcs.keySet()) {
            applyVisibility(onlineProfile, npcID);
        }
    }

    /**
     * Updates the visibility of this NPC for all players.
     *
     * @param npcID ID of the NPC
     */
    public void applyVisibility(final NPC npcID) {
        //check if the npc is in the default registry
        if (!npcID.getOwningRegistry().equals(CitizensAPI.getNPCRegistry())) {
            return;
        }
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            applyVisibility(onlineProfile, npcID.getId());
        }
    }

    /**
     * Updates the visibility of all NPCs for all onlineProfiles.
     */
    public void applyVisibility() {
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            for (final Integer npcID : npcs.keySet()) {
                applyVisibility(onlineProfile, npcID);
            }
        }
    }

    /**
     * Checks whenever the NPC is visible to the player.
     *
     * @param onlineProfile the profile of the player
     * @param npc           ID of the NPC
     * @return true if the NPC is visible to that player, false otherwise
     */
    public boolean isInvisible(final OnlineProfile onlineProfile, final NPC npc) {
        return npc.getEntity() != null && !hider.isVisible(onlineProfile, npc.getEntity().getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCSpawn(final NPCSpawnEvent event) {
        applyVisibility(event.getNPC());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> applyVisibility(PlayerConverter.getID(event.getPlayer())));
    }
}
