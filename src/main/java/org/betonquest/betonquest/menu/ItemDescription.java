package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Contains the description of an item in a menu. Variables are parsed and color codes are replaced.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ItemDescription {
    private final List<Variable<String>> lines;

    public ItemDescription(final QuestPackage pack, final Collection<String> content) throws QuestException {
        this.lines = new ArrayList<>();
        for (final String line : content) {
            this.lines.add(new Variable<>(BetonQuest.getInstance().getVariableProcessor(), pack, line, Argument.STRING));
        }
    }

    /**
     * Receive display name of item for specific player.
     *
     * @param profile the {@link Profile} of the player
     * @return the item's display name for the specified player.
     * @throws QuestException if the display name cannot be parsed
     */
    @Nullable
    public String getDisplayName(final Profile profile) throws QuestException {
        final Variable<String> displayName = this.lines.get(0);
        if (displayName == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', displayName.getValue(profile));
    }

    /**
     * Receive lore of the item for specific player.
     *
     * @param profile the {@link Profile} of the player
     * @return the item's lore for the specified player.
     * @throws QuestException if the lore cannot be parsed
     */
    public List<String> getLore(final Profile profile) throws QuestException {
        final List<Variable<String>> lines = this.lines.subList(1, this.lines.size());
        if (lines.isEmpty()) {
            return new ArrayList<>();
        }
        final List<String> lore = new ArrayList<>(lines.size());
        for (final Variable<String> line : lines) {
            lore.addAll(Arrays.asList(ChatColor.translateAlternateColorCodes('&', line.getValue(profile)).split("\n")));
        }
        return lore;
    }
}
