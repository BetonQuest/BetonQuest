package org.betonquest.betonquest.compatibility.protocollib.hider;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.protocollib.wrappers.EntityMetaDataHelper;
import org.betonquest.betonquest.compatibility.protocollib.wrappers.MetaDataHelper;
import org.betonquest.betonquest.compatibility.protocollib.wrappers.WrapperPlayServerEntityMetadata;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@CustomLog
public class NPCGlowing extends BukkitRunnable implements Listener {
    private static NPCGlowing instance;
    private final Map<Integer, Set<ConditionID>> npcs;
    private ProtocolManager manager;
    private final Listener bukkitListener;
    protected final Policy policy;

    protected Table<Integer, Integer, Boolean> observerEntityMap = HashBasedTable.create();


    private NPCGlowing(final Policy policy) {
        instance = this;
        this.policy = policy;
        npcs = new HashMap<>();
        manager = ProtocolLibrary.getProtocolManager();
        Bukkit.getPluginManager().registerEvents(bukkitListener = constructBukkit(), BetonQuest.getInstance());
        loadFromConfig();
        final int updateInterval = BetonQuest.getInstance().getConfig().getInt("npc_hider_check_interval", 5 * 20);
        runTaskTimer(BetonQuest.getInstance(), 0, updateInterval);
    }

    public static NPCGlowing getInstance() {
        return instance;
    }

    public static void start() {
        if (instance != null) {
            instance.stop();
        }
        instance = new NPCGlowing(Policy.BLACKLIST);
    }

    @Override
    public void run() {
        applyGlowing();
    }

    public void stop() {
        cancel();
        HandlerList.unregisterAll(this);
    }

    private Listener constructBukkit() {
        return new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onNPCSpawn(final NPCSpawnEvent event) {
                applyGlowing(event.getNPC());
            }

            @EventHandler(ignoreCancelled = true)
            public void onPlayerJoin(final PlayerJoinEvent event) {
                applyGlowing(event.getPlayer());
            }
        };
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private void loadFromConfig() {

        for (final ConfigPackage cfgPackage : Config.getPackages().values()) {
            final FileConfiguration custom = cfgPackage.getCustom().getConfig();
            if (custom == null) {
                continue;
            }
            final ConfigurationSection section = custom.getConfigurationSection("glowing_npcs");
            if (section == null) {
                continue;
            }
            npcs:
            for (final String npcIds : section.getKeys(false)) {
                final int npcId;
                try {
                    npcId = Integer.parseInt(npcIds);
                } catch (final NumberFormatException e) {
                    LOG.warning(cfgPackage, "NPC ID '" + npcIds + "' is not a valid number, in custom.yml hide_npcs", e);
                    continue npcs;
                }
                final Set<ConditionID> conditions = new HashSet<>();
                final String conditionsString = section.getString(npcIds);

                for (final String condition : conditionsString.split(",")) {
                    try {
                        conditions.add(new ConditionID(cfgPackage, condition));
                    } catch (final ObjectNotFoundException e) {
                        LOG.warning(cfgPackage, "Condition '" + condition + "' does not exist, in custom.yml hide_npcs with ID " + npcIds, e);
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

    public void applyGlowing(final Player player, final Integer npcID) {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);
        if (npc == null) {
            LOG.warning(null, "NPCHider could not update visibility for npc " + npcID + ": No npc with this id found!");
            return;
        }
        if (npc.isSpawned()) {
            final Set<ConditionID> conditions = npcs.get(npcID);
            if (conditions == null || conditions.isEmpty() || !BetonQuest.conditions(PlayerConverter.getID(player), conditions)) {
                removeGlowing(player, npc.getEntity());
            } else {
                SetGlowing(player, npc.getEntity());
            }
        }
    }



    private void applyGlowing(final Player player) {
        for (final Integer npcID : npcs.keySet()) {
            applyGlowing(player, npcID);
        }
    }

    public void applyGlowing(final NPC npcID) {
        //check if the npc is in the default registry
        if (!npcID.getOwningRegistry().equals(CitizensAPI.getNPCRegistry())) {
            return;
        }
        for (final Player p : Bukkit.getOnlinePlayers()) {
            applyGlowing(p, npcID.getId());
        }
    }

    private void applyGlowing(){
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final Integer npcID : npcs.keySet()) {
                applyGlowing(p, npcID);
            }
        }
    }
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private boolean SetGlowing(final Player player, final Entity npc) {
        validate(player, npc);
        final boolean hiddenBefore = setglowingVisibility(player, npc.getEntityId(), false);
        final MetaDataHelper dataHelper = new MetaDataHelper();
        EntityMetaDataHelper entityMetaDataHelper = new EntityMetaDataHelper(dataHelper);

        if (hiddenBefore) {
            entityMetaDataHelper.setGlowing(player, npc);
        }
        return hiddenBefore;
    }

    private boolean removeGlowing(final Player player, final Entity npc) {
        validate(player, npc);
        final boolean hiddenBefore = !setglowingVisibility(player, npc.getEntityId(), true);
        if (manager != null && hiddenBefore) {
            manager.updateEntity(npc, Collections.singletonList(player));
        }
        return hiddenBefore;
    }

    private void validate(final Player observer, final Entity entity) {
        Preconditions.checkNotNull(observer, "observer cannot be NULL.");
        Preconditions.checkNotNull(entity, "entity cannot be NULL.");
    }

    public void close() {
        if (manager != null) {
            HandlerList.unregisterAll(bukkitListener);
            manager = null;
        }
    }

    @SuppressWarnings("PMD.LinguisticNaming")
    protected boolean setglowingVisibility(final Player observer, final int entityID, final boolean glowing) {
        switch (policy) {
            case BLACKLIST:
                // Non-membership means they are glowing
                return !setMembership(observer, entityID, !glowing);
            case WHITELIST:
                return setMembership(observer, entityID, glowing);
            default:
                throw new IllegalArgumentException("Unknown policy: " + policy);
        }
    }

    protected boolean setMembership(final Player observer, final int entityID, final boolean member) {
        if (member) {
            return observerEntityMap.put(observer.getEntityId(), entityID, true) != null;
        } else {
            return observerEntityMap.remove(observer.getEntityId(), entityID) != null;
        }
    }

    protected boolean getMembership(final Player observer, final int entityID) {
        return observerEntityMap.contains(observer.getEntityId(), entityID);
    }

    protected boolean isGlowing(final Player observer, final int entityID) {
        // If we are using a whitelist, presence means visibility - if not, the opposite is the case
        final boolean presence = getMembership(observer, entityID);

        return (policy == Policy.WHITELIST) == presence;
    }

    public enum Policy {
        /**
         * All entities are notGlowing by default. Only entities specifically made Glowing may be seen.
         */
        WHITELIST,

        /**
         * All entities are Glowing by default. An entity can only be Glowing explicitly.
         */
        BLACKLIST,
    }
}
