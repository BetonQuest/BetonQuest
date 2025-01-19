package org.betonquest.betonquest.web;

import java.io.IOException;
import java.net.URL;

/**
 * This is an interface that provides basic methods to read information from a given {@link URL}
 * and return it as a {@link String}.
 */
public interface ContentSource {

    /**
     * This method can read a String from a given {@link URL}.
     *
     * @param url that should be read as {@link String}
     * @return the {@link String} represented by the given {@link URL}
     * @throws IOException is thrown if any problem occurred while reading the String from the {@link URL}
     */
    String get(URL url) throws IOException;
}
