package org.betonquest.betonquest.quest.objective.trade;

import io.papermc.paper.event.player.PlayerPurchaseEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;

/**
 * Objective that requires the player to trade.
 */
public class TradeObjective extends DefaultObjective {

    /**
     * The ingredients that the trade must contain any of.
     */
    private final Argument<List<ItemWrapper>> ingredients;

    /**
     * The results that the trade must produce any of.
     */
    private final Argument<List<ItemWrapper>> results;

    /**
     * If the trade must be exact as defined by the ingredients and results.
     */
    private final FlagArgument<Boolean> exact;

    /**
     * Constructor for the TradeObjective.
     *
     * @param service     the objective service
     * @param ingredients the ingredients that the trade must contain any of
     * @param results     the results that the trade must produce any of
     * @param exact       if the trade must be exact as defined by the ingredients and results
     */
    public TradeObjective(final ObjectiveService service, final Argument<List<ItemWrapper>> ingredients,
                          final Argument<List<ItemWrapper>> results, final FlagArgument<Boolean> exact) {
        super(service);
        this.ingredients = ingredients;
        this.results = results;
        this.exact = exact;
    }

    /**
     * Handles the execution of the trade event.
     *
     * @param event   the event that is called when a player purchases a trade
     * @param profile the profile of the player that purchased the trade
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onTrade(final PlayerPurchaseEvent event, final OnlineProfile profile) throws QuestException {
        final MerchantRecipe recipe = event.getTrade();
        final List<ItemWrapper> ingredientList = ingredients.getValue(profile);
        final List<ItemWrapper> resultList = results.getValue(profile);
        final boolean matchExact = exact.getValue(profile).orElse(false);
        if (!hasIngredients(recipe, ingredientList, profile, matchExact) || !hasResults(recipe, resultList, profile, matchExact)) {
            return;
        }
        getService().complete(profile);
    }

    private boolean hasResults(final MerchantRecipe recipe, final List<ItemWrapper> resultList, final OnlineProfile profile, final boolean matchExact) throws QuestException {
        if (resultList.isEmpty()) {
            return true;
        }
        final int exactSize = 1;
        if (matchExact && resultList.size() != exactSize) {
            return false;
        }
        for (final ItemWrapper result : resultList) {
            if (result.matches(recipe.getResult(), profile)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasIngredients(final MerchantRecipe recipe, final List<ItemWrapper> ingredientList, final OnlineProfile profile, final boolean matchExact) throws QuestException {
        if (ingredientList.isEmpty()) {
            return true;
        }
        if (matchExact && ingredientList.size() != recipe.getIngredients().size()) {
            return false;
        }
        boolean needsTwoMatches = matchExact && ingredientList.size() == 2;
        for (final ItemWrapper ingredient : ingredientList) {
            for (final ItemStack ingredientStack : recipe.getIngredients()) {
                if (ingredient.matches(ingredientStack, profile)) {
                    if (needsTwoMatches) {
                        needsTwoMatches = false;
                        continue;
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
