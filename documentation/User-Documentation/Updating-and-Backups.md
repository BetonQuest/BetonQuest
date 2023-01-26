## Updating in BetonQuest

There are two ways to update BetonQuest. You can either use the comfortable Auto-Updater or do a manual update by yourself.

**Disclaimer for all versions below 2.0**: The Auto-Updater has a single flaw - all comments in your configurations will be lost during the update process.

### Deciding what version to use

Each plugin version is a number that consists of three parts. For example 2.4.3, the first number (2 in this example) is named MAJOR,
the second MINOR (4 in this example) and the third PATCH (3 in this example).
When we release a new version of BetonQuest we will change these numbers in a specific way. Each number has a fixed meaning.

   | Update Strategy  | `MAJOR`                | `MINOR`                | `PATCH`                |
   |------------------|------------------------|------------------------|------------------------|
   | Bug Fixes        | :white_check_mark:     | :white_check_mark:     | :white_check_mark:     | 
   | New Features     | :white_check_mark:     | :white_check_mark:     | :x:                    | 
   | Breaking Changes | :white_check_mark:     | :x:                    | :x:                    | 
   
   You can also append `_DEV` to each strategy. This will download the dev builds for the corresponding version. This
   is not recommended for production/live servers, as devbuilds can contain bugs. Therefore, enabling a `_DEV` strategy will automatically disable
   automated updates. If you don't care about the risk you can re-enable it, otherwise check what the new devbuilds do before executing `/q update`.  

### Deciding if auto-updates are reasonable

Apart from the version you also have to choose if you want BetonQuest to update automatically or only after confirmation.
Having automatic updates enabled is handy for `PATCH` and `MINOR` but risky on `MAJOR` strategies. 
Automatic updates for any `_DEV` version are dangerous as these can hold very bad bugs. Only use this for test servers.

Disabling automated updates still allows for the use of `/q update`! You won't have to go to Spigot ever again :partying_face:

### Setup of the Auto-Updater

Read the [update](Configuration.md#updating) section to correctly setup the Auto-Updater. 

After updating to a new version (manually or automatically),
configuration files and database will be automatically backed up to a zip file to prevent losing your work due to errors.
Then, configuration will be converted to a new version. At the end, the localization will be updated with new languages, and a _changelog.txt_ file will be created.



If there were any unexpected errors during an update process, download the previous version,
restore your configs from backup, and disable autoupdating feature.
Don't forget to post your error as an Issue on [GitHub](https://github.com/BetonQuest/BetonQuest/issues/new?template=bug_report_template.md) so it can be fixed!

When you enter the server, BetonQuest will alert you about changes and ask you to read changelog.txt file located in plugin's main directory.


### Updating manually

Configs update automatically when a new dev build has been installed but all comments in your configs will be lost due to Spigots limitations.
We will make our own config system in BetonQuest 2.0. though so this will not last long. 

To keep your comments follow this guide:
This is a manual update, so you will have to change some errors in your quests afterwards.
If you already did this once and want to update to a newer dev build (using /q update) just check if the version
has changed by clicking the link in the third step. If it did not your comments are safe.

* Make a backup  
* Stop server.
* Open up /plugins/BetonQuest/config.yml
* Set the version: option to the one that you see here: This might change so please look it up each time you update!     
 https://github.com/BetonQuest/BetonQuest/blob/main/src/main/resources/config.yml
* Update BetonQuest.jar
* Start Server
* Fix all issues (we changed the stuff down below.)

## Backups

Every time the plugin updates the configuration, a backup will be created. 
This is especially important if a development version is being used because they may be unstable. 
A backup can also be created manually by running **/q backup** command. 
It needs to be run from the console on an empty server because it heavily uses the database.

You can find your backups in _backup_ directory in the plugin's folder.
They are .zip files containing all your configuration and _database-backup.yml_ file, which - as the name says - is your database backup.
To replace your configuration with an older backup, delete all the files (except backups and logs) and replace them with the files from .zip file.

If you want your database loaded, place _database-backup.yml_ file in plugin's directory.
When the plugin sees this file while enabling, it will backup the current database and load all data from that file to the database. 
A backup of the old database can be found in _backups_ folder, so if you ever need to load it back,
just rename it to _database-backup.yml_ and place it back in main plugin's directory. Note that _database-backup.yml_ file will be deleted after loading,
so it does not replace your database on next plugin start.
