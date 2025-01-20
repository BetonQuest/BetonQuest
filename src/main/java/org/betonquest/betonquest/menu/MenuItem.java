package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.menu.config.SimpleYMLSection;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An Item which Is displayed as option in a menu and has some events that are fired when item is clicked.
 */
public class MenuItem extends SimpleYMLSection {
    /**
     * Text config property for Item lore.
     */
    private static final String CONFIG_TEXT = "text";

    /**
     * Path component for text config property for Item lore.
     */
    private static final String CONFIG_TEXT_PATH = CONFIG_TEXT + ".";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The betonquest quest item this item is based on.
     */
    private final Item item;

    /**
     * HashMap with a language as key and the corresponding description as value.
     */
    private final Map<String, ItemDescription> descriptions;

    /**
     * Ids of all events that should be run on left click.
     */
    private final List<EventID> leftClick;

    /**
     * Ids of all events that should be run on shift-left click.
     */
    private final List<EventID> shiftLeftClick;

    /**
     * Ids of all events that should be run on right click.
     */
    private final List<EventID> rightClick;

    /**
     * Ids of all events that should be run on shift-right click.
     */
    private final List<EventID> shiftRightClick;

    /**
     * Ids of all events that should be run on middle-mouse click.
     */
    private final List<EventID> middleMouseClick;

    /**
     * Conditions that have to be matched to view the item.
     */
    private final List<ConditionID> conditions;

    /**
     * If the menu should be closed when the item is clicked.
     */
    private final boolean close;

    /**
     * Creates a new Menu Item.
     *
     * @param log          the custom logger for this class
     * @param pack         the quest package the item is in
     * @param menuID       the menu the item is in
     * @param name         the name of the item
     * @param section      the configuration representing the item
     * @param defaultClose if the item click closes as default
     * @throws InvalidConfigurationException if there are missing or invalid entries
     */
    public MenuItem(final BetonQuestLogger log, final QuestPackage pack, final MenuID menuID, final String name,
                    final ConfigurationSection section, final boolean defaultClose)
            throws InvalidConfigurationException {
        super(pack, name, section);
        this.log = log;
        try {
            //load item
            final ItemID itemID = new ItemID(pack, getString("item").trim());
            final VariableNumber amount = new VariableNumber(BetonQuest.getInstance().getVariableProcessor(), pack,
                    new DefaultSetting<>("1") {
                        @Override
                        @SuppressWarnings("PMD.ShortMethodName")
                        protected String of() throws Missing {
                            return getString("amount");
                        }
                    }.get());
            this.item = new Item(itemID, amount);
            // load description
            this.descriptions = new HashMap<>();
            try {
                this.descriptions.putAll(generateDescriptions(menuID.getFullID(), section));
            } catch (final Missing e) {
                log.warn("Missing description for menu item  '" + itemID.getFullID() + "' in menu '"
                        + menuID.getFullID() + "' in package '" + pack.getQuestPath() + "'! Reason: " + e.getMessage(), e);
            }
            if (config.isConfigurationSection("click")) {
                this.leftClick = getEvents("click.left", pack);
                this.shiftLeftClick = getEvents("click.shiftLeft", pack);
                this.rightClick = getEvents("click.right", pack);
                this.shiftRightClick = getEvents("click.shiftRight", pack);
                this.middleMouseClick = getEvents("click.middleMouse", pack);
            } else {
                final List<EventID> list = getEvents("click", pack);
                this.leftClick = list;
                this.shiftLeftClick = list;
                this.rightClick = list;
                this.shiftRightClick = list;
                this.middleMouseClick = list;
            }
            this.conditions = new ArrayList<>();
            this.conditions.addAll(getConditions("conditions", pack));
            this.conditions.addAll(getConditions("condition", pack));
            //load if menu should close when item is clicked
            this.close = new DefaultSetting<>(defaultClose) {
                @Override
                @SuppressWarnings("PMD.ShortMethodName")
                protected Boolean of() throws Missing, Invalid {
                    return getBoolean("close");
                }
            }.get();
        } catch (final ObjectNotFoundException | QuestException e) {
            throw new InvalidConfigurationException(e.getMessage(), e);
        }
    }

    /**
     * Action that happens on click.
     *
     * @param player that has clicked the item
     * @param type   type of the click action
     * @return if the menu should be closed after this operation
     */
    public boolean onClick(final Player player, final ClickType type) {
        return switch (type) {
            case LEFT -> executeEvents(leftClick, player);
            case SHIFT_LEFT -> executeEvents(shiftLeftClick, player);
            case RIGHT -> executeEvents(rightClick, player);
            case SHIFT_RIGHT -> executeEvents(shiftRightClick, player);
            case MIDDLE -> executeEvents(middleMouseClick, player);
            default -> false;
        };
    }

