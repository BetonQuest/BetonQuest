package org.betonquest.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.wand.LostWand;
import com.elmakers.mine.bukkit.api.wand.Wand;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;

/**
 * Checks if the player is holding a wand.
 */
@SuppressWarnings("PMD.CommentRequired")
public class WandCondition extends Condition {
    private final MagicAPI api;

    private final BiPredicate<Player, Profile> typeCheck;

    private final Map<String, VariableNumber> spells = new HashMap<>();

    @Nullable
    private final String name;

    @Nullable
    private final VariableNumber amount;

    public WandCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);
        final String string = instruction.next();
        final CheckType type = switch (string) {
            case "hand" -> CheckType.IN_HAND;
            case "inventory" -> CheckType.IN_INVENTORY;
            case "lost" -> CheckType.IS_LOST;
            default -> throw new QuestException("Unknown check type '" + string + "'");
        };
        typeCheck = getCheck(type);
        final String[] array = instruction.getArray(instruction.getOptional("spells"));
        putSpells(array, instruction.getPackage());
        name = instruction.getOptional("name");
        api = Utils.getNN((MagicAPI) Bukkit.getPluginManager().getPlugin("Magic"), "Magic plugin not found!");
        amount = instruction.get(instruction.getOptional("amount"), VariableNumber::new);
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private void putSpells(final String[] spells, final QuestPackage questPackage) throws QuestException {
        for (final String spell : spells) {
            final String[] spellParts = spell.split(":");
            if (spellParts.length != 2) {
                throw new QuestException("Incorrect spell format");
            }
            final VariableNumber level;
            try {
                level = new VariableNumber(questPackage, spellParts[1]);
            } catch (final QuestException e) {
                throw new QuestException("Could not parse spell level", e);
            }
            this.spells.put(spellParts[0], level);
        }
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.SwitchDensity"})
    private BiPredicate<Player, Profile> getCheck(final CheckType checkType) {
        return (player, profile) -> switch (checkType) {
            case IS_LOST -> {
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
            case IN_HAND -> {
                final ItemStack wandItem = player.getInventory().getItemInMainHand();
                if (!api.isWand(wandItem)) {
                    yield false;
                }
                final Wand wand1 = api.getWand(wandItem);
                yield checkWand(wand1, profile);
            }
            case IN_INVENTORY -> {
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
    protected Boolean execute(final Profile profile) {
        final Player player = profile.getOnlineProfile().get().getPlayer();
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
