package org.betonquest.betonquest.compatibility.holograms.fancyholograms;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.data.property.Visibility;
import de.oliver.fancyholograms.api.hologram.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * FancyHolograms specific implementation of BetonHologram.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class FancyHologramsHologram implements BetonHologram {

    /**
     * Vertical spacing between lines.
     */
    private static final float LINE_SPACING = 0.3f;

    /**
     * Instance containing only click events to remove them.
     */
    private static final MiniMessage CLICK_INSTANCE = MiniMessage.builder().tags(StandardTags.clickEvent()).build();

    /**
     * Instance containing all tags used for to serializing.
     */
    private static final MiniMessage NORMAL_INSTANCE = MiniMessage.miniMessage();

    /**
     * The hologram manager to create new Holograms.
     */
    private final HologramManager manager;

    /**
     * Data of the primary hologram.
     */
    private final TextHologramData baseData;

    /**
     * Primary hologram used for test display.
     */
    private final Hologram baseHologram;

    /**
     * Holograms representing each one line.
     */
    private final List<Hologram> holograms;

    /**
     * If the hologram is created but disabled.
     */
    private boolean disabled;

    /**
     * Create a BetonHologram to wrap the given DecentHolograms hologram.
     *
     * @param manager  The hologram manager to create new holograms
     * @param location Zhe base location for the hologram
     */
    public FancyHologramsHologram(final HologramManager manager, final Location location) {
        this.manager = manager;
        this.baseData = new TextHologramData("BQ Base Hologram", location);
        this.baseData.setPersistent(false);
        this.baseData.setVisibility(Visibility.MANUAL);
        this.baseData.setBackground(Hologram.TRANSPARENT);
        this.baseData.setText(List.of());
        this.baseHologram = manager.create(this.baseData);
        this.holograms = new ArrayList<>();
    }

    private Hologram createTextLine(final int offset, final Component text) {
        final Location location = getLocation();
        location.subtract(0, offset * LINE_SPACING, 0);
        final TextHologramData data = new TextHologramData("BQ Text Hologram", location);
        data.setPersistent(false);
        data.setVisibility(Visibility.MANUAL);
        data.setBackground(Hologram.TRANSPARENT);
        data.setText(List.of(translate(text)));
        final Hologram hologram = manager.create(data);
        hologram.createHologram();
        return hologram;
    }

    private Hologram createItemLine(final int offset, final ItemStack item) {
        final Location location = getLocation();
        location.subtract(0, offset * LINE_SPACING, 0);
        final ItemHologramData data = new ItemHologramData("BQ Item Line", location);
        data.setPersistent(false);
        data.setVisibility(Visibility.MANUAL);
        data.setItemStack(item);
        final Hologram hologram = manager.create(data);
        hologram.createHologram();
        return hologram;
    }

    @Override
    public void appendLine(final ItemStack item) {
        final int size = holograms.size();
        holograms.add(createItemLine(size, item));
    }

    @Override
    public void appendLine(final Component text) {
        final int size = holograms.size();
        holograms.add(createTextLine(size, text));
    }

    @Override
    public void setLine(final int index, final ItemStack item) {
        final Hologram oldLine = holograms.get(index);
        if (oldLine.getData() instanceof final ItemHologramData itemData) {
            itemData.setItemStack(item);
            return;
        }
        forceHide(oldLine);
        oldLine.deleteHologram();
        holograms.set(index, createItemLine(index, item));
    }

    @Override
    public void setLine(final int index, final Component text) {
        final Hologram oldLine = holograms.get(index);
        if (oldLine.getData() instanceof final TextHologramData textData) {
            textData.setText(List.of(translate(text)));
            return;
        }
        forceHide(oldLine);
        oldLine.deleteHologram();
        holograms.set(index, createTextLine(index, text));
    }

    /**
     * Converts into MiniMessage format and strips the url click events because they break placeholder resolving.
     */
    private String translate(final Component text) {
        return CLICK_INSTANCE.stripTags(NORMAL_INSTANCE.serialize(text));
    }

    @Override
    public void createLines(final int startingIndex, final int linesAdded) {
        for (int i = startingIndex; i < linesAdded; i++) {
            holograms.add(createTextLine(i, Component.empty()));
        }
    }

    @Override
    public void removeLine(final int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void forAllHolograms(final Consumer<Hologram> consumer) {
        consumer.accept(baseHologram);
        holograms.forEach(consumer);
    }

    @Override
    public void show(final Player player) {
        if (baseHologram.isViewer(player)) {
            return;
        }
        forAllHolograms(hologram -> hologram.forceShowHologram(player));
    }

    @Override
    public void hide(final Player player) {
        if (!baseHologram.isViewer(player)) {
            return;
        }
        forAllHolograms(hologram -> hologram.forceHideHologram(player));
    }

    @Override
    public void move(final Location location) {
        baseData.setLocation(location);
        for (final Hologram hologram : holograms) {
            hologram.getData().setLocation(location);
            hologram.refreshForViewers();
            location.subtract(0, LINE_SPACING, 0);
        }
    }

    @Override
    public void showAll() {
        if (disabled) {
            return;
        }
        Bukkit.getOnlinePlayers().forEach(player ->
                forAllHolograms(hologram -> hologram.forceShowHologram(player)));
    }

    @Override
    public void hideAll() {
        forAllHolograms(this::forceHide);
    }

    private void forceHide(final Hologram hologram) {
        for (final UUID viewer : hologram.getViewers()) {
            final Player player = Bukkit.getPlayer(viewer);
            if (player != null) {
                hologram.forceHideHologram(player);
            }
        }
    }

    @Override
    public void delete() {
        clear();
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void disable() {
        this.disabled = true;
        hideAll();
        forAllHolograms(Hologram::deleteHologram);
    }

    @Override
    public void enable() {
        this.disabled = false;
        forAllHolograms(Hologram::createHologram);
    }

    @Override
    public int size() {
        return holograms.size();
    }

    @Override
    public void clear() {
        hideAll();
        holograms.clear();
    }

    @Override
    public Location getLocation() {
        return baseData.getLocation();
    }
}
