# [CraftEngine](https://polymart.org/product/7624/craftengine)

### Items

CraftEngine usage is integrated to the [Items](../../../../Features/Items.md) system and thus used for actions and conditions.

In addition, you can also add `quest-item` argument to tag them as "QuestItem".

```YAML title="Example"
items:
  topazAxe: "craftEngine default:topaz_axe"
  amethystTorch: "craftEngine default:amethyst_torch quest-item"
conditions:
  hasTopazAxe: "hand topazAxe"
actions:
  giveAmethystTorch: "give amethystTorch:3"
```
