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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import pl.betoncraft.betonquest.utils.LogUtils;

/**
 * This zip a folder
 */
public class Zipper {
	/**
	 * A list of regular expressions, that should be excluded from the zip.
	 */
	private final static List<String> EXCUTIONS = Arrays.asList("^backup.*", "^database\\.db$", "^changelog\\.txt$",
			"^logs$");

	/**
	 * The folder, that should be zipped
	 */
	final private String sourceFolder;
	/**
	 * The name of the output zip
	 */
	final private String outputZipFile;
	/**
	 * A list of all files, that should be in the zip
	 */
	final private List<String> fileList = new ArrayList<>();

	/**
	 * Generate a zip file
	 * 
	 * @param source The source directory name
	 * @param output The output file name
	 */
	public Zipper(final String source, final String output) {
		sourceFolder = source;
		outputZipFile = findOutputName(output);
		generateFileList(new File(sourceFolder));
		zipIt();
	}

	private String findOutputName(final String output) {
		String modifiedOutput = output + ".zip";
		for (int i = 1; new File(modifiedOutput).exists(); i++) {
			modifiedOutput = output + "-" + i + ".zip";
		}
		return modifiedOutput;
	}

	private void generateFileList(final File node) {
		if (EXCUTIONS.stream().filter(exclution -> node.getName().matches(exclution)).findAny().isPresent()) {
			return;
		}

		if (node.isFile()) {
			fileList.add(node.getName());
		}
		if (node.isDirectory()) {
			for (final String filename : node.list()) {
				generateFileList(new File(node, filename));
			}
		}
	}

	private void zipIt() {
		final byte[] buffer = new byte[1024];
		try (FileOutputStream fos = new FileOutputStream(outputZipFile);
				ZipOutputStream zos = new ZipOutputStream(fos)) {
			for (final String file : this.fileList) {
				final ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);
				try (FileInputStream in = new FileInputStream(sourceFolder + File.separator + file)) {
					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
				}
			}
		} catch (IOException e) {
		    LogUtils.getLogger().log(Level.WARNING, "Couldn't zip the files");
            LogUtils.logThrowable(e);
		}
	}
}
