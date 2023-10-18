package org.betonquest.betonquest.commands.quest.download;

import com.martiansoftware.jsap.*;

import java.util.Arrays;

public class JsapDownloadCommandParser {
    private static final String DOWNLOAD_TEMPLATE_FLAG = "downloadTemplate";
    private static final String DOWNLOAD_PACKAGE_FLAG = "downloadPackage";
    private static final String LAYOUT_RAW_FLAG = "raw";
    private static final String LAYOUT_STRUCTURED_FLAG = "structured";
    private static final String REPOSITORY_SOURCE_PATH_FLAG = "repositorySourcePath";
    private static final String REPOSITORY_BASE_PACKAGE_FLAG = "repositoryBasePackage";
    private static final String LOCAL_BASE_PACKAGE_FLAG = "localBasePackage";
    private static final String SELECT_PACKAGE_FLAG = "selectedPackages";
    private static final String SELECT_FILE_FLAG = "selectedFiles";
    private static final String RECURSIVE_FLAG = "recursive";
    private static final String FORCE_FLAG = "force";
    public static final String GITHUB_NAMESPACE_FLAG = "gitHubNamespace";
    public static final String GIT_REFERENCE_FLAG = "gitRef";

    private final JSAP parser;

    public JsapDownloadCommandParser() {
        this.parser = createDownloadSubcommandJsap();
    }

    private JSAP createDownloadSubcommandJsap() {
        try {
            final JSAP jsap = new JSAP();
            jsap.registerParameter(new Switch(DOWNLOAD_TEMPLATE_FLAG, 'T', "downloadTemplate"));
            jsap.registerParameter(new Switch(DOWNLOAD_PACKAGE_FLAG, 'P', "downloadPackage"));
            jsap.registerParameter(new Switch(LAYOUT_RAW_FLAG, 'R', "raw"));
            jsap.registerParameter(new Switch(LAYOUT_STRUCTURED_FLAG, 'S', "structured"));
            jsap.registerParameter(new FlaggedOption(REPOSITORY_SOURCE_PATH_FLAG, JSAP.STRING_PARSER, "", true, 's', "source"));
            jsap.registerParameter(new FlaggedOption(REPOSITORY_BASE_PACKAGE_FLAG, JSAP.STRING_PARSER, "", true, 'b', "base-package"));
            jsap.registerParameter(new FlaggedOption(LOCAL_BASE_PACKAGE_FLAG, JSAP.STRING_PARSER, "", true, 'l', "local"));
            jsap.registerParameter(new FlaggedOption(SELECT_PACKAGE_FLAG, JSAP.STRING_PARSER, null, true, 'p', "package").setAllowMultipleDeclarations(true));
            jsap.registerParameter(new FlaggedOption(SELECT_FILE_FLAG, JSAP.STRING_PARSER, null, true, 'F', "file").setAllowMultipleDeclarations(true));
            jsap.registerParameter(new Switch(RECURSIVE_FLAG, 'r', "recursive"));
            jsap.registerParameter(new Switch(FORCE_FLAG, 'f', "force"));
            jsap.registerParameter(new UnflaggedOption(GITHUB_NAMESPACE_FLAG).setStringParser(JSAP.STRING_PARSER));
            jsap.registerParameter(new UnflaggedOption(GIT_REFERENCE_FLAG).setStringParser(JSAP.STRING_PARSER));
            return jsap;
        } catch (JSAPException jsapException) {
            throw new UnsupportedOperationException("JSAP parser is configured incorrectly!", jsapException);
        }
    }

    public DownloadCommand parse(final String... args) {
        final JSAPResult result = parser.parse(args);
        final boolean downloadPackagesSelected = result.getBoolean(DOWNLOAD_PACKAGE_FLAG);
        final boolean downloadTemplatesSelected = result.getBoolean(DOWNLOAD_TEMPLATE_FLAG);
        return new DownloadCommand(
                RepositoryLayoutRule.fromFlags(result.getBoolean(LAYOUT_STRUCTURED_FLAG), result.getBoolean(LAYOUT_RAW_FLAG)),
                downloadPackagesSelected || !downloadTemplatesSelected,
                downloadTemplatesSelected || !downloadPackagesSelected,
                result.getString(REPOSITORY_SOURCE_PATH_FLAG),
                result.getString(REPOSITORY_BASE_PACKAGE_FLAG),
                result.getString(LOCAL_BASE_PACKAGE_FLAG),
                Arrays.asList(result.getStringArray(SELECT_PACKAGE_FLAG)),
                Arrays.asList(result.getStringArray(SELECT_FILE_FLAG)),
                result.getBoolean(RECURSIVE_FLAG),
                result.getBoolean(FORCE_FLAG)
        );
    }
}
