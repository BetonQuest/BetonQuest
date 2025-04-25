package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.NodeMap;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Factory class for creating LuckPerms events.
 */
public class LuckPermsEventFactory implements PlayerEventFactory {

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
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
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
        final List<Variable<String>> permissions = parseList(instruction, unparsedPermissions);
        final List<Variable<String>> contexts = parseList(instruction, instruction.getOptional("context", ""));
        final Variable<String> value = instruction.getVariable(instruction.getOptional("value", ""), Argument.STRING);
        final Variable<Number> expiry = instruction.getVariable(instruction.getOptional("expiry", "0"),
                Argument.NUMBER_NOT_LESS_THAN_ONE);
        final Variable<String> timeUnit = instruction.getVariable(instruction.getOptional("unit", TimeUnit.DAYS.name()), Argument.STRING);

        return new LuckPermsNodeBuilder(permissions, value, contexts, expiry, timeUnit);
    }

    private List<Variable<String>> parseList(final Instruction instruction, final String unparsed) throws QuestException {
        if (unparsed.isEmpty()) {
            return List.of();
        }
        final List<Variable<String>> list = new ArrayList<>();
        for (final String input : unparsed.split(",")) {
            list.add(instruction.getVariable(input, Argument.STRING));
        }
        return list;
    }
}
