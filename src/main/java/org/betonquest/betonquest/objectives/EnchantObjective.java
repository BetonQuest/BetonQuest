package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
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
@SuppressWarnings("PMD.CommentRequired")
public class EnchantObjective extends CountingObjective implements Listener {
    private static final String JUST_ONE_ENCHANT = "one";

    private final QuestItem item;

    private final List<EnchantmentData> desiredEnchantments;

    private boolean requireOne;

    public EnchantObjective(final Instruction instruction) throws QuestException {
        super(instruction, "items_to_enchant");
        targetAmount = instruction.get(instruction.getOptional("amount", "1"), VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        item = instruction.getQuestItem();
        desiredEnchantments = instruction.getList(EnchantmentData::convert);
        if (desiredEnchantments.isEmpty()) {
            throw new QuestException("No enchantments were given! You must specify at least one enchantment.");
        }

        instruction.getOptionalArgument("requirementMode").ifPresent((mode) -> requireOne = JUST_ONE_ENCHANT.equalsIgnoreCase(mode));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(final EnchantItemEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getEnchanter());
        if (!containsPlayer(onlineProfile)) {
            return;
        }
        if (!item.compare(event.getItem())) {
            return;
        }

        if (matchesDesiredEnchants(event.getEnchantsToAdd()) && checkConditions(onlineProfile)) {
            getCountingData(onlineProfile).progress();
            completeIfDoneOrNotify(onlineProfile);
        }
    }

    private boolean matchesDesiredEnchants(final Map<Enchantment, Integer> addedEnchants) {
        int matches = 0;

        for (final EnchantmentData enchant : desiredEnchantments) {
            final Enchantment desiredEnchant = enchant.enchantment();
            final int desiredLevel = enchant.level();

            if (addedEnchants.containsKey(desiredEnchant)) {
                final int addedLevel = addedEnchants.get(desiredEnchant);
                if (addedLevel >= desiredLevel) {
                    matches++;
                }
            }
        }
        return requireOne ? matches > 0 : matches == desiredEnchantments.size();
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
