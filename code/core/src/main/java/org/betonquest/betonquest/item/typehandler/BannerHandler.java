package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles de-/serialization of banner patterns.
 */
public class BannerHandler implements ItemMetaHandler<BannerMeta> {

    /**
     * The number of values that describe one pattern layer.
     */
    private static final int PATTERN_PARTS = 2;

    /**
     * The required pattern existence.
     */
    private final Existence patternsE = Existence.WHATEVER;

    /**
     * The ordered banner patterns.
     */
    private ExistenceArgument<List<Pattern>> patterns = ExistenceArgument.whateverEmptyList();

    /**
     * The empty default constructor.
     */
    public BannerHandler() {
    }

    @Override
    public Class<BannerMeta> metaClass() {
        return BannerMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("patterns");
    }

    @Override
    @Nullable
    public String serializeToString(final BannerMeta bannerMeta) {
        if (bannerMeta.getPatterns().isEmpty()) {
            return null;
        }
        return "patterns:" + bannerMeta.getPatterns().stream()
                .map(pattern -> pattern.getColor() + ":" + pattern.getPattern().name())
                .collect(Collectors.joining(","));
    }

    @Override
    public void set(final Instruction instruction) throws QuestException {
        this.patterns = ExistenceArgument.applyList("patterns", instruction.parse(this::parsePattern));
    }

    private Pattern parsePattern(final String data) throws QuestException {
        final String[] parts = HandlerUtil.getSplit(data, "Banner pattern is null!", ":");
        if (parts.length != PATTERN_PARTS) {
            throw new QuestException("Wrong banner pattern format: " + data);
        }
        final DyeColor color;
        try {
            color = DyeColor.valueOf(parts[0].toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Unknown banner pattern color: " + parts[0], e);
        }
        final PatternType type;
        try {
            type = PatternType.valueOf(parts[1].toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Unknown banner pattern type: " + parts[1], e);
        }
        return new Pattern(color, type);
    }

    @Override
    public void populate(final BannerMeta bannerMeta, @Nullable final Profile profile) throws QuestException {
        bannerMeta.setPatterns(patterns.getValue(profile).getRight());
    }

    @Override
    public boolean check(final BannerMeta bannerMeta, @Nullable final Profile profile) throws QuestException {
        return switch (patternsE) {
            case WHATEVER -> true;
            case REQUIRED -> patterns.getValue(profile).equals(bannerMeta.getPatterns());
            case FORBIDDEN -> bannerMeta.getPatterns().isEmpty();
        };
    }
}
