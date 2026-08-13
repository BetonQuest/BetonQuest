package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
     * The ordered banner patterns.
     */
    private List<Pattern> patterns = List.of();

    /**
     * The required pattern existence.
     */
    private Existence patternsE = Existence.WHATEVER;

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
                .map(pattern -> pattern.getColor() + ":" + patternTypeName(pattern.getPattern()))
                .collect(Collectors.joining(","));
    }

    @Override
    public void set(final String key, final String data) throws QuestException {
        if (!"patterns".equals(key)) {
            throw new QuestException("Invalid banner key: " + key);
        }
        if (Existence.NONE_KEY.equalsIgnoreCase(data)) {
            patterns = List.of();
            patternsE = Existence.FORBIDDEN;
            return;
        }
        final String[] patternData = HandlerUtil.getSplit(data, "Banner patterns are null!", ",");
        final List<Pattern> parsedPatterns = new ArrayList<>(patternData.length);
        for (final String pattern : patternData) {
            parsedPatterns.add(parsePattern(pattern));
        }
        patterns = List.copyOf(parsedPatterns);
        patternsE = Existence.REQUIRED;
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
            type = (PatternType) PatternType.class.getMethod("valueOf", String.class)
                    .invoke(null, parts[1].toUpperCase(Locale.ROOT));
        } catch (final ReflectiveOperationException | IllegalArgumentException e) {
            throw new QuestException("Unknown banner pattern type: " + parts[1], e);
        }
        return new Pattern(color, type);
    }

    private String patternTypeName(final PatternType type) {
        try {
            return (String) PatternType.class.getMethod("name").invoke(type);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Could not serialize banner pattern type", e);
        }
    }

    @Override
    public void populate(final BannerMeta bannerMeta) {
        bannerMeta.setPatterns(patterns);
    }

    @Override
    public boolean check(final BannerMeta bannerMeta) {
        return switch (patternsE) {
            case WHATEVER -> true;
            case REQUIRED -> patterns.equals(bannerMeta.getPatterns());
            case FORBIDDEN -> bannerMeta.getPatterns().isEmpty();
        };
    }
}
