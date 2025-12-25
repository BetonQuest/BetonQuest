package org.betonquest.betonquest.compatibility.vault.event;

import net.milkbowl.vault.permission.Permission;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;

/**
 * Factory to create {@link PermissionEvent}s from {@link Instruction}s.
 */
public class PermissionEventFactory implements PlayerEventFactory {

    /**
     * Service where the permission will be modified.
     */
    private final Permission permissionService;

    /**
     * Create a new Factory to create Vault Permission Events.
     *
     * @param permissionService the service where the permission will be modified
     */
    public PermissionEventFactory(final Permission permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Boolean> add = instruction.parse("add"::equalsIgnoreCase).get();
        final Argument<Boolean> perm = instruction.parse("perm"::equalsIgnoreCase).get();
        final Argument<String> permission = instruction.string().get();
        final Argument<String> world;
        if (instruction.size() >= 5 && !instruction.nextElement().startsWith("conditions:")) {
            world = instruction.chainForArgument(instruction.current()).string().get();
        } else {
            world = null;
        }
        return new PermissionEvent(permissionService, permission, world, add, perm);
    }
}
