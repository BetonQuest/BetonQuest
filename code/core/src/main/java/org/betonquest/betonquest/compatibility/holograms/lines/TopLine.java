package org.betonquest.betonquest.compatibility.holograms.lines;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.VariableComponent;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;

import static net.kyori.adventure.text.Component.text;

/**
 * Creates a new instance for TopLine.
 */
public class TopLine extends AbstractLine {

    /**
     * Name of point with package.
     */
    private final String category;

    /**
     * Direction in which scores are ordered.
     */
    private final TopXObject.OrderType orderType;

    /**
     * Formatted top line string to display.
     */
    private final VariableComponent formatted;

    /**
     * Storage for rank data.
     */
    private final TopXObject topXObject;

    /**
     * Creates a new instance of ItemLine. Automatically creates and stores {@link TopXObject} from received data.
     *
     * @param loggerFactory logger factory to use
     * @param category      name of point with package
     * @param orderType     direction of order
     * @param limit         maximum number of lines displayed
     * @param formatted     formatted top line string to display
     */
    public TopLine(final BetonQuestLoggerFactory loggerFactory, final String category, final TopXObject.OrderType orderType,
                   final int limit, final VariableComponent formatted) {
        super(false, limit);
        this.category = category;
        this.orderType = orderType;
        this.formatted = formatted;
        this.topXObject = new TopXObject(loggerFactory.create(TopXObject.class), limit, category, orderType);
    }

    /**
     * Updates the stored {@link TopXObject} and returns found entries as String-Array.
     * If retrieved lines are less than the limit, it will be filled with empty lines.
     *
     * @return Formatted lines ready for display on a hologram
     */
    public Component[] getLines() {
        topXObject.queryDB();

        final Component[] lines = new Component[linesAdded];
        for (int i = 0; i < linesAdded; i++) {
            if (i >= topXObject.getLineCount()) {
                lines[i] = Component.empty();
                continue;
            }
            final TopXLine line = topXObject.getEntries().get(i);
            lines[i] = formatted.resolve(
                    new VariableReplacement("place", text(i + 1)),
                    new VariableReplacement("name", text(line.playerName())),
                    new VariableReplacement("score", text(line.count()))
            );
        }
        return lines;
    }

    @Override
    public String toString() {
        return "TopLine{"
                + "category='" + category + '\''
                + ", orderType=" + orderType
                + ", linesAdded=" + linesAdded
                + ", formatted=" + formatted
                + '}';
    }

    @Override
    public void setLine(final BetonHologram hologram, final int index) {
        final Component[] lines = getLines();
        for (int i = 0; i < lines.length; i++) {
            hologram.setLine(index + i, lines[i]);
        }
    }
}
