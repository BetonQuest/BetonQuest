package org.betonquest.betonquest.commands.quest.download;

import java.util.List;

/**
 * Command data for downloading remote quests.
 *
 * @param layoutRule            the rule to determine the repositories layout
 * @param downloadPackages      whether to download packages
 * @param downloadTemplates     whether to download templates
 * @param sourcePath            the source directory to search for packages in
 * @param repositoryBasePackage the base package to download the selected items from
 * @param localBasePackage      the base package to download the selected items to
 * @param packages              the packages to be downloaded
 * @param files                 the files to be downloaded
 * @param recursive             whether to download packages recursively
 * @param force                 whether to force the download by overwriting local files if necessary
 */
public record DownloadCommand(
        RepositoryLayoutRule layoutRule,
        boolean downloadPackages,
        boolean downloadTemplates,

        String sourcePath,
        String repositoryBasePackage,
        String localBasePackage,
        List<String> packages,
        List<String> files,
        boolean recursive,
        boolean force
) {
}
