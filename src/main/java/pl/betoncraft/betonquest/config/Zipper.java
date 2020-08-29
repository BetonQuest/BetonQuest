/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.config;

import pl.betoncraft.betonquest.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {
    private List<String> fileList = new ArrayList<>();
    private String outputZipFile;
    private String sourceFolder;

    public Zipper(final String source, final String output) {
        String modifiedOutput = output;
        int counter = 1;
        while (new File(modifiedOutput + ".zip").exists()) {
            counter++;
            modifiedOutput = output + "-" + counter;
        }
        outputZipFile = modifiedOutput + ".zip";
        sourceFolder = source;
        generateFileList(new File(sourceFolder));
        zipIt(outputZipFile);
    }

    /**
     * Zip it
     *
     * @param zipFile output ZIP file location
     */
    public void zipIt(final String zipFile) {

        final byte[] buffer = new byte[1024];

        try {

            final FileOutputStream fos = new FileOutputStream(zipFile);
            final ZipOutputStream zos = new ZipOutputStream(fos);

            for (final String file : this.fileList) {

                final ZipEntry zipEntry = new ZipEntry(file);
                zos.putNextEntry(zipEntry);

                final FileInputStream input = new FileInputStream(sourceFolder + File.separator + file);

                int len;
                while ((len = input.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                input.close();
            }

            zos.closeEntry();
            // remember close it
            zos.close();

        } catch (IOException e) {
            LogUtils.getLogger().log(Level.WARNING, "Couldn't zip the files");
            LogUtils.logThrowable(e);
        }
    }

    /**
     * Traverse a directory and get all files, and add the file into fileList
     *
     * @param node file or directory
     */
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
