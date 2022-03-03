package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.BlockSelector;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

import java.util.logging.Level;

/**
 * Player has to click on block (or air). Left click, right click and any one of
 * them is supported.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CommentRequired"})
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
        range = instruction.getVarNum(stringRange == null ? "0" : stringRange);
        cancel = instruction.hasArgument("cancel");
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(final PlayerInteractEvent event) {
        if (event.getHand() != null && event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID) || !action.match(event.getAction())) {
            return;
        }

        final Block clickedBlock = event.getClickedBlock();
        if (loc != null) {
            final Location current = clickedBlock == null ? event.getPlayer().getLocation() : clickedBlock.getLocation();
            try {
                final Location location = loc.getLocation(playerID);
                final double pRange = range.getDouble(playerID);
                if (!location.getWorld().equals(current.getWorld()) || current.distance(location) > pRange) {
                    return;
                }
            } catch (final QuestRuntimeException e) {
                LogUtils.getLogger().log(Level.WARNING, "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage());
                LogUtils.logThrowable(e);
                return;
            }
        }

        if ((selector == null || clickedBlock != null && (checkBlock(clickedBlock, event.getBlockFace()))) && checkConditions(playerID)) {
            if (cancel) {
                event.setCancelled(true);
            }
            completeObjective(playerID);
        }
    }

    private boolean checkBlock(final Block clickedBlock, final BlockFace blockFace) {
        return (selector.match(Material.WATER) || selector.match(Material.LAVA))
                && selector.match(clickedBlock.getRelative(blockFace), exactMatch) || selector.match(clickedBlock, exactMatch);
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
                LogUtils.getLogger().log(Level.WARNING, "Error while getting location property in '" + instruction.getID() + "' objective: "
                        + e.getMessage());
                LogUtils.logThrowable(e);
                return "";
            }
            return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
        }
        return "";
    }

    public enum Click {
        RIGHT, LEFT, ANY;

        public boolean match(final Action action) {
            if (action == Action.PHYSICAL) {
                return false;
            }
            return this == ANY
                    || this == RIGHT && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                    || this == LEFT && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK);
        }
    }

}
