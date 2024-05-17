package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.node.types.PermissionNode;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Factory class for creating LuckPerms events.
 */
public class LuckPermsEventFactory implements EventFactory {

    /**
     * The {@link LuckPerms} API.
     */
    private final LuckPerms luckPermsAPI;

    /**
     * Creates the LuckPerms event factory.
     *
     * @param luckPermsAPI The {@link LuckPerms} API.
     */
    public LuckPermsEventFactory(final LuckPerms luckPermsAPI) {
        this.luckPermsAPI = luckPermsAPI;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String action = instruction.next();

        return switch (action.toLowerCase(Locale.ROOT)) {
            case "addpermission" ->
                    new LuckPermsPermissionEvent(getPermissionNodes(instruction), luckPermsAPI, NodeMap::add);
            case "removepermission" ->
                    new LuckPermsPermissionEvent(getPermissionNodes(instruction), luckPermsAPI, NodeMap::remove);
            default ->
                    throw new InstructionParseException("Unknown action: " + action + ". Expected addPermission or removePermission.");
        };
    }

    private List<PermissionNode> getPermissionNodes(final Instruction instruction) throws InstructionParseException {
        final String unparsedPermissions = instruction.getOptional("permission", "");
        if (unparsedPermissions.isEmpty()) {
            throw new InstructionParseException("Missing permissions argument. Expected permissions:permission1,"
                    + "permission2,permission3,...");
        }
        final List<String> permissions = parseList(unparsedPermissions);
        final List<String> contexts = parseList(instruction.getOptional("context", ""));
        final String value = instruction.getOptional("value", "");
        final long expiry = instruction.getLong(instruction.getOptional("expiry"), 0L);
        final TimeUnit timeUnit = instruction.getEnum(instruction.getOptional("unit"), TimeUnit.class, TimeUnit.DAYS);

        final LuckPermsNodeBuilder luckPermsNodeBuilder = new LuckPermsNodeBuilder();
        return luckPermsNodeBuilder.getNodes(new LuckPermsNodeBuilder.PermissionData(permissions, value, contexts, expiry, timeUnit));
    }

    private List<String> parseList(final String unparsed) {
        if (unparsed.isEmpty()) {
            return List.of();
        }
        return List.of(unparsed.split(","));
    }
}
