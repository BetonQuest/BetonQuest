package pl.betoncraft.betonquest.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A method of selectinig blocks using a quasi similar method as Majong. 1.13+ version.
 * <p>
 * Block selector format:
 * prefix:type[state=value,...]
 * <p>
 * Where:
 * - prefix - (optional) Prefix of type. If missing will assume to be minecraft
 * - type - Type of block. IE: stone
 * - state flags - (optional) Comma separate list of states contained in brackets
 * <p>
 * Type can contain the wildcard character '*'. For example:
 * - *_LOG - Match everything ending with _LOG
 * - * - Match everything
 * <p>
 * State flags:
 * - If a state flag is missing, then it will not be compared against. If it is set then that state
 * must be set.
 * <p>
 * Example:
 * - redstone_wird[power=5] - Tests for all redstone_wire of power 5, irregardless of direction
 */
public class BlockSelector {
    private final List<Material> materials;
    private final Map<String, String> states;

    public BlockSelector(final String block) throws InstructionParseException {
        final String[] selectorParts = getSelectorParts(block);
        materials = getMaterials(selectorParts[0], selectorParts[1]);
        states = getStates(selectorParts[2]);

        if (materials.isEmpty()) {
            throw new InstructionParseException("Invalid selector, no material found for '" + block + "'!");
        }
    }

    public BlockSelector(final Block block) throws InstructionParseException {
        this(block.getBlockData().getAsString());
    }

    @Override
    public String toString() {
        return materials.toString() + (states == null ? "" : "[" + states.toString() + "]");
    }

    public Material getRandomMaterial() {
        final Random random = new Random();
        return materials.get(random.nextInt(materials.size()));
    }

    private String getStateAsString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[");

        for (final Map.Entry<String, String> entry : states.entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append("]");

        return builder.toString();
    }

    public void setToBlock(final Block block, final boolean applyPhysics) {
        final BlockState state = block.getState();

        if (states == null) {
            state.setType(getRandomMaterial());
        } else {
            try {
                state.setBlockData(Bukkit.createBlockData(getRandomMaterial(), getStateAsString()));
            } catch (final IllegalArgumentException exception) {
                LogUtils.getLogger().log(Level.SEVERE, "Could not place block '" + toString() + "'! Probably the block has a invalid blockstate: " + exception.getMessage(), exception);
            }
        }

        state.update(true, applyPhysics);
    }

    /**
     * Return true if material matches our selector. State is ignored
     */
    public boolean match(final Material material) {
        return materials.contains(material);
    }

    /**
     * Return true if block matches our selector
     *
     * @param block Block to test
     * @return boolean True if a match occurred
     */
    public boolean match(final Block block, final boolean exactMatch) {
        if (!match(block.getBlockData().getMaterial())) {
            return false;
        }

        final Map<String, String> blockStates = getStates(getSelectorParts(block.getBlockData().getAsString())[2]);
        if (states == null) {
            return !exactMatch || blockStates == null;
        }
        if (exactMatch && states.size() != blockStates.size()) {
            return false;
        }

        for (final String singleState : states.keySet()) {
            if (!blockStates.containsKey(singleState)) {
                return false;
            }

            final String blockState = blockStates.get(singleState);
            final String state = states.get(singleState);
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
            final String[] parts = restSelector.split(":");
            selectorParts[0] = parts[0].toLowerCase(Locale.ROOT);
            selectorParts[1] = parts[1].toLowerCase(Locale.ROOT);
        } else {
            selectorParts[0] = "minecraft";
            selectorParts[1] = restSelector.toLowerCase(Locale.ROOT);
        }

        return selectorParts;
    }

    private int getBracketIndex(final String text, final int openedBrackets) {
        final int indexOpen = text.lastIndexOf("[");
        final int indexClose = text.lastIndexOf("]");
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

    @SuppressWarnings("deprecation")
    private List<Material> getMaterials(final String namespaceString, final String keyString) {
        final List<Material> materials = new ArrayList<>();
        final Material fullMatch = Material.getMaterial(namespaceString + ":" + keyString);
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

        final Pattern namespacePattern = Pattern.compile("^" + namespaceString + "$");
        final Pattern keyPattern = Pattern.compile("^" + keyString + "$");
        for (final Material m : Material.values()) {
            final NamespacedKey namespacedKey;
            try {
                namespacedKey = m.getKey();
            } catch (final IllegalArgumentException e) {
                continue;
            }
            final Matcher namespaceMatcher = namespacePattern.matcher(namespacedKey.getNamespace());
            if (!namespaceMatcher.find()) {
                continue;
            }
            final Matcher keyMatcher = keyPattern.matcher(namespacedKey.getKey());
            if (!keyMatcher.find()) {
                continue;
            }
            materials.add(m);
        }

        return materials;
    }

    private Map<String, String> getStates(final String statesString) {
        if (statesString == null || statesString.isEmpty()) {
            return null;
        }
        final Map<String, String> states = new HashMap<>();
        for (final String state : statesString.split(",")) {
            final String[] parts = state.split("=", 2);
            states.put(parts[0].trim(), parts[1].trim());
        }
        return states;
    }
}
