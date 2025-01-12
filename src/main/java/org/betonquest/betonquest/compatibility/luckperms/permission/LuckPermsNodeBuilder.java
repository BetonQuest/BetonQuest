package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.context.MutableContextSet;
import net.luckperms.api.node.Node;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Builder class for LuckPerms {@link Node}s.
 * <p>
 * Data class for the permissions.
 *
 * @param permissions A list of permissions to add or remove.
 * @param value       The value of the permission. Either true or false.
 * @param contexts    The contexts for the permission.
 * @param expiry      The expiry time for the permission.
 * @param timeUnit    The {@link TimeUnit} for the expiry time.
 */
public record LuckPermsNodeBuilder(List<VariableString> permissions, VariableString value,
                                   List<VariableString> contexts,
                                   VariableNumber expiry, VariableString timeUnit) {

    /**
     * Builds a list of {@link Node}s.
     *
     * @param profile The {@link Profile} to get the data from.
     * @return A list of {@link Node}s.
     * @throws QuestException If an error occurs while building the nodes.
     */
    public List<Node> getNodes(final Profile profile) throws QuestException {
        final List<Node> buildNodes = new ArrayList<>();
        final String resolvedValue = value.getValue(profile);
        final MutableContextSet contextSet = parseContextSet(contexts, profile);
        final long resolvedExpiry = expiry.getValue(profile).longValue();
        final TimeUnit resolvedTimeUnit = getTimeUnit(timeUnit, profile);
        for (final VariableString permission : permissions) {
            Node node = getNode(permission.getValue(profile));
            if (!resolvedValue.isEmpty()) {
                node = addValue(node, Boolean.parseBoolean(resolvedValue));
            }
            if (!contextSet.isEmpty()) {
                node = addContextSet(node, contextSet);
            }
            if (resolvedExpiry > 0) {
                node = addExpiry(node, resolvedExpiry, resolvedTimeUnit);
            }

            buildNodes.add(node);
        }
        return buildNodes;
    }

    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
    private @NotNull TimeUnit getTimeUnit(final VariableString data, final Profile profile) throws QuestException {
        final String time = data.getValue(profile);
        TimeUnit unit;
        try {
            unit = TimeUnit.valueOf(time.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            unit = TimeUnit.DAYS;
            throw new QuestException("Invalid time unit: " + time + ". Setting default to + '" + unit.name() + "'.", e);
        }
        return unit;
    }

    private Node getNode(final String permission) {
        return Node.builder(permission).build();
    }

    private Node addValue(final Node builder, final boolean value) {
        return builder.toBuilder().value(value).build();
    }

    private Node addContextSet(final Node builder,
                               final MutableContextSet contextSet) {
        return builder.toBuilder().context(contextSet).build();
    }

    private MutableContextSet parseContextSet(final List<VariableString> contexts, final Profile profile) throws QuestException {
        final MutableContextSet contextSet = MutableContextSet.create();
        for (final VariableString context : contexts) {
            final String[] split = context.getValue(profile).split(";");
            contextSet.add(split[0], split[1]);
        }
        return contextSet;
    }

    private Node addExpiry(final Node builder, final long duration,
                           final TimeUnit unit) {
        return builder.toBuilder().expiry(duration, unit).build();
    }
}
