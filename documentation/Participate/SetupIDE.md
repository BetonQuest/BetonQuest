The BetonQuest Organisation recommends <a href="https://www.jetbrains.com/idea/" target="_blank">IntelliJ</a> (Community Edition) as the IDE (Integrated Development Environment).
The advantage of using IntelliJ is that this guide contains some steps and the project contains some files that help to fulfill our requirements regarding code and documentation style.
You can still use your preferred IDE, but then you need to check on your own that your changes fulfill our requirements.

##Installing IntelliJ 
First download <a href="https://www.jetbrains.com/idea/download/" target="_blank">IntelliJ</a> and install it.

After you installed IntelliJ, we recommend installing the plugin
<a href="https://plugins.jetbrains.com/plugin/7642-save-actions" target="_blank">Save Actions</a>.
The plugin automatically formats code, organizes imports, adds final modifiers, and fulfils some other requirements we have.
You don't need to configure that plugin, the project contains the configuration file.

## Check out the repository
You need a Git installation to be able to check out code from GitHub.
You can follow this <a href="https://docs.github.com/en/get-started/quickstart/set-up-git" target="_blank">guide</a> if you don't know how to install Git.  

Then you should <a href="https://docs.github.com/en/get-started/quickstart/fork-a-repo" target="_blank">fork</a> the BetonQuest repository to your own account on GitHub.

After you have setup the IDE,
<a href="https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository-from-github/cloning-a-repository" target="_blank">clone</a>
the BetonQuest repository from your account. You can also directly
<a href="https://blog.jetbrains.com/idea/2020/10/clone-a-project-from-github/" target="_blank">clone the repository in IntelliJ</a>.

??? "In case videos and other files like images are missing after cloning"
    We use <a href="https://git-lfs.github.com/" target="_blank">Git LFS</a> to store big files like media files, so you need to install that too.
    Once you have executed the file that you downloaded from the Git LFS website, just run `git lfs install`.
    Then use `git lfs pull` to actually download the files.

##Building the Plugin jar
You can build the plugin with Maven. Sometimes, IntelliJ auto-detects that BetonQuest is a Maven project. You can see
a "Maven" tab on the right side of the editor if that's the case. Otherwise, do this:
First, right-click the `pom.xml` file in the projects root folder. 
Then select "Add as Maven Project". 

To build the BetonQuest jar, you simply need to run `maven verify`.
You can do this from the command line or use IntelliJ's `Maven` tab (double-click on `BetonQuest/Lifecycle/verify`).
You can then find a `BetonQuest.jar` in the project's folder `/target/artifacts`.

If you want to build without checking our requirements and just want a jar, you can execute `package` instead of `verify`,
but you need to successfully run `verify`, before you make a pull request on GitHub!

###Build speed up
As BetonQuest has a lot of dependencies, the build can take a long lime, especially for the first build.
You can speed this up with the following configuration, that downloads all dependencies from our own Repository Manager
instead of searching through all repositories that are defined in the project.

If you do not already have the file, create a new file in your home directory. `<HOME DIRECTORY>\.m2\settings.xml`.
The home directory on Windows is `C:\Users\<YOUR USER NAME>`.
Then adopt or copy the following into the file:

````XML
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <mirrors>
    <mirror>
      <id>BetonQuest-Mirror</id>
      <url>https://betonquest.org/nexus/repository/default/</url>
      <mirrorOf>betonquest-papermc-repo,betonquest-enginehub-repo,betonquest-heroes-repo,betonquest-lumine-repo,betonquest-citizensnpcs-repo,betonquest-codemc-repo,betonquest-placeholderapi-repo,betonquest-dmulloy2-repo,betonquest-lichtspiele-repo,betonquest-elmakers-repo,betonquest-jitpack-repo,betonquest-sonatype-releases-repo,betonquest-sonatype-snapshots-repo</mirrorOf>
    </mirror>
  </mirrors>

</settings>
````

###Build on Start
The first build of a day can take a while, because every version gets re-checked once every day.
This is the reason, why an automatic build on startup reduces the time of following builds. It is really worth it to set it up.
In IntelliJ navigate to `File/Settings/Tools/Startup Tasks` click on the `Add` button and click `Add New Config`.
Now select `Maven`, set a `Name` like `BetonQuest Resolve Dependencies` and write `dependency:resolve`
into the field `Command line`. Then confirm with `Ok` twice.
Now after starting IntelliJ the `BetonQuest Resolve Dependencies` task should run automatically.

###Fulfil the Contributing Requirements
Run `maven verify` before opening a pull request to check if you change meets the project's requirements regarding code
style and quality.
GitHub Actions(automated code check on GitHub) will also verify these requirements when you open the pull request.

If any requirements are not met, `maven verify` will fail with this log message:
````
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
````
IntelliJ may also show something like `Failed to execute goal`. Here's a guide how to fix any requirement violations:

