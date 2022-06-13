package org.betonquest.betonquest.modules.schedule.impl.gametime;

import org.bukkit.event.world.TimeSkipEvent.SkipReason;

public enum GametimeSkipReason {

    COMMAND,

    PLUGIN,

    BED;

    public SkipReason toBukkit() {
        return switch (this) {
            case COMMAND -> SkipReason.COMMAND;
            case PLUGIN -> SkipReason.CUSTOM;
            case BED -> SkipReason.NIGHT_SKIP;
        };
    }

    public static GametimeSkipReason fromBukkit(final SkipReason skipReason) {
        return switch (skipReason) {
            case COMMAND -> COMMAND;
            case CUSTOM -> PLUGIN;
            case NIGHT_SKIP -> BED;
        };
    }
}
