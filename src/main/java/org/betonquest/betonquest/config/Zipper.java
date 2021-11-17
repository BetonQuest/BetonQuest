package org.betonquest.betonquest.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class Zipper {
    private final List<String> fileList = new ArrayList<>();
    private final String sourceFolder;

    public Zipper(final String source, final String output) {
        String modifiedOutput = output;
        int counter = 1;
        while (new File(modifiedOutput + ".zip").exists()) {
            counter++;
            modifiedOutput = output + "-" + counter;
        }
        final String outputZipFile = modifiedOutput + ".zip";
        sourceFolder = source;
        generateFileList(new File(sourceFolder));
        zipIt(outputZipFile);
    }

    /**
     * Zip it
     *
     * @param zipFile output ZIP file location
     */
    public final void zipIt(final String zipFile) {

        final byte[] buffer = new byte[1024];

        try (OutputStream fos = Files.newOutputStream(Paths.get(zipFile));
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (final String file : this.fileList) {

                final ZipEntry zipEntry = new ZipEntry(file);
                zos.putNextEntry(zipEntry);

                try (InputStream input = Files.newInputStream(Paths.get(sourceFolder + File.separator + file))) {
                    int len = input.read(buffer);
                    while (len > 0) {
                        zos.write(buffer, 0, len);
                        len = input.read(buffer);
                    }
                }
            }
            zos.closeEntry();
        } catch (final IOException e) {
            LOG.warning("Couldn't zip the files", e);
        }
    }

    /**
     * Traverse a directory and get all files, and add the file into fileList
     *
     * @param node file or directory
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public void generateFileList(final File node) {

        if (node.getName().matches("^backup.*") || node.getName().matches("^database\\.db$")
                || node.getName().matches("^changelog\\.txt$") || node.getName().matches("^logs$")) {
            return;
        }

        // add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }

        if (node.isDirectory()) {
            final String[] subNote = node.list();
            for (final String filename : subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    /**
     * Format the file path for zip
     *
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(final String file) {
        return file.substring(sourceFolder.length() + 1);
    }
}
