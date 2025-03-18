package org.betonquest.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.wand.LostWand;
import com.elmakers.mine.bukkit.api.wand.Wand;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;

/**
 * Checks if the player is holding a wand.
 */
public class WandCondition implements OnlineCondition {
    /**
     * Magic API.
     */
    private final MagicAPI api;

    /**
     * Execution of the wand type check.
     */
    private final BiPredicate<Player, Profile> typeCheck;

    /**
     * Required spells on the wand.
     */
    private final Map<String, VariableNumber> spells;

    /**
     * Wand name.
     */
    @Nullable
    private final VariableString name;

    /**
     * Required Wand amount.
     */
    @Nullable
    private final VariableNumber amount;

    /**
     * Create a new Magic Wand Condition.
     *
     * @param api    the magic api
     * @param type   the type of wand check
     * @param name   optional wand name
     * @param spells the required spells on the wand
     * @param amount optional required wand amount
     */
    public WandCondition(final MagicAPI api, final CheckType type, @Nullable final VariableString name,
                         final Map<String, VariableNumber> spells, @Nullable final VariableNumber amount) {
        this.api = api;
        this.name = name;
        this.spells = spells;
        this.typeCheck = getCheck(type);
        this.amount = amount;
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.SwitchDensity"})
    private BiPredicate<Player, Profile> getCheck(final CheckType checkType) {
        return (player, profile) -> switch (checkType) {
            case LOST -> {
                for (final LostWand lost : api.getLostWands()) {
                    final Player owner = Bukkit.getPlayer(UUID.fromString(lost.getOwnerId()));
                    if (owner == null) {
                        continue;
                    }
                    if (owner.equals(player)) {
                        yield true;
                    }
                }
                yield false;
            }
            case HAND -> {
                final ItemStack wandItem = player.getInventory().getItemInMainHand();
                if (!api.isWand(wandItem)) {
                    yield false;
                }
                final Wand wand1 = api.getWand(wandItem);
                yield checkWand(wand1, profile);
            }
            case INVENTORY -> {
                int heldAmount = 0;
                for (final ItemStack item : player.getInventory().getContents()) {
                    if (item == null || !api.isWand(item)) {
                        continue;
                    }
                    final Wand wand = api.getWand(item);
                    if (checkWand(wand, profile)) {
                        heldAmount += item.getAmount();
                        if (amount == null || heldAmount >= amount.getInt(profile)) {
                            yield true;
                        }
                    }
                }
                yield false;
            }
        };
    }

    @Override
    public boolean check(final OnlineProfile profile) {
        final Player player = profile.getPlayer();
        return typeCheck.test(player, profile);
    }

    /**
     * Checks if the given wand meets specified name and spells conditions.
     *
     * @param wand wand to check
     * @return true if the wand meets the conditions, false otherwise
     */
    @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
    private boolean checkWand(final Wand wand, final Profile profile) {
        if (name != null && !name.getString(profile).equalsIgnoreCase(wand.getTemplateKey())) {
            return false;
        }
        if (!spells.isEmpty()) {
            spell:
            for (final Map.Entry<String, VariableNumber> entry : spells.entrySet()) {
                final int level = entry.getValue().getInt(profile);
                for (final String wandSpell : wand.getSpells()) {
                    if (wandSpell.toLowerCase(Locale.ROOT).startsWith(entry.getKey().toLowerCase(Locale.ROOT)) && wand.getSpellLevel(entry.getKey()) >= level) {
                        continue spell;
                    }
                }
                return false;
            }
        }
        return true;
    }
}
