package org.betonquest.betonquest.quest.placeholder.statistic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.Optional;

/**
 * Factory for {@link StatisticPlaceholder}.
 */
public class StatisticPlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new statistic placeholder factory.
     *
     * @param loggerFactory the logger factory
     */
    public StatisticPlaceholderFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Statistic> statistic = instruction.enumeration(Statistic.class).get();
        final Optional<Argument<EntityType>> entity = instruction.enumeration(EntityType.class).get("entity");
        final Optional<Argument<Material>> material = instruction.enumeration(Material.class).get("material");
        if (entity.isPresent() && material.isPresent()) {
            throw new QuestException("You may only specify either 'entity' or 'material'. There is no statistic that requires both.");
        }
        return new StatisticPlaceholder(loggerFactory.create(StatisticPlaceholder.class), statistic,
                entity.orElse(null), material.orElse(null));
    }
}
