package org.betonquest.betonquest.quest.event.weather;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.event.worldselector.WorldSelector;
import org.bukkit.World;

/**
 * The weather event, changing the weather on the server.
 */
public class WeatherEvent implements Event {

    /**
     * The weather that will be set when the event is executed.
     */
    private final Weather weather;

    /**
     * The selector to get the world for that the weather should be set.
     */
    private final WorldSelector worldSelector;

    /**
     * Create the weather event to set the given state.
     *
     * @param weather       the weather to set
     * @param worldSelector to get the world that should be affected
     */
    public WeatherEvent(final Weather weather, final WorldSelector worldSelector) {
        this.weather = weather;
        this.worldSelector = worldSelector;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final World world = worldSelector.getWorld(profile);
        weather.applyTo(world);
    }
}
