---
icon: material/folder
tags:
  - Ressourcepack
  - NPC Images
---

This chapter focuses on creating a simple resource pack that includes the images we will later use in our conversations.
Skip this part if you already have a complete resource pack set up with all the images!

<div class="grid" markdown>
!!! danger "Requirements"
    * [Setup Guide](../../Getting-Started/Setup-Guide/Setting-up-a-Test-Server.md)
</div>

## Adding Images into your Ressourcepack

Put your textures in a texture folder of your choice. In this example we will use `textures/ui/dialogue/salty.png`.

### Manual

If u don't have any special plugins installed that generate the Ressourcepack for you - this is your way to go.

* Create a folder called "font" in the "minecraft" folder of your Ressourcepack
* Create a ``default.json`` in that folder
* Open the File and paste the following content inside of it


``` JSON title="default.json" linenums="1"
{
    "providers": [
        {
            "type": "bitmap",
            "file": "ui/dialogu/salty",
            "ascent": 0,
            "height": 42,
            "chars": [
                "ʩ"
            ]
        },
        {
            "type": "bitmap",
            "file": "ui/dialogu/wolf",
            "ascent": 0,
            "height": 42,
            "chars": [
                "ꀁ"
            ]
        }
    ]
}

```

You will probably need to modify ascent/height to your needs.

!!! question ""
    **Tip:** its better to use \uF116 and up Unicode's in order to have many many icons. 


### Oraxen 

* Create a new dialogue.yml file inside your Oraxen/glyphs folder
* Paste the following in it and modify it to your needs. 

``` YAML title="dialogue.yml" linenums="1"
salty:
  texture: custom/ui/dialogue/salty
  ascent: 0
  height: 42
  code: 
  
wolf:
  texture: custom/ui/dialogue/wolf
  ascent: 0
  height: 42
  code:   
```

!!! question ""
    **Tip:** You can leave "code:" empty in the newer versions of Oraxen 
             as oraxen will automatically generate a suitable unicode for your needs. 

After this is done you should be able to check if everything works by pasting the unicode character into your chat.

---
[:octicons-arrow-right-16: Lets modify our QuestPackage](./Conversations.md){ .md-button .md-button--primary}
