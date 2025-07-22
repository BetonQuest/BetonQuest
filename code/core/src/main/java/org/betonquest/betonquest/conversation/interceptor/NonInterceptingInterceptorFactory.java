package org.betonquest.betonquest.conversation.interceptor;

import org.betonquest.betonquest.api.profile.OnlineProfile;

/**
 * Factory to create a new {@link NonInterceptingInterceptor}.
 */
public class NonInterceptingInterceptorFactory implements InterceptorFactory {

    /**
     * The empty default constructor.
     */
    public NonInterceptingInterceptorFactory() {
    }

    @Override
    public Interceptor create(final OnlineProfile profile) {
        return new NonInterceptingInterceptor(profile);
    }
}
