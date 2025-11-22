package org.betonquest.betonquest.quest.objective.action;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

/**
 * Player has to click on a block (or air). Left click, right click and any one of
 * them is supported.
 */
public class ActionObjective extends Objective implements Listener {
    /**
     * The key for the location property.
     */
    public static final String PROPERTY_LOCATION = "location";

    /**
     * The action to check for.
     */
    private final Variable<Click> action;

    /**
     * The selector to check for the block.
     */
    @Nullable
    private final Variable<BlockSelector> selector;

    /**
     * If the block should be checked for exact match.
     */
    private final boolean exactMatch;

    /**
     * The location where the player has to click.
     */
    @Nullable
    private final Variable<Location> loc;

    /**
     * The range of the location.
     */
    private final Variable<Number> range;

    /**
     * If the event should be cancelled.
     */
    private final boolean cancel;

    /**
     * The equipment slot to check for the action.
     */
    @Nullable
    private final EquipmentSlot slot;

    /**
     * Creates a new instance of the ActionObjective.
     *
     * @param instruction the instruction
     * @param action      the action to check for
     * @param selector    the selector to check for the block
     * @param exactMatch  if the block should be checked for exact match
     * @param loc         the location where the player has to click
     * @param range       the range of the location
     * @param cancel      if the event should be canceled
     * @param slot        the equipment slot to check for the action
     * @throws QuestException if an error occurs while creating the objective
     */
    public ActionObjective(final Instruction instruction, final Variable<Click> action,
                           @Nullable final Variable<BlockSelector> selector, final boolean exactMatch,
                           @Nullable final Variable<Location> loc, final Variable<Number> range, final boolean cancel,
                           @Nullable final EquipmentSlot slot) throws QuestException {
        super(instruction);
        this.action = action;
        this.selector = selector;
        this.exactMatch = exactMatch;
        this.loc = loc;
        this.range = range;
        this.cancel = cancel;
        this.slot = slot;
    }

    /**
     * Checks if the player clicked on the block.
     *
     * @param event the event
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(final PlayerInteractEvent event) {
        qeHandler.handle(() -> {
            final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
            if (!containsPlayer(onlineProfile) || !action.getValue(onlineProfile).match(event.getAction()) || slot != null && slot != event.getHand()) {
                return;
            }

            final Block clickedBlock = event.getClickedBlock();
            if (loc != null) {
                final Location current = clickedBlock == null ? event.getPlayer().getLocation() : clickedBlock.getLocation();
                final Location location = loc.getValue(onlineProfile);
                final double pRange = range.getValue(onlineProfile).doubleValue();
                if (!location.getWorld().equals(current.getWorld()) || current.distance(location) > pRange) {
                    return;
                }
            }

            if (checkBlock(onlineProfile, clickedBlock, event.getBlockFace()) && checkConditions(onlineProfile)) {
                if (cancel) {
                    event.setCancelled(true);
                }
                completeObjective(onlineProfile);
            }
        });
    }

    private boolean checkBlock(final Profile profile, @Nullable final Block clickedBlock, final BlockFace blockFace) throws QuestException {
        if (selector == null) {
            return true;
        }
        if (clickedBlock == null) {
            return false;
        }
        final BlockSelector blockSelector = selector.getValue(profile);
        return (blockSelector.match(Material.WATER) || blockSelector.match(Material.LAVA))
                && blockSelector.match(clickedBlock.getRelative(blockFace), exactMatch)
                || blockSelector.match(clickedBlock, exactMatch);
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) throws QuestException {
        if (PROPERTY_LOCATION.equalsIgnoreCase(name)) {
            if (loc == null) {
                return "";
            }
            final Location location = loc.getValue(profile);
            return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
        }
        return "";
    }
}
