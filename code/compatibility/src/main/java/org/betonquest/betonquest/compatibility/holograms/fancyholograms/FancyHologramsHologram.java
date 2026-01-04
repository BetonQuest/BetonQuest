package org.betonquest.betonquest.compatibility.holograms.fancyholograms;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * FancyHolograms specific implementation of BetonHologram.
 * https://fancyinnovations.com/docs/minecraft-plugins/fancyholograms/api/getting-started
 */
public class FancyHologramsHologram implements BetonHologram {

    /**
     * The hologram object from FancyHolograms
     */
    private final Hologram hologram;
    private final HologramData hologramData;
    private final HologramManager hologramManager;

    /**
     * Create a BetonHologram to wrap the given FancyHolograms hologram.
     *
     * @param hologram The hologram object to wrap
     */
    public  FancyHologramsHologram(
            final Hologram hologram,
            final HologramData hologramData,
            final HologramManager hologramManager
    ) {
        this.hologram = hologram;
        this.hologramData = hologramData;
        this.hologramManager = hologramManager;
    }

    @Override
    public void appendLine(final ItemStack item) {
        if (hologramData instanceof final ItemHologramData itemHologramData) {
            itemHologramData.setItemStack(item);
        }
    }

    @Override
    public void appendLine(final Component text) {
        if (hologramData instanceof final TextHologramData textHologramData) {
            textHologramData.addLine(translate(text));
        }
    }

    @Override
    public void setLine(final int index, final ItemStack item) {
        if (hologramData instanceof final ItemHologramData itemHologramData) {
            itemHologramData.setItemStack(item);
        }
    }

    @Override
    public void setLine(final int index, final Component text) {
        if (hologramData instanceof final TextHologramData textHologramData) {
            textHologramData.setText(List.of(translate(text)));
        }
    }

    @Override
    public void createLines(final int startingIndex, final int linesToBeAdded) {

    }

    @Override
    public void removeLine(final int index) {

    }

    @Override
    public void show(final Player player) {

    }

    @Override
    public void hide(final Player player) {

    }

    @Override
    public void move(final Location location) {

    }

    @Override
    public void showAll() {

    }

    @Override
    public void hideAll() {

    }

    @Override
    public void delete() {

    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public void disable() {

    }

    @Override
    public void enable() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void clear() {

    }

    private String translate(final Component text) {
        return LegacyComponentSerializer.legacySection().serialize(text);
    }
}
