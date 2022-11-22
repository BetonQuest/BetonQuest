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
import org.betonquest.betonquest.compatibility.protocollib.api.GlowAPI;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
     * instance of GlowAPI
     */
    private GlowAPI glowAPI;

    /**
     * instance of NPCGlow
     */
    private static NPCGlow instance;
    /**
     * List of conditions for specific NPC that will get glow
     */
    private final Map<Integer, Set<ConditionID>> npcs;
    /**
     * List player that can seen glowing npc.
     */
    private final Map<Integer, Collection<Player>> npcPlayersMap;
    /**
     * Store NPCID for each color
     */
    private final Map<Integer, ChatColor> npcColor;

    /**
     * Constructor of NPCGlow instance
     */
    public NPCGlow() {
        super();
        npcPlayersMap = new HashMap<>();
        npcs = new HashMap<>();
        npcColor = new HashMap<>();
        glowAPI = new GlowAPI();
        Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), this::loadFromConfig, 5L);
        runTaskTimer(BetonQuest.getInstance(), 0, 5);
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
    @SuppressWarnings("PMD.CognitiveComplexity")
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
                final Set<ConditionID> conditions = conditionSet(cfgPackage, key, rawConditions);
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
                npcs.put(npcId, conditions);
                npcPlayersMap.put(npcId, new HashSet<>());
                npcColor.put(npcId, chatColor);
            }
        }
    }

    /**
     * Checked if player have met the conditions
     *
     * @param player player that will get checked
     * @param npcID  ID of npc that will get checked
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public void applyVisibility(final Player player, final Integer npcID) {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);

        if (npc == null) {
            LOG.warn("NPC Glow could not update Glowing for npc " + npcID + ": No npc with this id found!");
            return;
        }

        if (npc.isSpawned()) {
            final Set<ConditionID> conditions = npcs.get(npcID);
            final Entity entity = npc.getEntity();
            if (conditions == null || conditions.isEmpty() || !BetonQuest.conditions(PlayerConverter.getID(player), conditions)) {
                if (npcPlayersMap.containsKey(npcID)
                        && npcPlayersMap.get(npcID).contains(player)) {
                    npcPlayersMap.get(npcID).remove(player);
                    glowAPI.glowPacketAsync(entity, npcColor.get(npcID), false, player).join();
                }
            } else {
                if (npcPlayersMap.containsKey(npcID) && npcPlayersMap.get(npcID).add(player)) {
                    glowAPI.glowPacketAsync(entity, npcColor.get(npcID), true, player).join();
                }
            }
        }
    }

    /**
     * Updates all NPCs for All Players
     */
    public void applyGlow() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final Integer id : npcs.keySet()) {
                applyVisibility(p, id);
            }
        }

        npcPlayersMap.forEach((npcId, players) -> {
            final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            if (npc == null) {
                LOG.warn("NPC Glow could not update Glowing for npc " + npcId + ": No npc with this id found!");
                return;
            }
            final Entity entity = npc.getEntity();
            glowAPI.glowPacketAsync(entity, npcColor.get(npcId), true, players).join();
        });
    }

    /**
     * apply glow to npc for all online players if condition are met
     *
     * @param npc npc that get checked
     */
    public void applyGlow(final NPC npc) {
        if (!npc.getOwningRegistry().equals(CitizensAPI.getNPCRegistry())) {
            return;
        }
        Bukkit
                .getOnlinePlayers()
                .parallelStream()
                .forEach((player)
                        -> applyVisibility(player, npc.getId()));
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
     * @param npcs    List of NPCs that will be unGlow
     * @param players List of Players that will get the packet
     */
    public void resetGlow(final Collection<Integer> npcs, final Collection<? extends Player> players) {
        npcs
                .parallelStream()
                .forEach((npcId) -> {
                    final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
                    if (npc == null) {
                        return;
                    }
                    final Entity entity = npc.getEntity();
                    glowAPI.glowPacketAsync(entity, npcColor.get(npcId), false, players);
                });
    }

    /**
     * stop the NPCGlow instance, cleaning up all maps, Runnable, Listener, etc. And Reset all the glowing npc.
     */
    public void stop() {
        resetGlow(npcs.keySet(), Bukkit.getOnlinePlayers());
        npcPlayersMap.clear();
        npcs.clear();
        npcColor.clear();
        glowAPI = null;
        cancel();
        HandlerList.unregisterAll(this);
    }

    /**
     *  Creating Set collections of ConditionID from raw conditions
     *
     * @param cfgPackage QuestPackage that contain the raw conditions
     * @param key Name of the Glow Section
     * @param rawConditions String of raw conditions
     * @return Set Collections of ConditionID
     */
    private Set<ConditionID> conditionSet(final QuestPackage cfgPackage, final String key, final String rawConditions){
        final Set<ConditionID> conditions = new HashSet<>();
        if(rawConditions == null){
            return conditions;
        }
        for (final String condition : rawConditions.split(",")) {
            try {
                conditions.add(new ConditionID(cfgPackage, condition));
            } catch (final ObjectNotFoundException e) {
                LOG.warn(cfgPackage, "Condition '" + condition + "' does not exist, in glow_npc with ID " + key, e);
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
        npcPlayersMap.remove(npc.getId());
        npcs.remove(npc.getId());
        npcColor.remove(npc.getId());
    }
    /**
     * If NPC death it gets remove the npc from all map.
     *
     * @param event NPCDeath
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCDeath(final NPCDeathEvent event) {
        final NPC npc = event.getNPC();
        npcPlayersMap.remove(npc.getId());
        npcs.remove(npc.getId());
        npcColor.remove(npc.getId());
    }

    /**
     * Restart the NPCGlow Instance when citizens plugin is getting reload
     *
     * @param event CitizensReload
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCitizensReload(final CitizensReloadEvent event){
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
     * Remove Player that just quit from all map.
     *
     * @param event PlayerQuit
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        npcPlayersMap
                .values()
                .parallelStream()
                .forEach((players) -> players.remove(event.getPlayer()));
    }

}
