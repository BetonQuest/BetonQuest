__**What to do Now?!**__
1. First of all: **Backup your system!**  This is really important, there could be unknown breaking bugs (not really,
   but who knows)!
2. Then download the `BetonQuest-Artifacts` from this
   link: https://github.com/BetonQuest/BetonQuest/actions/runs/1796964705 and use the `BetonQuest.jar` inside the zip
   file.
3. Ensure your server is running on **java 17**
4. Change everything, as explained in the *Changelog* down below.
5. Start you server, everything in-game should now work as before.

__**Changelog**__
- Java 17 is now required
- Every current Quest need to be moved to the folder `BetonQuest/QuestPackages`, simply move the folders in there!
- `main.yml` is now renamed to `package.yml`
- `events`, `objectives`, `conditions`, `journal` and `items` needs now an extra prefix like:
```yaml
events:
  myEvent: ....
```
- `conversations` and `menus` needs now a extra prefix like:
```yaml
conversations:
  Wolf2323:
    NPC_options: ....
    ....
```
or alternatively like:
```yaml
conversations.Wolf2323:
  NPC_options: ....
  ....
```
`Wolf2323` works like the file name (-> conversation ID in `package.yml`)!

__**Explanation:**__
- It is now possible to have as many or few files as you want in one **QP** (QuestPackage). You can create as many (
  nested) sub-folders in your package as you like.
  _All `yml` files found in the same folder or in sub-folders as the `package.yml` will be **merged**. This means we
  combine them in the memory. This means that you can now create a quest completely in the  `package.yml` or in as many
  files as you want, and the file names and folder names do not matter anymore. The only thing you cannot do is to
  define the same event, objective,... name twice in the same QP._

-It is now allowed to nest QPs inside QPs.
_A QP is limited by all folders "above" the folder that contain the `package.yml` and all folders nested inside it that
contain another `package.yml`. Any other files and folder without a package.yml are considered as contents of one
package._
