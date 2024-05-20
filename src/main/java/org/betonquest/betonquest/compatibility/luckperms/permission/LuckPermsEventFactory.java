package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.NodeMap;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.ArrayList;
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
                    new LuckPermsPermissionEvent(getPermissionNodeBuilder(instruction), luckPermsAPI, NodeMap::add);
            case "removepermission" ->
                    new LuckPermsPermissionEvent(getPermissionNodeBuilder(instruction), luckPermsAPI, NodeMap::remove);
            default ->
                    throw new InstructionParseException("Unknown action: " + action + ". Expected addPermission or removePermission.");
        };
    }

    private LuckPermsNodeBuilder getPermissionNodeBuilder(final Instruction instruction) throws InstructionParseException {
        final String unparsedPermissions = instruction.getOptional("permission", "");
        if (unparsedPermissions.isEmpty()) {
            throw new InstructionParseException("Missing permissions argument. Expected permissions:permission1,"
                    + "permission2,permission3,...");
        }
        final QuestPackage pack = instruction.getPackage();
        final List<VariableString> permissions = parseList(pack, unparsedPermissions);
        final List<VariableString> contexts = parseList(pack, instruction.getOptional("context", ""));
        final VariableString value = new VariableString(pack, instruction.getOptional("value", ""));
        final VariableNumber expiry = instruction.getVarNum(instruction.getOptional("expiry", "0"));
        final VariableString timeUnit = new VariableString(pack, instruction.getOptional("unit", TimeUnit.DAYS.name()));

        return new LuckPermsNodeBuilder(permissions, value, contexts, expiry, timeUnit);
    }

    private List<VariableString> parseList(final QuestPackage pack, final String unparsed) throws InstructionParseException {
        if (unparsed.isEmpty()) {
            return List.of();
        }
        final List<VariableString> list = new ArrayList<>();
        for (final String input : unparsed.split(",")) {
            list.add(new VariableString(pack, input));
        }
        return list;
    }
}
