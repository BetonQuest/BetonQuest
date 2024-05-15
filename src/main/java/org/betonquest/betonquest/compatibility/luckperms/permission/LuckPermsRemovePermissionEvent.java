package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.PermissionNode;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Event to add permissions to a player using LuckPerms
 */
public class LuckPermsRemovePermissionEvent implements Event {

    /**
     * The {@link LuckPerms} API.
     */
    private final LuckPerms luckPermsAPI;

    /**
     * The list of {@link PermissionNode}s to remove.
     */
    private final List<PermissionNode> permissionNodes;

    /**
     * The Constructor for the {@link LuckPermsRemovePermissionEvent}.
     *
     * @param permissionNodes The list of {@link PermissionNode}s to remove.
     * @param luckPermsAPI    The {@link LuckPerms} API.
     */
    public LuckPermsRemovePermissionEvent(final List<PermissionNode> permissionNodes, final LuckPerms luckPermsAPI) {
        this.luckPermsAPI = luckPermsAPI;
        this.permissionNodes = permissionNodes;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final OfflinePlayer offlinePlayer = profile.getPlayer();
        final UserManager userManager = luckPermsAPI.getUserManager();
        final CompletableFuture<User> userFuture = userManager.loadUser(offlinePlayer.getUniqueId());
        userFuture.thenAcceptAsync(user -> {
            for (final PermissionNode permission : permissionNodes) {
                user.data().remove(permission);
                userManager.saveUser(user);
            }
        });
    }
}