    private boolean executeEvents(final List<EventID> variables, final Player player) {
        final OnlineProfile profile = PlayerConverter.getID(player);
        for (final EventID eventID : variables) {
            log.debug(pack, "Item " + name + ": Run event " + eventID);
            BetonQuest.event(profile, eventID);
        }
        return this.close;
    }

    /**
     * Checks if this item should be displayed to the player.
     *
     * @param profile the player of the {@link Profile} should get the item displayed
     * @return true if all display conditions are met, false otherwise
     */
    public boolean display(final Profile profile) {
        for (final ConditionID condition : this.conditions) {
            if (BetonQuest.condition(profile, condition)) {
                log.debug(pack, "Item " + name + ": condition " + condition + " returned true");
            } else {
                log.debug(pack, "Item " + name + " wont be displayed: condition" + condition + " returned false.");
                return false;
            }
        }
        return true;
    }

    /**
     * Generates the menu item for a specific player.
     *
     * @param profile the player from the {@link Profile} this item will be displayed to
     * @return the item as a bukkit item stack
     */
    public ItemStack generateItem(final Profile profile) {
        try {
            final String lang = BetonQuest.getInstance().getPlayerDataStorage().get(profile).getLanguage();
            final ItemStack item = this.item.generate(profile);
            final ItemMeta meta = item.getItemMeta();
            if (!descriptions.isEmpty()) {
                ItemDescription description = this.descriptions.get(lang);
                if (description == null) {
                    description = this.descriptions.get(Config.getLanguage());
                }
                if (description == null) {
                    log.error(pack, "Couldn't add custom text to '" + name + "': No text for language '"
                            + Config.getLanguage() + "' " + "specified");
                } else {
                    meta.setDisplayName(description.getDisplayName(profile));
                    meta.setLore(description.getLore(profile));
                    item.setItemMeta(meta);
                }
            }
            return item;
        } catch (final QuestException e) {
            log.error(pack, "QuestException while creating '" + name + "': " + e.getMessage());
            return new ItemStack(Material.AIR);
        }
    }

    /**
     * The name of the menu item.
     *
     * @return the items internal id
     */
    public String getId() {
        return name;
    }

    private Map<String, ItemDescription> generateDescriptions(final String menuID, final ConfigurationSection section)
            throws Missing, QuestException {
        final Map<String, ItemDescription> descriptions = new HashMap<>();

        if (section.contains(CONFIG_TEXT)) {
            if (section.isConfigurationSection(CONFIG_TEXT)) {
                descriptions.putAll(generateLanguageDescriptions(menuID, section));
            } else if (section.isString(CONFIG_TEXT)) {
                descriptions.put(Config.getLanguage(), new ItemDescription(this.pack, getString(CONFIG_TEXT).lines().toList()));
            } else if (section.isList(CONFIG_TEXT)) {
                descriptions.put(Config.getLanguage(), new ItemDescription(this.pack, getStringList(CONFIG_TEXT)));
            } else {
                throw new QuestException("Unrecognized item '" + name + "' text configuration in menu '"
                        + menuID + "'");
            }
        }

        return descriptions;
    }

    private Map<String, ItemDescription> generateLanguageDescriptions(final String menuID, final ConfigurationSection section)
            throws Missing, QuestException {
        final Map<String, ItemDescription> descriptions = new HashMap<>();

        final ConfigurationSection textSection = section.getConfigurationSection(CONFIG_TEXT);
        if (textSection != null) {
            for (final String lang : textSection.getKeys(false)) {
                if (section.isString(CONFIG_TEXT_PATH + lang)) {
                    descriptions.put(lang, new ItemDescription(this.pack, getString(CONFIG_TEXT_PATH + lang).lines().toList()));
                } else if (section.isList(CONFIG_TEXT_PATH + lang)) {
                    descriptions.put(lang, new ItemDescription(this.pack, getStringList(CONFIG_TEXT_PATH + lang)));
                } else {
                    throw new QuestException("Unrecognized item '" + name + "' text language '" + lang
                            + "' configuration in menu '" + menuID + "'");
                }
            }
            if (!descriptions.containsKey(Config.getLanguage())) {
                throw new Missing(CONFIG_TEXT_PATH + Config.getLanguage());
            }
        }

        return descriptions;
    }
}
