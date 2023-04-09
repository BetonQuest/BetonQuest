package org.betonquest.betonquest.compatibility.holograms.lines;

import org.betonquest.betonquest.compatibility.holograms.BetonHologram;

import java.util.Arrays;

/**
 * Creates a new instance for TopLine.
 */
public class TopLine extends AbstractLine {
    /**
     * Name of point. Must follow formatting <code>package.name</code>.
     */
    private final String category;

    /**
     * Direction in which scores are ordered.
     */
    private final TopXObject.OrderType orderType;

    /**
     * Color codes for individual elements of the displayed line in the exact order: <br>
     * <code>{#.} {name} {-} {score}</code>
     */
    private final char[] colors;

    /**
     * Storage for rank data
     */
    private final TopXObject topXObject;

    /**
     * Creates a new instance of ItemLine. Automatically creates and stores {@link TopXObject} from received data.
     *
     * @param category  Name of point as <code>package.name</code>
     * @param orderType Direction of order
     * @param limit     Maximum number of lines displayed
     * @param colors    Color codes for individual parts of display (#, name, dash, and score)
     */
    @SuppressWarnings("PMD.UseVarargs")
    public TopLine(final String category, final TopXObject.OrderType orderType, final int limit, final char[] colors) {
        super(false, limit);
        this.category = category;
        this.orderType = orderType;
        this.colors = colors.clone();

        topXObject = new TopXObject(
                limit,
                category,
                orderType);
    }

    /**
     * Updates the stored {@link TopXObject} and returns found entries as String-Array.
     * If retrieved lines are less than the limit, it will be filled with empty lines.
     *
     * @return Formatted lines ready for display on a hologram
     */
    public String[] getLines() {
        topXObject.queryDB();

        final String[] lines = new String[linesAdded];
        for (int i = 0; i < linesAdded; i++) {
            if (i >= topXObject.getLineCount()) {
                lines[i] = "";
                continue;
            }
            final TopXLine line = topXObject.getEntries().get(i);
            lines[i] = "§" + colors[0] + (i + 1) + ". §" + colors[1] + line.playerName() + "§" + colors[2] + " - §" + colors[3] + line.count();
        }
        return lines;
    }

    @Override
    public String toString() {
        return "TopLine{" +
                "category='" + category + '\'' +
                ", orderType=" + orderType +
                ", linesAdded=" + linesAdded +
                ", colors=" + Arrays.toString(colors) +
                '}';
    }

    @Override
    public void setLine(final BetonHologram hologram, final int index) {
        final String[] lines = getLines();
        for (int i = 0; i < lines.length; i++) {
            hologram.setLine(index + i, lines[i]);
        }
    }
}
