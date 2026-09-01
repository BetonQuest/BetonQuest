package org.betonquest.betonquest.quest.placeholder.statistic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

/**
 * A placeholder that gets a statistic of a player.
 */
public class StatisticPlaceholder implements PlayerPlaceholder {

    /**
     * The warning message to log when a statistic placeholder is given an invalid argument.
     */
    private static final String WARN_MESSAGE = "Statistic placeholder for '%s' does not accept %s but has '%s' given.";

    /**
     * The error message to throw when a statistic placeholder is not given an argument.
     */
    private static final String ERROR_MESSAGE = "Statistic placeholder for '%s' requires %s given.";

    /**
     * The error message to throw when a statistic placeholder is given an invalid argument.
     */
    private static final String MISMATCH_MESSAGE = "Statistic placeholder for '%s' requires %s but has '%s' given.";

    /**
     * The block material fill-in to use.
     */
    private static final String BLOCK_MATERIAL_MESSAGE = "a block material";

    /**
     * The item material fill-in to use.
     */
    private static final String ITEM_MATERIAL_MESSAGE = "an item material";

    /**
     * The material fill-in to use.
     */
    private static final String MATERIAL_MESSAGE = "a material";

    /**
     * The entity type fill-in to use.
     */
    private static final String ENTITY_MESSAGE = "an entity type";

    /**
     * The logger to log warnings to.
     */
    private final BetonQuestLogger log;

    /**
     * The statistic to get.
     */
    private final Argument<Statistic> statistic;

    /**
     * The entity type to get the statistic for or null if the statistic is not entity-specific.
     */
    @Nullable
    private final Argument<EntityType> entityType;

    /**
     * The material to get the statistic for or null if the statistic is not material-specific.
     */
    @Nullable
    private final Argument<Material> material;

    /**
     * Creates a new StatisticPlaceholder.
     *
     * @param log        the logger
     * @param statistic  the statistic to get
     * @param entityType the entity type to get the statistic for or null if the statistic is not entity-specific
     * @param material   the material to get the statistic for or null if the statistic is not material-specific
     */
    public StatisticPlaceholder(final BetonQuestLogger log, final Argument<Statistic> statistic, @Nullable final Argument<EntityType> entityType,
                                @Nullable final Argument<Material> material) {
        this.log = log;
        this.statistic = statistic;
        this.entityType = entityType;
        this.material = material;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final OfflinePlayer player = profile.getPlayer();
        final Statistic statistic = this.statistic.getValue(profile);
        final EntityType entityTypeValue = entityType == null ? null : entityType.getValue(profile);
        final Material materialValue = material == null ? null : material.getValue(profile);
        return getStatistic(player, statistic, entityTypeValue, materialValue);
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    private String getStatistic(final OfflinePlayer statSource, final Statistic statistic, @Nullable final EntityType entityType, @Nullable final Material material) throws QuestException {
        final String statKey = statistic.getKey().asString();
        final int statValue = switch (statistic.getType()) {
            case UNTYPED -> {
                if (entityType != null) {
                    log.warn(WARN_MESSAGE.formatted(statKey, ENTITY_MESSAGE, entityType.getKey().asString()));
                }
                if (material != null) {
                    log.warn(WARN_MESSAGE.formatted(statKey, MATERIAL_MESSAGE, material.getKey().asString()));
                }
                yield statSource.getStatistic(statistic);
            }
            case ITEM -> {
                if (material == null) {
                    throw new QuestException(ERROR_MESSAGE.formatted(statKey, ITEM_MATERIAL_MESSAGE));
                }
                if (!material.isItem()) {
                    throw new QuestException(MISMATCH_MESSAGE.formatted(statKey, ITEM_MATERIAL_MESSAGE, material.getKey().asString()));
                }
                yield statSource.getStatistic(statistic, material);
            }
            case BLOCK -> {
                if (material == null) {
                    throw new QuestException(ERROR_MESSAGE.formatted(statKey, BLOCK_MATERIAL_MESSAGE));
                }
                if (!material.isBlock()) {
                    throw new QuestException(MISMATCH_MESSAGE.formatted(statKey, BLOCK_MATERIAL_MESSAGE, material.getKey().asString()));
                }
                yield statSource.getStatistic(statistic, material);
            }
            case ENTITY -> {
                if (entityType == null) {
                    throw new QuestException(ERROR_MESSAGE.formatted(statKey, ENTITY_MESSAGE));
                }
                yield statSource.getStatistic(statistic, entityType);
            }
        };
        return String.valueOf(statValue);
    }
}
