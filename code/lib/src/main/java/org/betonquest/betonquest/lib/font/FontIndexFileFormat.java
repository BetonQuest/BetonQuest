package org.betonquest.betonquest.lib.font;

import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.betonquest.betonquest.api.common.component.font.Font;
import org.betonquest.betonquest.api.common.component.font.FontIndexReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
    JSON("json") {
        @Override
        public Font read(final InputStream inputStream) {
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
     * where the least significant 21 bits represent the Unicode codepoint of the character
     * and the most significant 11 bits represent the width as an unsigned integer.
     * Due to the limitation of unsigned 11 bits as width,
     * only characters with a maximum width of 2<sup>11</sup>-1 (=2047) may be used in this format.
     */
    BINARY("bin") {
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

    /**
     * The file extension for this format.
     */
    private final String extension;

    FontIndexFileFormat(final String extension) {
        this.extension = extension;
    }

    /**
     * Get the format for the given file extension.
     *
     * @param extension the file extension
     * @return the format or BINARY by default if the extension is unknown
     */
    public static FontIndexFileFormat fromExtension(final String extension) {
        return Arrays.stream(values())
                .filter(format -> format.extension.equalsIgnoreCase(extension))
                .findFirst().orElse(BINARY);
    }

    /**
     * Get the file extension for this format.
     *
     * @return the file extension
     */
    public String getExtension() {
        return extension;
    }
}
