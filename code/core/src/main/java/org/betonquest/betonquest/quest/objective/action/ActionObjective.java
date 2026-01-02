package org.betonquest.betonquest.quest.objective.action;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Player has to click on a block (or air). Left click, right click and any one of
 * them is supported.
 */
public class ActionObjective extends DefaultObjective {

    /**
     * The key for the location property.
     */
    public static final String PROPERTY_LOCATION = "location";

    /**
     * The action to check for.
     */
    private final Argument<Click> action;

    /**
     * The selector to check for the block or an empty optional if any block is allowed.
     */
    private final Argument<Optional<BlockSelector>> selector;

    /**
     * If the block should be checked for exact match.
     */
    private final FlagArgument<Boolean> exactMatch;

    /**
     * The location where the player has to click.
     */
    @Nullable
    private final Argument<Location> loc;

    /**
     * The range of the location.
     */
    private final Argument<Number> range;

    /**
     * If the event should be cancelled.
     */
    private final FlagArgument<Boolean> cancel;

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
    public ActionObjective(final Instruction instruction, final Argument<Click> action,
                           final Argument<Optional<BlockSelector>> selector, final FlagArgument<Boolean> exactMatch,
                           @Nullable final Argument<Location> loc, final Argument<Number> range,
                           final FlagArgument<Boolean> cancel, @Nullable final EquipmentSlot slot) throws QuestException {
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
     * @param event         the event
     * @param onlineProfile the profile of the player
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    public void onInteract(final PlayerInteractEvent event, final OnlineProfile onlineProfile) {
        qeHandler.handle(() -> {
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
                if (cancel.getValue(onlineProfile).orElse(false)) {
                    event.setCancelled(true);
                }
                completeObjective(onlineProfile);
            }
        });
    }

    private boolean checkBlock(final Profile profile, @Nullable final Block clickedBlock, final BlockFace blockFace) throws QuestException {
        final Optional<BlockSelector> blockSelector = selector.getValue(profile);
        if (blockSelector.isEmpty()) {
            return true;
        }
        if (clickedBlock == null) {
            return false;
        }
        final BlockSelector selectorValue = blockSelector.get();
        return (selectorValue.match(Material.WATER) || selectorValue.match(Material.LAVA))
                && selectorValue.match(clickedBlock.getRelative(blockFace), exactMatch.getValue(profile).orElse(false))
                || selectorValue.match(clickedBlock, exactMatch.getValue(profile).orElse(false));
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
