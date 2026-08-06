package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NoNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.betonquest.betonquest.quest.action.give.GiveAction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Gives an item to the player.
 */
public class GiveSubCommand extends QuestCommandPart {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The item manager.
     */
    private final ItemManager itemManager;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public GiveSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams);
        this.playerDataStorage = constructorParams.playerDataStorage();
        this.itemManager = constructorParams.itemManager();
    }

    @Override
    public List<String> names() {
        return List.of("give", "g");
    }

    @Override
    public void handle(final CommandSender sender, final String... args) {
        // sender must be a player
        if (!(sender instanceof Player)) {
            log.debug("Cannot continue, sender must be player");
            return;
        }
        // and the item name must be specified
        if (args.length < 2) {
            log.debug("Cannot continue, item's name must be supplied");
            sendMessage(sender, "specify_item");
            return;
        }
        try {
            final ItemIdentifier itemID;
            try {
                itemID = getIdentifier(ItemIdentifier.class, args[1]);
            } catch (final QuestException e) {
                sendMessage(sender, "error",
                        new VariableReplacement("error", Component.text(e.getMessage())));
                log.warn("Could not find Item: " + e.getMessage(), e);
                return;
            }
            final OnlineAction give = new GiveAction(
                    new DefaultArgument<>(List.of(new Item(itemManager, itemID, new DefaultArgument<>(1)))),
                    new NoNotificationSender(),
                    new IngameNotificationSender(log, localizations, itemID.getPackage(), itemID.getFull(), NotificationLevel.ERROR,
                            "inventory_full_backpack", "inventory_full"),
                    new IngameNotificationSender(log, localizations, itemID.getPackage(), itemID.getFull(), NotificationLevel.ERROR,
                            "inventory_full_drop", "inventory_full"),
                    profile -> Optional.empty(), playerDataStorage);
            give.execute(profileProvider.getProfile((Player) sender));
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Error while creating an item: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<List<String>> complete(final String... args) {
        if (args.length == 2) {
            return completeId(args, AccessorType.ITEMS);
        }
        return Optional.of(new ArrayList<>());
    }
}
