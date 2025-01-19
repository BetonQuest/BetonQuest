package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.compatibility.holograms.lines.AbstractLine;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Location;

import java.util.List;

/**
 * Wrapper class for {@link BetonHologram} that stores data parsed from hologram configuration.
 *
 * @param holograms     A list of actual hologram
 * @param interval      Interval in ticks that lie between updates to the visibility and content
 * @param staticContent Indicates whether the displayed content of the hologram is changing after a while.
 *                      HolographicDisplays variables are not updated BetonQuest, it does not make a hologram flexible.
 *                      However, content updates such as refreshing the top list do.
 *                      <p>
 *                      If <code>true</code>, {@link HologramWrapper#updateContent()} will end instantly to not cause
 *                      unneeded load.
 * @param conditionList List of all specified conditions. Hologram will only be visible if all conditions are met. If
 *                      none are specified, the hologram will be visible at all times.
 *                      <p>
 *                      If empty, {@link HologramWrapper#updateVisibility()} will end instantly to not cause
 *                      unneeded load.
 * @param cleanedLines  List of validated lines. Used by {@link #updateContent()} to update content without
 *                      revalidating content and dealing with potential errors.
 * @param questPackage  {@link QuestPackage} in which the hologram is specified in.
 * @param varMaxRange   The maximum range in which the hologram is visible.
 *                      {@link VariableNumber} represents this range.
 */
public record HologramWrapper(int interval, List<BetonHologram> holograms, boolean staticContent,
                              ConditionID[] conditionList,
                              List<AbstractLine> cleanedLines, QuestPackage questPackage,
                              VariableNumber varMaxRange) {
    /**
     * Checks whether all conditions are met by a players and displays or hides the hologram.
     */
    public void updateVisibility() {
        final int maxRange = varMaxRange.getInt(null);
        if (conditionList.length == 0 && maxRange <= 0) {
            for (final BetonHologram hologram : holograms) {
                hologram.showAll();
            }
            return;
        }

        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            updateVisibilityForPlayer(onlineProfile);
        }
    }

    /**
     * Update the visibility for a particular player
     *
     * @param profile The online player's profile
     */
    public void updateVisibilityForPlayer(final OnlineProfile profile) {
        final boolean conditionsMet = BetonQuest.conditions(profile, conditionList);

        for (final BetonHologram hologram : holograms) {
            final boolean playerOutOfRange = isPlayerOutOfRange(profile, hologram);

            if (conditionsMet && !playerOutOfRange) {
                hologram.show(profile.getPlayer());
            } else {
                hologram.hide(profile.getPlayer());
            }
        }
    }

    /**
     * Checks if the player is out of range from the specified hologram.
     *
     * @param profile  The online profile of the player
     * @param hologram The hologram to check the distance from
     * @return {@code true} if the player is out of range, {@code false} otherwise
     */
    public boolean isPlayerOutOfRange(final OnlineProfile profile, final BetonHologram hologram) {
        final int maxRange = varMaxRange.getInt(profile);
        if (maxRange > 0) {
            final Location playerLocation = profile.getPlayer().getLocation();
            final Location hologramLocation = hologram.getLocation();

            if (!playerLocation.getWorld().equals(hologramLocation.getWorld())) {
                return true;
            }
            final double distanceSquared = playerLocation.distanceSquared(hologramLocation);
            final double maxRangeSquared = maxRange * maxRange;

            return distanceSquared > maxRangeSquared;
        }
        return false;
    }

    /**
     * Fills the hologram with content. Called after a hologram is first created or if plugin is reloaded.
     */
    public void initialiseContent() {
        for (final BetonHologram betonHologram : holograms) {
            betonHologram.clear();
        }
        final int length = cleanedLines.stream().mapToInt(AbstractLine::getLinesAdded).sum();
        for (final BetonHologram betonHologram : holograms) {
            betonHologram.createLines(0, length);
        }
        int index = 0;
        for (final AbstractLine line : cleanedLines) {
            final int finalIndex = index;
            for (final BetonHologram hologram : holograms) {
                line.setLine(hologram, finalIndex);
            }
            index += line.getLinesAdded();
        }
    }

    /**
     * Updates the content if necessary.
     */
    public void updateContent() {
        if (staticContent) {
            return;
        }

        int index = 0;
        for (final AbstractLine line : cleanedLines) {
            if (line.isNotStaticText()) {
                final int finalIndex = index;
                for (final BetonHologram hologram : holograms) {
                    line.setLine(hologram, finalIndex);
                }
            }
            index += line.getLinesAdded();
        }
    }
}
