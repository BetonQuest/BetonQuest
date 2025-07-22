package org.betonquest.betonquest.compatibility.protocollib.conversation;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.interceptor.Interceptor;
import org.betonquest.betonquest.conversation.interceptor.InterceptorFactory;

/**
 * Factory to create a new {@link PacketInterceptor}.
 */
public class PacketInterceptorFactory implements InterceptorFactory {

    /**
     * The empty default constructor.
     */
    public PacketInterceptorFactory() {
    }

    @Override
    public Interceptor create(final OnlineProfile profile) {
        return new PacketInterceptor(profile);
    }
}
