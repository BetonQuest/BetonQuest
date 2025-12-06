package org.betonquest.betonquest.library.font;

import net.kyori.adventure.key.Key;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.common.component.font.Font;
import org.betonquest.betonquest.api.common.component.font.FontIndexFileFormat;
import org.betonquest.betonquest.api.common.component.font.FontIndexReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A utility class to load fonts from index files.
 */
public class FontRetriever {

    /**
     * Create an empty new {@link FontRetriever}.
     */
    public FontRetriever() {
    }

    /**
     * Load a {@link Font} from an index file and register it with the given key.
     *
     * @param pathToFile  the path to the index file
     * @param indexReader the {@link FontIndexReader} implementation to read the font from the index file
     * @return an optional containing the loaded font, or an empty optional if an error occurred
     * @see FontIndexFileFormat
     */
    public Optional<Font> loadFontFromIndex(final Path pathToFile, final FontIndexReader indexReader) {
        try (InputStream resource = Files.newInputStream(pathToFile)) {
            return Optional.of(indexReader.read(resource));
        } catch (final IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Get the key for a font index file according to the file name.
     * Everything before the first dot is the key name.
     *
     * @param fontIndexFile the font index file
     * @return the key
     */
    public Key getKeyForFontIndexFile(final Path fontIndexFile) {
        final String fileName = fontIndexFile.getFileName().toString();
        final String keyName = fileName.substring(0, fileName.indexOf('.'));
        return Key.key(keyName, '+');
    }

    /**
     * Load all font index files from a given directory and transform them into {@link Font}s.
     * It will ignore all files that do not have the given extension.
     * If the path is a file, this method will behave like {@link #loadFontFromIndex(Path, FontIndexReader)}.
     *
     * @param path      the path to the directory
     * @param reader    the font index reader to read the font from the index file
     * @param extension the file extension of the font index file without the dot
     * @return a list of loaded fonts or an empty list if no fonts were found
     */
    public List<Pair<Key, Font>> loadFonts(final Path path, final FontIndexReader reader, final String extension) {
        if (!Files.isDirectory(path)) {
            final Optional<Font> font = loadFontFromIndex(path, reader);
            return font.map(elem -> Pair.of(getKeyForFontIndexFile(path), elem))
                    .map(List::of).orElse(List.of());
        }
        final List<Pair<Key, Font>> fonts = new ArrayList<>();
        final File[] files = path.toFile().listFiles((f, name) -> name.endsWith("." + extension));
        if (files == null) {
            return fonts;
        }
        for (final File file : files) {
            fonts.addAll(loadFonts(file.toPath(), reader, extension));
        }
        return fonts;
    }

    /**
     * Load all font index files from a given directory and transform them into {@link Font}s.
     * If the path is a file, this method will behave like {@link #loadFontFromIndex(Path, FontIndexReader)}.
     * It will detect the file extension from the path and find the corresponding {@link FontIndexFileFormat} as reader.
     *
     * @param path the path to the directory
     * @return a list of loaded fonts or an empty list if no fonts were found
     */
    public List<Pair<Key, Font>> loadFonts(final Path path) {
        if (!Files.isDirectory(path)) {
            final String extension = FilenameUtils.getExtension(path.toString());
            return loadFonts(path, FontIndexFileFormat.fromExtension(extension), extension);
        }
        final List<Pair<Key, Font>> fonts = new ArrayList<>();
        for (final FontIndexFileFormat format : FontIndexFileFormat.values()) {
            fonts.addAll(loadFonts(path, format, format.getExtension()));
        }
        return fonts;
    }
}
