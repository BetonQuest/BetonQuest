package org.betonquest.betonquest.quest.event.glow;

import fr.skytasul.glowingentities.GlowingBlocks;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows for setting glowing blocks using an event.
 */
public class GlowEvent implements Event {
    /**
     * Location of the block to glow.
     */
    private final CompoundLocation location;

    /**
     * Optional second location for a cuboid region between the locations.
     */
    private final CompoundLocation region;

    /**
     * Optional color for the glow effect.
     */
    private final ChatColor color;

    /**
     * GlowingBlocks instance to allow for glowing blocks.
     */
    private final GlowingBlocks glowingBlocks;

    /**
     * Type of the GlowEvent [add or remove a glow].
     */
    private final boolean type;

    /**
     * @param location      Location parameter of the block to glow.
     * @param region        Optional location parameter to make a cuboid region between the locations glow.
     * @param glowingBlocks Instance of GlowingBlocks to allow blocks to glow.
     * @param color         Color of the Block to glow.
     * @param type          Whether to add glowing blocks or remove them.
     */
    public GlowEvent(final CompoundLocation location, final CompoundLocation region, final GlowingBlocks glowingBlocks, final ChatColor color, final boolean type) {
        this.location = location;
        this.region = region;
        this.glowingBlocks = glowingBlocks;
        this.color = color;
        this.type = type;
    }

    /**
     * @param profile the {@link Profile} the event is executed for.
     * @throws QuestRuntimeException if Location can't be converted or Block can't be made glowing.
     */
    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getPlayer().getPlayer() != null ? profile.getPlayer().getPlayer() : null;
        if (player == null) {
            return;
        }
        for (final Block block : getBlocks(profile)) {
            try {
                if (type) {
                    glowingBlocks.setGlowing(block, player, color);
                } else {
                    glowingBlocks.unsetGlowing(block, player);
                }
            } catch (ReflectiveOperationException ex) {
                throw new QuestRuntimeException(ex);
            }
        }
    }

    /**
     * @param profile Player profile.
     * @return List of Blocks containing either a single block or every block between location and region, if region is set.
     * @throws QuestRuntimeException if Location can't be converted.
     */
    private List<Block> getBlocks(final Profile profile) throws QuestRuntimeException {
        final List<Block> blockList = new ArrayList<>();
        if (region == null) {
            blockList.add(location.getLocation(profile).getBlock());
            return blockList;
        }
        for (int x = Integer.min(location.getLocation(profile).getBlockX(), region.getLocation(profile).getBlockX()); x <= Integer.max(location.getLocation(profile).getBlockX(), region.getLocation(profile).getBlockX()); x++) {
            for (int y = Integer.min(location.getLocation(profile).getBlockY(), region.getLocation(profile).getBlockY()); y <= Integer.max(location.getLocation(profile).getBlockY(), region.getLocation(profile).getBlockY()); y++) {
                for (int z = Integer.min(location.getLocation(profile).getBlockZ(), region.getLocation(profile).getBlockZ()); z <= Integer.max(location.getLocation(profile).getBlockZ(), region.getLocation(profile).getBlockZ()); z++) {
                    blockList.add(new Location(location.getLocation(profile).getWorld(), x, y, z).getBlock());
                }
            }
        }
        return blockList;
    }
}
