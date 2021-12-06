package org.betonquest.betonquest.objectives;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.compatibility.protocollib.hider.EntityHider;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@CustomLog
@SuppressWarnings("PMD.CommentRequired")
public class CollectItemObjective extends CountingObjective implements Listener {

    private static final int MAX_QUESTITEM = 1;
    private final Instruction.Item[] questItems;
    private final CompoundLocation location;
    private final boolean isProtected;
    private final boolean isPrivate;
    private final EventID[] failEvents;
    /**
     * Saves the entity of each dropped item alongside the player that is allowed to pick the item up.
     */
    private final Map<Entity, UUID> entityPlayerMap = new ConcurrentHashMap<>();

    private EntityHider hider;

    public CollectItemObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "item_to_collect");

        questItems = instruction.getItemList();
        location = instruction.getLocation(instruction.getOptional("loc"));

        if (location == null) {
            throw new InstructionParseException("No drop location given!");
        }
        this.isProtected = "true".equals(instruction.getOptional("protected"));
        this.isPrivate = "true".equals(instruction.getOptional("private"));
        failEvents = instruction.getList(instruction.getOptional("fail"), instruction::getEvent).toArray(new EventID[0]);

        if (this.isPrivate) {
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
                throw new InstructionParseException("You need to install ProtocolLib to use private item drops!");
            } else {
                hider = new EntityHider(BetonQuest.getInstance(), EntityHider.Policy.BLACKLIST);
            }
        }
        if (questItems.length != MAX_QUESTITEM) {
            throw new InstructionParseException("Only one item can be added to this objective.");
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void start(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        for (final Instruction.Item questitem : questItems) {
            final QuestItem item = questitem.getItem();
            final VariableNumber amount = questitem.getAmount();
            final int amountInt = amount.getInt(playerID);

            final ItemStack generateItem = item.generate(amountInt, playerID);

            Location loc = null;
            try {
                loc = location.getLocation(playerID);
            } catch (QuestRuntimeException e) {
                LOG.error(instruction.getPackage(), "Unable to parse" + instruction.getID(), e);
            }

            targetAmount = amountInt;

            final Location finalLoc = loc;
            new BukkitRunnable() {
                @Override
                public void run() {
                    final Entity droppedItem = finalLoc.getWorld().dropItem(finalLoc, generateItem);
                    droppedItem.setInvulnerable(true);
                    entityPlayerMap.put(droppedItem, player.getUniqueId());
                    if (isPrivate) {
                        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (!onlinePlayer.equals(player)) {
                                hider.hideEntity(onlinePlayer, droppedItem);
                            }
                        }
                    }
                }
            }.runTask(BetonQuest.getInstance());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHopperPickupItem(final EntityPickupItemEvent event) {
        final Item item = event.getItem();
        if (!entityPlayerMap.containsKey(item)) {
            return;
        }

        final UUID ownerUUID = entityPlayerMap.get(item);
        final Entity pickupEntity = event.getEntity();

        if (isProtected && !event.getEntity().getUniqueId().equals(ownerUUID)) {
            event.setCancelled(true);
            return;
        }

        if (pickupEntity instanceof Player) {
            final Player pickupPlayer = (Player) pickupEntity;
            final String playerID = PlayerConverter.getID(pickupPlayer);
            if (pickupPlayer.getUniqueId().equals(ownerUUID)) {
                if (checkConditions(playerID)) {
                    getCountingData(playerID).progress(item.getItemStack().getAmount());
                    completeIfDoneOrNotify(playerID);
                    entityPlayerMap.remove(item);
                } else {
                    event.setCancelled(true);
                }
            } else {
                failObjective(ownerUUID.toString());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityPickupItem(final InventoryPickupItemEvent event) {
        final Item item = event.getItem();
        if (!entityPlayerMap.containsKey(item)) {
            return;
        }

        if (isProtected) {
            event.setCancelled(true);
            return;
        }
        failObjective(entityPlayerMap.get(event.getItem()).toString());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        for (final Map.Entry<Entity, UUID> items : entityPlayerMap.entrySet()) {
            if (isPrivate && !event.getPlayer().getUniqueId().equals(items.getValue())) {
                hider.hideEntity(event.getPlayer(), items.getKey());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMergingItem(final ItemMergeEvent event) {
        if (!entityPlayerMap.containsKey(event.getEntity())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDespawn(final ItemDespawnEvent event) {
        if (!entityPlayerMap.containsKey(event.getEntity())) {
            return;
        }
        if (isProtected) {
            event.setCancelled(true);
            return;
        }
        failObjective(entityPlayerMap.get(event.getEntity()).toString());
    }

    @Override
    public void stop() {
        final CollectItemObjective instance = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                HandlerList.unregisterAll(instance);
                entityPlayerMap.clear();
            }
        }.runTask(BetonQuest.getInstance());
    }

    @Override
    public void stop(final String playerID) {
        if (!entityPlayerMap.containsValue(UUID.fromString(playerID))) {
            return;
        }
        for (final Map.Entry<Entity, UUID> items : entityPlayerMap.entrySet()) {
            final UUID ownerUUID = items.getValue();
            if (ownerUUID.equals(UUID.fromString(playerID))) {
                final Entity item = items.getKey();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        item.remove();
                        entityPlayerMap.values().remove(ownerUUID);
                    }
                }.runTask(BetonQuest.getInstance());
            }
        }
    }

    private void failObjective(final String playerID) {
        if (failEvents == null) {
            return;
        }
        for (final EventID event : failEvents) {
            BetonQuest.event(playerID, event);
        }
        BetonQuest.getInstance().getPlayerData(playerID).removeRawObjective((ObjectiveID) instruction.getID());
        cancelObjectiveForPlayer(playerID);
    }
}
