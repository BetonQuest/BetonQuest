package org.betonquest.betonquest.utils;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A method of selecting blocks using regex and block states.
 * <p>
 * Block selector format:
 * namespace:material[state=value,...]
 * <p>
 * Where:
 * - namespace - (optional) The material namespace. If left out then it will be assumed to be 'minecraft'. Regex allowed
 * - material - The material the block is made of. Regex and Tags are allowed
 * - state - (optional) The block states can be provided in a comma separated `key=value` list surrounded by square
 * brackets. Regex allowed
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.GodClass"})
public class BlockSelector {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

    private final List<Material> materials;

    private final Map<String, String> states;

    private final Random random = new Random();

    /**
     * Create a {@link BlockSelector} from a {@link String}.
     *
     * @param block The {@link String} of the {@link BlockSelector} in the format of {@link BlockData#getAsString()}
     * @throws QuestException Is thrown, if no material match that selector string
     */
    public BlockSelector(final String block) throws QuestException {
        final String[] selectorParts = getSelectorParts(block);
        materials = getMaterials(selectorParts[0], selectorParts[1]);
        states = getStates(selectorParts[2]);

        if (materials.isEmpty()) {
            throw new QuestException("Invalid selector, no material found for '" + block + "'!");
        }
    }

    /**
     * Create a {@link BlockSelector} from a {@link Block}.
     *
     * @param block The {@link Block} of the {@link BlockSelector}
     * @throws QuestException Is thrown, if no material match that selector string
     */
    public BlockSelector(final Block block) throws QuestException {
        this(block.getBlockData().getAsString());
    }

    /**
     * Return the {@link BlockSelector} as a string in a readable format. All matched blocks will be listed.
     *
     * @return The readable {@link BlockSelector}
     */
    @Override
    public String toString() {
        return materials + (states.isEmpty() ? "" : "[" + states + "]");
    }

    /**
     * Get a random {@link Material}. If only one Material is represented by this {@link BlockSelector} this will be returned.
     *
     * @return A {@link Material}
     */
    public Material getRandomMaterial() {
        return materials.get(random.nextInt(materials.size()));
    }

    /**
     * Get a list of all {@link Material}s that match this {@link BlockSelector}.
     *
     * @return A copy of the list of matching {@link Material}s
     */
    public List<Material> getMaterials() {
        return new ArrayList<>(materials);
    }

