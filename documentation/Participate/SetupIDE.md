BetonQuest recommend to use IntelliJ as IDE, but you can still use your preferred IDE.
The advantage of using IntelliJ is that the setup contains some steps that helps fully fill our requirements.
If you still want to use your own IDE, you need to check by our own, that you fully fill our requirements.
You can adopt some steps to your preferred IDE to get a similar setup.

##Installing IntelliJ 
First download <a href="https://www.jetbrains.com/idea/download/" target="_blank">IntelliJ</a> and install it.

After you have installed IntelliJ, we recommend to installing the plugin
<a href="https://plugins.jetbrains.com/plugin/7642-save-actions" target="_blank">Save Actions</a>.
The plugin helps to auto format code, organize imports, add final modifiers, and some other requirements we have.
You don't need to configure that plugin, the project contains the configuration file.

After you have set up the IDE, checkout the repository.

##Building with maven
To build the BetonQuest jar, you simply need to run `maven verify`.
You can execute this directly trough maven or in IntelliJ with the `Maven` tab by
double-click on `BetonQuest/Lifecycle/verify`.
You can then find the jar in the folder `/target/artifacts`

If you want to build without checking our requirements and just want a jar, you can execute `package` instead,
but you need to successfully run `verify`, before you make a PR (Pull Request) on GitHub!

###Optimize the build
As BetonQuest has a lot of dependencies, the build can take a long lime, especially for the first build.
You can speed up this with the following configuration, that request all dependencies at our own Repository Manager.

If you do not already have the file, create a new file `${user.home}/.m2/settings.xml`.
Then adopt or copy the following to the file (this is maybe outdated):

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
This sets mirrors for the original repositories, that now point to our repository.
When new repositories added in the pom.xml, you need to add this repository to this list, to keep the improvement.

###Build on start
The first build of a day can take a while, because every version gets checked every day once.
This is the reason, why an automatic build on startup reduce the time of following builds, and it worth it set it up.
In IntelliJ navigate to `File/Settings/Tools/Startup Tasks` click on the `Add` button and click `Add New Config`.
Now select `Maven`, set a `Name` like `BetonQuest resolve dependencies` and write `dependency:resolve`
into the field `Command line`. Then confirm with `Ok` twice.
Now after starting IntelliJ the `BetonQuest resolve dependencies` task should run automatically.

###Fully fil the requirements
The build-pipeline checks like 95% off our requirements.
That means `maven verify` will check the most requirements for you, and GitHub Actions is doing the rest for you.
Everything that is not covered by the build-pipeline is really special and will be checked in the PRs by our review.

If you now run into a problem when you execute `maven verify` you will notice it by the message:
````
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
````
or IntelliJ shows something like `Failed to execute goal`. Here you can read, how to solve the requirements violations:

!!! note ""
    === "PMD"
        **<a href="https://pmd.github.io/latest/" target="_blank">PMD Page</a><br><br>**
        PMD will cover the most requirements we have. You notice that you have an issue there when you see the message:
        ````
        [ERROR] Failed to execute goal org.apache.maven.plugins:maven-pmd-plugin:3.14.0:check (default) on project betonquest: You have 1 PMD violation.
        ````
        If you have this message you also have messages, that looks like this:
        ````
        [INFO] PMD Failure: org.betonquest.betonquest.BetonQuest:143 Rule:AvoidLiteralsInIfCondition Priority:3 Avoid using Literals in Conditional Statements.
        ````
        If you read this, you may know what is wrong. If you don't know why, visit the
        <a href="https://pmd.github.io/latest/" target="_blank">PMD</a> page.
        Then you type in the rule e.g. `AvoidLiteralsInIfCondition` in the search and click on the rule.
        Then you get a detailed description, what is wrong.
        If you still don't know how to solve it, ask the developers on Discord for help with PMD.
    === "SpotBugs"
        **<a href="https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html" target="_blank">SpotBugs Page</a><br><br>**
        SpotBugs searches for some more problems, the most of them are potential bugs. You notice SpotBugs by a message like this:
        ````
        Failed to execute goal com.github.spotbugs:spotbugs-maven-plugin:4.2.2:check (default) on project betonquest: failed with 1 bugs and 0 errors 
        ````
        If you have this message you also have messages, that looks like this:
        ````
        [ERROR] Medium: Null passed for non-null parameter of org.betonquest.betonquest.utils.PlayerConverter.getPlayer(String) in org.betonquest.betonquest.BetonQuest.condition(String, ConditionID) [org.betonquest.betonquest.BetonQuest, org.betonquest.betonquest.BetonQuest] Method invoked at BetonQuest.java:[line 349]Known null at BetonQuest.java:[line 344] NP_NULL_PARAM_DEREF
        ````
        SpotBugs errors are a little more complicated to read,
        but you find e.g. `NP_NULL_PARAM_DEREF` at the end of the line, and you can search on the
        <a href="https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html#" target="_blank">SpotBugs</a> page for it.
        If you have problems solving this issues you can ask on your Discord for help with SpotBugs.
    === "CheckStyle"
        **<a href="https://checkstyle.sourceforge.io/checks.html" target="_blank">CheckStyle Page</a><br><br>**
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
        
        

 
##Advanced Edits
* Clone in IntelliJ and select the "Docs" project scope.

* Create a new branch before you start editing anything.

Make sure <a href="https://www.python.org/downloads/" target="_blank">Python3</a> is installed on your local system,
and you added it to the path. If you use Python for more than this you might want to look into
<a href="https://docs.python.org/3/library/venv.html" target="_blank">python virtual environments</a> to avoid conflicts.
This should not be the case for any non-devs though.

You may need to install <a href="https://www.gtk.org/" target="_blank">GTK</a> if you are on Windows.
You can also use this <a href="https://github.com/tschoonj/GTK-for-Windows-Runtime-Environment-Installer/" target="_blank">GTK installer</a> for Windows instead. 

Install all other dependencies by entering `pip install -r config/docs-requirements.txt` in the console.

In case you are a material-mkdocs insider (paid premium version):
Set your license key by executing `set MKDOCS_MATERIAL_INSIDERS=LICENSE_KEY_HERE` (Windows) in the console.
Then run `pip install -r config/docs-requirements-insiders.txt` instead of `docs-requirements.txt`.


The only thing that's missing once you have done all that is all large files (images & videos). We use 
<a href="https://git-lfs.github.com/" target="_blank">Git LFS</a> to store them, so you need to install that too.
Just run `git lfs install` once you have executed the file that you downloaded from the Git LFS website.  
Then use `git lfs pull` to actually download the files.

Congrats! You should be ready to go.

### See your changes live

You are now primarily working with tools called _mkdocs_ and  _mkdocs-material-theme_ in case you want to google anything.
All files are regular markdown files though.
 
MkDocs enables you to create a website that shows you your changes while you make them.
Execute this to see a preview of the webpage on <a href="http://127.0.0.1:8000" target="_blank">127.0.0.1:8000</a>:

```BASH
mkdocs serve
```
