package org.betonquest.betonquest.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestBiFunction;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunction;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a color section in a particle data function.
 * <p>
 * This class manages the parsing and evaluation of RGB color components defined in a configuration section.
 * Each color component (red, green, blue) is represented as a {@link DeltaFunction}, allowing dynamic
 * color values to be computed based on delta parameters.
 *
 * @since 3.4.0
 */
public class ColorSection {

    /**
     * The name of the section containing the color configuration.
     */
    private final String sectionName;

    /**
     * Argument for the red component of the color.
     */
    @Nullable
    private Argument<DeltaFunction> colorR;

    /**
     * Argument for the green component of the color.
     */
    @Nullable
    private Argument<DeltaFunction> colorG;

    /**
     * Argument for the blue component of the color.
     */
    @Nullable
    private Argument<DeltaFunction> colorB;

    /**
     * Creates a new color section with the specified section name.
     *
     * @param sectionName the name of the configuration section containing color data
     */
    public ColorSection(final String sectionName) {
        this.sectionName = sectionName;
    }

    /**
     * Reads and parses the RGB color components from the given section instruction.
     * <p>
     * This method extracts the "r", "g", and "b" values from the specified section and
     * parses them using the provided {@link DeltaFunctionParser}. The parsed values are
     * stored in the respective color component fields.
     *
     * @param instruction         the section instruction to read the color data from
     * @param deltaFunctionParser the parser to convert raw values into delta functions
     * @throws QuestException if the section is invalid, missing required values or cannot be parsed
     */
    public void read(final SectionInstruction instruction, final DeltaFunctionParser deltaFunctionParser) throws QuestException {
        this.colorR = instruction.read().value(sectionName, "r").parse(deltaFunctionParser).get();
        this.colorG = instruction.read().value(sectionName, "g").parse(deltaFunctionParser).get();
        this.colorB = instruction.read().value(sectionName, "b").parse(deltaFunctionParser).get();
    }

    /**
     * Creates and returns a color function that evaluates RGB values dynamically based on delta parameters.
     * <p>
     * The returned function accepts two numeric delta parameters and produces a {@link Color} instance
     * by evaluating each color component's delta function. The resulting RGB values are clamped to the
     * valid range of 0-255.
     *
     * @param profile the profile to evaluate the color function for, or null if no profile context is needed
     * @return a bi-function that takes two delta parameters and returns a computed color
     * @throws QuestException if the color section has not been properly initialized
     */
    public QuestBiFunction<Number, Number, Color> getColor(@Nullable final Profile profile) throws QuestException {
        if (colorR == null || colorG == null || colorB == null) {
            throw new QuestException("Particle requires color-section '%s'".formatted(sectionName));
        }
        final DeltaFunction redDelta = colorR.getValue(profile);
        final DeltaFunction greenDelta = colorG.getValue(profile);
        final DeltaFunction blueDelta = colorB.getValue(profile);
        return (firstDelta, secondDelta) -> {
            final Number redValue = redDelta.evaluate(firstDelta, secondDelta);
            final Number greenValue = greenDelta.evaluate(firstDelta, secondDelta);
            final Number blueValue = blueDelta.evaluate(firstDelta, secondDelta);
            return color(redValue, greenValue, blueValue);
        };
    }

    /**
     * Creates a {@link Color} instance from the given RGB component values.
     * <p>
     * Each component is converted to an integer and clamped to the valid RGB range (0-255)
     * before constructing the color.
     *
     * @param red   the red color component value
     * @param green the green color component value
     * @param blue  the blue color component value
     * @return a color instance with the specified RGB values
     */
    private Color color(final Number red, final Number green, final Number blue) {
        return Color.fromRGB(clampColor(red.intValue()), clampColor(green.intValue()), clampColor(blue.intValue()));
    }

    private int clampColor(final int value) {
        return Math.max(0, Math.min(255, value));
    }
}
