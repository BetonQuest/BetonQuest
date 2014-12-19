/**
 * 
 */
package pl.betoncraft.betonquest.compatibility.vault;

import net.milkbowl.vault.permission.Permission;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * @author co0sh
 *
 */
public class PermissionEvent extends QuestEvent {

	/**
	 * @param playerID
	 * @param instructions
	 */
	@SuppressWarnings("deprecation")
	public PermissionEvent(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 4) {
			BetonQuest.getInstance().getLogger().info("Error in permission event syntax: " + instructions);
			return;
		}
		boolean add = parts[1].equalsIgnoreCase("add");
		boolean perm = parts[2].equalsIgnoreCase("perm");
		String permission = parts[3];
		String world = null;
		if (parts.length == 5) {
			world = parts[4];
		}
		Permission vault = Compatibility.getPermission();
		String player = PlayerConverter.getPlayer(playerID).getName();
		if (add) {
			if (perm) {
				// world add perm
				vault.playerAdd(world, player, permission);
			} else {
				// world add group
				vault.playerAddGroup(world, player, permission);
			}
		} else {
			if (perm) {
				// world remove perm
				vault.playerRemove(world, player, permission);
			} else {
				// world remove group
				vault.playerRemoveGroup(world, player, permission);
			}
		}
	}

}
