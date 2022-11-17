package org.betonquest.betonquest.compatibility.protocollib.hider;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public final class NPCHider extends BukkitRunnable implements Listener {

    private static NPCHider instance;

    private final EntityHider hider;
    private final Map<Integer, Set<ConditionID>> npcs;

    private NPCHider() {
        super();
        npcs = new HashMap<>();
        final int updateInterval = BetonQuest.getInstance().getPluginConfig().getInt("npc_hider_check_interval", 5 * 20);
        hider = new EntityHider(BetonQuest.getInstance(), EntityHider.Policy.BLACKLIST);
        loadFromConfig();
        runTaskTimer(BetonQuest.getInstance(), 0, updateInterval);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Starts (or restarts) the NPCHider. It loads the current configuration for hidden NPCs
     */
    public static void start() {
        if (instance != null) {
            instance.stop();
        }
        instance = new NPCHider();
    }

    /**
     * @return the currently used NPCHider instance
     */
    public static NPCHider getInstance() {
        return instance;
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private void loadFromConfig() {

        for (final QuestPackage cfgPackage : Config.getPackages().values()) {
            final ConfigurationSection custom = cfgPackage.getConfig();
            if (custom == null) {
                continue;
            }
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
                    LOG.warn(cfgPackage, "NPC ID '" + npcIds + "' is not a valid number, in hide_npcs", e);
                    continue npcs;
                }
                final Set<ConditionID> conditions = new HashSet<>();
                final String conditionsString = section.getString(npcIds);

                for (final String condition : conditionsString.split(",")) {
                    try {
                        conditions.add(new ConditionID(cfgPackage, condition));
                    } catch (final ObjectNotFoundException e) {
                        LOG.warn(cfgPackage, "Condition '" + condition + "' does not exist, in hide_npcs with ID " + npcIds, e);
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
            LOG.warn("NPCHider could not update visibility for npc " + npcID + ": No npc with this id found!");
            return;
        }
        if (npc.isSpawned()) {
            final Set<ConditionID> conditions = npcs.get(npcID);
            if (conditions == null || conditions.isEmpty() || !BetonQuest.conditions(onlineProfile, conditions)) {
                hider.showEntity(onlineProfile, npc.getEntity());
            } else {
                hider.hideEntity(onlineProfile, npc.getEntity());
            }
        }
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
        if (npc.getEntity() == null) {
            return false;
        }
        return !hider.isVisible(onlineProfile, npc.getEntity().getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCSpawn(final NPCSpawnEvent event) {
        applyVisibility(event.getNPC());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        applyVisibility(PlayerConverter.getID(event.getPlayer()));
    }
}
