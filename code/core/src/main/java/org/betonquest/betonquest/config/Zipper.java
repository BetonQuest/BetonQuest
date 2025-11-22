package org.betonquest.betonquest.config;

import org.betonquest.betonquest.BetonQuest;
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
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(Zipper.class, "Zipper");

    private Zipper() {
    }

    /**
     * Zip a source file or directory to the given outputZip path.
     * You can optionally define skipEntries, which are regex expressions,
     * which define what get skipped from being zipped.
     *
     * @param source      the source file or directory
     * @param outputZip   the output zip file without the ending .zip
     * @param skipEntries regex expressions that should be skipped during zip process
     */
    public static void zip(final File source, final String outputZip, final String... skipEntries) {
        final List<Path> files = generateFileList(source, skipEntries);
        final Path outputZipFile = getOutputPath(outputZip);
        zipFiles(source, files, outputZipFile);
    }

    private static List<Path> generateFileList(final File node, final String... skipEntries) {
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
                LOG.warn("Directory '" + node.getPath() + "' could not be read!");
            } else {
                for (final File filename : subNote) {
                    fileList.addAll(generateFileList(filename, skipEntries));
                }
            }
        }

        return fileList;
    }

    private static Path getOutputPath(final String outputZip) {
        Path output;
        int counter = 0;
        do {
            final String offset = counter == 0 ? "" : "-" + counter;
            output = Paths.get(outputZip + offset + ".zip");
            counter++;
        } while (Files.exists(output));
        return output;
    }

    private static void zipFiles(final File source, final List<Path> files, final Path zipFile) {
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
            LOG.warn("Couldn't zip the files in directory '" + source.getPath() + "'!", e);
        }
    }

    private static String generateZipEntry(final File source, final Path file) {
        return source.toURI().relativize(file.toUri()).getPath();
    }
}
