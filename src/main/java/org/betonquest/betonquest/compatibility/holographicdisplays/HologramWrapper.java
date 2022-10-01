package org.betonquest.betonquest.compatibility.holographicdisplays;

import lombok.CustomLog;
import lombok.Getter;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.compatibility.holographicdisplays.lines.AbstractLine;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Wrapper class for {@link Hologram} that stores data parsed from hologram configuration inside <code>custom.yml</code>.
 */
@SuppressWarnings("PMD.CommentRequired")
@Getter
@CustomLog
public class HologramWrapper {

    /**
     * Actual hologram
     */
    private final Hologram hologram;

    /**
     * Interval in ticks that lie between updates to the visibility and content
     */
    private final int interval;

    /**
     * Indicates whether the displayed content of the hologram is changing after a while. HolographicDisplays variables
     * are not updated BetonQuest, it does not make a hologram flexible. However, vontent updates such as refreshing the
     * top list do.
     * <p>
     * If <code>true</code>, {@link HologramWrapper#updateContent()} will end instantly to not cause unneeded load.
     */
    private final boolean staticContent;

    /**
     * List of all specified conditions. Hologram will only be visible if all conditions are met. If none are specified,
     * the hologram will be visible at all times.
     * <p>
     * If empty, {@link HologramWrapper#updateVisibility()} will end instantly to not cause unneeded load.
     */
    private final ConditionID[] conditionList;

    /**
     * List of validated lines. Used by {@link #updateContent()} to update content without revalidating content and
     * dealing with potential errors.
     */
    private final List<AbstractLine> cleanedLines;

    /**
     * Name of hologram from <code>custom.yml</code>
     */
    private final String identifier;

    /**
     * {@link QuestPackage} in which the hologram is specified in.
     */
    private final QuestPackage questPackage;

    /**
     * Creates new instance of hologram.
     *
     * @param interval      Duration in ticks between updates of visibility and content
     * @param hologram      Actual hologram from HolographicDisplays API
     * @param staticContent Whether the content updates on its own
     * @param conditionList List of conditions to see the hologram
     * @param cleanedLines  List of error-free lines that can be safely displayed
     * @param identifier    Name of hologram
     * @param questPackage  Package in which hologram is specified in
     */
    public HologramWrapper(final int interval, final Hologram hologram, final boolean staticContent, final ConditionID[] conditionList, final List<AbstractLine> cleanedLines, final String identifier, final QuestPackage questPackage) {
        this.interval = interval;
        this.hologram = hologram;
        this.staticContent = staticContent;
        this.conditionList = conditionList.clone();
        this.cleanedLines = cleanedLines;
        this.identifier = identifier;
        this.questPackage = questPackage;
    }

    /**
     * Checks whether all conditions are met by a players and displays or hides the hologram.
     */
    public void updateVisibility() {
        if (conditionList.length == 0) {
            hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
            return;
        }

        for (final Player player : Bukkit.getOnlinePlayers()) {
            final String playerID = PlayerConverter.getID(player);
            if (BetonQuest.conditions(playerID, conditionList)) {
                hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
            } else {
                hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN);
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
