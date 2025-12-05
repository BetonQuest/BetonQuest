package org.betonquest.betonquest.api.common.component.font;

import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents default font index file formats and their respective readers.
 * <ul>
 *     <li>{@link #JSON}: JSON index file format</li>
 *     <li>{@link #BINARY}: Binary index file format</li>
 * </ul>
 */
public enum FontIndexFileFormat implements FontIndexReader {

    /**
     * JSON index file format.
     * The file should contain a JSON object with isolated characters as keys
     * paired with their respective width as integer values.
     */
    JSON {
        @Override
        public Font read(final InputStream inputStream) throws IOException {
            final JsonObject jsonObject = new Gson().fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
            final Map<Integer, Integer> widths = jsonObject.entrySet().stream()
                    .map(entry -> Map.entry(entry.getKey().codePointAt(0), entry.getValue().getAsInt()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return c -> widths.getOrDefault(c, DEFAULT_CHARACTER_WIDTH);
        }
    },

    /**
     * Binary index file format.
     * The file should contain a sequence of 4-byte blocks,
     * where the least significant 21 bits represent the character codepoint in {@code UTF-16} as used by java
     * and the most significant 11 bits represent the width as unsigned integer.
     * Due to the limitation of 11 bits unsigned as width,
     * only characters with a maximum width of 2<sup>11</sup>-1 (=2047) may be used in this format.
     */
    BINARY {
        @SuppressWarnings("PMD.AssignmentInOperand")
        @Override
        public Font read(final InputStream inputStream) throws IOException {
            final Map<Integer, Integer> widths = new HashMap<>();
            final byte[] block = new byte[4];
            final int codepointMask = 0x1FFFFF;
            int read;
            while ((read = inputStream.read(block)) > 0) {
                if (read != block.length) {
                    throw new IOException("Invalid block size: " + read);
                }
                final int blockValue = Ints.fromByteArray(block);
                //int[32 bits]: [11 bits value][21 bits codepoint]
                widths.put(blockValue & codepointMask, blockValue >>> 21);
            }
            return c -> widths.getOrDefault(c, DEFAULT_CHARACTER_WIDTH);
        }
    };

    /**
     * Default width for characters without a specific width in the index file.
     * In case the width is unknown, use this.
     */
    private static final int DEFAULT_CHARACTER_WIDTH = 6;
}
