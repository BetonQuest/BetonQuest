package org.betonquest.betonquest.menu;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.menu.config.SimpleYMLSection;
import org.betonquest.betonquest.utils.PlayerConverter;
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
 * A Item which Is displayed as option in a menu and has some events that are fired when item is clicked
 */
@SuppressWarnings("PMD.CommentRequired")
public class MenuItem extends SimpleYMLSection {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(MenuItem.class);

    /**
     * The betonquest quest item this item is based on
     */
    private final Item item;

    /**
     * HashMap with a language as key and the corresponding description as value
     */
    private final Map<String, ItemDescription> descriptions;

    /**
     * Ids of all events that should be run on left click
     */
    private final List<EventID> leftClick;

    /**
     * Ids of all events that should be run on shift-left click
     */
    private final List<EventID> shiftLeftClick;

    /**
     * Ids of all events that should be run on right click
     */
    private final List<EventID> rightClick;

    /**
     * Ids of all events that should be run on shift-right click
     */
    private final List<EventID> shiftRightClick;

    /**
     * Ids of all events that should be run on middle-mouse click
     */
    private final List<EventID> middleMouseClick;

    /**
     * Conditions that have to be matched to view the item
     */
    private final List<ConditionID> conditions;

    /**
     * If the menu should be closed when the item is clicked
     */
    private final boolean close;

