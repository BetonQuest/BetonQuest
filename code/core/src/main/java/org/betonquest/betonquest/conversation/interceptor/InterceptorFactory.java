package org.betonquest.betonquest.conversation.interceptor;

import org.betonquest.betonquest.api.profile.OnlineProfile;

/**
 * Factory to create a new {@link Interceptor}.
 */
@FunctionalInterface
public interface InterceptorFactory {

    /**
     * Creates a new {@link Interceptor}.
     *
     * @param profile the profile of the player
     * @return the new interceptor
     */
    Interceptor create(OnlineProfile profile);
}
