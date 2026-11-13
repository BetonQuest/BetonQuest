package org.betonquest.betonquest.quest.action.animateparticle;

import com.destroystokyo.paper.ParticleBuilder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Action to animate a particle.
 */
public class AnimateParticleAction implements NullableAction {

    /**
     * The plugin instance to use.
     */
    private final Plugin plugin;

    /**
     * The scheduler instance to use.
     */
    private final BukkitScheduler scheduler;

    /**
     * The particle to use.
     */
    private final Argument<Particle> particle;

    /**
     * The origin position of the particle animation.
     */
    private final Argument<Location> origin;

    /**
     * The animation instance to use.
     */
    private final ParticleFunction animation;

    /**
     * The start delta value.
     */
    private final Argument<Number> start;

    /**
     * The end delta value.
     */
    private final Argument<Number> end;

    /**
     * The sample of particles to emit.
     */
    private final Argument<Number> sample;

    /**
     * The visibility range of the particle animation.
     */
    private final Argument<Number> range;

    /**
     * Extra data for the particle.
     */
    private final Argument<Number> extra;

    /**
     * Particle animation duration.
     */
    private final Argument<Number> duration;

    /**
     * Particle animation duration unit.
     */
    private final Argument<TimeUnit> unit;

    /**
     * Particle animation batch size.
     */
    private final Argument<Number> batch;

    /**
     * The offset of the particle.
     */
    private final Argument<Vector> offset;

    /**
     * The number of particles to emit.
     */
    private final Argument<Number> count;

    /**
     * The color of the particle.
     */
    @Nullable
    private final Argument<Vector> color;

    /**
     * The item for {@link Particle#ITEM_CRACK}.
     */
    @Nullable
    private final Argument<ItemWrapper> item;

    /**
     * The blockdata for various particle types.
     */
    @Nullable
    private final Argument<BlockData> blockData;

    /**
     * Creates a new AnimateParticleAction.
     *
     * @param plugin    the plugin instance to use
     * @param scheduler the scheduler instance to use
     * @param particle  the particle to use
     * @param origin    the origin position of the particle animation
     * @param animation the animation instance to use
     * @param start     the start delta value
     * @param end       the end delta value
     * @param sample    the count of particles to emit
     * @param range     the visibility range of the particle animation
     * @param extra     extra data for the particle
     * @param duration  particle animation duration
     * @param unit      particle animation duration unit
     * @param batch     particle animation batch size
     * @param offset    the offset of the particle
     * @param count     the number of particles to emit per sample
     * @param color     the color of the particle
     * @param item      the item for {@link Particle#ITEM_CRACK}
     * @param blockData the blockdata for various particle types
     */
    public AnimateParticleAction(final Plugin plugin, final BukkitScheduler scheduler, final Argument<Particle> particle, final Argument<Location> origin,
                                 final ParticleFunction animation, final Argument<Number> start, final Argument<Number> end, final Argument<Number> sample,
                                 final Argument<Number> range, final Argument<Number> extra, final Argument<Number> duration, final Argument<TimeUnit> unit,
                                 final Argument<Number> batch, final Argument<Vector> offset, final Argument<Number> count, @Nullable final Argument<Vector> color,
                                 @Nullable final Argument<ItemWrapper> item, @Nullable final Argument<BlockData> blockData) {
        this.plugin = plugin;
        this.scheduler = scheduler;
        this.particle = particle;
        this.origin = origin;
        this.animation = animation;
        this.start = start;
        this.end = end;
        this.sample = sample;
        this.range = range;
        this.extra = extra;
        this.duration = duration;
        this.unit = unit;
        this.batch = batch;
        this.offset = offset;
        this.count = count;
        this.color = color;
        this.item = item;
        this.blockData = blockData;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final List<Vector> vectorList = calculateVectorList(profile);
        final ParticleBuilder particleBuilder = prepareBuilder(profile);
        final int visibilityRange = range.getValue(profile).intValue();
        final Location sourceLocation = origin.getValue(profile);
        final int size = vectorList.size();
        final long time = unit.getValue(profile).getTicks(duration.getValue(profile).intValue());
        if (time <= 0) {
            renderParticles(particleBuilder, sourceLocation, vectorList, 0, size, visibilityRange);
        }
        final int batchSize = batch.getValue(profile).intValue();
        final long delay = time * batchSize / size;
        for (int i = 0; i < size; i++) {
            scheduleRenderParticles(particleBuilder, sourceLocation, vectorList, i * batchSize, batchSize, visibilityRange, delay * i);
        }
    }

    private ParticleBuilder prepareBuilder(@Nullable final Profile profile) throws QuestException {
        final ParticleBuilder particleBuilder = new ParticleBuilder(particle.getValue(profile))
                .extra(extra.getValue(profile).doubleValue())
                .offset(offset.getValue(profile).getX(), offset.getValue(profile).getY(), offset.getValue(profile).getZ())
                .count(count.getValue(profile).intValue());
        if (color != null) {
            final Vector value = color.getValue(profile);
            particleBuilder.color(value.getBlockX(), value.getBlockY(), value.getBlockZ());
        } else if (item != null) {
            particleBuilder.data(item.getValue(profile).generate(profile));
        } else if (blockData != null) {
            particleBuilder.data(blockData.getValue(profile));
        }
        if (profile != null && profile.getOnlineProfile().isPresent()) {
            particleBuilder.source(profile.getPlayer().getPlayer());
        }
        return particleBuilder;
    }

    private List<Vector> calculateVectorList(@Nullable final Profile profile) throws QuestException {
        return animation.calculateSeries(profile, start.getValue(profile).doubleValue(),
                end.getValue(profile).doubleValue(), sample.getValue(profile).intValue());
    }

    private void scheduleRenderParticles(final ParticleBuilder particleBuilder, final Location sourceLocation, final List<Vector> vectorList,
                                         final int start, final int amount, final int visibilityRange, final long delay) {
        scheduler.runTaskLater(plugin, () -> renderParticles(particleBuilder, sourceLocation, vectorList, start, amount, visibilityRange), delay);
    }

    private void renderParticles(final ParticleBuilder particleBuilder, final Location sourceLocation, final List<Vector> vectorList,
                                 final int start, final int amount, final int visibilityRange) {
        final int end = Math.min(start + amount, vectorList.size());
        for (int i = start; i < end; i++) {
            final Location targetLocation = sourceLocation.clone().add(vectorList.get(i));
            particleBuilder.location(targetLocation).receivers(visibilityRange).spawn();
        }
    }
}
