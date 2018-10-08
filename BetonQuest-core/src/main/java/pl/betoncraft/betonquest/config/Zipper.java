/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {
    List<String> fileList = new ArrayList<>();
    private String OUTPUT_ZIP_FILE;
    private String SOURCE_FOLDER;

    public Zipper(String source, String output) {
        String modifiedOutput = output;
        int i = 1;
        while (new File(modifiedOutput + ".zip").exists()) {
            i++;
            modifiedOutput = output + "-" + i;
        }
        OUTPUT_ZIP_FILE = modifiedOutput + ".zip";
        SOURCE_FOLDER = source;
        generateFileList(new File(SOURCE_FOLDER));
        zipIt(OUTPUT_ZIP_FILE);
    }

    /**
     * Zip it
     *
     * @param zipFile
     *            output ZIP file location
     */
    public void zipIt(String zipFile) {

        byte[] buffer = new byte[1024];

        try {

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (String file : this.fileList) {

                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in = new FileInputStream(SOURCE_FOLDER + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            // remember close it
            zos.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Traverse a directory and get all files, and add the file into fileList
     *
     * @param node
     *            file or directory
     */
    public void generateFileList(File node) {

        if (node.getName().matches("^backup.*") || node.getName().matches("^database\\.db$")
                || node.getName().matches("^changelog\\.txt$") || node.getName().matches("^logs$")) {
            return;
        }

        // add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    /**
     * Format the file path for zip
     *
     * @param file
     *            file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file) {
        return file.substring(SOURCE_FOLDER.length() + 1, file.length());
    }
}