!!! note ""
    === "PMD"
        Visit the <a href="https://pmd.github.io/latest/" target="_blank">**PMD Page**</a> for general info.
        <br><br>
        PMD mainly checks for code smells. PMD's errors look like this:
        ````
        [ERROR] Failed to execute goal org.apache.maven.plugins:maven-pmd-plugin:3.14.0:check (default) on project betonquest: You have 1 PMD violation.
        ````
        If you have this message you also have messages, that looks like this:
        ````
        [INFO] PMD Failure: org.betonquest.betonquest.BetonQuest:143 Rule:AvoidLiteralsInIfCondition Priority:3 Avoid using Literals in Conditional Statements.
        ````
        If you read this, you may know what is wrong. If you don't know why, visit the
        <a href="https://pmd.github.io/latest/" target="_blank">PMD</a> page.
        Then you type in the rule e.g. `AvoidLiteralsInIfCondition` in the search bar and click on the rule.
        You will get a detailed description about what is wrong.
        If you still don't know how to solve it, ask the developers on Discord for help with PMD.
    === "SpotBugs"
        Visit the <a href="https://spotbugs.readthedocs.io/en/stable/index.html" target="_blank">**SpotBugs Page**</a> for general info.
        <br><br>
        SpotBugs searches for additional problems, most of them are potential bugs. SpotBugs' errors look like this:
        ````
        Failed to execute goal com.github.spotbugs:spotbugs-maven-plugin:4.2.2:check (default) on project betonquest: failed with 1 bugs and 0 errors 
        ````
        If your log contains such a message, it will also contain another message that looks like this:
        ````
        [ERROR] Medium: Null passed for non-null parameter of org.betonquest.betonquest.utils.PlayerConverter.getPlayer(String) in org.betonquest.betonquest.BetonQuest.condition(String, ConditionID) [org.betonquest.betonquest.BetonQuest, org.betonquest.betonquest.BetonQuest] Method invoked at BetonQuest.java:[line 349]Known null at BetonQuest.java:[line 344] NP_NULL_PARAM_DEREF
        ````
        SpotBugs errors are a little complicated to read,
        but if you find e.g. `NP_NULL_PARAM_DEREF` at the end of the line you can simply search it on the
        <a href="https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html" target="_blank">SpotBugs</a> page.
        If you have problems solving these kinds of issues you can ask on our Discord for help with SpotBugs.
    === "CheckStyle"
        Visit the <a href="https://checkstyle.sourceforge.io/checks.html" target="_blank">**CheckStyle Page**</a> for general info.
        <br><br>
        CheckStyle checks the code formatting and style. We have only configured two checks.
        The first check is the import order, and the second check is that you do not use star imports,
        excepting some junit imports.
        There is only a basic check for the imports, and it looks like this:
        ````
        [ERROR] src/main/java/org/betonquest/betonquest/BetonQuest.java:[16,1] (imports) ImportOrder: Wrong order for 'edu.umd.cs.findbugs.annotations.SuppressFBWarnings' import.
        ````
        CheckStyle is very simple to read, normally it is in your language and self explaining.  
        In the above error you can find e.g `ImportOrder` and you can search on the
        <a href="https://checkstyle.sourceforge.io/checks.html" target="_blank">CheckStyle</a> page for it.
        If you need help solving an issue here, you can also ask us on our Discord for help with CheckStyle.
        <br><br><br>
    === "EditorConfig"
        Visit the <a href="https://editorconfig.org/" target="_blank">**EditorConfig Page**</a> for general info.
        <br><br>
        EditorConfig it natively supported by many IDEs and editors.
        It checks for some really basic formatting like brackets, line endings, indention and some more.
        EditorConfig violations look like this:
        ````
        [ERROR] There are .editorconfig violations. You may want to run
        [ERROR]     mvn editorconfig:format
        [ERROR] to fix them automagically.
        ````
        If your log contains such a message, it will also contain another message that looks like this if you want to solve it manual:
        ````
        [ERROR] src\main\java\org\betonquest\betonquest\BetonQuest.java@284,54: Delete 5 characters - violates trim_trailing_whitespace = true, reported by org.ec4j.linters.TextLinter
        ````
        We recommend running `mvn editorconfig:format` or use an IDE that support EditorConfig.

 
##Building the Documentation
Make sure <a href="https://www.python.org/downloads/" target="_blank">Python3</a> is installed on your local system
and added to the PATH environment variable. The Python installer allows you to do so with a checkbox called something like
"Add Python to environment variables".
You also need to install <a href="https://www.gtk.org/" target="_blank">GTK</a>, the easiest way is to use this 
<a href="https://github.com/tschoonj/GTK-for-Windows-Runtime-Environment-Installer/" target="_blank">GTK installer</a>
if you are on Windows. 

Install all other dependencies by entering `pip install -r config/docs-requirements.txt` in the terminal on the project's root directory.

??? "In case you are a material-mkdocs insider (paid premium version)"  
    Set your license key by executing `set MKDOCS_MATERIAL_INSIDERS=LICENSE_KEY_HERE` (Windows) in the terminal.
    Then run `pip install -r config/docs-requirements-insiders.txt` instead of `docs-requirements.txt`.

### See your changes live
You are now primarily working with tools called _mkdocs_ and  _mkdocs-material-theme_ in case you want to google anything.
All files are regular markdown files though.
 
MkDocs enables you to create a website that shows you your changes while you make them.
Execute this in the terminal on the project's root directory, to see a preview of the webpage on <a href="http://127.0.0.1:8000" target="_blank">127.0.0.1:8000</a>:

```BASH
mkdocs serve
```
??? info "Hosting on your entire local network"
    You can also execute this variation to host the website in your local network.
    This can be useful for testing changes on different devices but is not needed for most tasks.
    Make sure the hosting device's firewall exposes the port 8000.
    ```BASH
    mkdocs serve -a 0.0.0.0:8000
    ```
