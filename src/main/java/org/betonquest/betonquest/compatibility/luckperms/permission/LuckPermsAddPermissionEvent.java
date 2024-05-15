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
public class LuckPermsAddPermissionEvent implements Event {

    /**
     * The {@link LuckPerms} API.
     */
    private final LuckPerms luckPermsAPI;

    /**
     * The list of {@link PermissionNode}s to add.
     */
    private final List<PermissionNode> permissionNodes;

    /**
     * The Constructor for the {@link LuckPermsAddPermissionEvent}.
     *
     * @param permissionNodes The list of {@link PermissionNode}s to add.
     * @param luckPermsAPI    The {@link LuckPerms} API.
     */
    public LuckPermsAddPermissionEvent(final List<PermissionNode> permissionNodes, final LuckPerms luckPermsAPI) {
        this.permissionNodes = permissionNodes;
        this.luckPermsAPI = luckPermsAPI;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final OfflinePlayer offlinePlayer = profile.getPlayer();
        final UserManager userManager = luckPermsAPI.getUserManager();
        final CompletableFuture<User> userFuture = userManager.loadUser(offlinePlayer.getUniqueId());
        userFuture.thenAcceptAsync(user -> {
            for (final PermissionNode permission : permissionNodes) {
                user.data().add(permission);
                userManager.saveUser(user);
            }
        });
    }
}
