package org.betonquest.betonquest.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.menu.config.SimpleYMLSection;
import org.betonquest.betonquest.message.ParsedSectionMessage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An Item which Is displayed as option in a menu and has some events that are fired when item is clicked.
 */
public class MenuItem extends SimpleYMLSection {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The betonquest quest item this item is based on.
     */
    private final Item item;

    /**
     * Parsed message which can be got.
     */
    @Nullable
    private final ParsedSectionMessage descriptions;

    /**
     * Ids of all events that should be run on clicks.
     */
    private final ClickEvents clickEvents;

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
     * @param name         the name of the item
     * @param section      the configuration representing the item
     * @param defaultClose if the item click closes as default
     * @throws InvalidConfigurationException if there are missing or invalid entries
     */
    public MenuItem(final BetonQuestLogger log, final QuestPackage pack,
                    final String name, final ConfigurationSection section, final boolean defaultClose)
            throws InvalidConfigurationException {
        super(pack, name, section);
        this.log = log;
        final BetonQuest instance = BetonQuest.getInstance();
        try {
            //load item
            final ItemID itemID = new ItemID(pack, getString("item").trim());
            final VariableNumber amount = new VariableNumber(instance.getVariableProcessor(), pack,
                    new DefaultSetting<>("1") {
                        @Override
                        @SuppressWarnings("PMD.ShortMethodName")
                        protected String of() throws Missing {
                            return getString("amount");
                        }
                    }.get());
            this.item = new Item(instance.getFeatureAPI(), itemID, amount);
            // load description
            if (section.contains("text")) {
                this.descriptions = new ParsedSectionMessage(instance.getVariableProcessor(), instance.getMessageParser(),
                        instance.getPlayerDataStorage(), pack, section, "text", instance);
            } else {
                this.descriptions = null;
                log.debug(pack, "No description for menu item '" + pack.getQuestPath() + "." + section.getName() + "' set.");
            }
            if (config.isConfigurationSection("click")) {
                this.clickEvents = new ClickEvents(
                        getEvents("click.left", pack),
                        getEvents("click.shiftLeft", pack),
                        getEvents("click.right", pack),
                        getEvents("click.shiftRight", pack),
                        getEvents("click.middleMouse", pack)
                );
            } else {
                this.clickEvents = new ClickEvents(getEvents("click", pack));
            }
            this.conditions = getConditions("conditions", pack);
            //load if menu should close when item is clicked
            this.close = new DefaultSetting<>(defaultClose) {
                @Override
                @SuppressWarnings("PMD.ShortMethodName")
                protected Boolean of() throws Missing, Invalid {
                    try {
                        return Argument.BOOLEAN.apply(getString("close"));
                    } catch (final QuestException e) {
                        throw new Invalid(e.getMessage(), e);
                    }
                }
            }.get();
        } catch (final QuestException e) {
            throw new InvalidConfigurationException(e.getMessage(), e);
        }
    }

    /**
     * Action that happens on click.
     *
     * @param profile that has clicked the item
     * @param type    type of the click action
     * @return if the menu should be closed after this operation
     */
    public boolean onClick(final OnlineProfile profile, final ClickType type) {
        return switch (type) {
            case LEFT -> executeEvents(clickEvents.leftClick, profile);
            case SHIFT_LEFT -> executeEvents(clickEvents.shiftLeftClick, profile);
            case RIGHT -> executeEvents(clickEvents.rightClick, profile);
            case SHIFT_RIGHT -> executeEvents(clickEvents.shiftRightClick, profile);
            case MIDDLE -> executeEvents(clickEvents.middleMouseClick, profile);
            default -> false;
        };
    }

    private boolean executeEvents(final List<EventID> variables, final OnlineProfile profile) {
        for (final EventID eventID : variables) {
            log.debug(pack, "Item " + name + ": Run event " + eventID);
            BetonQuest.getInstance().getQuestTypeAPI().event(profile, eventID);
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
            if (BetonQuest.getInstance().getQuestTypeAPI().condition(profile, condition)) {
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
            final ItemStack item = this.item.generate(profile);
            if (descriptions != null) {
                Component description = descriptions.asComponent(profile);
                if (description.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET) {
                    // TODO version switch:
                    //  Replace with decorationIfAbsent with Adventure 4.12 (in Paper 1.19?)
                    description = description.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                }
                final List<Component> lines = ComponentLineWrapper.splitNewLine(description);
                if (!lines.isEmpty()) {
                    final Component displayName = lines.get(0);
                    final List<Component> lore = lines.subList(1, lines.size());
                    item.editMeta(meta -> {
                        meta.displayName(displayName);
                        if (!lore.isEmpty()) {
                            meta.lore(lore);
                        }
                    });
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

    /**
     * Contains the ids of events that should be run on a click.
     */
    public record ClickEvents(List<EventID> leftClick, List<EventID> shiftLeftClick,
                              List<EventID> rightClick, List<EventID> shiftRightClick,
                              List<EventID> middleMouseClick) {

        /**
         * Fills all click types with the same list.
         *
         * @param click the events to execute on any click
         */
        public ClickEvents(final List<EventID> click) {
            this(click, click, click, click, click);
        }
    }
}
