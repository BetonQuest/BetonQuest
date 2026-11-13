package org.betonquest.betonquest.mc_1_20_6.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunction;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunctionProvider;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunction;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Vibration;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * A vibration provider usable as an argument.
 */
public class VibrationProvider implements ParticleDataFunctionProvider {

    /**
     * The vibration arrival time.
     */
    @Nullable
    private Argument<DeltaFunction> vibrationTime;

    /**
     * The time unit of the vibration arrival time.
     */
    @Nullable
    private Argument<TimeUnit> vibrationTimeUnit;

    /**
     * The vibration destination.
     */
    @Nullable
    private Argument<Vibration.Destination> target;

    /**
     * The constructor.
     */
    public VibrationProvider() {
    }

    @Override
    public boolean match(final Particle particle) {
        return particle.getDataType().equals(Vibration.class);
    }

    @Override
    public void read(final SectionInstruction instruction, final DeltaFunctionParser deltaFunctionParser) throws QuestException {
        final String vibrationSection = "vibration";
        this.vibrationTime = instruction.read().value(vibrationSection, "time").parse(deltaFunctionParser).get();
        this.vibrationTimeUnit = instruction.read().value(vibrationSection, "unit").enumeration(TimeUnit.class).getOptional(TimeUnit.SECONDS);
        final Optional<Argument<Vibration.Destination>> targetLocation = instruction.read().value(vibrationSection, "location")
                .location().map(this::getLocationDestination).getOptional();
        final Optional<Argument<Vibration.Destination>> targetEntity = instruction.read().value(vibrationSection, "entity")
                .uuid().map(this::getEntityDestination).getOptional();
        this.target = targetLocation.or(() -> targetEntity).orElseThrow(() -> new QuestException("No vibration target specified!"));
    }

    @Override
    public ParticleDataFunction<?> getValue(@Nullable final Profile profile) throws QuestException {
        if (vibrationTime == null || target == null || vibrationTimeUnit == null) {
            throw new QuestException("Vibration particle requires 'time' and 'location' or 'entity' argument");
        }
        final DeltaFunction timeValue = vibrationTime.getValue(profile);
        final TimeUnit timeUnit = vibrationTimeUnit.getValue(profile);
        final Vibration.Destination destination = target.getValue(profile);
        return (d1, d2) -> new Vibration(destination, (int) timeUnit.getTicks(timeValue.evaluate(d1, d2).longValue()));
    }

    private Vibration.Destination getEntityDestination(final UUID uuid) throws QuestException {
        final Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) {
            throw new QuestException("Entity with uuid '" + uuid + "' not found!");
        }
        return new Vibration.Destination.EntityDestination(entity);
    }

    private Vibration.Destination getLocationDestination(final Location location) {
        return new Vibration.Destination.BlockDestination(location);
    }
}