    @SuppressWarnings({"PMD.ExceptionAsFlowControl", "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public MenuItem(final QuestPackage pack, final String name, final ConfigurationSection section) throws InvalidConfigurationException {
        super(pack, name, section);
        try {
            //load item
            final ItemID itemID = new ItemID(pack, getString("item").trim());
            final VariableNumber amount;
            amount = new VariableNumber(pack, new DefaultSetting<>("1") {
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
                if (section.isConfigurationSection("text")) {
                    for (final String lang : section.getConfigurationSection("text").getKeys(false)) {
                        this.descriptions.put(lang, new ItemDescription(this.pack, getStringList("text." + lang)));
                    }
                    if (!this.descriptions.containsKey(Config.getLanguage())) {
                        throw new Missing("text." + Config.getLanguage());
                    }
                } else {
                    this.descriptions.put(Config.getLanguage(),
                            new ItemDescription(this.pack, getStringList("text")));
                }
            } catch (final Missing ignored) {
            }
            //load events
            this.leftClick = new ArrayList<>();
            this.shiftLeftClick = new ArrayList<>();
            this.rightClick = new ArrayList<>();
            this.shiftRightClick = new ArrayList<>();
            this.middleMouseClick = new ArrayList<>();
            if (config.isConfigurationSection("click")) {
                try {
                    this.leftClick.addAll(getEvents("click.left", pack));
                } catch (final Missing ignored) {
                }
                try {
                    this.shiftLeftClick.addAll(getEvents("click.shiftLeft", pack));
                } catch (final Missing ignored) {
                }
                try {
                    this.rightClick.addAll(getEvents("click.right", pack));
                } catch (final Missing ignored) {
                }
                try {
                    this.shiftRightClick.addAll(getEvents("click.shiftRight", pack));
                } catch (final Missing ignored) {
                }
                try {
                    this.middleMouseClick.addAll(getEvents("click.middleMouse", pack));
                } catch (final Missing ignored) {
                }
            } else {
                try {
                    final List<EventID> list = getEvents("click", pack);
                    this.leftClick.addAll(list);
                    this.shiftLeftClick.addAll(list);
                    this.rightClick.addAll(list);
                    this.shiftRightClick.addAll(list);
                    this.middleMouseClick.addAll(list);
                } catch (final Missing ignored) {
                }
            }
            //load display conditions
            this.conditions = new ArrayList<>();
            try {
                this.conditions.addAll(getConditions("conditions", pack));
            } catch (final Missing ignored) {
            }
            try {
                this.conditions.addAll(getConditions("condition", pack));
            } catch (final Missing ignored) {
            }
            //load if menu should close when item is clicked
            this.close = new DefaultSetting<>(BetonQuest.getInstance().getRpgMenu().getConfiguration().defaultCloseOnClick) {
                @Override
                @SuppressWarnings("PMD.ShortMethodName")
                protected Boolean of() throws Missing, Invalid {
                    return getBoolean("close");
                }
            }.get();
        } catch (final ObjectNotFoundException | InstructionParseException e) {
            throw new InvalidConfigurationException(e.getMessage(), e);
        }
    }

    /**
     * Action that happens on click
     *
     * @param player that has clicked the item
     * @param type   type of the click action
     * @return if the menu should be closed after this operation
     */
    @SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.CyclomaticComplexity"})
    public boolean onClick(final Player player, final ClickType type) {
        switch (type) {
            case LEFT:
                for (final EventID eventID : this.leftClick) {
                    LOG.debug(pack, "Item " + name + ": Run event " + eventID);
                    BetonQuest.event(PlayerConverter.getID(player), eventID);
                }
                return this.close;
            case SHIFT_LEFT:
                for (final EventID eventID : this.shiftLeftClick) {
                    LOG.debug(pack, "Item " + name + ": Run event " + eventID);
                    BetonQuest.event(PlayerConverter.getID(player), eventID);
                }
                return this.close;
            case RIGHT:
                for (final EventID eventID : this.rightClick) {
                    LOG.debug(pack, "Item " + name + ": Run event " + eventID);
                    BetonQuest.event(PlayerConverter.getID(player), eventID);
                }
                return this.close;
            case SHIFT_RIGHT:
                for (final EventID eventID : this.shiftRightClick) {
                    LOG.debug(pack, "Item " + name + ": Run event " + eventID);
                    BetonQuest.event(PlayerConverter.getID(player), eventID);
                }
                return this.close;
            case MIDDLE:
                for (final EventID eventID : this.middleMouseClick) {
                    LOG.debug(pack, "Item " + name + ": Run event " + eventID);
                    BetonQuest.event(PlayerConverter.getID(player), eventID);
                }
                return this.close;
            default:
                return false;
        }
    }

    /**
     * Checks if this item should be displayed to the player
     *
     * @param profile the player of the {@link Profile} should get the item displayed
     * @return true if all display conditions are met, false otherwise
     */
    public boolean display(final Profile profile) {
        for (final ConditionID condition : this.conditions) {
            if (BetonQuest.condition(profile, condition)) {
                LOG.debug(pack, "Item " + name + ": condition " + condition + " returned true");
            } else {
                LOG.debug(pack, "Item " + name + " wont be displayed: condition" + condition + " returned false.");
                return false;
            }
        }
        return true;
    }

    /**
     * Generates the menu item for a specific player
     *
     * @param profile the player from the {@link Profile} this item will be displayed to
     * @return the item as a bukkit item stack
     */
    @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidCatchingNPE"})
    @SuppressFBWarnings("DCN_NULLPOINTER_EXCEPTION")
    public ItemStack generateItem(final Profile profile) {
        try {
            final String lang = BetonQuest.getInstance().getPlayerData(profile).getLanguage();
            final ItemStack item = this.item.generate(profile);
            final ItemMeta meta = item.getItemMeta();
            if (!descriptions.isEmpty()) {
                ItemDescription description = this.descriptions.get(lang);
                if (description == null) {
                    description = this.descriptions.get(Config.getLanguage());
                }
                try {
                    meta.setDisplayName(description.getDisplayName(profile));
                    meta.setLore(description.getLore(profile));
                    item.setItemMeta(meta);
                } catch (final NullPointerException npe) {
                    LOG.error(pack, "Couldn't add custom text to '" + name + "': No text for language '" + Config.getLanguage() + "' " +
                            "specified");
                }
            }
            return item;
        } catch (final QuestRuntimeException qre) {
            LOG.error(pack, "QuestRuntimeException while creating '" + name + "': " + qre.getMessage());
            return new ItemStack(Material.AIR);
        }
    }

    /**
     * @return the items internal id
     */
    public String getId() {
        return name;
    }

    /**
     * Extended, static copy of org.betonquest.betonquest.Instruction.Item for easier quest item handling
     */
    @SuppressWarnings("PMD.ShortClassName")
    public static class Item {

        private final ItemID itemID;
        private final QuestItem questItem;
        private final VariableNumber amount;

        public Item(final ItemID itemID, final VariableNumber amount) throws InstructionParseException {
            this.itemID = itemID;
            this.questItem = new QuestItem(itemID);
            this.amount = amount;
        }

        public ItemID getID() {
            return itemID;
        }

        public QuestItem getItem() {
            return questItem;
        }

        public boolean isItemEqual(final ItemStack item) {
            return questItem.compare(item);
        }

        public VariableNumber getAmount() {
            return amount;
        }

        public ItemStack generate(final Profile profile) throws QuestRuntimeException {
            return questItem.generate(amount.getInt(profile), profile);
        }
    }

}
