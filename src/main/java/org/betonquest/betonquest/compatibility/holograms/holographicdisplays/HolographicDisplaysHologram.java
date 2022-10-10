package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;


import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyMethods"})
public class HolographicDisplaysHologram implements BetonHologram {
    private static boolean REGISTERED_PLACEHOLDERS = false;

    private final Hologram hologram;

    @SuppressWarnings({"PMD.UnusedFormalParameter"}) //This parameter is necessary due to reflection.
    public HolographicDisplaysHologram(final String name, final Location location) {
        if (!REGISTERED_PLACEHOLDERS) {
            HolographicDisplaysAPI.get(BetonQuest.getInstance()).registerIndividualPlaceholder("bq", new HologramPlaceholder());
            HolographicDisplaysAPI.get(BetonQuest.getInstance()).registerGlobalPlaceholder("bqg", new HologramGlobalPlaceholder());
            HolographicDisplaysHologram.REGISTERED_PLACEHOLDERS = true;
        }
        hologram = HolographicDisplaysAPI.get(BetonQuest.getInstance()).createHologram(location);
        hologram.setPlaceholderSetting(PlaceholderSetting.ENABLE_ALL);
    }

    @Override
    public void appendLine(final ItemStack item) {
        hologram.getLines().appendItem(item);
    }

    @Override
    public void appendLine(final String text) {
        hologram.getLines().appendText(text);
    }

    @Override
    public void setLine(final int index, final ItemStack item) {
        final HologramLines lines = hologram.getLines();
        lines.remove(index);
        lines.insertItem(index, item);
    }

    @Override
    public void setLine(final int index, final String text) {
        final HologramLines lines = hologram.getLines();
        lines.remove(index);
        lines.insertText(index, text);
    }

    @Override
    public void insertLine(final int index, final ItemStack item) {
        hologram.getLines().insertItem(index, item);
    }

    @Override
    public void insertLine(final int index, final String text) {
        hologram.getLines().insertText(index, text);
    }

    @Override
    public void removeLine(final int index) {
        hologram.getLines().remove(index);
    }

    @Override
    public void show(final Player player) {
        hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
    }

    @Override
    public void hide(final Player player) {
        hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN);
    }

    @Override
    public void move(final Location location) {
        hologram.setPosition(location);
    }

    @Override
    public void showAll() {
        final VisibilitySettings settings = hologram.getVisibilitySettings();
        settings.setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
        settings.clearIndividualVisibilities();
    }

    @Override
    public void hideAll() {
        final VisibilitySettings settings = hologram.getVisibilitySettings();
        settings.setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        settings.clearIndividualVisibilities();

    }

    @Override
    public void delete() {
        hologram.delete();
    }

    @Override
    public int size() {
        return hologram.getLines().size();
    }

    @Override
    public void clear() {
        hologram.getLines().clear();
    }
}
