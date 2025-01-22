package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableString;
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
    private final List<VariableString> lines;

    public ItemDescription(final QuestPackage pack, final Collection<String> content) throws QuestException {
        this.lines = new ArrayList<>();
        for (final String line : content) {
            this.lines.add(new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, line));
        }
    }

    /**
     * Receive display name of item for specific player.
     *
     * @param profile the {@link Profile} of the player
     * @return the item's display name for the specified player.
     */
    @Nullable
    public String getDisplayName(final Profile profile) {
        final VariableString displayName = this.lines.get(0);
        if (displayName == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', displayName.getString(profile));
    }

    /**
     * Receive lore of the item for specific player.
     *
     * @param profile the {@link Profile} of the player
     * @return the item's lore for the specified player.
     */
    public List<String> getLore(final Profile profile) {
        final List<VariableString> lines = this.lines.subList(1, this.lines.size());
        if (lines.isEmpty()) {
            return new ArrayList<>();
        }
        final List<String> lore = new ArrayList<>(lines.size());
        for (final VariableString line : lines) {
            lore.addAll(Arrays.asList(ChatColor.translateAlternateColorCodes('&', line.getString(profile)).split("\n")));
        }
        return lore;
    }
}
