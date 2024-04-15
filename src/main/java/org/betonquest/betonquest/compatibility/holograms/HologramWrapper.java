package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.holograms.lines.AbstractLine;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;

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
 */
public record HologramWrapper(int interval, List<BetonHologram> holograms, boolean staticContent,
                              ConditionID[] conditionList,
                              List<AbstractLine> cleanedLines, QuestPackage questPackage) {
    /**
     * Checks whether all conditions are met by a players and displays or hides the hologram.
     */
    public void updateVisibility() {
        if (conditionList.length == 0) {
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
        if (BetonQuest.conditions(profile, conditionList)) {
            for (final BetonHologram hologram : holograms) {
                hologram.show(profile.getPlayer());
            }
        } else {
            for (final BetonHologram hologram : holograms) {
                hologram.hide(profile.getPlayer());
            }
        }
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
