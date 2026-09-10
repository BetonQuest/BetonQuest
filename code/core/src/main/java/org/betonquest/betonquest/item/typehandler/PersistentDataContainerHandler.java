package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.NamespacedKeyParser;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Handles de-/serialization of {@link PersistentDataContainer} values.
 * <p>
 * Only the 6 primitive, string and nested pdc (all without their array variants) types are supported.
 */
public class PersistentDataContainerHandler implements ItemMetaHandler.Standard {

    /**
     * Character to split key-type-value.
     */
    private static final char SPLIT_CHAR = ';';

    /**
     * Expected length of key-type-value.
     */
    private static final int LENGTH = 3;

    /**
     * The empty default Constructor.
     */
    public PersistentDataContainerHandler() {
    }

    @Override
    public Set<String> keys() {
        return Set.of("pdc");
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        final PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.isEmpty()) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        buildStrings(builder, container, "");
        return "\"pdc:" + builder.substring(1) + "\"";
    }

    private void buildStrings(final StringBuilder builder, final PersistentDataContainer container, final String path) {
        if (container.isEmpty()) {
            builder.append(',').append(path);
            return;
        }
        for (final NamespacedKey key : container.getKeys()) {
            if (container.has(key, PersistentDataType.TAG_CONTAINER)) {
                buildStrings(builder, container.get(key, PersistentDataType.TAG_CONTAINER), path + key + ">");
                continue;
            }
            final Object normal = normal(container, key);
            if (normal != null) {
                builder.append(',').append(path).append(key).append(SPLIT_CHAR).append(normal);
            }
        }
    }

    @Nullable
    private Object normal(final PersistentDataContainer container, final NamespacedKey key) {
        for (final PersistentDataType<?, ?> type : List.of(
                PersistentDataType.BYTE, PersistentDataType.SHORT, PersistentDataType.INTEGER, PersistentDataType.LONG,
                PersistentDataType.FLOAT, PersistentDataType.DOUBLE, PersistentDataType.STRING)) {
            if (container.has(key, type)) {
                return type.getComplexType().getSimpleName() + SPLIT_CHAR + container.get(key, type);
            }
        }
        return null;
    }

    @Override
    @Nullable
    public Attribute parse(final Instruction instruction) throws QuestException {
        final ExistenceArgument<List<PdcPath<?>>> argument = ExistenceArgument.applyListOrNull("pdc", instruction.parse(PdcPath::parse));
        if (argument == null) {
            return null;
        }
        return profile -> new Resolved(argument.getValue(profile));
    }

    /**
     * The pdc entry which is optional prefixed/nested by the path.
     *
     * @param path  the path keys to the actual key of the value, if nested
     * @param key   the key of the value
     * @param type  the object data type
     * @param value the object value
     * @param <Z>   the type of the value
     */
    private record PdcPath<Z>(List<NamespacedKey> path, NamespacedKey key, PersistentDataType<?, Z> type, Z value) {

        private static PdcPath<?> parse(final String string) throws QuestException {
            final String[] split = string.split(">");
            final List<NamespacedKey> path = new ArrayList<>(split.length - 1);
            for (int i = 0; i < split.length - 1; i++) {
                path.add(NamespacedKeyParser.parse(split[i]));
            }
            final String last = split[split.length - 1];
            final String[] lastSplit = last.split(String.valueOf(SPLIT_CHAR));
            if (lastSplit.length != LENGTH) {
                throw new QuestException("Invalid key;type;value format: " + last);
            }
            final NamespacedKey key = NamespacedKeyParser.parse(lastSplit[0]);
            try {
                return parseTypeValue(path, key, lastSplit[1], lastSplit[2]);
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Can't parse value for type '%s': %s".formatted(lastSplit[1], e.getMessage()), e);
            }
        }

        private static PdcPath<?> parseTypeValue(final List<NamespacedKey> path, final NamespacedKey key,
                                                 final String typeString, final String valueString) throws QuestException {
            final PersistentDataType<?, ?> type;
            final Object value;
            switch (typeString.toUpperCase(Locale.ROOT)) {
                case "BYTE" -> {
                    type = PersistentDataType.BYTE;
                    value = Byte.parseByte(valueString);
                }
                case "SHORT" -> {
                    type = PersistentDataType.SHORT;
                    value = Short.parseShort(valueString);
                }
                case "INTEGER" -> {
                    type = PersistentDataType.INTEGER;
                    value = Integer.parseInt(valueString);
                }
                case "LONG" -> {
                    type = PersistentDataType.LONG;
                    value = Long.parseLong(valueString);
                }
                case "FLOAT" -> {
                    type = PersistentDataType.FLOAT;
                    value = Float.parseFloat(valueString);
                }
                case "DOUBLE" -> {
                    type = PersistentDataType.DOUBLE;
                    value = Double.parseDouble(valueString);
                }
                case "STRING" -> {
                    type = PersistentDataType.STRING;
                    value = valueString;
                }
                default -> throw new QuestException("unknown persistent data type: " + typeString);
            }
            return new PdcPath(path, key, type, value);
        }

        private void apply(final PersistentDataContainer container) {
            applyInner(container, path);
        }

        private void applyInner(final PersistentDataContainer container, final List<NamespacedKey> innerPath) {
            if (innerPath.isEmpty()) {
                container.set(key, type, value);
                return;
            }
            final NamespacedKey workingKey = innerPath.get(0);
            final PersistentDataContainer target;
            if (container.has(workingKey, PersistentDataType.TAG_CONTAINER)) {
                target = container.get(workingKey, PersistentDataType.TAG_CONTAINER);
                assert target != null;
            } else {
                target = container.getAdapterContext().newPersistentDataContainer();
            }
            applyInner(target, innerPath.subList(1, innerPath.size()));
            container.set(workingKey, PersistentDataType.TAG_CONTAINER, target);
        }

        private boolean check(final PersistentDataContainer container) {
            PersistentDataContainer target = container;
            for (final NamespacedKey key : path) {
                if (target.has(key, PersistentDataType.TAG_CONTAINER)) {
                    target = target.get(key, PersistentDataType.TAG_CONTAINER);
                    assert target != null;
                } else {
                    return false;
                }
            }
            return target.has(key, type) && value.equals(target.get(key, type));
        }
    }

    /**
     * The resolved attribute.
     *
     * @param pdc The pdc entries.
     */
    private record Resolved(Pair<Existence, List<PdcPath<?>>> pdc) implements ResolvedAttribute.Standard {

        @Override
        public void populate(final ItemMeta meta) {
            final List<PdcPath<?>> list = pdc.getRight();
            if (list == null) {
                return;
            }
            final PersistentDataContainer container = meta.getPersistentDataContainer();
            list.forEach(pdc -> pdc.apply(container));
        }

        @Override
        public boolean check(final ItemMeta meta) {
            return switch (pdc.getLeft()) {
                case WHATEVER -> true;
                case REQUIRED -> {
                    final PersistentDataContainer container = meta.getPersistentDataContainer();
                    for (final PdcPath<?> tree : pdc.getRight()) {
                        if (!tree.check(container)) {
                            yield false;
                        }
                    }
                    yield true;
                }
                case FORBIDDEN -> meta.getPersistentDataContainer().isEmpty();
            };
        }
    }
}
