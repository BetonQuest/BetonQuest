package org.betonquest.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.compatibility.protocollib.hider.EntityHider;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"PMD.CommentRequired", "PMD.FieldNamingConventions", "PMD.UnusedNullCheckInEquals", "PMD.CollapsibleIfStatements"})
public class DropItemEvent extends QuestEvent implements Listener {

    private final Instruction.Item[] questItems;
    private final CompoundLocation location;
    private final boolean isPrivate;
    private final boolean isIndestructible;

    private final Map<Entity, UUID> entityPlayerMap = new HashMap<>();
    private final List<Entity> IndestructibleItem = new ArrayList<>();
    private final EntityHider hider = new EntityHider(BetonQuest.getInstance(), EntityHider.Policy.BLACKLIST);

    public DropItemEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        Bukkit.getPluginManager().registerEvents(BukkitEvent(), BetonQuest.getInstance());

        questItems = instruction.getItemList();
        location = instruction.getLocation();
        isPrivate = instruction.hasArgument("private");
        isIndestructible = instruction.hasArgument("indestructible");

        if (isPrivate) {
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
                throw new InstructionParseException("You Need ProtocolLib installed to Use Private Drop Item");
            }
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        for (final Instruction.Item item : questItems) {
            final QuestItem questItem = item.getItem();
            final VariableNumber amount = item.getAmount();

            final ItemStack generateItem = questItem.generate(amount.getInt(playerID), playerID);
            final Player player = PlayerConverter.getPlayer(playerID);
            final Location loc = location.getLocation(playerID);
            final Entity droppedItem = loc.getWorld().dropItem(loc, generateItem);

            if (isPrivate) {
                entityPlayerMap.put(droppedItem, player.getUniqueId());
                for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayer.equals(player)) {
                        hider.hideEntity(onlinePlayer, droppedItem);
                    }
                }
            }
            if (isIndestructible) {
                IndestructibleItem.add(droppedItem);
            }
        }
        return null;
    }

    @SuppressWarnings({"PMD.MethodNamingConventions", "PMD.NPathComplexity", "PMD.AvoidDuplicateLiterals"})
    private Listener BukkitEvent() {
        return new Listener() {

            @SuppressWarnings("PMD.ConfusingTernary")
            @EventHandler(ignoreCancelled = true)
            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            public void onPickupItem(final EntityPickupItemEvent event) {
                for (final Entity item : entityPlayerMap.keySet()) {
                    if (item != null && event.getItem().equals(item)) {
                        if (!event.getEntity().getUniqueId().equals(entityPlayerMap.get(item))) {
                            event.setCancelled(true);
                        } else {
                            entityPlayerMap.remove(item);
                        }
                    }
                }
                IndestructibleItem.removeIf(item -> item != null && event.getItem().equals(item));
            }

            @EventHandler(ignoreCancelled = true)
            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            public void onPlayerJoin(final PlayerJoinEvent event) {
                for (final Entity item : entityPlayerMap.keySet()) {
                    if (item != null && !event.getPlayer().getUniqueId().equals(entityPlayerMap.get(item))) {
                        hider.hideEntity(event.getPlayer(), item);
                    }
                }
            }

            @EventHandler(ignoreCancelled = true)
            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            public void onMergingItem(final ItemMergeEvent event) {
                for (final Entity item : entityPlayerMap.keySet()) {
                    if (item != null && event.getEntity().equals(item)) {
                        event.setCancelled(true);
                    }
                }
                for (final Entity item : IndestructibleItem) {
                    if (item != null && event.getEntity().equals(item)) {
                        event.setCancelled(true);
                    }
                }
            }

            @EventHandler(ignoreCancelled = true)
            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            public void onItemDespawn(final ItemDespawnEvent event) {
                for (final Entity item : IndestructibleItem) {
                    if (item != null
                            && event.getEntity().equals(item)
                            && event.getEntity().getType().equals(EntityType.DROPPED_ITEM)) {
                        event.setCancelled(true);
                    }
                }
                for (final Entity item : entityPlayerMap.keySet()) {
                    if (item != null && event.getEntity().equals(item)) {
                        entityPlayerMap.remove(item);
                    }
                }
            }

            @EventHandler(ignoreCancelled = true)
            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            public void onItemDamage(final EntityDamageEvent event) {
                IndestructibleItem.removeIf(item -> item != null
                        && event.getEntity().equals(item)
                        && event.getEntityType().equals(EntityType.DROPPED_ITEM));
                for (final Entity item : entityPlayerMap.keySet()) {
                    if (item != null
                            && event.getEntity().getType().equals(EntityType.DROPPED_ITEM)) {
                        entityPlayerMap.remove(item);
                    }
                }
            }
        };
    }
}
