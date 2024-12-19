package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
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
import org.jetbrains.annotations.Nullable;

import static org.bukkit.event.block.Action.LEFT_CLICK_AIR;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

/**
 * Player has to click on a block (or air). Left click, right click and any one of
 * them is supported.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CommentRequired"})
public class ActionObjective extends Objective implements Listener {
    /**
     * The key for the location property.
     */
    private static final String LOCATION_PROPERTY = "location";

    /**
     * The key for any action.
     */
    private static final String ANY = "any";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final Click action;

    @Nullable
    private final BlockSelector selector;

    private final boolean exactMatch;

    @Nullable
    private final VariableLocation loc;

    private final VariableNumber range;

    private final boolean cancel;

    @Nullable
    private final EquipmentSlot slot;

    public ActionObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

        action = instruction.getEnum(Click.class);
        if (ANY.equalsIgnoreCase(instruction.next())) {
            selector = null;
        } else {
            selector = instruction.getBlockSelector(instruction.current());
        }
        exactMatch = instruction.hasArgument("exactMatch");
        loc = instruction.getLocation(instruction.getOptional("loc"));
        range = instruction.getVarNum(instruction.getOptional("range", "0"));
        cancel = instruction.hasArgument("cancel");
        final String handString = instruction.getOptional("hand");
        if (handString == null || handString.equalsIgnoreCase(EquipmentSlot.HAND.toString())) {
            slot = EquipmentSlot.HAND;
        } else if (handString.equalsIgnoreCase(EquipmentSlot.OFF_HAND.toString())) {
            slot = EquipmentSlot.OFF_HAND;
        } else if (ANY.equalsIgnoreCase(handString)) {
            slot = null;
        } else {
            throw new InstructionParseException("Invalid hand value: " + handString);
        }
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(final PlayerInteractEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(onlineProfile) || !action.match(event.getAction()) || slot != null && slot != event.getHand()) {
            return;
        }

        final Block clickedBlock = event.getClickedBlock();
        if (loc != null) {
            final Location current = clickedBlock == null ? event.getPlayer().getLocation() : clickedBlock.getLocation();
            try {
                final Location location = loc.getValue(onlineProfile);
                final double pRange = range.getValue(onlineProfile).doubleValue();
                if (!location.getWorld().equals(current.getWorld()) || current.distance(location) > pRange) {
                    return;
                }
            } catch (final QuestRuntimeException e) {
                log.warn(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
                return;
            }
        }

        if (checkBlock(clickedBlock, event.getBlockFace()) && checkConditions(onlineProfile)) {
            if (cancel) {
                event.setCancelled(true);
            }
            completeObjective(onlineProfile);
        }
    }

    private boolean checkBlock(@Nullable final Block clickedBlock, final BlockFace blockFace) {
        if (selector == null) {
            return true;
        }
        if (clickedBlock == null) {
            return false;
        }
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
    public String getProperty(final String name, final Profile profile) {
        if (LOCATION_PROPERTY.equalsIgnoreCase(name)) {
            if (loc == null) {
                return "";
            }
            final Location location;
            try {
                location = loc.getValue(profile);
            } catch (final QuestRuntimeException e) {
                log.warn(instruction.getPackage(), "Error while getting location property in '" + instruction.getID() + "' objective: "
                        + e.getMessage(), e);
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
            return this == ANY || this == RIGHT && (action == RIGHT_CLICK_AIR || action == RIGHT_CLICK_BLOCK)
                    || this == LEFT && (action == LEFT_CLICK_AIR || action == LEFT_CLICK_BLOCK);
        }
    }
}
