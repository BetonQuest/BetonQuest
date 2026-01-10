package org.betonquest.betonquest.quest.objective.enchant;

import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveService;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An objective that requires the player to enchant a {@link QuestItem}.
 */
public class EnchantObjective extends CountingObjective {

    /**
     * The item to enchant.
     */
    private final Argument<ItemWrapper> item;

    /**
     * The desired enchantments.
     */
    private final Argument<List<EnchantmentData>> desiredEnchantments;

    /**
     * True if at least one enchantment is required, false if all enchantments are required.
     */
    private final boolean requireOne;

    /**
     * Constructor for the EnchantObjective.
     *
     * @param service             the objective factory service
     * @param targetAmount        the target amount of items to enchant
     * @param item                the item to enchant
     * @param desiredEnchantments the desired enchantments
     * @param requireOne          true if at least one enchantment is required, false if all enchantments are required
     * @throws QuestException if there is an error in the instruction
     */
    public EnchantObjective(final ObjectiveService service, final Argument<Number> targetAmount, final Argument<ItemWrapper> item,
                            final Argument<List<EnchantmentData>> desiredEnchantments, final boolean requireOne) throws QuestException {
        super(service, targetAmount, "items_to_enchant");
        this.item = item;
        this.desiredEnchantments = desiredEnchantments;
        this.requireOne = requireOne;
    }

    /**
     * Checks if the item is enchanted with the desired enchantments.
     *
     * @param event         the enchantment event
     * @param onlineProfile the profile of the player that enchanted the item
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onEnchant(final EnchantItemEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (!item.getValue(onlineProfile).matches(event.getItem(), onlineProfile)) {
            return;
        }
        if (matchesDesiredEnchants(onlineProfile, event.getEnchantsToAdd())) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
        }
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
