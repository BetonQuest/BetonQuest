package org.betonquest.betonquest.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.feature.BackpackFactory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The backpack command. It opens profile's backpack.
 */
public class BackpackCommand {

    /**
     * The command description.
     */
    public static final String DESCRIPTION = "Opens quest backpack";

    /**
     * The command aliases.
     */
    public static final List<String> ALIASES = List.of("bb", "bbackpack", "betonbackpack", "betonquestbackpack");

    /**
     * The command label.
     */
    private static final String COMMAND_LABEL = "backpack";

    /**
     * The permission required to use this command.
     */
    private static final String PERMISSION = "betonquest.backpack";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Factory to create backpacks.
     */
    private final BackpackFactory backpackFactory;

    /**
     * Creates a new /backpack command.
     *
     * @param log             the logger that will be used for logging
     * @param profileProvider the profile provider instance
     * @param backpackFactory the factory to create backpacks
     */
    public BackpackCommand(final BetonQuestLogger log, final ProfileProvider profileProvider, final BackpackFactory backpackFactory) {
        this.log = log;
        this.profileProvider = profileProvider;
        this.backpackFactory = backpackFactory;
    }

    /**
     * Creates the Brigadier command node.
     *
     * @return the command node
     */
    public LiteralCommandNode<CommandSourceStack> createCommandNode() {
        return Commands.literal(COMMAND_LABEL)
                .requires(source -> source.getSender().hasPermission(PERMISSION))
                .executes(context -> execute(context.getSource().getSender()))
                .build();
    }

    private int execute(final CommandSender sender) {
        // command sender must be a player, console can't have a backpack
        if (sender instanceof final Player player) {
            final OnlineProfile onlineProfile = profileProvider.getProfile(player);
            log.debug("Executing /backpack command for " + onlineProfile);
            backpackFactory.createBackpack(onlineProfile);
        }
        return Command.SINGLE_SUCCESS;
    }
}
