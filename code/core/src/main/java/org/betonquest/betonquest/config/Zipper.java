package org.betonquest.betonquest.config;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Gives the possibility to zip files or folders to a target file.
 */
public final class Zipper {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The source file or directory.
     */
    private final File source;

    /**
     * The regex expressions that should be skipped during the zip process.
     */
    private final String[] skipEntries;

    /**
     * Create a new Zipper instance.
     *
     * @param log         the custom logger for this class
     * @param source      the source file or directory
     * @param skipEntries the regex expressions that should be skipped during the zip process
     */
    public Zipper(final BetonQuestLogger log, final File source, final String... skipEntries) {
        this.log = log;
        this.source = source;
        this.skipEntries = skipEntries.clone();
    }

    /**
     * Zip a source file or directory to the given outputZip path.
     * You can optionally define skipEntries, which are regex expressions,
     * which define what gets skipped from being zipped.
     *
     * @param outputZip the output zip file without the ending .zip
     */
    public void zip(final String outputZip) {
        final List<Path> files = generateFileList(source);
        final Path outputZipFile = getOutputPath(outputZip);
        zipFiles(files, outputZipFile);
    }

    private List<Path> generateFileList(final File node) {
        final List<Path> fileList = new ArrayList<>();
        for (final String skip : skipEntries) {
            if (node.getName().matches(skip)) {
                return fileList;
            }
        }

        if (node.isFile()) {
            fileList.add(node.toPath());
        }

        if (node.isDirectory()) {
            final File[] subNote = node.listFiles();
            if (subNote == null) {
                log.warn("Directory '" + node.getPath() + "' could not be read!");
            } else {
                for (final File filename : subNote) {
                    fileList.addAll(generateFileList(filename));
                }
            }
        }

        return fileList;
    }

    private Path getOutputPath(final String outputZip) {
        Path output;
        int counter = 0;
        do {
            final String offset = counter == 0 ? "" : "-" + counter;
            output = Paths.get(outputZip + offset + ".zip");
            counter++;
        } while (Files.exists(output));
        return output;
    }

    private void zipFiles(final List<Path> files, final Path zipFile) {
        final byte[] buffer = new byte[1024];

        try (OutputStream fos = Files.newOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (final Path file : files) {
                zos.putNextEntry(new ZipEntry(generateZipEntry(source, file)));

                try (InputStream input = Files.newInputStream(file)) {
                    int len = input.read(buffer);
                    while (len > 0) {
                        zos.write(buffer, 0, len);
                        len = input.read(buffer);
                    }
                }
            }
            zos.closeEntry();
        } catch (final IOException e) {
            log.warn("Couldn't zip the files in directory '" + source.getPath() + "'!", e);
        }
    }

    private String generateZipEntry(final File source, final Path file) {
        return source.toURI().relativize(file.toUri()).getPath();
    }
}
