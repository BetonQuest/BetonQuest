package org.betonquest.betonquest.compatibility.holographicdisplays;

import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.holographicdisplays.lines.AbstractLine;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;

import java.util.List;

/**
 * Wrapper class for {@link Hologram} that stores data parsed from hologram configuration inside <code>custom.yml</code>.
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
public record HologramWrapper(int interval, Hologram hologram, boolean staticContent, ConditionID[] conditionList,
                              List<AbstractLine> cleanedLines, String identifier, QuestPackage questPackage) {
    /**
     * Checks whether all conditions are met by a players and displays or hides the hologram.
     */
    public void updateVisibility() {
        if (conditionList.length == 0) {
            hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
            return;
        }

        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            if (BetonQuest.conditions(onlineProfile, conditionList)) {
                hologram.getVisibilitySettings().setIndividualVisibility(onlineProfile.getPlayer(), VisibilitySettings.Visibility.VISIBLE);
            } else {
                hologram.getVisibilitySettings().setIndividualVisibility(onlineProfile.getPlayer(), VisibilitySettings.Visibility.HIDDEN);
            }
        }
    }

    /**
     * Updates the content if necessary. On first load after the server start this fills holograms with static content,
     * but is ignored by them afterwards.
     */
    public void updateContent() {
        if (staticContent && hologram.getLines().size() > 0) { //Allow first initializing of static holograms
            return;
        }

        hologram.getLines().clear();
        for (final AbstractLine line : cleanedLines) {
            line.addLine(hologram);
        }
    }
}
