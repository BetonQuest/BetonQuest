package pl.betoncraft.betonquest.compatibility.vault;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Manages player's permissions
 */
public class PermissionEvent extends QuestEvent {

    private final String world, permission;
    private final boolean add, perm;

    public PermissionEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        add = instruction.next().equalsIgnoreCase("add");
        perm = instruction.next().equalsIgnoreCase("perm");
        permission = instruction.next();
        if (instruction.size() >= 5) {
            world = instruction.next();
        } else {
            world = null;
        }
    }

    @Override
    protected Void execute(final String playerID) {
        // Run in Main Thread
        final Permission vault = VaultIntegrator.getPermission();
        final Player player = PlayerConverter.getPlayer(playerID);
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
        return null;
    }
}
