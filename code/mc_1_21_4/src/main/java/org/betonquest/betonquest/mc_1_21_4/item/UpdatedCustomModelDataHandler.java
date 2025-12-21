package org.betonquest.betonquest.mc_1_21_4.item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.item.typehandler.Existence;
import org.betonquest.betonquest.item.typehandler.ItemMetaHandler;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles de-/serialization of CustomModelData.
 */
@SuppressWarnings("UnstableApiUsage")
public class UpdatedCustomModelDataHandler implements ItemMetaHandler<ItemMeta> {

    /**
     * The required custom model data existence.
     */
    private Existence existence = Existence.WHATEVER;

    /**
     * The CustomModelData floats.
     */
    private List<Float> floats = List.of();

    /**
     * The CustomModelData flags.
     */
    private List<Boolean> flags = List.of();

    /**
     * The CustomModelData strings.
     */
    private List<String> strings = List.of();

    /**
     * The CustomModelData colors.
     */
    private List<Color> colors = List.of();

    /**
     * The required 'item_model' existence.
     */
    private Existence modelE = Existence.WHATEVER;

    /**
     * The 'item_model' set.
     */
    @Nullable
    private NamespacedKey model;

    /**
     * The empty default Constructor.
     */
    public UpdatedCustomModelDataHandler() {
    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("custom-model-data", "no-custom-model-data", "item-model", "no-item-model");
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        final StringBuilder builder = new StringBuilder(100);
        if (meta.hasCustomModelData()) {
            final CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
            builder.append(" custom-model-data:")
                    .append(cmd.getFloats().stream().map(Object::toString).collect(Collectors.joining(",")))
                    .append(';')
                    .append(cmd.getFlags().stream().map(Object::toString).collect(Collectors.joining(",")))
                    .append(';')
                    .append(Strings.join(cmd.getStrings(), ','))
                    .append(';')
                    .append(cmd.getColors().stream().map(color -> Integer.toHexString(color.asARGB()).toUpperCase(Locale.ROOT))
                            .collect(Collectors.joining(",")));
        }
        if (meta.hasItemModel()) {
            builder.append(" item-model:").append(meta.getItemModel());
        }
        return builder.isEmpty() ? null : builder.substring(1);
    }

    @Override
    public void set(final String key, final String data) throws QuestException {
        switch (key) {
            case "custom-model-data" -> {
                this.existence = Existence.REQUIRED;
                try {
                    setCmd(data);
                } catch (final QuestException e) {
                    throw new QuestException("Could not parse custom-model-data '" + data + "': " + e.getMessage(), e);
                }
            }
            case "no-custom-model-data" -> this.existence = Existence.FORBIDDEN;
            case "item-model" -> {
                this.modelE = Existence.REQUIRED;
                this.model = Utils.getNN(NamespacedKey.fromString(data), "The item-model '" + data + "' could not be parsed!");
            }
            case "no-item-model" -> this.modelE = Existence.FORBIDDEN;
            default -> throw new QuestException("Unknown custom model data key: " + key);
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        if (existence == Existence.REQUIRED) {
            final CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
            cmd.setFloats(floats);
            cmd.setFlags(flags);
            cmd.setStrings(strings);
            cmd.setColors(colors);
            meta.setCustomModelDataComponent(cmd);
        }
        if (modelE == Existence.REQUIRED) {
            meta.setItemModel(model);
        }
    }

    @Override
    public boolean check(final ItemMeta data) {
        if (!(modelE == Existence.WHATEVER
                || modelE == Existence.FORBIDDEN && !data.hasItemModel()
                || modelE == Existence.REQUIRED && data.hasItemModel() && Objects.equals(model, data.getItemModel()))) {
            return false;
        }
        return existence == Existence.WHATEVER
                || existence == Existence.FORBIDDEN && !data.hasCustomModelData()
                || existence == Existence.REQUIRED && data.hasCustomModelData() && check(data.getCustomModelDataComponent());
    }

    private boolean check(final CustomModelDataComponent cmd) {
        return floats.equals(cmd.getFloats()) && flags.equals(cmd.getFlags()) && strings.equals(cmd.getStrings())
                && colors.equals(cmd.getColors());
    }

    @SuppressFBWarnings("SF_SWITCH_FALLTHROUGH")
    @SuppressWarnings({"PMD.ImplicitSwitchFallThrough", "PMD.CyclomaticComplexity"})
    private void setCmd(final String data) throws QuestException {
        final String[] split = data.split(";", -1);
        switch (split.length) {
            case 4:
                final String[] colors = StringUtils.split(split[3], ",");
                this.colors = new ArrayList<>(colors.length);
                for (final String part : colors) {
                    try {
                        this.colors.add(Color.fromARGB(Integer.parseUnsignedInt(part, 16)));
                    } catch (final IllegalArgumentException e) {
                        throw new QuestException("Invalid color: " + part, e);
                    }
                }
            case 3:
                this.strings = Arrays.asList(StringUtils.split(split[2], ","));
            case 2:
                final String[] booleans = StringUtils.split(split[1], ",");
                this.flags = new ArrayList<>(booleans.length);
                for (final String part : booleans) {
                    this.flags.add(DefaultArgumentParsers.BOOLEAN.apply(part));
                }
            case 1:
                final String[] floats = StringUtils.split(split[0], ",");
                this.floats = new ArrayList<>(floats.length);
                for (final String part : floats) {
                    try {
                        this.floats.add(Float.parseFloat(part));
                    } catch (final NumberFormatException e) {
                        throw new QuestException("Could not parse number: " + part, e);
                    }
                }
                break;
            default:
                throw new QuestException("Invalid length: " + split.length);
        }
    }
}
