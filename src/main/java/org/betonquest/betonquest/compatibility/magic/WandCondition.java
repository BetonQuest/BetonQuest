package org.betonquest.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.wand.LostWand;
import com.elmakers.mine.bukkit.api.wand.Wand;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Checks if the player is holding a wand.
 */
@SuppressWarnings("PMD.CommentRequired")
public class WandCondition extends Condition {
    private final MagicAPI api;

    private final CheckType type;

    private final Map<String, VariableNumber> spells = new HashMap<>();

    @Nullable
    private final String name;

    @Nullable
    private final VariableNumber amount;

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public WandCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.next();
        switch (string) {
            case "hand":
                type = CheckType.IN_HAND;
                break;
            case "inventory":
                type = CheckType.IN_INVENTORY;
                break;
            case "lost":
                type = CheckType.IS_LOST;
                break;
            default:
                throw new InstructionParseException("Unknown check type '" + string + "'");
        }
        final String[] array = instruction.getArray(instruction.getOptional("spells"));
        for (final String spell : array) {
            if (spell.contains(":")) {
                final VariableNumber level;
                final String[] spellParts = spell.split(":");
                try {
                    level = new VariableNumber(instruction.getPackage(), spellParts[1]);
                } catch (final InstructionParseException e) {
                    throw new InstructionParseException("Could not parse spell level", e);
                }
                this.spells.put(spellParts[0], level);
            } else {
                throw new InstructionParseException("Incorrect spell format");
            }
        }
        name = instruction.getOptional("name");
        api = Utils.getNN((MagicAPI) Bukkit.getPluginManager().getPlugin("Magic"), "Magic plugin not found!");
        amount = instruction.getVarNum(instruction.getOptional("amount"));
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @Override
    protected Boolean execute(final Profile profile) {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        int heldAmount;

        switch (type) {
            case IS_LOST:
                for (final LostWand lost : api.getLostWands()) {
                    final Player owner = Bukkit.getPlayer(UUID.fromString(lost.getOwnerId()));
                    if (owner == null) {
                        continue;
                    }
                    if (owner.equals(player)) {
                        return true;
                    }
                }
                return false;
            case IN_HAND:
                final ItemStack wandItem = player.getInventory().getItemInMainHand();
                if (!api.isWand(wandItem)) {
                    return false;
                }
                final Wand wand1 = api.getWand(wandItem);
                return checkWand(wand1, profile);
            case IN_INVENTORY:
                heldAmount = 0;
                for (final ItemStack item : player.getInventory().getContents()) {
                    if (item == null) {
                        continue;
                    }
                    if (api.isWand(item)) {
                        final Wand wand2 = api.getWand(item);
                        if (checkWand(wand2, profile)) {
                            heldAmount += item.getAmount();
                            if (amount == null || heldAmount >= amount.getInt(profile)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * Checks if the given wand meets specified name and spells conditions.
     *
     * @param wand wand to check
     * @return true if the wand meets the conditions, false otherwise
     */
    @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
    private boolean checkWand(final Wand wand, final Profile profile) {
        if (name != null && !name.equalsIgnoreCase(wand.getTemplateKey())) {
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

    private enum CheckType {
        IS_LOST, IN_HAND, IN_INVENTORY
    }

}
