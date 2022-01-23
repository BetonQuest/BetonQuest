package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Player has to click on block (or air). Left click, right click and any one of
 * them is supported.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CommentRequired"})
@CustomLog
public class ActionObjective extends Objective implements Listener {

    private final Click action;
    private final BlockSelector selector;
    private final boolean exactMatch;
    private final CompoundLocation loc;
    private final VariableNumber range;
    private final boolean cancel;

    public ActionObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        action = instruction.getEnum(Click.class);
        if ("any".equalsIgnoreCase(instruction.next())) {
            selector = null;
        } else {
            selector = instruction.getBlockSelector(instruction.current());
        }
        exactMatch = instruction.hasArgument("exactMatch");
        loc = instruction.getLocation(instruction.getOptional("loc"));
        final String stringRange = instruction.getOptional("range");
        range = instruction.getVarNum(stringRange == null ? "1" : stringRange);
        cancel = instruction.hasArgument("cancel");
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(final PlayerInteractEvent event) {
        // Only fire the event for the main hand to avoid that the event is triggered two times.
        if (event.getHand() == EquipmentSlot.OFF_HAND && event.getHand() != null) {
            return; // off hand packet, ignore.
        }
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID)) {
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        if (loc != null && clickedBlock != null) {
            try {
                final Location location = loc.getLocation(playerID);
                final double pRange = range.getDouble(playerID);
                if (!location.getWorld().equals(clickedBlock.getWorld())
                        || clickedBlock.getLocation().distance(location) > pRange) {
                    return;
                }
            } catch (final QuestRuntimeException e) {
                LOG.warning(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
            }
        }
        if (selector == null) {
            switch (action) {
                case RIGHT:
                    if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)
                            || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && checkConditions(playerID)) {
                        if (cancel) {
                            event.setCancelled(true);
                        }
                        completeObjective(playerID);
                    }
                    break;
                case LEFT:
                    if ((event.getAction().equals(Action.LEFT_CLICK_AIR)
                            || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) && checkConditions(playerID)) {
                        if (cancel) {
                            event.setCancelled(true);
                        }
                        completeObjective(playerID);
                    }
                    break;
                case ANY:
                default:
                    if ((event.getAction().equals(Action.LEFT_CLICK_AIR)
                            || event.getAction().equals(Action.LEFT_CLICK_BLOCK)
                            || event.getAction().equals(Action.RIGHT_CLICK_AIR)
                            || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && checkConditions(playerID)) {
                        if (cancel) {
                            event.setCancelled(true);
                        }
                        completeObjective(playerID);
                    }
                    break;
            }
        } else {
            final Action actionEnum;
            switch (action) {
                case RIGHT:
                    actionEnum = Action.RIGHT_CLICK_BLOCK;
                    break;
                case LEFT:
                    actionEnum = Action.LEFT_CLICK_BLOCK;
                    break;
                case ANY:
                default:
                    actionEnum = null;
                    break;
            }
            if ((actionEnum == null && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                    || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) || event.getAction().equals(actionEnum))
                    && clickedBlock != null && ((selector.match(Material.FIRE) || selector.match(Material.LAVA) || selector.match(Material.WATER))
                    && selector.match(clickedBlock.getRelative(event.getBlockFace()), exactMatch)
                    || selector.match(clickedBlock, exactMatch)) && checkConditions(playerID)) {
                if (cancel) {
                    event.setCancelled(true);
                }
                completeObjective(playerID);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if ("location".equalsIgnoreCase(name)) {
            if (loc == null) {
                return "";
            }
            final Location location;
            try {
                location = loc.getLocation(playerID);
            } catch (final QuestRuntimeException e) {
                LOG.warning(instruction.getPackage(), "Error while getting location property in '" + instruction.getID() + "' objective: "
                        + e.getMessage(), e);
                return "";
            }
            return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
        }
        return "";
    }

    public enum Click {
        RIGHT, LEFT, ANY
    }

}
