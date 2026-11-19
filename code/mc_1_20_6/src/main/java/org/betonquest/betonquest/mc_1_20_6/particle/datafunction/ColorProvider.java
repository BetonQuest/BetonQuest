package org.betonquest.betonquest.mc_1_20_6.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestBiFunction;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.particle.datafunction.ColorSection;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunction;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunctionProvider;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

/**
 * A color provider usable as an argument.
 */
public class ColorProvider implements ParticleDataFunctionProvider {

    /**
     * Handler for the color.
     */
    private final ColorSection color;

    /**
     * Creates a new color provider.
     */
    public ColorProvider() {
        this.color = new ColorSection("color");
    }

    @Override
    public ParticleDataFunction<Color> getValue(@Nullable final Profile profile) throws QuestException {
        final QuestBiFunction<Number, Number, Color> colorFunction = color.getColor(profile);
        return colorFunction::apply;
    }

    @Override
    public boolean match(final Particle particle) {
        return particle.getDataType().equals(Color.class);
    }

    @Override
    public void read(final SectionInstruction instruction, final DeltaFunctionParser deltaFunctionParser) throws QuestException {
        this.color.read(instruction, deltaFunctionParser);
    }
}
