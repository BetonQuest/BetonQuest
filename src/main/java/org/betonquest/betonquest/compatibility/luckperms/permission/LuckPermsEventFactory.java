package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.NodeMap;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;

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
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.next();

        return switch (action.toLowerCase(Locale.ROOT)) {
            case "addpermission" ->
                    new LuckPermsPermissionEvent(getNodeBuilder(instruction), luckPermsAPI, NodeMap::add);
            case "removepermission" ->
                    new LuckPermsPermissionEvent(getNodeBuilder(instruction), luckPermsAPI, NodeMap::remove);
            default ->
                    throw new QuestException("Unknown action: " + action + ". Expected addPermission or removePermission.");
        };
    }

    private LuckPermsNodeBuilder getNodeBuilder(final Instruction instruction) throws QuestException {
        final String unparsedPermissions = instruction.getOptional("permission", "");
        if (unparsedPermissions.isEmpty()) {
            throw new QuestException("Missing permissions argument. Expected permissions:permission1,"
                    + "permission2,permission3,...");
        }
        final List<VariableString> permissions = parseList(instruction, unparsedPermissions);
        final List<VariableString> contexts = parseList(instruction, instruction.getOptional("context", ""));
        final VariableString value = instruction.get(instruction.getOptional("value", ""), VariableString::new);
        final VariableNumber expiry = instruction.get(instruction.getOptional("expiry", "0"),
                VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final VariableString timeUnit = instruction.get(instruction.getOptional("unit", TimeUnit.DAYS.name()), VariableString::new);

        return new LuckPermsNodeBuilder(permissions, value, contexts, expiry, timeUnit);
    }

    private List<VariableString> parseList(final Instruction instruction, final String unparsed) throws QuestException {
        if (unparsed.isEmpty()) {
            return List.of();
        }
        final List<VariableString> list = new ArrayList<>();
        for (final String input : unparsed.split(",")) {
            list.add(instruction.get(input, VariableString::new));
        }
        return list;
    }
}
