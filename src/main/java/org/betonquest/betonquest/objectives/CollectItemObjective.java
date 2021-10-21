package org.betonquest.betonquest.objectives;

import lombok.SneakyThrows;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.compatibility.protocollib.hider.EntityHider;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("PMD.CommentRequired")
public class CollectItemObjective extends CountingObjective implements Listener {

    private final Instruction.Item[] questItems;
    private final CompoundLocation location;
    private final boolean isProtected;
    private final boolean isPrivate;
    private final EventID[] failEvents;

    /**
     * Saves the entity of each dropped item alongside the player that is allowed to pick the item up.
     */
    private final Map<Entity, UUID> entityPlayerMap = new HashMap<>();

    private EntityHider hider;

    public CollectItemObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "item_to_collect");

        questItems = instruction.getItemList();

        final String location = instruction.getOptional("location");
        if (location == null) {
            throw new InstructionParseException("No drop location given!");
        } else {
            this.location = instruction.getLocation(location);
        }

        final String getProtected = instruction.getOptional("protected");
        this.isProtected = getProtected != null && getProtected.equals("true");

        final String getPrivate = instruction.getOptional("private");
        this.isPrivate = getPrivate != null && getPrivate.equals("true");

        failEvents = instruction.getList(instruction.getOptional("fail"), instruction::getEvent).toArray(new EventID[0]);

        if (this.isPrivate) {
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
                throw new InstructionParseException("You need to install ProtocolLib to use private item drops!");
            } else {
                hider = new EntityHider(BetonQuest.getInstance(), EntityHider.Policy.BLACKLIST);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @SneakyThrows
    @Override
    public void start(final String playerID) {
        for (final Instruction.Item item : questItems) {
            final Player player = PlayerConverter.getPlayer(playerID);
            final QuestItem questItem = item.getItem();
            final VariableNumber amount = item.getAmount();
            final int amountInt = amount.getInt(playerID);

            final ItemStack generateItem = questItem.generate(amountInt, playerID);
            final Location loc = location.getLocation(playerID);

            Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
                final Entity droppedItem = loc.getWorld().dropItem(loc, generateItem);
                entityPlayerMap.put(droppedItem, player.getUniqueId());
                if (isPrivate) {
                    for(final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        hider.hideEntity(onlinePlayer, droppedItem);
                    }
                }
            });
            targetAmount = amountInt;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupItem(final EntityPickupItemEvent event) {
        final Item item = event.getItem();
        if (!entityPlayerMap.containsKey(item)) {
            return;
        }

        final UUID ownerUUID = entityPlayerMap.get(item);
        final Entity pickupEntity = event.getEntity();
        if (pickupEntity instanceof Player) {
            final Player pickupPlayer = (Player) pickupEntity;
            final String playerID = PlayerConverter.getID(pickupPlayer);
            if (pickupPlayer.getUniqueId().equals(ownerUUID)) {
                if (containsPlayer(playerID) && checkConditions(playerID)) {
                    getCountingData(playerID).progress(item.getItemStack().getAmount());
                    completeIfDoneOrNotify(playerID);
                }
                entityPlayerMap.remove(item);
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        for (final Map.Entry<Entity, UUID> items : entityPlayerMap.entrySet()) {
            final Entity item = items.getKey();
            final UUID ownerUUID = items.getValue();
            if (isPrivate && !event.getPlayer().getUniqueId().equals(ownerUUID)) {
                hider.hideEntity(event.getPlayer(), item);
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
        for (final Map.Entry<Entity, UUID> items : entityPlayerMap.entrySet()) {
            final Entity item = items.getKey();
            final String playerID = items.getValue().toString();
            if (event.getEntity().equals(item)) {
                if (isProtected) {
                    event.setCancelled(true);
                } else {
                    failObjective(playerID);
                    cancelObjectiveForPlayer(playerID);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDamage(final EntityDamageEvent event) {
        for (final Map.Entry<Entity, UUID> items : entityPlayerMap.entrySet()) {
            final Entity item = items.getKey();
            final UUID ownerUUID = items.getValue();
            if (event.getEntity().getType() == EntityType.DROPPED_ITEM
                    && event.getEntity().equals(item)) {
                final String playerID = ownerUUID.toString();
                if (containsPlayer(playerID)) {
                    failObjective(playerID);
                    cancelObjectiveForPlayer(playerID);
                }
            }
        }
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void stop(final String playerID) {
        final Iterator<Map.Entry<Entity, UUID>> iterator = entityPlayerMap.entrySet().iterator();
        while (iterator.hasNext()) {
            entityPlayerMap.values().remove(UUID.fromString(playerID));
        }
    }

    private void failObjective(final String playerID) {
        if (failEvents == null) {
            return;
        }
        for (final EventID event : failEvents) {
            BetonQuest.event(playerID, event);
        }
    }
}
