<style>
.bq-inline-example-container {
  width: clamp(500px,55%,100%) !important;
}
.table {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-gap: 1rem;
}
@media (min-width: 1800px) {
  .table { grid-template-columns: repeat(2, 1fr); }
}
td {
  white-space: nowrap;
}
</style>

# 📥 Updating BetonQuest
BetonQuest has a comfortable Auto-Updater, that you can use without any problem, but there are some things that you need
to be aware of. Alternatively you can install updated manual by your self.

Read the [update](../Configuration.md#updating) section to correctly set up the Auto-Updater.

For a production/live system it is recommended to [disable automatic updates](#enable-or-disable-auto-updates)
and to check out the [CHANGELOG](../CHANGELOG.md) before you execute `/q update` for manual updates.

!!! warning "Updating to 2.0"
If you update to BetonQuest 2.0 you should read the [Migration](Migration.md) guide to see, what you need to do
manually.

## Understanding Versioning
<div class="table" markdown="block">
<div markdown="block">

A plugin version is a number that consists of three parts in the format `MAJOR.MINOR.PATCH`.  
Example: `2.4.3`

When we release a new version of BetonQuest we will change these numbers in a specific way. Each number has a fixed
meaning, that is explained in the following table.

</div>
<div markdown="block">

| Digit            |    `MAJOR` (2)     |    `MINOR` (4)     |    `PATCH` (3)     |
|:-----------------|:------------------:|:------------------:|:------------------:|
| Bug Fixes        | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| New Features     | :white_check_mark: | :white_check_mark: |        :x:         |
| Breaking Changes | :white_check_mark: |        :x:         |        :x:         |

</div>
</div>

## Choose an Update Strategy
Valid update strategies are: `MAJOR`, `MINOR`, `PATCH` and `MAJOR_DEV`, `MINOR_DEV`, `PATCH_DEV`

`MINOR` and `PATCH` strategy are really safe to use, even on a production/live system. You will receive bugfixes and new
features, that will not have an impact on your existing stuff. `MAJOR` strategy is defensively not recommended for
production/live systems, they can break everything. For a test system, you need to decide, if you want to stay on the
latest version with `MAJOR`, or you want ot be more stable.

By adding the `_DEV` suffix to the version, you also download dev-builds for the corresponding version. Dev-builds can
contain everything, bug fixes, new features, but also new bugs, or even worse it beaks something, so don't use this on a
production/live system without checking out the changes.

## Enable or Disable automatic updates
Apart from the version you also have to choose if you want BetonQuest to update automatically or only after
confirmation. Having automatic updates enabled is handy for `PATCH` and `MINOR` but risky on `MAJOR` strategies.
Automatic updates for any `_DEV` version are dangerous as these can contain very bad bugs. Only use this for test
servers.

Disabling automatic updates still allows the use of `/q update`!

## Backups and restoring

After updating to a new version (manually or automatically), configuration files and database will be automatically
backed up to a zip file to prevent losing your work due to errors. Then, configuration will be converted to a new
version. At the end, the localization will be updated with new languages, and a _changelog.txt_ file will be created.

If there were any unexpected errors during an update process, download the previous version, restore your configs from
backup, and disable autoupdating feature. Don't forget to post your error as an Issue
on [GitHub](https://github.com/BetonQuest/BetonQuest/issues/new?template=bug_report_template.md) so it can be fixed!

## Backups
Every time the plugin updates the configuration, a backup will be created. This is especially important if a development
version is being used because they may be unstable. A backup can also be created manually by running **/q backup**
command. It needs to be run from the console on an empty server because it heavily uses the database.

You can find your backups in _backup_ directory in the plugin's folder. They are .zip files containing all your
configuration and _database-backup.yml_ file, which - as the name says - is your database backup. To replace your
configuration with an older backup, delete all the files (except backups and logs) and replace them with the files from
.zip file.

If you want your database loaded, place _database-backup.yml_ file in plugin's directory. When the plugin sees this file
while enabling, it will backup the current database and load all data from that file to the database. A backup of the
old database can be found in _backups_ folder, so if you ever need to load it back, just rename it to _
database-backup.yml_ and place it back in main plugin's directory. Note that _database-backup.yml_ file will be deleted
after loading, so it does not replace your database on next plugin start.
