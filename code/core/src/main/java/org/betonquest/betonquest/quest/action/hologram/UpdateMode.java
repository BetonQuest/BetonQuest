package org.betonquest.betonquest.quest.action.hologram;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.compatibility.holograms.HologramRunner;
import org.betonquest.betonquest.compatibility.holograms.HologramWrapper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Specific update action to do with holograms for a profile.
 */
public enum UpdateMode implements BiConsumer<OnlineProfile, HologramWrapper>, Consumer<OnlineProfile> {
    /**
     * Refreshes the visibility of Holograms.
     */
    VISIBILITY((profile, wrapper) -> wrapper.updateVisibilityForPlayer(profile), HologramRunner::refresh),
    /**
     * Updates the content of Holograms.
     */
    CONTENT((profile, wrapper) -> wrapper.updateContent(), profile -> HologramRunner.updateAllContents()),
    /**
     * All modes combined.
     */
    ALL(CONTENT.biConsumer.andThen(VISIBILITY.biConsumer), CONTENT.consumer.andThen(VISIBILITY.consumer));

    /**
     * Action to do with a given specific hologram.
     */
    private final BiConsumer<OnlineProfile, HologramWrapper> biConsumer;

    /**
     * Action to do when no specific hologram is given.
     */
    private final Consumer<OnlineProfile> consumer;

    UpdateMode(final BiConsumer<OnlineProfile, HologramWrapper> biConsumer, final Consumer<OnlineProfile> consumer) {
        this.biConsumer = biConsumer;
        this.consumer = consumer;
    }

    @Override
    public void accept(final OnlineProfile onlineProfile, final HologramWrapper hologramWrapper) {
        biConsumer.accept(onlineProfile, hologramWrapper);
    }

    @Override
    public void accept(final OnlineProfile onlineProfile) {
        consumer.accept(onlineProfile);
    }
}
