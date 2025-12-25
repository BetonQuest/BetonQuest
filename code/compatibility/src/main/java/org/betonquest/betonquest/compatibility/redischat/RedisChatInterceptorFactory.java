package org.betonquest.betonquest.compatibility.redischat;

import dev.unnm3d.redischat.api.RedisChatAPI;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.interceptor.Interceptor;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;

/**
 * Factory to create a new {@link RedisChatInterceptor}.
 */
public class RedisChatInterceptorFactory implements InterceptorFactory {

    /**
     * API used for stopping the chat.
     */
    private final RedisChatAPI api;

    /**
     * Create a new interceptor factory.
     *
     * @param api the redis chat api used to create the interceptor
     */
    public RedisChatInterceptorFactory(final RedisChatAPI api) {
        this.api = api;
    }

    @Override
    public Interceptor create(final OnlineProfile profile) {
        return new RedisChatInterceptor(profile, api);
    }
}
