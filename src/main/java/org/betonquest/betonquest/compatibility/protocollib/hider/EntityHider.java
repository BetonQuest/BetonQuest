package org.betonquest.betonquest.compatibility.protocollib.hider;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * EntityHider From: https://gist.github.com/aadnk/5871793
 * We use the fork: https://gist.github.com/dmulloy2/5526f5bf906c064c255e
 */
@SuppressWarnings("PMD.CommentRequired")
public class EntityHider implements Listener {
    // Packets that update remote player entities
    private static final PacketType[] ENTITY_PACKETS = {
            PacketType.Play.Server.ENTITY_EQUIPMENT,
            PacketType.Play.Server.ANIMATION,
            PacketType.Play.Server.NAMED_ENTITY_SPAWN,
            PacketType.Play.Server.COLLECT,
            PacketType.Play.Server.SPAWN_ENTITY,
            PacketType.Play.Server.SPAWN_ENTITY_LIVING,
            PacketType.Play.Server.SPAWN_ENTITY_PAINTING,
            PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB,
            PacketType.Play.Server.ENTITY_VELOCITY,
            PacketType.Play.Server.REL_ENTITY_MOVE,
            PacketType.Play.Server.ENTITY_LOOK,
            PacketType.Play.Server.REL_ENTITY_MOVE_LOOK,
            PacketType.Play.Server.ENTITY_TELEPORT,
            PacketType.Play.Server.ENTITY_HEAD_ROTATION,
            PacketType.Play.Server.ENTITY_STATUS,
            PacketType.Play.Server.ATTACH_ENTITY,
            PacketType.Play.Server.ENTITY_METADATA,
            PacketType.Play.Server.ENTITY_EFFECT,
            PacketType.Play.Server.REMOVE_ENTITY_EFFECT,
            PacketType.Play.Server.BLOCK_BREAK_ANIMATION,
            PacketType.Play.Server.COMBAT_EVENT

            // We don't handle DESTROY_ENTITY though
    };
    // Current policy
    protected final Policy policy;
    // Listeners
    private final Listener bukkitListener;
    private final PacketAdapter protocolListener;
    protected Table<Integer, Integer, Boolean> observerEntityMap = HashBasedTable.create();
    private ProtocolManager manager;

    /**
     * Construct a new entity hider.
     *
     * @param plugin - the plugin that controls this entity hider.
     * @param policy - the default visibility policy.
     */
    public EntityHider(final Plugin plugin, final Policy policy) {
        Preconditions.checkNotNull(plugin, "plugin cannot be NULL.");

        // Save policy
        this.policy = policy;
        this.manager = ProtocolLibrary.getProtocolManager();

        // Register events and packet listener
        plugin.getServer().getPluginManager().registerEvents(
                bukkitListener = constructBukkit(), plugin);
        manager.addPacketListener(
                protocolListener = constructProtocol(plugin));
    }

    /**
     * Set the visibility status of a given entity for a particular observer.
     *
     * @param observer - the observer player.
     * @param entityID - ID of the entity that will be hidden or made visible.
     * @param visible  - TRUE if the entity should be made visible, FALSE if not.
     * @return TRUE if the entity was visible before this method call, FALSE otherwise.
     */
    @SuppressWarnings("PMD.LinguisticNaming")
    protected boolean setVisibility(final Player observer, final int entityID, final boolean visible) {
        switch (policy) {
            case BLACKLIST:
                // Non-membership means they are visible
                return !setMembership(observer, entityID, !visible);
            case WHITELIST:
                return setMembership(observer, entityID, visible);
            default:
                throw new IllegalArgumentException("Unknown policy: " + policy);
        }
    }

    /**
     * Add or remove the given entity and observer entry from the table.
     *
     * @param observer - the player observer.
     * @param entityID - ID of the entity.
     * @param member   - TRUE if they should be present in the table, FALSE otherwise.
     * @return TRUE if they already were present, FALSE otherwise.
     */
    // Helper method
    @SuppressWarnings("PMD.LinguisticNaming")
    protected boolean setMembership(final Player observer, final int entityID, final boolean member) {
        if (member) {
            return observerEntityMap.put(observer.getEntityId(), entityID, true) != null;
        } else {
            return observerEntityMap.remove(observer.getEntityId(), entityID) != null;
        }
    }

    /**
     * Determine if the given entity and observer is present in the table.
     *
     * @param observer - the player observer.
     * @param entityID - ID of the entity.
     * @return TRUE if they are present, FALSE otherwise.
     */
    protected boolean getMembership(final Player observer, final int entityID) {
        return observerEntityMap.contains(observer.getEntityId(), entityID);
    }

    /**
     * Determine if a given entity is visible for a particular observer.
     *
     * @param observer - the observer player.
     * @param entityID -  ID of the entity that we are testing for visibility.
     * @return TRUE if the entity is visible, FALSE otherwise.
     */
    protected boolean isVisible(final Player observer, final int entityID) {
        // If we are using a whitelist, presence means visibility - if not, the opposite is the case
        final boolean presence = getMembership(observer, entityID);

        return (policy == Policy.WHITELIST) == presence;
    }

