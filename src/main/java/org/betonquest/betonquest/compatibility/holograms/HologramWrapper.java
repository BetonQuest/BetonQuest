package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.holograms.lines.AbstractLine;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;

import java.util.List;

@SuppressWarnings("PMD.CommentSize")
/**
 * Wrapper class for {@link BetonHologram} that stores data parsed from hologram configuration inside <code>custom.yml</code>.
 *
 * @param hologram      Actual hologram
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
 * @param identifier    Name of hologram from <code>custom.yml</code>
 * @param questPackage  {@link QuestPackage} in which the hologram is specified in.
 */
public record HologramWrapper(int interval, BetonHologram hologram, boolean staticContent, ConditionID[] conditionList,
                              List<AbstractLine> cleanedLines, String identifier, QuestPackage questPackage) {
    /**
     * Checks whether all conditions are met by a players and displays or hides the hologram.
     */
    public void updateVisibility() {
        if (conditionList.length == 0) {
            hologram.showAll();
            return;
        }

        PlayerConverter.getOnlineProfiles().forEach(this::updateVisibilityForPlayer);
    }

    /**
     * Update the visibility for a particular player
     *
     * @param profile The online player's profile
     */
    public void updateVisibilityForPlayer(final OnlineProfile profile) {
        if (BetonQuest.conditions(profile, conditionList)) {
            hologram.show(profile.getOnlinePlayer());
        } else {
            hologram.hide(profile.getOnlinePlayer());
        }
    }

    /**
     * Fills the hologram with content. Called after a hologram is first created or if plugin is reloaded.
     */
    public void initialiseContent() {
        hologram.clear();
        int length = 0;
        for (final AbstractLine line : cleanedLines) {
            length += line.getLinesAdded();
        }
        hologram.createLines(0, length);
        int index = 0;
        for (final AbstractLine line : cleanedLines) {
            line.setLine(hologram, index);
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
                line.setLine(hologram, index);
            }
            index += line.getLinesAdded();
        }
    }
}
