package org.betonquest.betonquest.menu;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.ConfigPackage;
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
@CustomLog
@SuppressWarnings("PMD.CommentRequired")
public class MenuItem extends SimpleYMLSection {

    /**
     * The package this item is inside
     */
    private final ConfigPackage pack;

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
     * Ids of all events that should be run on right click
     */
    private final List<EventID> rightClick;

    /**
     * Conditions that have to be matched to view the item
     */
    private final List<ConditionID> conditions;

    /**
     * If the menu should be closed when the item is clicked
     */
    private final boolean close;

    @SuppressWarnings({"PMD.ExceptionAsFlowControl", "PMD.EmptyCatchBlock", "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public MenuItem(final ConfigPackage pack, final String name, final ConfigurationSection section) throws InvalidConfigurationException {
        super(name, section);
        try {
            this.pack = pack;
            //load item
            final ItemID itemID = new ItemID(pack, getString("item").trim());
            final VariableNumber amount;
            amount = new VariableNumber(pack.getName(), new DefaultSetting<String>("1") {
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
            } catch (final Missing missing) {
            }
            //load events
            this.leftClick = new ArrayList<>();
            this.rightClick = new ArrayList<>();
            if (config.isConfigurationSection("click")) {
                try {
                    this.leftClick.addAll(getEvents("click.left", pack));
                } catch (final Missing e) {
                }
                try {
                    this.rightClick.addAll(getEvents("click.right", pack));
                } catch (final Missing e) {
                }
            } else {
                try {
                    final List<EventID> list = getEvents("click", pack);
                    this.leftClick.addAll(list);
                    this.rightClick.addAll(list);
                } catch (final Missing e) {
                }
            }
            //load display conditions
            this.conditions = new ArrayList<>();
            try {
                this.conditions.addAll(getConditions("conditions", pack));
            } catch (final Missing e) {
            }
            try {
                this.conditions.addAll(getConditions("condition", pack));
            } catch (final Missing e) {
            }
            //load if menu should close when item is clicked
            this.close = new DefaultSetting<Boolean>(BetonQuest.getInstance().getRpgMenu().getConfiguration().defaultCloseOnClick) {
                @Override
                @SuppressWarnings("PMD.ShortMethodName")
                protected Boolean of() throws Missing, Invalid {
                    return getBoolean("close");
                }
            }.get();
        } catch (ObjectNotFoundException | InstructionParseException e) {
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
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public boolean onClick(final Player player, final ClickType type) {
        switch (type) {
            case RIGHT:
            case SHIFT_RIGHT:
                for (final EventID eventID : this.rightClick) {
                    LOG.debug(pack, "Item " + name + ": Run event " + eventID);
                    BetonQuest.event(PlayerConverter.getID(player), eventID);
                }
                return this.close;
            case LEFT:
            case SHIFT_LEFT:
                for (final EventID eventID : this.leftClick) {
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
     * @param player that should get the item displayed
     * @return true if all display conditions are met, false otherwise
     */
    public boolean display(final Player player) {
        for (final ConditionID condition : this.conditions) {
            if (BetonQuest.condition(PlayerConverter.getID(player), condition)) {
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
     * @param player the player this item will be displayed to
     * @return the item as a bukkit item stack
     */
    @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidCatchingNPE"})
    @SuppressFBWarnings("DCN_NULLPOINTER_EXCEPTION")
    public ItemStack generateItem(final Player player) {
        try {
            final String playerId = PlayerConverter.getID(player);
            final String lang = BetonQuest.getInstance().getPlayerData(playerId).getLanguage();
            final ItemStack item = this.item.generate(playerId);
            final ItemMeta meta = item.getItemMeta();
            if (!descriptions.isEmpty()) {
                ItemDescription description = this.descriptions.get(lang);
                if (description == null) {
                    description = this.descriptions.get(Config.getLanguage());
                }
                try {
                    meta.setDisplayName(description.getDisplayName(playerId));
                    meta.setLore(description.getLore(playerId));
                    item.setItemMeta(meta);
                } catch (final NullPointerException npe) {
                    LOG.error(pack, "Couldn't add custom text to §7" + name + "§4: No text for language §7" + Config.getLanguage() + "§4 " +
                            "specified");
                }
            }
            return item;
        } catch (final QuestRuntimeException qre) {
            LOG.error(pack, "QuestRuntimeException while creating §7" + name + "§4: " + qre.getMessage());
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

        public ItemStack generate(final String playerID) throws QuestRuntimeException {
            return questItem.generate(amount.getInt(playerID), playerID);
        }
    }

}
