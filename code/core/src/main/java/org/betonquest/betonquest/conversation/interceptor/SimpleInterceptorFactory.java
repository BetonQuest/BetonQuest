package org.betonquest.betonquest.conversation.interceptor;

import org.betonquest.betonquest.api.profile.OnlineProfile;

/**
 * Factory to create a new {@link SimpleInterceptor}.
 */
public class SimpleInterceptorFactory implements InterceptorFactory {

    /**
     * The empty default constructor.
     */
    public SimpleInterceptorFactory() {
    }

    @Override
    public Interceptor create(final OnlineProfile profile) {
        return new SimpleInterceptor(profile);
    }
}
