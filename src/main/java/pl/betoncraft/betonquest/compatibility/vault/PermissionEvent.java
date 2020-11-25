package pl.betoncraft.betonquest.compatibility.vault;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Manages player's permissions
 */
public class PermissionEvent extends QuestEvent {

    private final String world;
    private final String permission;
    private final boolean add;
    private final boolean perm;

    public PermissionEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        add = "add".equalsIgnoreCase(instruction.next());
        perm = "perm".equalsIgnoreCase(instruction.next());
        permission = instruction.next();
        if (instruction.size() >= 5) {
            world = instruction.next();
        } else {
            world = null;
        }
    }

    @Override
    protected Void execute(final String playerID) {
        final Permission vault = VaultIntegrator.getPermission();
        final Player player = PlayerConverter.getPlayer(playerID);
        if (add) {
            if (perm) {
                vault.playerAdd(world, player, permission);
            } else {
                vault.playerAddGroup(world, player, permission);
            }
        } else {
            if (perm) {
                vault.playerRemove(world, player, permission);
            } else {
                vault.playerRemoveGroup(world, player, permission);
            }
        }
        return null;
    }
}
