package org.betonquest.betonquest.mc_1_21_4.item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.BooleanParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles de-/serialization of CustomModelData.
 */
@SuppressWarnings("UnstableApiUsage")
public class UpdatedCustomModelDataHandler implements ItemMetaHandler.Standard {

    /**
     * The empty default Constructor.
     */
    public UpdatedCustomModelDataHandler() {
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
    @Nullable
    public Attribute parse(final Instruction instruction) throws QuestException {
        final Optional<Argument<CustomModelData>> data = instruction.parse(resolvedString -> {
            try {
                return CustomModelData.parseCmd(resolvedString);
            } catch (final QuestException e) {
                throw new QuestException("Could not parse custom-model-data '" + resolvedString + "': " + e.getMessage(), e);
            }
        }).get("custom-model-data");
        final FlagArgument<Boolean> noData = instruction.bool().getFlag("no-custom-model-data", true);
        final ExistenceArgument<@Nullable NamespacedKey> model = ExistenceArgument.applyOrNull("item-model", instruction.namespacedKey());
        final FlagArgument<Boolean> noModel = instruction.bool().getFlag("no-item-model", true);
        if (data.isEmpty() && noData.getState() == FlagState.ABSENT && model == null && noModel.getState() == FlagState.ABSENT) {
            return null;
        }
        return new NonResolved(data.orElseGet(() -> profile -> new CustomModelData()), noData, ExistenceArgument.fallback(model), noModel);
    }

    /**
     * The attribute with placeholders.
     *
     * @param data    The CustomModelData with existence.
     * @param noData  If 'custom model data' is forbidden.
     * @param model   The item model with existence.
     * @param noModel If 'item_model' is forbidden.
     */
    private record NonResolved(Argument<CustomModelData> data, FlagArgument<Boolean> noData,
                               Argument<Pair<Existence, NamespacedKey>> model, FlagArgument<Boolean> noModel)
            implements Attribute {

        @Override
        public ResolvedAttribute<ItemMeta> resolve(@Nullable final Profile profile) throws QuestException {
            final CustomModelData data = this.data.getValue(profile);
            final boolean noData = this.noData.getValue(profile).orElse(false);
            final Pair<Existence, @Nullable NamespacedKey> model = this.model.getValue(profile);
            final boolean noModel = this.noModel.getValue(profile).orElse(false);
            return new Resolved(data, model, noModel, noData);
        }
    }

    /**
     * The resolved attribute.
     *
     * @param data    The CustomModelData with existence.
     * @param noData  If 'custom model data' is forbidden.
     * @param model   The item model with existence.
     * @param noModel If 'item_model' is forbidden.
     */
    private record Resolved(CustomModelData data, Pair<Existence, @Nullable NamespacedKey> model, boolean noModel,
                            boolean noData) implements ResolvedAttribute.Standard {

        @Override
        public void populate(final ItemMeta meta) {
            if (data.existence() == Existence.REQUIRED) {
                data.set(meta);
            }
            if (model.getLeft() == Existence.REQUIRED) {
                meta.setItemModel(model.getRight());
            }
        }

        @Override
        public boolean check(final ItemMeta meta) {
            if (noModel && !meta.hasItemModel()) {
                return false;
            }
            final Existence modelE = model.getLeft();
            return (modelE == Existence.WHATEVER
                    || modelE == Existence.FORBIDDEN && !meta.hasItemModel()
                    || modelE == Existence.REQUIRED && meta.hasItemModel() && Objects.equals(model.getRight(), meta.getItemModel())
            ) && data.check(meta, noData);
        }
    }

    /**
     * The resolved custom-model-data argument.
     *
     * @param existence The required custom model data existence.
     * @param floats    The CustomModelData floats.
     * @param flags     The CustomModelData flags.
     * @param strings   The CustomModelData strings.
     * @param colors    The CustomModelData colors.
     */
    private record CustomModelData(Existence existence, List<Float> floats, List<Boolean> flags,
                                   List<String> strings, List<Color> colors) {

        private CustomModelData() {
            this(Existence.WHATEVER, List.of(), List.of(), List.of(), List.of());
        }

        @SuppressFBWarnings("SF_SWITCH_FALLTHROUGH")
        @SuppressWarnings({"PMD.ImplicitSwitchFallThrough", "PMD.CyclomaticComplexity"})
        private static CustomModelData parseCmd(final String data) throws QuestException {
            final List<Float> floatList;
            List<Boolean> flagList = List.of();
            List<String> stringList = List.of();
            List<Color> colorList = List.of();
            final String[] split = data.split(";", -1);
            switch (split.length) {
                case 4:
                    final String[] colors = StringUtils.split(split[3], ",");
                    colorList = new ArrayList<>(colors.length);
                    for (final String part : colors) {
                        try {
                            colorList.add(Color.fromARGB(Integer.parseUnsignedInt(part, 16)));
                        } catch (final IllegalArgumentException e) {
                            throw new QuestException("Invalid color: " + part, e);
                        }
                    }
                case 3:
                    stringList = Arrays.asList(StringUtils.split(split[2], ","));
                case 2:
                    final String[] booleans = StringUtils.split(split[1], ",");
                    flagList = new ArrayList<>(booleans.length);
                    final BooleanParser booleanParser = new BooleanParser();
                    for (final String part : booleans) {
                        flagList.add(booleanParser.apply(part));
                    }
                case 1:
                    final String[] floats = StringUtils.split(split[0], ",");
                    floatList = new ArrayList<>(floats.length);
                    for (final String part : floats) {
                        try {
                            floatList.add(Float.parseFloat(part));
                        } catch (final NumberFormatException e) {
                            throw new QuestException("Could not parse number: " + part, e);
                        }
                    }
                    return new CustomModelData(Existence.REQUIRED, floatList, flagList, stringList, colorList);
                default:
                    throw new QuestException("Invalid length: " + split.length);
            }
        }

        private void set(final ItemMeta meta) {
            final CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
            cmd.setFloats(floats);
            cmd.setFlags(flags);
            cmd.setStrings(strings);
            cmd.setColors(colors);
            meta.setCustomModelDataComponent(cmd);
        }

        private boolean check(final ItemMeta meta, final boolean noData) {
            if (noData) {
                return checkEmpty(meta.getCustomModelDataComponent());
            }
            return existence == Existence.WHATEVER
                    || existence == Existence.FORBIDDEN && checkEmpty(meta.getCustomModelDataComponent())
                    || existence == Existence.REQUIRED && checkRequired(meta.getCustomModelDataComponent());
        }

        private boolean checkEmpty(final CustomModelDataComponent cmd) {
            return cmd.getFloats().isEmpty() && cmd.getFlags().isEmpty() && cmd.getStrings().isEmpty() && cmd.getColors().isEmpty();
        }

        private boolean checkRequired(final CustomModelDataComponent cmd) {
            return floats.equals(cmd.getFloats()) && flags.equals(cmd.getFlags()) && strings.equals(cmd.getStrings())
                    && checkColors(cmd);
        }

        /**
         * The color gets modified inside the component (alpha channel) so it is not equal to the actual given color.
         */
        private boolean checkColors(final CustomModelDataComponent cmd) {
            final List<Color> current = cmd.getColors();
            cmd.setColors(this.colors);
            final List<Color> wanted = cmd.getColors();
            return wanted.equals(current);
        }
    }
}
