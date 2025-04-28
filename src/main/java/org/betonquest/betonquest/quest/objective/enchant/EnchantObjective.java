package org.betonquest.betonquest.quest.objective.enchant;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An objective that requires the player to enchant a {@link QuestItem}.
 */
public class EnchantObjective extends CountingObjective implements Listener {

    /**
     * The item to enchant.
     */
    private final Variable<Item> item;

    /**
     * The desired enchantments.
     */
    private final VariableList<EnchantmentData> desiredEnchantments;

    /**
     * True if at least one enchantment is required, false if all enchantments are required.
     */
    private final boolean requireOne;

    /**
     * Constructor for the EnchantObjective.
     *
     * @param instruction         the instruction that created this objective
     * @param targetAmount        the target amount of items to enchant
     * @param item                the item to enchant
     * @param desiredEnchantments the desired enchantments
     * @param requireOne          true if at least one enchantment is required, false if all enchantments are required
     * @throws QuestException if there is an error in the instruction
     */
    public EnchantObjective(final Instruction instruction, final Variable<Number> targetAmount, final Variable<Item> item,
                            final VariableList<EnchantmentData> desiredEnchantments, final boolean requireOne) throws QuestException {
        super(instruction, targetAmount, "items_to_enchant");
        this.item = item;
        this.desiredEnchantments = desiredEnchantments;
        this.requireOne = requireOne;
    }

    /**
     * Checks if the item is enchanted with the desired enchantments.
     *
     * @param event the enchantment event
     */
    @EventHandler(ignoreCancelled = true)
    public void onEnchant(final EnchantItemEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getEnchanter());
        if (!containsPlayer(onlineProfile)) {
            return;
        }
        qeHandler.handle(() -> {
            if (!item.getValue(onlineProfile).matches(event.getItem())) {
                return;
            }
            if (matchesDesiredEnchants(onlineProfile, event.getEnchantsToAdd()) && checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress();
                completeIfDoneOrNotify(onlineProfile);
            }
        });
    }

    private boolean matchesDesiredEnchants(final OnlineProfile onlineProfile, final Map<Enchantment, Integer> addedEnchants) throws QuestException {
        int matches = 0;

        final List<EnchantmentData> resolvedDesiredEnchants = desiredEnchantments.getValue(onlineProfile);
        for (final EnchantmentData enchant : resolvedDesiredEnchants) {
            final Enchantment desiredEnchant = enchant.enchantment();
            final int desiredLevel = enchant.level();

            if (addedEnchants.containsKey(desiredEnchant)) {
                final int addedLevel = addedEnchants.get(desiredEnchant);
                if (addedLevel >= desiredLevel) {
                    matches++;
                }
            }
        }
        return requireOne ? matches > 0 : matches == resolvedDesiredEnchants.size();
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    /**
     * Represents an enchantment and its level.
     *
     * @param enchantment the enchantment
     * @param level       the level
     */
    public record EnchantmentData(Enchantment enchantment, int level) {

        /**
         * Converts user input to an EnchantmentData object.
         *
         * @param string the string to parse
         * @return the parsed EnchantmentData object
         * @throws QuestException if the user defined string is not a valid enchantment or does not
         *                        contain a level
         */
        @SuppressWarnings({"deprecation", "PMD.AvoidLiteralsInIfCondition"})
        public static EnchantmentData convert(final String string) throws QuestException {
            final String[] parts = string.split(":");
            final Enchantment enchantment = Enchantment.getByName(parts[0].toUpperCase(Locale.ROOT));
            if (enchantment == null) {
                throw new QuestException("Enchantment type '" + parts[0] + "' does not exist");
            }
            int level = 1;
            if (parts.length == 2) {
                try {
                    level = Integer.parseInt(parts[1]);
                } catch (final NumberFormatException e) {
                    throw new QuestException("Could not parse enchantment level: " + string, e);
                }
            }
            return new EnchantmentData(enchantment, level);
        }
    }
}
