package org.betonquest.betonquest.compatibility.protocollib;

import lombok.CustomLog;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensReloadEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import org.apache.commons.lang3.EnumUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.protocollib.api.GlowAPI;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * NPCGlow class
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
@CustomLog
public final class NPCGlow extends BukkitRunnable implements Listener {

    /**
     * instance of NPCGlow
     */
    private static NPCGlow instance;
    /**
     * List of conditions for specific NPC that will get glow
     */
    private final Map<Integer, List<NPCData>> npcConditions;
    /**
     * A map of NPCs with a collection of profiles that can see the npc glowing.
     */
    private final Map<NPCData, Collection<OnlineProfile>> npcProfilesMap;
    /**
     * instance of GlowAPI
     */
    private GlowAPI glowAPI;

    /**
     * Constructor of NPCGlow instance
     */
    public NPCGlow() {
        super();
        npcProfilesMap = new HashMap<>();
        npcConditions = new HashMap<>();
        glowAPI = new GlowAPI();
        Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), this::loadFromConfig, 5L);
        runTaskTimerAsynchronously(BetonQuest.getInstance(), 0, 5);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Starts (or restarts) the NPCGlow. It loads the current configuration for hidden NPCs
     */
    public static void start() {
        synchronized (NPCGlow.class) {
            if (instance != null) {
                instance.stop();
            }
            instance = new NPCGlow();
        }
    }

    /**
     * @return the currently used NPCGlow instance
     */
    public static NPCGlow getInstance() {
        return instance;
    }

    /**
     * load all data from config
     */
    @SuppressWarnings({"PMD.CognitiveComplexity","PMD.CyclomaticComplexity"})
    private void loadFromConfig() {
        for (final QuestPackage cfgPackage : Config.getPackages().values()) {
            final ConfigurationSection custom = cfgPackage.getConfig();
            final ConfigurationSection section = custom.getConfigurationSection("glow_npc");
            if (section == null) {
                continue;
            }

            for (final String key : section.getKeys(false)) {
                final String npcSectionId = section.getString(key + ".id");
                final String color = section.getString(key + ".color");
                final String rawConditions = section.getString(key + ".conditions");
                final Set<ConditionID> conditions = parseConditions(cfgPackage, key, rawConditions);
                if (npcSectionId == null) {
                    LOG.warn(cfgPackage, "No ID found in glow_npc for '" + key + "'");
                    continue;
                }
                final int npcId;
                try {
                    npcId = Integer.parseInt(npcSectionId);
                } catch (final NumberFormatException e) {
                    LOG.warn(cfgPackage, "NPC ID '" + npcSectionId + "' is not a valid number, in glow_npc", e);
                    continue;
                }
                if (CitizensAPI.getNPCRegistry().getById(npcId) == null) {
                    LOG.warn(cfgPackage, "NPC Glow could not update Glowing for npc " + npcSectionId + ": No npc with this id found!");
                    continue;
                }
                ChatColor chatColor = ChatColor.WHITE;
                if (color != null && EnumUtils.isValidEnum(ChatColor.class, color.toUpperCase(Locale.ROOT))) {
                    chatColor = ChatColor.valueOf(color.toUpperCase(Locale.ROOT));
                }

                final NPCData npcData = new NPCData(npcId, conditions, chatColor);
                if (npcConditions.containsKey(npcId)) {
                    npcConditions.get(npcId).add(npcData);
                } else {
                    npcConditions.putIfAbsent(npcId, new ArrayList<>());
                    npcConditions.get(npcId).add(npcData);
                }

                npcProfilesMap.put(npcData, new HashSet<>());
            }
        }
    }

    /**
     * Applies the glow if profile meets the conditions.
     *
     * @param profile profile that will get checked
     * @param npc     the npc that will get checked
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public void applyVisibility(final OnlineProfile profile, final NPC npc) {
        if (!npc.isSpawned()) {
            return;
        }

        final Integer npcID = npc.getId();
        if (!npcConditions.containsKey(npcID)) {
            return;
        }

        final Entity entity = npc.getEntity();
        npcConditions.get(npcID).parallelStream().forEach(npcData -> {
            final Set<ConditionID> conditions = npcData.conditions();

            final Collection<OnlineProfile> glowingProfiles = npcProfilesMap.get(npcData);
            if (conditions.isEmpty() || BetonQuest.conditions(profile, conditions)) {
                if (npcProfilesMap.containsKey(npcData) && glowingProfiles.add(profile)) {
                    glowAPI.sendGlowPacket(entity, npcData.color(), true, profile);
                }
            } else {
                if (npcProfilesMap.containsKey(npcData) && glowingProfiles.contains(profile)) {
                    glowingProfiles.remove(profile);
                    glowAPI.sendGlowPacket(entity, npcData.color(), false, profile);
                }
            }
        });
    }

    /**
     * Updates all NPCs for all profiles.
     */
    public void applyGlow() {
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            npcConditions.keySet().parallelStream()
                    .forEach((id) -> applyVisibility(onlineProfile, CitizensAPI.getNPCRegistry().getById(id)));
        }

        npcProfilesMap.forEach((npcData, profiles) -> {
            final int npcId = npcData.npcID();
            final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            if (npc == null) {
                LOG.warn("NPC Glow could not update Glowing for npc " + npcId + ": No npc with this id found!");
                return;
            }
            final Entity entity = npc.getEntity();
            glowAPI.sendGlowPacket(entity, npcData.color(), true, profiles);
        });
    }

    /**
     * Applies glow to npc for all online profiles if the conditions are met.
     *
     * @param npc npc that gets checked
     */
    public void applyGlow(final NPC npc) {
        if (!npc.getOwningRegistry().equals(CitizensAPI.getNPCRegistry())) {
            return;
        }
        PlayerConverter.getOnlineProfiles().parallelStream()
                .forEach((profile) -> applyVisibility(profile, npc));
    }

    /**
     * Starting the Runnable
     */
    @Override
    public void run() {
        applyGlow();
    }

    /**
     * Reset all Glowing NPCs
     *
     * @param npcs     List of NPCs that will be unGlow
     * @param profiles List of profiles that will get the packet
     */
    public void resetGlow(final Collection<Integer> npcs, final Collection<? extends OnlineProfile> profiles) {
        npcs.parallelStream()
                .forEach((npcId) -> {
                    final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
                    if (npc == null) {
                        return;
                    }
                    final Entity entity = npc.getEntity();
                    glowAPI.sendGlowPacket(entity, ChatColor.WHITE, false, profiles);
                });
    }

    /**
     * stop the NPCGlow instance, cleaning up all maps, Runnable, Listener, etc. And Reset all the glowing npc.
     */
    public void stop() {
        resetGlow(npcConditions.keySet(), PlayerConverter.getOnlineProfiles());
        npcProfilesMap.clear();
        npcConditions.clear();
        glowAPI = null;
        cancel();
        HandlerList.unregisterAll(this);
    }

    /**
     * Parses the provided instruction string into a set of conditions.
     *
     * @param questPackage  QuestPackage that contain the raw conditions
     * @param key           Name of the Glow Section
     * @param rawConditions String of raw conditions
     * @return Set Collections of ConditionID
     */
    private Set<ConditionID> parseConditions(final QuestPackage questPackage, final String key, final String rawConditions) {
        final Set<ConditionID> conditions = new HashSet<>();
        if (rawConditions == null) {
            return conditions;
        }
        for (final String condition : rawConditions.split(",")) {
            try {
                conditions.add(new ConditionID(questPackage, condition));
            } catch (final ObjectNotFoundException e) {
                LOG.warn(questPackage, "Condition '" + condition + "' does not exist, in glow_npc with ID " + key, e);
            }
        }
        return conditions;
    }

    /**
     * If NPC got despawn it gets remove the npc from all map.
     *
     * @param event NPCDespawn
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCDespawn(final NPCDespawnEvent event) {
        final NPC npc = event.getNPC();
        final int npcId = npc.getId();
        if(!npcConditions.containsKey(npcId)){
            return;
        }
        for (final NPCData npcData : npcConditions.get(npcId)) {
            npcProfilesMap.remove(npcData);
        }
        npcConditions.remove(npcId);
    }

    /**
     * If NPC death it gets remove the npc from all map.
     *
     * @param event NPCDeath
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCDeath(final NPCDeathEvent event) {
        final int npcId = event.getNPC().getId();
        if(!npcConditions.containsKey(npcId)){
            return;
        }
        for (final NPCData npcData : npcConditions.get(npcId)) {
            npcProfilesMap.remove(npcData);
        }
        npcConditions.remove(npcId);
    }

    /**
     * Restart the NPCGlow Instance when citizens plugin is getting reload
     *
     * @param event CitizensReload
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCitizensReload(final CitizensReloadEvent event) {
        start();
    }

    /**
     * apply glowing to npc that just spawn (if it registered on map).
     *
     * @param event NPCSpawn
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCSpawn(final NPCSpawnEvent event) {
        applyGlow(event.getNPC());
    }

    /**
     * Removes the profile that just quit from all maps.
     *
     * @param event PlayerQuitEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        npcProfilesMap.values().forEach(
                (profileCollection) -> profileCollection.remove(PlayerConverter.getID(event.getPlayer())));
    }

    /**
     * NPC Data class to wrapped all the glow Data from {@link NPCGlow}
     *
     * @param npcID      ID of NPC that will be registered
     * @param conditions List of conditions for glowing
     * @param color      Color of the glow
     */
    private record NPCData(Integer npcID, Set<ConditionID> conditions, ChatColor color){
    }
}