    private String getStateAsString() {
        final StringBuilder builder = new StringBuilder();
        builder.append('[');

        for (final Map.Entry<String, String> entry : states.entrySet()) {
            builder.append(entry.getKey()).append('=').append(entry.getValue()).append(',');
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append(']');

        return builder.toString();
    }

    /**
     * Get a BlockData.
     * The Material is randomly selected from {@link BlockSelector#getRandomMaterial()}.
     * If the states contain regex {@link IllegalArgumentException} is thrown, if you apply this with
     * {@link BlockState#setBlockData(BlockData)}.
     *
     * @return A {@link BlockData}
     */
    public BlockData getBlockData() {
        if (states.isEmpty()) {
            return Bukkit.createBlockData(getRandomMaterial());
        } else {
            return Bukkit.createBlockData(getRandomMaterial(), getStateAsString());
        }
    }

    /**
     * Set the {@link BlockData} returned from {@link BlockSelector#getBlockData()} to a block.
     *
     * @param block        The block that is changed by the {@link BlockData}
     * @param applyPhysics If physics should be active for that block
     */
    public void setToBlock(final Block block, final boolean applyPhysics) {
        final BlockState state = block.getState();

        try {
            state.setBlockData(getBlockData());
        } catch (final IllegalArgumentException exception) {
            log.error("Could not place block '" + this + "'! Probably the block has a invalid blockstate: " + exception.getMessage(), exception);
        }

        state.update(true, applyPhysics);
    }

    /**
     * Checks if a {@link Material} matched this {@link BlockSelector}. The {@link BlockState} is ignored.
     *
     * @param material The {@link Material} that should be compared
     * @return True if the {@link Material} is represented by this {@link BlockSelector}
     */
    public boolean match(final Material material) {
        return materials.contains(material);
    }

    /**
     * Checks if a {@link Block} matched this {@link BlockSelector}.
     *
     * @param block      The {@link Block} that should be compared
     * @param exactMatch If false, the target block may have more {@link BlockState}s than this {@link BlockSelector}.
     *                   If true, the target block is not allowed to have more {@link BlockState}s than this
     *                   {@link BlockSelector}.
     * @return True if the {@link Material} is represented by this {@link BlockSelector} and the {@link BlockState} matches.
     */
    public boolean match(final Block block, final boolean exactMatch) {
        if (!match(block.getBlockData().getMaterial())) {
            return false;
        }

        final Map<String, String> blockStates = getStates(getSelectorParts(block.getBlockData().getAsString())[2]);
        if (states.isEmpty()) {
            return !exactMatch || blockStates.isEmpty();
        }
        if (exactMatch && states.size() != blockStates.size()) {
            return false;
        }

        for (final Map.Entry<String, String> entry : states.entrySet()) {
            final String singleState = entry.getKey();
            if (!blockStates.containsKey(singleState)) {
                return false;
            }

            final String blockState = blockStates.get(singleState);
            final String state = entry.getValue();
            if (!blockState.equals(state)) {
                final Pattern statePattern = Pattern.compile("^" + state + "$");
                final Matcher stateMatcher = statePattern.matcher(blockState);
                if (!stateMatcher.find()) {
                    return false;
                }
            }
        }
        return true;
    }

    private String[] getSelectorParts(final String selector) {
        final String[] selectorParts = new String[3];
        String restSelector = selector;

        if (restSelector.endsWith("]")) {
            final int index = getBracketIndex(restSelector, 0);
            selectorParts[2] = restSelector.substring(index + 1, restSelector.length() - 1).toLowerCase(Locale.ROOT);
            restSelector = restSelector.substring(0, index);
        }

        if (restSelector.contains(":")) {
            final int index = restSelector.indexOf(':');
            selectorParts[0] = index == 0 ? "minecraft" : restSelector.substring(0, index).toLowerCase(Locale.ROOT);
            selectorParts[1] = restSelector.substring(index + 1).toLowerCase(Locale.ROOT);
        } else {
            selectorParts[0] = "minecraft";
            selectorParts[1] = restSelector.toLowerCase(Locale.ROOT);
        }

        return selectorParts;
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private int getBracketIndex(final String text, final int openedBrackets) {
        final int indexOpen = text.lastIndexOf('[');
        final int indexClose = text.lastIndexOf(']');
        if (indexOpen == -1 && indexClose == -1) {
            return -1;
        }
        if (indexOpen > indexClose) {
            if (openedBrackets == 1) {
                return indexOpen;
            }
            return getBracketIndex(text.substring(0, indexOpen), openedBrackets - 1);
        }
        if (indexClose > indexOpen) {
            return getBracketIndex(text.substring(0, indexClose), openedBrackets + 1);
        }
        return -1;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private List<Material> getMaterials(final String namespaceString, final String keyString) throws QuestException {
        final List<Material> materials = new ArrayList<>();
        final Material fullMatch = Material.matchMaterial(namespaceString + ":" + keyString);
        if (fullMatch != null) {
            materials.add(fullMatch);
            return materials;
        }

        if (keyString.contains(":")) {
            final String[] groupParts = keyString.split(":");
            final NamespacedKey namespacedKey = new NamespacedKey(namespaceString, groupParts[1]);
            final Tag<Material> tag = Bukkit.getTag(groupParts[0], namespacedKey, Material.class);
            if (tag != null) {
                materials.addAll(tag.getValues());
            }
            return materials;
        }

        final Pattern namespacePattern;
        final Pattern keyPattern;
        try {
            namespacePattern = Pattern.compile("^" + namespaceString + "$");
            keyPattern = Pattern.compile("^" + keyString + "$");
        } catch (final PatternSyntaxException exception) {
            throw new QuestException("Invalid Regex: " + exception.getMessage(), exception);
        }
        for (final Material m : Material.values()) {
            if (m.isLegacy()) {
                continue;
            }
            final NamespacedKey namespacedKey = m.getKey();
            final Matcher namespaceMatcher = namespacePattern.matcher(namespacedKey.getNamespace());
            if (!namespaceMatcher.matches()) {
                continue;
            }
            final Matcher keyMatcher = keyPattern.matcher(namespacedKey.getKey());
            if (!keyMatcher.matches()) {
                continue;
            }
            materials.add(m);
        }

        return materials;
    }

    private Map<String, String> getStates(@Nullable final String statesString) {
        final Map<String, String> states = new HashMap<>();
        if (statesString == null || statesString.isEmpty()) {
            return states;
        }
        for (final String state : statesString.split(",")) {
            final String[] parts = state.split("=", 2);
            states.put(parts[0].trim(), parts[1].trim());
        }
        return states;
    }
}
