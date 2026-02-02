## LuckPerms[](https://luckperms.net/)

### Context Integration

Any BetonQuest tag (global and per-player) can be used as a LuckPerms context. This means that a player needs the specified tag for a permission
to be true - this removes the need for tons of `permission add ...` actions as you can hook your existing
quest progress tags right into LuckPerms permission
[contexts](https://luckperms.net/wiki/Context).
The syntax is as follows:

| key                                        | value |
|--------------------------------------------|-------|
| betonquest:tag:PACKAGE_NAME>TAG_NAME       | true  |
| betonquest:globaltag:PACKAGE_NAME>TAG_NAME | true  |
| betonquest:tag:myPackage>tagName           | true  |
| betonquest:globaltag:myQuest>someTag       | true  |

Check the [Luck Perms documentation](https://luckperms.net/wiki/Context)
for an in-depth explanation on what contexts are and how to add them to permissions.

### Permissions

If you prefer to directly add or remove permissions without triggering the LuckPerms changelog chat notifications,
you can utilize the `luckperms addPermission` and `luckperms removePermission` actions.
You also have the possibility to assign groups to the player via the `group.<GroupName>` permission.

```YAML title="Example"
actions:
  addDefaultGroup: "luckperms addPermission permission:group.default,group.quester" #(1)!
  addNegated: "luckperms addPermission permission:tutorial.done value:false" #(2)!
  addWithContext: "luckperms addPermission permission:group.legend context:server;lobby" #(3)!
  addTemporary: "luckperms addPermission permission:donator.level.one expiry:20 unit:MINUTES" #(4)!
  removeTutorial: "luckperms removePermission permission:tutorial.done"
  removeMultiple: "luckperms removePermission permission:tutorial.done,group.default" #(5)!
```

1. You can define single or multiple permissions with the `permission` key. You need to separate them with a comma.
2. You can define Permissions with a optional `value` of `false` to negate them and give them to the player. If you want to override the value of the permission, you can use the `value` argument and set it to `true`.
3. You can also add optional `context`s to the permissions like `server;lobby`. Read more about contexts [here](https://luckperms.net/wiki/Context). You can define multiple contexts by separating them with a comma.
4. With the key `expiry` you can define the time until the permission expires. There can only be one expiry argument. If you dont use the `unit` parameter, it defaults do DAYS. Other units can be found [here](https://help.intrexx.com/apidocs/jdk17/api/java.base/java/util/concurrent/TimeUnit.html).
5. You can remove multiple permissions at once by separating them with a comma.

You can also add `context`, `value` and `expiry` to the `removePermission` action
but its not recommended as it only removes exact matches.
Instead only use the permission to remove.