    /**
     * Remove the given entity from the underlying map.
     *
     * @param entity    - the entity to remove.
     * @param destroyed - TRUE if the entity was killed, FALSE if it is merely unloading.
     */
    protected void removeEntity(final Entity entity, final boolean destroyed) {
        final int entityID = entity.getEntityId();

        final List<Map.Entry<Integer, Map<Integer, Boolean>>> list = new CopyOnWriteArrayList<>(observerEntityMap.rowMap().entrySet());

        for (final Map.Entry<Integer, Map<Integer, Boolean>> integerMapEntry : list) {
            integerMapEntry.getValue().remove(entityID);
        }
    }

    /**
     * Invoked when a player logs out.
     *
     * @param player - the player that jused logged out.
     */
    protected void removePlayer(final Player player) {
        // Cleanup
        observerEntityMap.rowMap().remove(player.getEntityId());
    }

    /**
     * Construct the Bukkit event listener.
     *
     * @return Our listener.
     */
    private Listener constructBukkit() {
        return new Listener() {
            @EventHandler
            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            public void onEntityDeath(final EntityDeathEvent event) {
                removeEntity(event.getEntity(), true);
            }

            @EventHandler
            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            public void onChunkUnload(final ChunkUnloadEvent event) {
                for (final Entity entity : event.getChunk().getEntities()) {
                    removeEntity(entity, false);
                }
            }

            @EventHandler
            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            public void onPlayerQuit(final PlayerQuitEvent event) {
                removePlayer(event.getPlayer());
            }
        };
    }

    /**
     * Construct the packet listener that will be used to intercept every entity-related packet.
     *
     * @param plugin - the parent plugin.
     * @return The packet listener.
     */
    private PacketAdapter constructProtocol(final Plugin plugin) {
        return new PacketAdapter(plugin, ENTITY_PACKETS) {
            @Override
            public void onPacketSending(final PacketEvent event) {
                final int index = event.getPacketType() == PacketType.Play.Server.COMBAT_EVENT ? 1 : 0;

                if (!event.isPlayerTemporary()) {
                    final Integer entityID = event.getPacket().getIntegers().readSafely(index);
                    if (entityID != null && !isVisible(event.getPlayer(), entityID)) {
                        event.setCancelled(true);
                    }
                }
            }
        };
    }

    /**
     * Toggle the visibility status of an entity for a player.
     * <p>
     * If the entity is visible, it will be hidden. If it is hidden, it will become visible.
     *
     * @param observer - the player observer.
     * @param entity   - the entity to toggle.
     * @return TRUE if the entity was visible before, FALSE otherwise.
     */
    public final boolean toggleEntity(final Player observer, final Entity entity) {
        if (isVisible(observer, entity.getEntityId())) {
            return hideEntity(observer, entity);
        } else {
            return !showEntity(observer, entity);
        }
    }

    /**
     * Allow the observer to see an entity that was previously hidden.
     *
     * @param observer - the observer.
     * @param entity   - the entity to show.
     * @return TRUE if the entity was hidden before, FALSE otherwise.
     */
    public final boolean showEntity(final Player observer, final Entity entity) {
        validate(observer, entity);
        final boolean hiddenBefore = !setVisibility(observer, entity.getEntityId(), true);

        // Resend packets
        if (manager != null && hiddenBefore) {
            manager.updateEntity(entity, Collections.singletonList(observer));
        }
        return hiddenBefore;
    }

    /**
     * Prevent the observer from seeing a given entity.
     *
     * @param observer - the player observer.
     * @param entity   - the entity to hide.
     * @return TRUE if the entity was previously visible, FALSE otherwise.
     */
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public final boolean hideEntity(final Player observer, final Entity entity) {
        validate(observer, entity);
        final boolean visibleBefore = setVisibility(observer, entity.getEntityId(), false);

        if (visibleBefore) {
            final PacketContainer destroyEntity = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
            destroyEntity.getIntegerArrays().write(0, new int[]{entity.getEntityId()});

            // Make the entity disappear
            try {
                manager.sendServerPacket(observer, destroyEntity);
            } catch (final InvocationTargetException e) {
                throw new RuntimeException("Cannot send server packet.", e);
            }
        }
        return visibleBefore;
    }

    /**
     * Determine if the given entity has been hidden from an observer.
     * <p>
     * Note that the entity may very well be occluded or out of range from the perspective
     * of the observer. This method simply checks if an entity has been completely hidden
     * for that observer.
     *
     * @param observer - the observer.
     * @param entity   - the entity that may be hidden.
     * @return TRUE if the player may see the entity, FALSE if the entity has been hidden.
     */
    public final boolean canSee(final Player observer, final Entity entity) {
        validate(observer, entity);

        return isVisible(observer, entity.getEntityId());
    }

    // For valdiating the input parameters
    private void validate(final Player observer, final Entity entity) {
        Preconditions.checkNotNull(observer, "observer cannot be NULL.");
        Preconditions.checkNotNull(entity, "entity cannot be NULL.");
    }

    /**
     * Retrieve the current visibility policy.
     *
     * @return The current visibility policy.
     */
    public Policy getPolicy() {
        return policy;
    }

    public void close() {
        if (manager != null) {
            HandlerList.unregisterAll(bukkitListener);
            manager.removePacketListener(protocolListener);
            manager = null;
        }
    }

    /**
     * The current entity visibility policy.
     *
     * @author Kristian
     */
    public enum Policy {
        /**
         * All entities are invisible by default. Only entities specifically made visible may be seen.
         */
        WHITELIST,

        /**
         * All entities are visible by default. An entity can only be hidden explicitly.
         */
        BLACKLIST,
    }
}
