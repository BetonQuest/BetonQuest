package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.context.MutableContextSet;
import net.luckperms.api.node.Node;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;

import java.util.ArrayList;
import java.util.List;
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
public record LuckPermsNodeBuilder(Variable<List<String>> permissions, Variable<String> value,
                                   Variable<List<String>> contexts,
                                   Variable<Number> expiry, Variable<TimeUnit> timeUnit) {

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
        final TimeUnit resolvedTimeUnit = timeUnit.getValue(profile);
        for (final String permission : permissions.getValue(profile)) {
            Node node = getNode(permission);
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

    private MutableContextSet parseContextSet(final Variable<List<String>> contexts, final Profile profile) throws QuestException {
        final MutableContextSet contextSet = MutableContextSet.create();
        for (final String context : contexts.getValue(profile)) {
            final String[] split = context.split(";");
            contextSet.add(split[0], split[1]);
        }
        return contextSet;
    }

    private Node addExpiry(final Node builder, final long duration,
                           final TimeUnit unit) {
        return builder.toBuilder().expiry(duration, unit).build();
    }
}
