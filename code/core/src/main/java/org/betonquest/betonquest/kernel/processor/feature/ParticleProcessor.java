package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.identifier.FunctionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ParticleIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.particle.ParticleAnimationProvider;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.service.function.Functions;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.particle.Particles;
import org.betonquest.betonquest.id.particle.DefaultParticleIdentifier;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;
import org.betonquest.betonquest.particle.BatchScheduler;
import org.betonquest.betonquest.particle.BukkitBatchScheduler;
import org.betonquest.betonquest.particle.DefaultParticleAnimationProvider;
import org.betonquest.betonquest.particle.DefaultParticleFunction;
import org.betonquest.betonquest.particle.ParticleFunction;
import org.betonquest.betonquest.particle.ParticleRenderMode;
import org.betonquest.betonquest.particle.ThreadBatchScheduler;
import org.betonquest.betonquest.particle.datafunction.EmptyDataProvider;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunctionProvider;
import org.betonquest.betonquest.particle.deltafunction.ConstantDeltaFunction;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunction;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Processor for loading and managing particle animation configurations from quest packages.
 * <p>
 * This processor reads particle animation definitions from configuration sections and creates
 * {@link DefaultParticleAnimationProvider} instances that can generate particle animations for profiles.
 * <p>
 * The processor implements the {@link Particles} service interface to provide access to loaded particle
 * animations via their {@link ParticleIdentifier}.
 *
 * @see DefaultParticleAnimationProvider
 * @see ParticleIdentifier
 * @see Particles
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class ParticleProcessor extends SectionProcessor<ParticleIdentifier, DefaultParticleAnimationProvider> implements Particles {

    /**
     * The parser for delta functions.
     */
    private final DeltaFunctionParser deltaFunctionParser;

    /**
     * The default batch scheduler to run particle animations.
     */
    private final BatchScheduler batchScheduler;

    /**
     * The list of registered particle data function providers.
     * <p>
     * These providers are used to supply additional particle-specific data based on the particle type.
     * They are matched against the particle type and used to read and process particle data configuration.
     */
    private final List<ParticleDataFunctionProvider> dataFunctionProviders;

    /**
     * Creates a new ParticleProcessor to load and manage particle animation configurations.
     *
     * @param log                       the logger for this class
     * @param instructionApi            the instruction API for parsing configuration sections as instructions
     * @param identifierFactory         the identifier factory to create {@link ParticleIdentifier}s
     * @param functionIdentifierFactory the identifier factory to create {@link FunctionIdentifier}s for delta functions
     * @param plugin                    the plugin instance used for scheduling particle batches
     * @param functions                 the function service for resolving custom functions
     */
    public ParticleProcessor(final BetonQuestLogger log, final Instructions instructionApi, final IdentifierFactory<ParticleIdentifier> identifierFactory,
                             final IdentifierFactory<FunctionIdentifier> functionIdentifierFactory,
                             final Plugin plugin, final Functions functions) {
        super(log, instructionApi, identifierFactory, "Particle", DefaultParticleIdentifier.PARTICLES_SECTION);
        this.deltaFunctionParser = new DeltaFunctionParser(functionIdentifierFactory, functions);
        this.batchScheduler = new BukkitBatchScheduler(plugin, false);
        this.dataFunctionProviders = new ArrayList<>();
    }

    @Override
    protected Map.Entry<ParticleIdentifier, DefaultParticleAnimationProvider> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final Argument<Particle> particleType = instruction.read().value("type").enumeration(Particle.class).get();
        final Argument<DeltaFunction> range = instruction.read().value("range").parse(deltaFunctionParser).getOptional(new ConstantDeltaFunction(32));

        final SectionInstruction animationPart = instruction.subSection("animation");
        final Argument<Number> duration = animationPart.read().value("duration").number().getOptional(0);
        final Argument<TimeUnit> timeunit = animationPart.read().value("timeunit").enumeration(TimeUnit.class).getOptional(TimeUnit.SECONDS);
        final Argument<Number> batchsize = animationPart.read().value("batchsize").number().getOptional(1);
        final Argument<Number> primarySample = animationPart.read().value("sampling", "primary").number().get();
        final Optional<Argument<Number>> secondarySample = animationPart.read().value("sampling", "secondary").number().getOptional();
        final Argument<ParticleRenderMode> renderMode = animationPart.read().value("order").enumeration(ParticleRenderMode.class).getOptional(ParticleRenderMode.FORWARD);
        final Argument<BatchScheduler> scheduler = animationPart.read().value("thread").bool()
                .map(useThreads -> useThreads ? new ThreadBatchScheduler() : batchScheduler).getOptional(batchScheduler);

        final SectionInstruction shapePart = instruction.subSection("shape");
        final Argument<DeltaFunction> posX = shapePart.read().value("x").parse(deltaFunctionParser).getOptional(ConstantDeltaFunction.ZERO);
        final Argument<DeltaFunction> posY = shapePart.read().value("y").parse(deltaFunctionParser).getOptional(ConstantDeltaFunction.ZERO);
        final Argument<DeltaFunction> posZ = shapePart.read().value("z").parse(deltaFunctionParser).getOptional(ConstantDeltaFunction.ZERO);
        final Argument<DeltaFunction> offsetX = shapePart.read().value("offset", "x").parse(deltaFunctionParser).getOptional(ConstantDeltaFunction.ZERO);
        final Argument<DeltaFunction> offsetY = shapePart.read().value("offset", "y").parse(deltaFunctionParser).getOptional(ConstantDeltaFunction.ZERO);
        final Argument<DeltaFunction> offsetZ = shapePart.read().value("offset", "z").parse(deltaFunctionParser).getOptional(ConstantDeltaFunction.ZERO);
        final Argument<DeltaFunction> count = shapePart.read().value("count").parse(deltaFunctionParser).getOptional(new ConstantDeltaFunction(1));
        final Argument<DeltaFunction> extra = shapePart.read().value("extra").parse(deltaFunctionParser).getOptional(new ConstantDeltaFunction(1));

        final Argument<ParticleFunction> particleFunction = new ParticleFunctionProvider(particleType, posX, posY, posZ, offsetX, offsetY, offsetZ, count, extra, range, readDataFunction(instruction));
        final DefaultParticleAnimationProvider animationProvider = new DefaultParticleAnimationProvider(scheduler, primarySample, secondarySample.orElse(null), batchsize, duration, timeunit, renderMode, particleFunction);
        return Map.entry(getIdentifier(instruction.getPackage(), sectionName), animationProvider);
    }

    private QuestFunction<Particle, ParticleDataFunctionProvider> readDataFunction(final SectionInstruction instruction) throws QuestException {
        if (!instruction.getSection().isConfigurationSection("data")) {
            return particle -> new EmptyDataProvider();
        }
        final SectionInstruction dataSection = instruction.subSection("data");
        return particle -> {
            final ParticleDataFunctionProvider dataFunctionProvider = dataFunctionProviders.stream().filter(provider -> provider.match(particle)).findFirst().orElse(new EmptyDataProvider());
            dataFunctionProvider.read(dataSection, deltaFunctionParser);
            return dataFunctionProvider;
        };
    }

    /**
     * Add a data function provider.
     *
     * @param dataFunctionProvider the data function provider to add
     */
    public void addDataFunctionProvider(final ParticleDataFunctionProvider dataFunctionProvider) {
        this.dataFunctionProviders.add(dataFunctionProvider);
    }

    @Override
    public ParticleAnimationProvider getParticleAnimation(final ParticleIdentifier particleIdentifier) throws QuestException {
        return get(particleIdentifier);
    }

    /**
     * A particle function provider usable as an argument.
     *
     * @param particle     the particle type
     * @param posX         the posX delta function
     * @param posY         the posY delta function
     * @param posZ         the posZ delta function
     * @param offsetX      the offset posX delta function
     * @param offsetY      the offset posY delta function
     * @param offsetZ      the offset posZ delta function
     * @param count        the number of particles
     * @param extra        the extra value
     * @param range        the range of the particle
     * @param dataFunction the function to get additional particle data
     */
    private record ParticleFunctionProvider(Argument<Particle> particle, Argument<DeltaFunction> posX,
                                            Argument<DeltaFunction> posY, Argument<DeltaFunction> posZ,
                                            Argument<DeltaFunction> offsetX, Argument<DeltaFunction> offsetY,
                                            Argument<DeltaFunction> offsetZ, Argument<DeltaFunction> count,
                                            Argument<DeltaFunction> extra, Argument<DeltaFunction> range,
                                            QuestFunction<Particle, ParticleDataFunctionProvider> dataFunction) implements Argument<ParticleFunction> {

        @Override
        public ParticleFunction getValue(@Nullable final Profile profile) throws QuestException {
            final Particle particleValue = particle.getValue(profile);
            return new DefaultParticleFunction(particleValue, posX.getValue(profile), posY.getValue(profile), posZ.getValue(profile),
                    offsetX.getValue(profile), offsetY.getValue(profile), offsetZ.getValue(profile), extra.getValue(profile), range.getValue(profile),
                    count.getValue(profile), dataFunction.apply(particleValue).getValue(profile));
        }
    }
}
