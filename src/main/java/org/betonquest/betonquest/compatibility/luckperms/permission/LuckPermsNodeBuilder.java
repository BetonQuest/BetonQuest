package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.context.MutableContextSet;
import net.luckperms.api.node.types.PermissionNode;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Builder class for LuckPerms {@link PermissionNode}s.
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
     * Builds a list of {@link PermissionNode}s.
     *
     * @param profile The {@link Profile} to get the data from.
     * @return A list of {@link PermissionNode}s.
     * @throws QuestRuntimeException If an error occurs while building the nodes.
     */
    public List<PermissionNode> getNodes(final Profile profile) throws QuestRuntimeException {
        final List<PermissionNode> buildNodes = new ArrayList<>();
        final String resolvedValue = value.getString(profile);
        final MutableContextSet contextSet = parseContextSet(contexts, profile);
        final long resolvedExpiry = (long) expiry.getDouble(profile);
        final TimeUnit resolvedTimeUnit = getTimeUnit(timeUnit, profile);
        for (final VariableString permission : permissions) {
            PermissionNode.Builder builder = nodeBuilder(permission.getString(profile));
            if (!resolvedValue.isEmpty()) {
                builder = addValue(builder, Boolean.parseBoolean(resolvedValue));
            }
            if (!contextSet.isEmpty()) {
                builder = addContextSet(builder, contextSet);
            }
            if (resolvedExpiry > 0) {
                builder = addExpiry(builder, resolvedExpiry, resolvedTimeUnit);
            }

            buildNodes.add(buildNode(builder));
        }
        return buildNodes;
    }

    private @NotNull TimeUnit getTimeUnit(final VariableString data, final Profile profile) throws QuestRuntimeException {
        final String time = data.getString(profile);
        TimeUnit unit;
        try {
            unit = TimeUnit.valueOf(time.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            unit = TimeUnit.DAYS;
            throw new QuestRuntimeException("Invalid time unit: " + time + ". Setting default to + '" + unit.name() + "'.", e);
        }
        return unit;
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

    private MutableContextSet parseContextSet(final List<VariableString> contexts, final Profile profile) {
        final MutableContextSet contextSet = MutableContextSet.create();
        for (final VariableString context : contexts) {
            final String[] split = context.getString(profile).split(";");
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
}
