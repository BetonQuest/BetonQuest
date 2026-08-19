package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adds item held in hand to items.yml file.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class ItemSubCommand extends QuestCommandPart {

    /**
     * The quest package manager.
     */
    private final QuestPackageManager questPackageManager;

    /**
     * The item type registry.
     */
    private final ItemTypeRegistry itemTypeRegistry;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public ItemSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams, List.of("item", "items", "i"), "<name>");
        this.questPackageManager = constructorParams.questPackageManager();
        this.itemTypeRegistry = constructorParams.itemTypeRegistry();
    }

    @Override
    public void handle(final CommandSender sender, final String... args) {
        // sender must be a player
        if (!(sender instanceof final Player player)) {
            log.debug("Cannot continue, sender must be player");
            return;
        }
        // and the item name must be specified
        if (args.length < 2) {
            log.debug("Cannot continue, item's name must be supplied");
            sendMessage(sender, "specify_item");
            return;
        }
        if (args.length < 3) {
            log.debug("Cannot continue, item's serializer must be supplied");
            sendMessage(sender, "specify_key");
            return;
        }

        final String itemID = args[1];
        final String pack;
        final String name;
        if (itemID.contains(Identifier.SEPARATOR)) {
            final String[] parts = itemID.split(Identifier.SEPARATOR);
            pack = parts[0];
            name = parts[1];
        } else {
            pack = null;
            name = itemID;
        }
        // define parts of the final string
        final QuestPackage configPack = questPackageManager.getPackages().get(pack);
        if (configPack == null) {
            log.debug("Cannot continue, package does not exist");
            sendMessage(sender, "specify_package");
            return;
        }
        final ItemStack item = player.getInventory().getItemInMainHand();
        final String instructions;
        try {
            instructions = itemTypeRegistry.getSerializer(args[2]).serialize(item);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Could not serialize item: " + e.getMessage(), e);
            return;
        }
        // save it in items.yml
        log.debug("Saving item to configuration as " + args[1] + " (" + args[2] + ")");
        final String path = "items." + name;
        final boolean exists = configPack.getConfig().isSet(path);
        configPack.getConfig().set(path, args[2] + " " + instructions);
        try {
            if (!exists) {
                final ConfigAccessor itemFile = configPack.getOrCreateConfigAccessor("items.yml");
                configPack.getConfig().associateWith(path, itemFile.getConfig());
            }
            configPack.saveAll();
        } catch (final IOException | InvalidConfigurationException e) {
            log.warn(configPack, e.getMessage(), e);
            return;
        }
        // done
        sendMessage(sender, "item_created",
                new VariableReplacement("item", Component.text(args[1])));
    }

    @Override
    public Optional<List<String>> complete(final String... args) {
        if (args.length == 2) {
            return completeId(args, AccessorType.ITEMS);
        }
        if (args.length == 3) {
            return Optional.of(List.copyOf(itemTypeRegistry.serializerKeySet()));
        }
        return Optional.of(new ArrayList<>());
    }
}
