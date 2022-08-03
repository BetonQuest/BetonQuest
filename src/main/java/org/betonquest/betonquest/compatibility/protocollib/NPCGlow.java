package org.betonquest.betonquest.compatibility.protocollib;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.CustomLog;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import org.apache.commons.lang3.EnumUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerEntityMetadata;
import org.betonquest.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerScoreboardTeam;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyMethods"})
@CustomLog
public final class NPCGlow extends BukkitRunnable implements Listener {

    private final Map<Integer, Set<ConditionID>> npcs;
    /**
     * List player that can seen glowing npc.
     */
    private final Map<Integer, Set<Player>> npcPlayersMap;
    private final Map<ChatColor, Set<NPC>> npcColor;

    public NPCGlow() {
        super();
        npcPlayersMap = new HashMap<>();
        npcs = new HashMap<>();
        npcColor = new HashMap<>();
        Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), () -> {
            loadFromConfig();
            applyTeamAsync(Mode.TEAM_CREATED);
        }, 20L);
        runTaskTimer(BetonQuest.getInstance(), 0, 5);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @SuppressWarnings({"PMD.ShortVariable", "PMD.NPathComplexity", "PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    private void loadFromConfig() {

        for (final QuestPackage cfgPackage : Config.getPackages().values()) {
            final ConfigurationSection custom = cfgPackage.getConfig();
            if (custom == null) {
                continue;
            }

            final ConfigurationSection section = custom.getConfigurationSection("glow_npc");
            if (section == null) {
                continue;
            }

            for (final String key : section.getKeys(false)) {
                final String id = section.getString(key + ".id");
                final String color = section.getString(key + ".color");
                final String rawConditions = section.getString(key + ".conditions");
                final Set<ConditionID> conditions = new HashSet<>();
                if (rawConditions != null) {
                    for (final String condition : rawConditions.split(",")) {
                        try {
                            conditions.add(new ConditionID(cfgPackage, condition));
                        } catch (final ObjectNotFoundException e) {
                            LOG.warn(cfgPackage, "Condition '" + condition + "' does not exist, in glow_npc with ID " + id, e);
                        }
                    }
                }
                final int npcId;
                try {
                    npcId = Integer.parseInt(id);
                } catch (final NumberFormatException e) {
                    LOG.warn(cfgPackage, "NPC ID '" + id + "' is not a valid number, in glow_npc", e);
                    continue;
                }
                if (CitizensAPI.getNPCRegistry().getById(npcId) == null) {
                    LOG.warn(cfgPackage, "NPC Glow could not update Glowing for npc " + id + ": No npc with this id found!");
                    continue;
                }
                ChatColor chatColor = ChatColor.WHITE;
                if(color != null && EnumUtils.isValidEnum(ChatColor.class, color.toUpperCase(Locale.ROOT))){
                    chatColor = ChatColor.valueOf(color.toUpperCase(Locale.ROOT));
                }
                if (npcs.containsKey(npcId)) {
                    npcs.get(npcId).addAll(conditions);
                } else {
                    npcs.put(npcId, conditions);
                }
                if (!npcPlayersMap.containsKey(npcId)) {
                    npcPlayersMap.put(npcId, new HashSet<>());
                }
                if(npcColor.containsKey(chatColor)){
                    npcColor.get(chatColor).add(CitizensAPI.getNPCRegistry().getById(npcId));
                } else {
                    npcColor.put(chatColor, new HashSet<>());
                    npcColor.get(chatColor).add(CitizensAPI.getNPCRegistry().getById(npcId));
                }
            }
        }
    }

    public void applyVisibility(final Player player, final Integer npcID) {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);

        if (npc == null) {
            LOG.warn("NPC Glow could not update Glowing for npc " + npcID + ": No npc with this id found!");
            return;
        }

        if (npc.isSpawned()) {
            final Set<ConditionID> conditions = npcs.get(npcID);
            if (conditions == null || conditions.isEmpty() || !BetonQuest.conditions(PlayerConverter.getID(player), conditions)) {
                if (npcPlayersMap.containsKey(npcID)
                        && npcPlayersMap.get(npcID).contains(player)) {
                    npcPlayersMap.get(npcID).remove(player);
                    applyGlow(npcID, false, player);
                }
            } else {
                if (npcPlayersMap.containsKey(npcID)) {
                    npcPlayersMap.get(npcID).add(player);
                }
            }
        }
    }

    /**
     * Create and Sending glow packet for a player
     *
     * @param npc    ID for npc that Will get Glow
     * @param glow   true if npc need to be glowing
     * @param player Player that can see the Glowing NPC
     */
    public void applyGlow(final int npc, final boolean glow, final Player player) {
        final Entity entity = CitizensAPI.getNPCRegistry().getById(npc).getEntity();

        final WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
        final WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
        final WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setEntity(entity);
        byte mask = watcher.getByte(0);
        if (glow) {
            mask |= 0x40;
        } else {
            mask &= ~0x40;
        }
        watcher.setObject(0, serializer, mask);

        packet.setEntityID(entity.getEntityId());
        packet.setMetadata(watcher.getWatchableObjects());
        packet.sendPacket(player);
    }

    public void applyGlow(final Player player) {
        npcs
                .keySet()
                .parallelStream()
                .forEach((npcID) -> {
                    applyVisibility(player, npcID);
                });
    }

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
     * Updates all NPCs for All Players
     */
    public void applyGlow() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final Integer id : npcs.keySet()) {
                applyVisibility(p, id);
            }
        }

        npcPlayersMap.forEach((k, v) -> {
            final NPC npc = CitizensAPI.getNPCRegistry().getById(k);
            if (npc == null) {
                LOG.warn("NPC Glow could not update Glowing for npc " + k + ": No npc with this id found!");
                return;
            }
            applyGlowAsync(npc, v).join();
        });
    }

    /**
     * Create and Sending glow packet in background (async) for a List of players
     *
     * @param npc     NPC that will be Glowing
     * @param players List of players that can see the glowing NPC
     * @return void
     */
    public CompletableFuture<Void> applyGlowAsync(final NPC npc, final Collection<? extends Player> players) {
        final Entity entity = npc.getEntity();
        return CompletableFuture.runAsync(() -> {
            players
                    .parallelStream()
                    .forEach((player) -> {
                        final WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
                        final WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
                        final WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
                        watcher.setEntity(entity);
                        byte mask = watcher.getByte(0);
                        mask |= 0x40;
                        watcher.setObject(0, serializer, mask);

                        packet.setEntityID(entity.getEntityId());
                        packet.setMetadata(watcher.getWatchableObjects());
                        packet.sendPacket(player);
                    });
        });
    }

    public void applyTeamAsync(final Mode mode){
        npcColor.forEach((color, npcs) -> {
            Bukkit
                    .getOnlinePlayers()
                    .parallelStream()
                    .forEach((player) -> {
                applyTeamAsync(npcs, mode, color, player).join();
            });
        });
    }

    /**
     * Create and Sending Scoreboard Team packet in background (async) for a List of npcs
     *
     * @param npcs NPCs that will be in the Team
     * @param player player that can see the glowing NPC
     * @return void
     */
    public CompletableFuture<Void> applyTeamAsync(final Set<NPC> npcs, final Mode mode, final ChatColor color, final Player player){
        return CompletableFuture.runAsync(() -> {
            final WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
            packet.setMode(mode.ordinal());
            packet.setName("Color#" + color.name());
            packet.setColor(color);
            if(mode == Mode.TEAM_REMOVED){
                packet.sendPacket(player);
                return;
            }

            packet.setPlayers(npcs.parallelStream().map((npc) -> {
                final Entity entity = npc.getEntity();
                if (entity instanceof OfflinePlayer) {
                    return entity.getName();
                } else {
                    return entity.getUniqueId().toString();
                }
            }).collect(Collectors.toList()));
            packet.sendPacket(player);
        });
    }

    @Override
    public void run() {
        applyGlow();
    }

    public void resetGlow(final Collection<Integer> npcs, final boolean glow, final Collection<? extends Player> players) {
        npcs
                .parallelStream()
                .forEach((npcId) -> {
                    final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
                    if (npc == null) {
                        return;
                    }
                    players.forEach((player) -> {
                        applyGlow(npcId, glow, player);
                    });
                });
        applyTeamAsync(Mode.TEAM_REMOVED);
    }

    /**
     * Stops the NPCGlow, cleaning up all listeners, runnables, etc. And Reset all the glowing npc.
     */
    public void stop() {
        resetGlow(npcs.keySet(), false, Bukkit.getOnlinePlayers());
        npcPlayersMap.clear();
        npcs.clear();
        npcColor.clear();
        loadFromConfig();
        applyTeamAsync(Mode.TEAM_CREATED);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogin(final PlayerJoinEvent event) {
        applyGlow(event.getPlayer());
        applyTeamAsync(Mode.TEAM_CREATED);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCSpawn(final NPCSpawnEvent event) {
        applyGlow(event.getNPC());
        applyTeamAsync(Mode.TEAM_CREATED);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        npcPlayersMap
                .values()
                .parallelStream()
                .forEach((players) -> {
                    players.remove(event.getPlayer());
                });
    }

    protected enum Mode {
        TEAM_CREATED,
        TEAM_REMOVED,
        TEAM_UPDATED,
        PLAYERS_ADDED,
        PLAYERS_REMOVED
    }
}
