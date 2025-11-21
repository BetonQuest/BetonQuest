package org.betonquest.betonquest.compatibility.vault.event;

import net.milkbowl.vault.permission.Permission;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Manages player's permissions.
 */
public class PermissionEvent implements PlayerEvent {
    /**
     * Service where the permission will be modified.
     */
    private final Permission vault;

    /**
     * World to restrict permission to it.
     */
    @Nullable
    private final Variable<String> world;

    /**
     * Permission to modify.
     */
    private final Variable<String> permission;

    /**
     * If the permission should be added. When not it will be removed.
     */
    private final Variable<Boolean> add;

    /**
     * If the permission should be interpreted as permission. When not it is interpreted as group.
     */
    private final Variable<Boolean> perm;

    /**
     * Create a new Vault Permission event.
     *
     * @param permissionService the service where the permission will be modified
     * @param permission        the permission to add or remove
     * @param world             the world if the permission should be restricted to it
     * @param add               if the permission should be added or removed
     * @param perm              if the permission should be interpreted as permission or group
     */
    public PermissionEvent(final Permission permissionService, final Variable<String> permission,
                           @Nullable final Variable<String> world, final Variable<Boolean> add, final Variable<Boolean> perm) {
        this.vault = permissionService;
        this.permission = permission;
        this.world = world;
        this.add = add;
        this.perm = perm;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final boolean add = this.add.getValue(profile);
        final boolean perm = this.perm.getValue(profile);
        final String world = this.world == null ? null : this.world.getValue(profile);
        final String permission = this.permission.getValue(profile);
        if (add) {
            if (perm) {
                vault.playerAdd(world, profile.getPlayer(), permission);
            } else {
                vault.playerAddGroup(world, profile.getPlayer(), permission);
            }
        } else {
            if (perm) {
                vault.playerRemove(world, profile.getPlayer(), permission);
            } else {
                vault.playerRemoveGroup(world, profile.getPlayer(), permission);
            }
        }
    }
}
