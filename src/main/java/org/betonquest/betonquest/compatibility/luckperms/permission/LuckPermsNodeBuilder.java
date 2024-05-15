package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.context.MutableContextSet;
import net.luckperms.api.node.types.PermissionNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Builder class for LuckPerms {@link PermissionNode}s.
 */
public class LuckPermsNodeBuilder {

    /**
     * Default constructor.
     */
    public LuckPermsNodeBuilder() {
        // Empty
    }

    /**
     * Builds a list of {@link PermissionNode}s from the given {@link PermissionData}.
     *
     * @param data The {@link PermissionData} to build the {@link PermissionNode}s from.
     * @return A list of {@link PermissionNode}s.
     */
    public List<PermissionNode> getNodes(final PermissionData data) {
        final List<PermissionNode> buildNodes = new ArrayList<>();
        for (final String permission : data.permissions) {
            PermissionNode.Builder builder = nodeBuilder(permission);
            if (!data.value.isEmpty()) {
                builder = addValue(builder, Boolean.parseBoolean(data.value));
            }
            if (!data.contexts.isEmpty()) {
                builder = addContextSet(builder, parseContextSet(data.contexts));
            }
            if (data.expiry > 0) {
                builder = addExpiry(builder, data.expiry, data.timeUnit);
            }

            buildNodes.add(buildNode(builder));
        }
        return buildNodes;
    }

    private PermissionNode.Builder nodeBuilder(final String permission) {
        return PermissionNode.builder(permission);
    }

    private PermissionNode.Builder addValue(final PermissionNode.Builder builder, final boolean value) {
        return builder.value(value);
    }

    private PermissionNode.Builder addContextSet(final PermissionNode.Builder builder,
                                                 final MutableContextSet contextSet) {
        return builder.context(contextSet);
    }

    private MutableContextSet parseContextSet(final List<String> contexts) {
        final MutableContextSet contextSet = MutableContextSet.create();
        for (final String context : contexts) {
            final String[] split = context.split(";");
            contextSet.add(split[0], split[1]);
        }
        return contextSet;
    }

    private PermissionNode.Builder addExpiry(final PermissionNode.Builder builder, final long duration,
                                             final TimeUnit unit) {
        return builder.expiry(duration, unit);
    }

    private PermissionNode buildNode(final PermissionNode.Builder builder) {
        return builder.build();
    }

    /**
     * Data class for the permissions.
     *
     * @param permissions A list of permissions to add or remove.
     * @param value       The value of the permission. Either true or false.
     * @param contexts    The contexts for the permission.
     * @param expiry      The expiry time for the permission.
     * @param timeUnit    The {@link TimeUnit} for the expiry time.
     */
    public record PermissionData(List<String> permissions, String value, List<String> contexts, long expiry,
                                 TimeUnit timeUnit) {
    }
}
