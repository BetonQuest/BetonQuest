package org.betonquest.betonquest.compatibility.vault.action;

import net.milkbowl.vault.permission.Permission;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.jetbrains.annotations.Nullable;

/**
 * Manages player's permissions.
 */
public class PermissionAction implements PlayerAction {

    /**
     * Service where the permission will be modified.
     */
    private final Permission vault;

    /**
     * World to restrict permission to it.
     */
    @Nullable
    private final Argument<String> world;

    /**
     * Permission to modify.
     */
    private final Argument<String> permission;

    /**
     * If the permission should be added. When not it will be removed.
     */
    private final Argument<Boolean> add;

    /**
     * If the permission should be interpreted as permission. When not it is interpreted as group.
     */
    private final Argument<Boolean> perm;

    /**
     * Create a new Vault Permission action.
     *
     * @param permissionService the service where the permission will be modified
     * @param permission        the permission to add or remove
     * @param world             the world if the permission should be restricted to it
     * @param add               if the permission should be added or removed
     * @param perm              if the permission should be interpreted as permission or group
     */
    public PermissionAction(final Permission permissionService, final Argument<String> permission,
                            @Nullable final Argument<String> world, final Argument<Boolean> add, final Argument<Boolean> perm) {
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

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
