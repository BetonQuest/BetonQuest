package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

/**
 * The player must step on the pressure plate
 */
@SuppressWarnings("PMD.CommentRequired")
public class StepObjective extends Objective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(StepObjective.class);

    /**
     * The key for the location property.
     */
    private static final String LOCATION_KEY = "location";

    @Nullable
    private static final BlockSelector PRESSURE_PLATE_SELECTOR = getPressurePlateSelector();

    private final VariableLocation loc;

    public StepObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        loc = instruction.get(VariableLocation::new);
    }

    @Nullable
    private static BlockSelector getPressurePlateSelector() {
        try {
            return new BlockSelector(".*_PRESSURE_PLATE");
        } catch (final QuestException e) {
            LOG.reportException(e);
        }
        return null;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @EventHandler(ignoreCancelled = true)
    public void onStep(final PlayerInteractEvent event) {
        // Only fire the event for the main hand to avoid that the event is triggered two times.
        if (event.getHand() == EquipmentSlot.OFF_HAND && event.getHand() != null) {
            return; // off hand packet, ignore.
        }
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        try {
            final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
            final Block block = loc.getValue(onlineProfile).getBlock();
            if (!clickedBlock.equals(block)) {
                return;
            }

            if (PRESSURE_PLATE_SELECTOR == null || !PRESSURE_PLATE_SELECTOR.match(block.getBlockData().getMaterial())) {
                return;
            }
            if (!containsPlayer(onlineProfile)) {
                return;
            }
            // player stepped on the pressure plate
            if (checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
        } catch (final QuestException e) {
            LOG.warn(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
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
    public String getProperty(final String name, final Profile profile) {
        if (LOCATION_KEY.equalsIgnoreCase(name)) {
            final Block block;
            try {
                block = loc.getValue(profile).getBlock();
            } catch (final QuestException e) {
                LOG.warn(instruction.getPackage(), "Error while getting location property in '" + instruction.getID() + "' objective: "
                        + e.getMessage(), e);
                return "";
            }
            return "X: " + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ();
        }
        return "";
    }
}
