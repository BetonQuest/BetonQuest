package org.betonquest.betonquest.conversation.menu.display;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a view of lines in a conversation display.
 * This interface allows for different types of line views, enabling flexibility in how lines are displayed.
 */
public interface LineView {
    /**
     * Returns the lines of this view.
     *
     * @return a new modifiable list of lines
     */
    @Contract(pure = true)
    List<Line> getLines();

    /**
     * Returns the size of this view, i.e., the number of lines it contains.
     *
     * @return the number of lines in this view
     */
    int getSize();

    /**
     * An empty line view that contains no lines.
     */
    class Empty implements LineView {
        /**
         * The empty default constructor.
         */
        public Empty() {
        }

        @Override
        public List<Line> getLines() {
            return new ArrayList<>();
        }

        @Override
        public int getSize() {
            return 0;
        }
    }

    /**
     * A holder view that holds a list of lines.
     * This view is used to group lines together, allowing for easy management and display.
     */
    class Holder implements LineView {
        /**
         * The lines to hold in this view.
         */
        private final List<Line> lines;

        /**
         * Creates a new holder view with the given lines.
         *
         * @param lines the lines to hold
         */
        public Holder(final Line... lines) {
            this.lines = List.of(lines);
        }

        /**
         * Creates a new holder view with the given lines.
         *
         * @param lines the lines to hold
         */
        public Holder(final List<Line> lines) {
            this.lines = lines;
        }

        @Override
        public List<Line> getLines() {
            return new ArrayList<>(lines);
        }

        @Override
        public int getSize() {
            return lines.size();
        }
    }

    /**
     * A filler view that fills a line view to a certain height.
     */
    class Filler implements LineView {
        /**
         * An empty line that can be used to fill the view.
         */
        private static final Line.Fixed EMPTY_LINE = new Line.Fixed(Component.empty());

        /**
         * The line view to fill to a certain height.
         */
        private final LineView lineView;

        /**
         * The height to fill the lines to.
         */
        private final int height;

        /**
         * Creates a new filler view that fills the given line view to the specified height.
         *
         * @param lineView the line view to fill
         * @param height   the height to fill the lines to
         */
        public Filler(final LineView lineView, final int height) {
            this.lineView = lineView;
            this.height = height;
        }

        @Override
        public List<Line> getLines() {
            final List<Line> lines = lineView.getLines();
            if (lines.size() >= height) {
                return lines;
            }

            final int fillCount = height - lines.size();
            for (int i = 0; i < fillCount; i++) {
                lines.add(EMPTY_LINE);
            }
            return lines;
        }

        @Override
        public int getSize() {
            return Math.max(height, lineView.getSize());
        }
    }

    /**
     * A combiner view that combines multiple line views into a single view.
     */
    class Combiner implements LineView {
        /**
         * The line views to combine into a single view.
         */
        private final LineView[] views;

        /**
         * Creates a new combiner view that combines multiple line views.
         *
         * @param views the line views to combine
         */
        public Combiner(final LineView... views) {
            this.views = views.clone();
        }

        @Override
        public List<Line> getLines() {
            final List<Line> combinedLines = new ArrayList<>();
            for (final LineView view : views) {
                combinedLines.addAll(view.getLines());
            }
            return combinedLines;
        }

        @Override
        public int getSize() {
            return Arrays.stream(views).mapToInt(LineView::getSize).sum();
        }
    }

    /**
     * An excerpt view that displays a portion of a line view.
     * It shows a fixed number of lines, with the ability to scroll up and down.
     */
    class Excerpt implements LineView {
        /**
         * The max height of lines to display.
         */
        private final int height;

        /**
         * A supplier for the current cursor position.
         */
        private final Supplier<Integer> cursor;

        /**
         * The line view to excerpt from.
         */
        private final LineView lineView;

        /**
         * The line to display when scrolling up.
         */
        private final Line scrollUp;

        /**
         * The line to display when scrolling down.
         */
        private final Line scrollDown;

        /**
         * Creates a new excerpt view.
         *
         * @param height     the max height of lines to display.
         * @param cursor     a supplier for the current cursor position
         * @param lineView   the line view to excerpt from
         * @param scrollUp   the line to display when scrolling up
         * @param scrollDown the line to display when scrolling down
         */
        public Excerpt(final int height, final Supplier<Integer> cursor, final LineView lineView, final Line scrollUp, final Line scrollDown) {
            this.height = height;
            this.cursor = cursor;
            this.lineView = lineView;
            this.scrollUp = scrollUp;
            this.scrollDown = scrollDown;
        }

        @Override
        public List<Line> getLines() {
            final List<Line> rawLines = lineView.getLines();
            if (rawLines.size() <= height) {
                return rawLines;
            }
            final int pos = Math.min(cursor.get(), rawLines.size() - height);
            final List<Line> lines = rawLines.subList(pos, pos + height);
            if (pos > 0) {
                lines.set(0, scrollUp);
            }
            if (pos + height < rawLines.size()) {
                lines.set(height - 1, scrollDown);
            }
            return lines;
        }

        @Override
        public int getSize() {
            return height;
        }
    }
}
