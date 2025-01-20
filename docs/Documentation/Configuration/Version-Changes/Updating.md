---
icon: material/download
---
<style>
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

# :material-download: Updating BetonQuest
BetonQuest has a comfortable Auto-Updater, that you can use without any problem, but there are some things that you need
to be aware of. Alternatively you can install updated manual by your self.

Read the [update](../Configuration.md#updating) section to correctly set up the Auto-Updater.

For a production/live system it is recommended to [disable automatic updates](#enable-or-disable-automatic-updates)
and to check out the [CHANGELOG](../../CHANGELOG.md) before you execute `/q update` for manual updates.

!!! warning "Updating BetonQuest"
    If you update BetonQuest from a previous Major version you should check what you need to update manually.
    
    From 1.X to 2.X you should read the [Migration-1-2](Migration-1-2.md) guide.  
    From 2.X to 3.X you should read the [Migration-2-3](Migration-2-3.md) guide.

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
features, that will not have an impact on your existing stuff. `MAJOR` strategy is not recommended for production/live
systems, they can break everything. For a test system, you need to decide weather you want to stay on the latest version
with `MAJOR` or you want to be more stable.

By adding the `_DEV` suffix to the update strategy, you also download dev-builds for the corresponding version.
Dev-builds can contain everything: Bug fixes and new features, but also new bugs or even worse it beaks something, so
don't use this on a production/live system without checking out the changes.

## Enable or Disable automatic updates
Apart from the version you also have to choose if you want BetonQuest to update automatically or only after
confirmation. Having automatic updates enabled is handy for `PATCH` and `MINOR` but risky on `MAJOR` strategies.
Automatic updates for any `_DEV` version are dangerous as these can contain very severe bugs. Only use this for test
servers.

Disabling automatic updates still allows the use of `/q update`!

## Backups and Restoring
### Creating Backups
After a manual or automatic update BetonQuest might update some quest packages or the database to a new format. If a
file or the database is touched, an automatic backup will be created and is saved as a zip file to `BetonQuest/Backups/`
to prevent losing your work due to errors.

A backup can also be created manually by running the [backup command](../Commands-and-permissions.md#commands).

### Restoring Backups
You find your backups as zip file in the folder `BetonQuest/Backups/` containing every configuration and a dump of you
database. To restore a chosen backup stop your server, delete all the files in the folder `BetonQuest/`,
except `BetonQuest/Backups/`, and replace them with the files from the chosen backup zip file and start your server
again.

If you only want to restore the database then stop your server, only delete the existing database file and extract the
database backup file from the zip archive and start your server again.
